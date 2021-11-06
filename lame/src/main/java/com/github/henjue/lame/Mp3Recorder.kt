package com.github.henjue.lame

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.annotation.RequiresPermission

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

/**
 * @author henjue henjue@gmail.com
 */
class Mp3Recorder constructor(
        val os: OutputStream,
        val samplingRate: Int = DEFAULT_SAMPLING_RATE,
        val channelConfig: Int = AudioFormat.CHANNEL_IN_MONO,
        val audioFormat: PCMFormat = PCMFormat.PCM_16BIT
) {
    var audioRecord: AudioRecord? = null
    private var bufferSize = 0
    private var ringBuffer: RingBuffer? = null
    private var buffer: ByteArray?=null
    private var encodeThread: DataEncodeThread? = null
    private var isRecording = false
    fun isRecording(): Boolean {
        return this.isRecording;
    }
    interface OnProcessListener{
        fun onRecordStart()
        fun onRecordCompleted();
        fun onError(e:Throwable);
    }
    var listener:OnProcessListener?=null
    /**
     * Default constructor. Setup recorder with default sampling rate 1 channel,
     * 16 bits pcm
     *
     * @param file output file
     */
    constructor(file: File, samplingRate: Int = DEFAULT_SAMPLING_RATE, channelConfig: Int = AudioFormat.CHANNEL_IN_MONO,
                audioFormat: PCMFormat =
                    PCMFormat.PCM_16BIT
    ) : this(FileOutputStream(file), samplingRate, channelConfig, audioFormat) {
    }
    var handler:Handler = Handler(Looper.getMainLooper())

    /**
     * Start recording. Create an encoding thread. Start record from this
     * thread.
     *
     * @throws IOException IOException
     */
    @Throws(IOException::class)
    fun startRecording() {
        if (isRecording) return
        Log.d(TAG, "Start recording")
        Log.d(TAG, "BufferSize = $bufferSize")
        // Initialize audioRecord if it's null.
        if (audioRecord == null) {
            initAudioRecorder()
        }
        audioRecord?.startRecording()
        object : Thread() {
            override fun run() {
                isRecording = true
                handler.post {
                    listener?.onRecordStart()
                }
                while (isRecording) {
                    val bytes = buffer?.let { audioRecord?.read(it, 0, bufferSize) }?:0
                    if (bytes > 0) {
                        buffer?.let { ringBuffer?.write(it, bytes) }
                    }
                }

                // release and finalize audioRecord
                try {
                    audioRecord?.stop()
                    audioRecord?.release()
                    audioRecord = null

                    // stop the encoding thread and try to wait
                    // until the thread finishes its job
                    val msg = Message.obtain(encodeThread?.getHandler(),
                        DataEncodeThread.PROCESS_STOP
                    )
                    msg.sendToTarget()
                    encodeThread?.join()
                    handler.post {
                        listener?.onRecordCompleted()
                    }
                } catch (e: InterruptedException) {
                    Log.d(TAG, "Faile to join encode thread")
                    handler.post{
                        listener?.onError(e)
                    }
                } finally {
                    try {
                        os.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        }.start()
    }

    /**
     * @throws IOException IOException
     */
    @Throws(IOException::class)
    fun stopRecording() {
        Log.d(TAG, "stop recording")
        isRecording = false
    }

    /**
     * Initialize audio recorder
     */
    @Throws(IOException::class)
    @RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    private fun initAudioRecorder() {
        val bytesPerFrame = audioFormat.bytesPerFrame
        /* Get number of samples. Calculate the buffer size (round up to the
           factor of given frame size) */
        var frameSize = AudioRecord.getMinBufferSize(samplingRate,
                channelConfig, audioFormat.audioFormat) / bytesPerFrame
        if (frameSize % FRAME_COUNT != 0) {
            frameSize += (FRAME_COUNT - frameSize % FRAME_COUNT)
            Log.d(TAG, "Frame size: $frameSize")
        }
        bufferSize = frameSize * bytesPerFrame

        /* Setup audio recorder */audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC,
                samplingRate, channelConfig, audioFormat.audioFormat,
                bufferSize)

        // Setup RingBuffer. Currently is 10 times size of hardware buffer
        // Initialize buffer to hold data
        ringBuffer = RingBuffer(10 * bufferSize)
        buffer = ByteArray(bufferSize)
        LameEncoder.init(samplingRate, 1, samplingRate, BIT_RATE)

        // Create and run thread used to encode data
        // The thread will
        encodeThread = DataEncodeThread(ringBuffer!!, os!!, bufferSize)
        encodeThread?.start()
        audioRecord?.setRecordPositionUpdateListener(encodeThread, encodeThread?.getHandler())
        audioRecord?.positionNotificationPeriod = FRAME_COUNT
    }

    companion object {
        private val TAG = Mp3Recorder::class.java.simpleName
        private const val DEFAULT_SAMPLING_RATE = 22050
        private const val FRAME_COUNT = 160

        /* Encoded bit rate. MP3 file will be encoded with bit rate 32kbps */
        private const val BIT_RATE = 32
    }

}
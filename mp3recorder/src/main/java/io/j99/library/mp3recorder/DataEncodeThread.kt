package io.j99.library.mp3recorder

import android.media.AudioRecord
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import io.j99.library.mp3recorder.LameEncoder.encode
import io.j99.library.mp3recorder.LameEncoder.flush
import java.io.IOException
import java.io.OutputStream
import java.lang.ref.WeakReference
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.CountDownLatch

class DataEncodeThread(private val ringBuffer: RingBuffer, private val os: OutputStream,
                       private val bufferSize: Int) : Thread(), AudioRecord.OnRecordPositionUpdateListener {
    private var handler: StopHandler? = null
    private val buffer: ByteArray = ByteArray(bufferSize)
    private val mp3Buffer: ByteArray = ByteArray((7200 + buffer.size * 2 * 1.25).toInt())
    private val handlerInitLatch = CountDownLatch(1)

    /**
     * @author buihong_ha
     * @see https://groups.google.com/forum/?fromgroups=.!msg/android-developers/1aPZXZG6kWk/lIYDavGYn5UJ
     */
    internal class StopHandler(encodeThread: DataEncodeThread) : Handler() {
        var encodeThread: WeakReference<DataEncodeThread> = WeakReference(encodeThread)
        override fun handleMessage(msg: Message) {
            if (msg.what == PROCESS_STOP) {
                val threadRef = encodeThread.get()
                // Process all data in ring buffer and flush
                // left data to file
                while (threadRef!!.processData() > 0);
                // Cancel any event left in the queue
                removeCallbacksAndMessages(null)
                threadRef.flushAndRelease()
                looper.quit()
            }
            super.handleMessage(msg)
        }

    }

    override fun run() {
        Looper.prepare()
        handler = StopHandler(this)
        handlerInitLatch.countDown()
        Looper.loop()
    }

    /**
     * Return the handler attach to this thread
     *
     * @return Handler
     */
    fun getHandler(): Handler? {
        try {
            handlerInitLatch.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            Log.e(TAG, "Error when waiting handle to init")
        }
        return handler
    }

    override fun onMarkerReached(recorder: AudioRecord) {
        // Do nothing
    }

    override fun onPeriodicNotification(recorder: AudioRecord) {
        processData()
    }

    /**
     * Get data from ring buffer
     * Encode it to mp3 frames using lame encoder
     *
     * @return Number of bytes read from ring buffer
     * 0 in case there is no data left
     */
    private fun processData(): Int {
        val bytes = ringBuffer.read(buffer, bufferSize)
        if (bytes > 0) {
            val innerBuf = ShortArray(bytes / 2)
            ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()[innerBuf]
            val encodedSize = encode(innerBuf, innerBuf, bytes / 2, mp3Buffer)
            if (encodedSize < 0) {
                Log.e(TAG, "Lame encoded size: $encodedSize")
            }
            try {
                os.write(mp3Buffer, 0, encodedSize)
            } catch (e: IOException) {
                Log.e(TAG, "Unable to write to file",e)
            }
            return bytes
        }
        return 0
    }

    /**
     * Flush all data left in lame buffer to file
     */
    private fun flushAndRelease() {
        val flushResult = flush(mp3Buffer)
        if (flushResult > 0) {
            try {
                os.write(mp3Buffer, 0, flushResult)
            } catch (e: IOException) {
                // TODO: Handle error when flush
                Log.e(TAG, "Lame flush error",e)
            }
        }
    }

    companion object {
        private val TAG = DataEncodeThread::class.java.simpleName
        const val PROCESS_STOP = 1
    }

}
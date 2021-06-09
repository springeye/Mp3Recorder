package io.j99.mp3recorder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.j99.library.mp3recorder.Mp3Recorder
import io.j99.mp3recorder.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity(), Mp3Recorder.OnProcessListener {
    var recordind=false;
    var recorder:Mp3Recorder?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRecord.setOnClickListener {
            recordind=!recordind;
            if(recordind){
                val file = File(filesDir, "test.mp3")
                println(file)
                recorder = Mp3Recorder(file)
                recorder?.listener=this
                recorder?.startRecording()
                binding.btnRecord.text="stop"
            }else{
                recorder?.stopRecording()
                binding.btnRecord.text="start"
            }
        }

    }

    override fun onRecordStart() {
        println("开始录音")
    }


    override fun onRecordCompleted() {
        println("完成录音")
    }


    override fun onError(e: Throwable) {
        e.printStackTrace()
    }

}
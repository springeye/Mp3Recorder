package com.github.henjue.lame.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.henjue.lame.example.databinding.ActivityMain2Binding
import com.github.henjue.lame.Mp3Recorder
import java.io.File

class MainActivity : AppCompatActivity(), Mp3Recorder.OnProcessListener {
    var recordind=false;
    var recorder: Mp3Recorder?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityMain2Binding.inflate(layoutInflater)
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
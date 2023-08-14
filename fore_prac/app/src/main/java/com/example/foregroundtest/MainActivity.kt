package com.example.foregroundtest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.foregroundtest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.foreStart).setOnClickListener {
            val intent = Intent(this, SampleForegroundService::class.java)
            applicationContext.startForegroundService(intent)
        }
        findViewById<Button>(R.id.foreEnd).setOnClickListener {
            val intent = Intent(this, SampleForegroundService::class.java)
            applicationContext.stopService(intent)
        }
//        binding.foreStart.setOnClickListener {
//            val intent = Intent(this, SampleForegroundService::class.java)
//            applicationContext.startForegroundService(intent)
//        }
//        binding.foreEnd.setOnClickListener {
//            val intent = Intent(this, SampleForegroundService::class.java)
//            applicationContext.stopService(intent)
//        }
    }

}
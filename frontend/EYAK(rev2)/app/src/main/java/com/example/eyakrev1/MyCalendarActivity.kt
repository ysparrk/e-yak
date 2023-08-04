package com.example.eyakrev1

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.eyakrev1.databinding.ActivityMainBinding
import com.example.eyakrev1.databinding.ActivityMyCalendarBinding

class MyCalendarActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMyCalendarBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


    }
}
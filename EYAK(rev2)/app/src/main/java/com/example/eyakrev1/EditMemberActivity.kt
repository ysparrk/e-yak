package com.example.eyakrev1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.eyakrev1.databinding.ActivityEditMemberBinding

class EditMemberActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityEditMemberBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}
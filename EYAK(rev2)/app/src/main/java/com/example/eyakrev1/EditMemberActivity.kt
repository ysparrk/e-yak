package com.example.eyakrev1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.example.eyakrev1.databinding.ActivityEditMemberBinding

class EditMemberActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityEditMemberBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        loadData()
    }

    private fun loadData() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val nickName = pref.getString("KEY_NICKNAME", "WOW")

        if(nickName != "WOW"){
            binding.nickNameEdit.setText(nickName)
        }
    }
}
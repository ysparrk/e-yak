package com.example.eyakrev1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.example.eyakrev1.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.signup.setOnClickListener {
            if (binding.nickNameInput.text.isNotBlank()) {  // 저장하면 안되는 경우 지정
                saveData(
                    binding.nickNameInput.text.toString(),
                )
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun saveData(nickName: String){
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = pref.edit()

        editor.putString("KEY_NICKNAME", nickName).apply()
    }
}
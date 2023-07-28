package com.example.eyakrev1

import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.eyakrev1.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes

class LoginActivity : AppCompatActivity() {

//    private lateinit var oneTapClient: SignInClient
//    private lateinit var signInRequest: BeginSignInRequest
//    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
//    private var showOneTapUI = true
//    private val TAG = "LoginActivity" // 원하는 태그 이름으로 변경 가능

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.tmpBtn.setOnClickListener {
            //val intent = Intent(this, MainActivity::class.java)
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        binding.googleLoginLinearLayout.setOnClickListener {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


    }
}
package com.example.eyakrev1

import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.PreferenceManager
import com.example.eyakrev1.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    
    // 주요 참고자료
    // https://www.youtube.com/watch?v=ER0xid0w-_Y

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private var oneTapClient: SignInClient? = null
    private var signUpRequest: BeginSignInRequest? = null
    private var signInRequest: BeginSignInRequest? = null

    private val oneTapResult = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()){ result ->
        try {
            val credential = oneTapClient?.getSignInCredentialFromIntent(result.data)
            val idToken = credential?.googleIdToken
            when {
                idToken != null -> {
                    // Got an ID token from Google. Use it to authenticate
                    // with your backend
                    val msg = "idToken: $idToken"
                    Snackbar.make(binding.root, msg, Snackbar.LENGTH_INDEFINITE).show()
                    Log.d("google one tap", msg)
                    
                    // sharedPreference에 저장
                    val pref = PreferenceManager.getDefaultSharedPreferences(this)
                    val editor = pref.edit()
                    editor.putString("GOOGLE_TOKEN", idToken).apply()
                }
                else -> {
                    // shouldn't happen
                    Log.d("google one tap", "No iD token!")
                    Snackbar.make(binding.root, "No ID token!", Snackbar.LENGTH_INDEFINITE).show()
                }
            }

        } catch (e: ApiException) {
            when (e.statusCode) {
                CommonStatusCodes.CANCELED -> {
                    Log.d("google one tap", "One-tap dialog was closed")
                    // Don't re-prompt the user
                    Snackbar.make(binding.root, "One-tap dialog was closed", Snackbar.LENGTH_INDEFINITE).show()
                }
                CommonStatusCodes.NETWORK_ERROR -> {
                    Log.d("google one tap", "One-tap encountered a network error")
                    // Try again or just ignore
                    Snackbar.make(binding.root, "One-tap encountered a network error", Snackbar.LENGTH_INDEFINITE).show()
                }
                else -> {
                    Log.d("google one tap", "Couldn't get credential from result" + " (${e.localizedMessage})")
                    Snackbar.make(binding.root, "Couldn't get credential from result.\" +\n" + " (${e.localizedMessage})", Snackbar.LENGTH_INDEFINITE).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = pref.edit()

        oneTapClient = Identity.getSignInClient(this)
        signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID
                    .setServerClientId(getString(R.string.your_web_client_id))
                    // Show all accounts on the device
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .build()
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID
                    .setServerClientId(getString(R.string.your_web_client_id))
                    // Show all accounts on the device
                    .setFilterByAuthorizedAccounts(true)
                    .build())
            .build()

        // 이미 구글 토큰을 발급 받았을 경우
        // 이를 활용해서 서버에 로그인 시도 => 성공하면 메인페이지로, 실패하면 회원가입
        if (pref.getString("GOOGLE_TOKEN", null) != null) {
            // 서버에 로그인 시도

            // 로그인 성공하면 메인페이지로
            
            // 로그인 실패하면 구글 토큰부터 재발급 (만료를 대비하여) => 회원가입 진행
        }
        // 구글 토큰이 없다면 로그인 버튼 눌러서 진행하도록

        binding.tmpBtn.setOnClickListener {
            //val intent = Intent(this, MainActivity::class.java)
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        binding.googleLoginLinearLayout.setOnClickListener {
            displaySignIn()


        }
    }

    private fun tryLoginToServer(googleToken: String) {

    }


    private fun displaySignIn() {
        oneTapClient?.beginSignIn(signInRequest!!)
            ?.addOnSuccessListener(this) { result ->
                try {
                    val ib = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    oneTapResult.launch(ib)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e("google login btn click", "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            ?.addOnFailureListener(this) { e ->
                // No Google Accounts found. Just continue presenting the signed-out UI
                displaySignUp()
                Log.d("google login btn click", e.localizedMessage!!)
            }
    }

    private fun displaySignUp() {
        oneTapClient?.beginSignIn(signUpRequest!!)
            ?.addOnSuccessListener(this) { result ->
                try {
                    val ib = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    oneTapResult.launch(ib)
                } catch (e: IntentSender.SendIntentException) {
                    Log.e("google login btn click", "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            ?.addOnFailureListener(this) { e ->
                // No Google Accounts found. Just continue presenting the signed-out UI
                displaySignUp()
                Log.d("google login btn click", e.localizedMessage!!)
            }
    }
}
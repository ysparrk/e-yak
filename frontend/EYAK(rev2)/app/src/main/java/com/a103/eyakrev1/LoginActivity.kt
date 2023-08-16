package com.a103.eyakrev1

import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.PreferenceManager
import com.a103.eyakrev1.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val api = EyakService.create()

    private var oneTapClient: SignInClient? = null
    private var signUpRequest: BeginSignInRequest? = null
    private var signInRequest: BeginSignInRequest? = null

    private val oneTapResult = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()){ result ->
        try {
            val credential = oneTapClient?.getSignInCredentialFromIntent(result.data)

            val idToken = credential?.googleIdToken
            when {
                idToken != null -> {

                    // sharedPreference에 저장
                    val pref = PreferenceManager.getDefaultSharedPreferences(this)
                    val editor = pref.edit()
                    editor.putString("GOOGLE_TOKEN", idToken).apply()

                    tryLoginToServer(idToken, isAutoLogin = false)
                }
                else -> {
                    Snackbar.make(binding.root, "No ID token!", Snackbar.LENGTH_INDEFINITE).show()
                }
            }

        } catch (e: ApiException) {
            when (e.statusCode) {
                CommonStatusCodes.CANCELED -> {
                    Log.d("google one tap", "One-tap dialog was closed")
                }
                CommonStatusCodes.NETWORK_ERROR -> {
                    Log.d("google one tap", "One-tap encountered a network error")
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
        // 자동 로그인 시도
        val loadedGoogleToken = pref.getString("GOOGLE_TOKEN", null)
        tryLoginToServer(loadedGoogleToken, isAutoLogin = true)

        // 구글 토큰이 없다면 로그인 버튼 눌러서 진행하도록
        binding.googleLoginLinearLayout.setOnClickListener {
            displaySignIn()
        }
    }

    private fun tryLoginToServer(loadedGoogleToken: String?, isAutoLogin: Boolean) {
        if (loadedGoogleToken != null) {
            // 서버에 로그인 시도
            val data = LoginBodyModel("google", loadedGoogleToken)
            api.signIn(data).enqueue(object: Callback<LoginResponseModel> {
                override fun onResponse(call: Call<LoginResponseModel>, response: Response<LoginResponseModel>) {
                    if (response.code() == 400) {
                        Log.d("로그", "로그인 400 Bad Request: 해당하는 이 member가 존재하지 않는 경우, AccessToken이 유효하지 않은 경우")
                        // 회원가입 페이지를 띄워주자
                        if (!isAutoLogin) {
                            val intent = Intent(getApplicationContext(), SignupActivity::class.java)
                            startActivity(intent)
                        }
                    } else if (response.code() == 200) {
                        Log.d("로그", "로그인 200 OK")

                        // 서버로부터 받은 토큰을 저장하자
                        Toast.makeText(applicationContext, "[ ${response.body()?.memberDto?.nickname} ]님 안녕하세요", Toast.LENGTH_SHORT).show()

                        val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                        val editor = pref.edit()
                        editor.putString("SERVER_ACCESS_TOKEN", response.body()?.accessToken)
                            .putString("SERVER_REFRESH_TOKEN", response.body()?.refreshToken)
                            .putInt("SERVER_USER_ID", response.body()?.memberDto!!.id)
                            .putString("KEY_NICKNAME", response.body()?.memberDto!!.nickname)
                            .putString("wakeTime", response.body()?.memberDto!!.wakeTime)
                            .putString("breakfastTime", response.body()?.memberDto!!.breakfastTime)
                            .putString("lunchTime", response.body()?.memberDto!!.lunchTime)
                            .putString("dinnerTime", response.body()?.memberDto!!.dinnerTime)
                            .putString("bedTime", response.body()?.memberDto!!.bedTime)
                            .putString("eatingDuration", response.body()?.memberDto!!.eatingDuration)
                            .apply()
                        ///////////////////////////////

                        // 메인 페이지를 띄워주자
                        val intent = Intent(getApplicationContext(), MainActivity::class.java)
                        startActivity(intent)
                    }

                }

                override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                    Log.d("로그", "로그인 onFailure")

                    // 로그인 실패 시 회원가입 페이지로 이동 (토큰은 발급받은 상태)
                    // 거기서 회원가입 정보를 입력 받고 회원가입 진행
                    // 자동 로그인 때는 이 작업을 강제시키지 말자
                    // 네트워크 에러인 경우, 저 주소를 찾을 수 없다고 나온다
                    if (!isAutoLogin && t.message.toString().startsWith("Unable to resolve host")) {
                        Toast.makeText(getApplicationContext(),"네트워크에 문제가 있습니다", Toast.LENGTH_SHORT).show();
                    } else if (!isAutoLogin) { // 네트워크는 연결되어 있는데, 로그인에 실패한 경우
                        val intent = Intent(getApplicationContext(), SignupActivity::class.java)
                        startActivity(intent)
                    }
                }
            })
        }
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
                // displaySignUp()
                Log.d("google login btn click", e.localizedMessage!!)
            }
    }
}
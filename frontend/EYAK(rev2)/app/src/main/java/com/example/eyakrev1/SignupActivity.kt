package com.example.eyakrev1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.preference.PreferenceManager
import com.example.eyakrev1.databinding.ActivitySignupBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }

    val api = EyakService.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        
        // 중복 검사를 진행했는지 여부
        var isDuplicateChecked = false
        
        // 중복 체크 버튼을 클릭하면
        binding.nickNameChk.setOnClickListener {
            // 서버로부터 중복체크 요청을 보내고 확인을 받아야함 => 중복되지 않았을 경우에만 true로 바꿔주자

            api.checkDuplicate(nickname = binding.nickNameInput.text.toString()).enqueue(object: Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d("log",response.toString())
                    Log.d("log", response.body().toString())

                    // 통신에 성공하더라도, statusCode 기반으로 할 행동을 정의하자
                    if (response.code() == 400) {
                        Log.d("log","중복체크 실패")
                    } else if (response.code() == 200) {
                        if (response.body().toString() == "true") { // if true => 중복인거
                            Toast.makeText(getApplicationContext(), "이미 사용중인 닉네임입니다", Toast.LENGTH_SHORT).show()
                        } else { // if false
                            isDuplicateChecked = true
                            Toast.makeText(getApplicationContext(), "중복 검사 완료되었습니다!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    // 실패
                    Log.d("log",t.message.toString())
                    Log.d("log","fail")
                }
            })
        }


        val nickNameInputView = binding.nickNameInput
        nickNameInputView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // 이전 중복체크 여부와 관계 없이 다시 수행해야함
                isDuplicateChecked = false
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.signup.setOnClickListener {
            if (binding.nickNameInput.text.isNotBlank()) {

                // 닉네임 중복 검사를 진행하지 않았을 경우
                if (!isDuplicateChecked) {
                    Toast.makeText(getApplicationContext(), "닉네임 중복 검사를 해주세요!", Toast.LENGTH_SHORT).show()
                } else if (!binding.checkBox.isChecked()) {
                    Toast.makeText(getApplicationContext(), "개인 정보 수집에 동의해주세요!", Toast.LENGTH_SHORT).show()
                } else {
                    // sharedPreference에 저장
                    trySignUp(
                        binding.nickNameInput.text.toString(),
                        binding.wakeTimeH.text.toString(),
                        binding.wakeTimeM.text.toString(),
                        binding.breakfastTimeH.text.toString(),
                        binding.breakfastTimeM.text.toString(),
                        binding.lunchTimeH.text.toString(),
                        binding.lunchTimeM.text.toString(),
                        binding.dinnerTimeH.text.toString(),
                        binding.dinnerTimeM.text.toString(),
                        binding.bedTimeH.text.toString(),
                        binding.bedTimeM.text.toString(),
                        binding.eatingTimeH.text.toString(),
                        binding.eatingTimeM.text.toString(),
                    )

//                    val intent = Intent(this, MainActivity::class.java)
//                    startActivity(intent)
                }
            }
            else { // 닉네임이 빈 문자열이면 저장하면 안된다
                Toast.makeText(getApplicationContext(), "사용하실 닉네임을 입력해주세요!", Toast.LENGTH_SHORT).show()
            }


        }

    }
    
    // 디폴트 값을 부여하자
    private fun trySignUp(nickName: String, wakeTimeH: String, wakeTimeM: String, breakfastTimeH: String, breakfastTimeM: String,
                         lunchTimeH: String, lunchTimeM: String, dinnerTimeH: String, dinnerTimeM: String,
                         bedTimeH: String, bedTimeM: String, eatingTimeH: String, eatingTimeM: String){
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = pref.edit()

        editor.putString("KEY_NICKNAME", nickName)
            .putString("KEY_WAKE_TIME_H", if(wakeTimeH != "") wakeTimeH else "06")
            .putString("KEY_WAKE_TIME_M", if(wakeTimeM != "") wakeTimeM else "50")
            .putString("KEY_BREAKFAST_TIME_H", if(breakfastTimeH != "") breakfastTimeH else "07")
            .putString("KEY_BREAKFAST_TIME_M", if(breakfastTimeM != "") breakfastTimeM else "20")
            .putString("KEY_LUNCH_TIME_H", if(lunchTimeH != "") lunchTimeH else "11")
            .putString("KEY_LUNCH_TIME_M", if(lunchTimeM != "") lunchTimeM else "10")
            .putString("KEY_DINNER_TIME_H", if(dinnerTimeH != "") dinnerTimeH else "19")
            .putString("KEY_DINNER_TIME_M", if(dinnerTimeM != "") dinnerTimeM else "20")
            .putString("KEY_BED_TIME_H", if(bedTimeH != "") bedTimeH else "01")
            .putString("KEY_BED_TIME_M", if(bedTimeM != "") bedTimeM else "00")
            .putString("KEY_EATING_TIME_H", if(eatingTimeH != "") eatingTimeH else "00")
            .putString("KEY_EATING_TIME_M", if(eatingTimeM != "") eatingTimeM else "20")
            .apply()

        val data = SignUpBodyModel(providerName = "google",
                                   token = pref.getString("GOOGLE_TOKEN", ""),
                                   nickname = pref.getString("KEY_NICKNAME", ""),
                                   wakeTime = "${pref.getString("KEY_WAKE_TIME_H", "06")}:${pref.getString("KEY_WAKE_TIME_M", "50")}:00",
                                   breakfastTime = "${pref.getString("KEY_BREAKFAST_TIME_H", "07")}:${pref.getString("KEY_BREAKFAST_TIME_M", "20")}:00",
                                   lunchTime = "${pref.getString("KEY_LUNCH_TIME_H", "11")}:${pref.getString("KEY_LUNCH_TIME_M", "10")}:00",
                                   dinnerTime = "${pref.getString("KEY_DINNER_TIME_H", "19")}:${pref.getString("KEY_DINNER_TIME_M", "20")}:00",
                                   bedTime = "${pref.getString("KEY_BED_TIME_H", "01")}:${pref.getString("KEY_BED_TIME_M", "00")}:00",
                                   eatingDuration = "${pref.getString("KEY_EATING_TIME_H", "00")}:${pref.getString("KEY_EATING_TIME_M", "20")}:00")

        val tempToken = pref.getString("GOOGLE_TOKEN", "")

        api.signUp(data).enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("log",response.toString())
                Log.d("log", response.body().toString())

                // 통신에 성공하더라도, statusCode 기반으로 할 행동을 정의하자
                if (response.code() == 400) {
                    Log.d("log","회원가입 실패")
                    Toast.makeText(getApplicationContext(), "이미 가입하셨거나, 유효하지 않은 토큰입니다", Toast.LENGTH_SHORT).show()
                } else if (response.code() == 201) {
                    // 회원 가입에 성공했으니 Login Activity로 이동 => sharedPreference 이미 있으니, 자동 로그인 시도할거 => 자동 로그인에 성공하면, Main Activity로 이동할테니
                    val intent = Intent(getApplicationContext(), LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // 실패
//                Log.d("log",t.message.toString())
//                Log.d("log","fail")
            }
        })
    }
}
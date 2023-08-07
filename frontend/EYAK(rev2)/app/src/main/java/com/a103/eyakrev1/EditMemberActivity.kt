package com.a103.eyakrev1

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.a103.eyakrev1.databinding.ActivityEditMemberBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EditMemberActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityEditMemberBinding.inflate(layoutInflater)
    }

    private val api = EyakService.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        loadData()

        binding.deleteAccountButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("정말 탈퇴하시겠습니까?")
                .setMessage("회원 탈퇴 시 복구할 수 없으니, 신중하게 선택하시기 바랍니다")
                .setPositiveButton("탈퇴하기", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                        val memberId = pref.getInt("SERVER_USER_ID", -1)
                        val serverAccessToken = pref.getString("SERVER_ACCESS_TOKEN", "")

                        api.deleteAccount(memberId = memberId, Authorization = "Bearer ${serverAccessToken}").enqueue(object: Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                                Log.d("log", response.toString())
//                                Log.d("log", response.body().toString())

                                if (response.code() == 401) {
                                    Toast.makeText(applicationContext, "인증되지 않은 사용자입니다", Toast.LENGTH_SHORT).show()
                                } else if (response.code() == 200) {
                                    Toast.makeText(applicationContext, "지금까지 지금이:약을 이용해주셔서 감사합니다", Toast.LENGTH_LONG).show()

                                    // 로그인 페이지를 띄워주자
                                    val intent = Intent(getApplicationContext(), LoginActivity::class.java)
                                    startActivity(intent)
                                }
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Toast.makeText(applicationContext, "연결이 불안정합니다", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                })
                .setNegativeButton("취소하기", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        Log.d("MyTag", "negative")
                    }
                })
                .create()
                .show()
        }

        binding.editCancel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.editCommit.setOnClickListener {
            editCommitButtonClicked(
                wakeTimeH = binding.wakeTimeHEdit.text.toString(),
                wakeTimeM = binding.wakeTimeMEdit.text.toString(),
                breakfastTimeH = binding.breakfastTimeHEdit.text.toString(),
                breakfastTimeM = binding.breakfastTimeMEdit.text.toString(),
                lunchTimeH = binding.lunchTimeHEdit.text.toString(),
                lunchTimeM = binding.lunchTimeMEdit.text.toString(),
                dinnerTimeH = binding.dinnerTimeHEdit.text.toString(),
                dinnerTimeM = binding.dinnerTimeMEdit.text.toString(),
                bedTimeH = binding.bedTimeHEdit.text.toString(),
                bedTimeM = binding.bedTimeMEdit.text.toString(),
                eatingTimeH = binding.eatingTimeHEdit.text.toString(),
                eatingTimeM = binding.eatingTimeMEdit.text.toString(),
            )
        }
    }

    private fun loadData() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val nickName = pref.getString("KEY_NICKNAME", "")
        val wakeTimeH = pref.getString("KEY_WAKE_TIME_H", "")
        val wakeTimeM = pref.getString("KEY_WAKE_TIME_M", "")
        val breakfastTimeH = pref.getString("KEY_BREAKFAST_TIME_H", "")
        val breakfastTimeM = pref.getString("KEY_BREAKFAST_TIME_M", "")
        val lunchTimeH = pref.getString("KEY_LUNCH_TIME_H", "")
        val lunchTimeM = pref.getString("KEY_LUNCH_TIME_M", "")
        val dinnerTimeH = pref.getString("KEY_DINNER_TIME_H", "")
        val dinnerTimeM = pref.getString("KEY_DINNER_TIME_M", "")
        val bedTimeH = pref.getString("KEY_BED_TIME_H", "")
        val bedTimeM = pref.getString("KEY_BED_TIME_M", "")
        val eatingTimeH = pref.getString("KEY_EATING_TIME_H", "")
        val eatingTimeM = pref.getString("KEY_EATING_TIME_M", "")

        if(nickName != ""){
            binding.nickNameEdit.setText(nickName)
        }

        binding.wakeTimeHEdit.setText(wakeTimeH)
        binding.wakeTimeMEdit.setText(wakeTimeM)
        binding.breakfastTimeHEdit.setText(breakfastTimeH)
        binding.breakfastTimeMEdit.setText(breakfastTimeM)
        binding.lunchTimeHEdit.setText(lunchTimeH)
        binding.lunchTimeMEdit.setText(lunchTimeM)
        binding.dinnerTimeHEdit.setText(dinnerTimeH)
        binding.dinnerTimeMEdit.setText(dinnerTimeM)
        binding.bedTimeHEdit.setText(bedTimeH)
        binding.bedTimeMEdit.setText(bedTimeM)
        binding.eatingTimeHEdit.setText(eatingTimeH)
        binding.eatingTimeMEdit.setText(eatingTimeM)
    }

    private fun editCommitButtonClicked(wakeTimeH: String, wakeTimeM: String, breakfastTimeH: String, breakfastTimeM: String,
                         lunchTimeH: String, lunchTimeM: String, dinnerTimeH: String, dinnerTimeM: String,
                         bedTimeH: String, bedTimeM: String, eatingTimeH: String, eatingTimeM: String){
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = pref.edit()

        val memberId = pref.getInt("SERVER_USER_ID", -1)
        val serverAccessToken = pref.getString("SERVER_ACCESS_TOKEN", "")
        val data = ChangeAccountInfoBodyModel(
            wakeTime = "${wakeTimeH}:${wakeTimeM}:00",
            breakfastTime = "${breakfastTimeH}:${breakfastTimeM}:00",
            lunchTime = "${lunchTimeH}:${lunchTimeM}:00",
            dinnerTime = "${dinnerTimeH}:${dinnerTimeM}:00",
            bedTime = "${bedTimeH}:${bedTimeM}:00",
            eatingDuration = "${eatingTimeH}:${eatingTimeM}:00",
        )

        api.changeAccountInfo(memberId = memberId, params = data, Authorization = "Bearer ${serverAccessToken}").enqueue(object: Callback<ChangeAccountInfoResponseModel> {
            override fun onResponse(call: Call<ChangeAccountInfoResponseModel>, response: Response<ChangeAccountInfoResponseModel>) {
                Log.d("log", response.toString())
                Log.d("log", response.body().toString())

                if (response.code() == 401) {
                    Toast.makeText(applicationContext, "인증되지 않은 사용자입니다", Toast.LENGTH_SHORT).show()
                } else if (response.code() == 400) {
                    Toast.makeText(applicationContext, "해당 유저는 존재하지 않습니다", Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 200) {
                    Toast.makeText(applicationContext, "성공적으로 변경되었습니다", Toast.LENGTH_LONG).show()

                    // 새로운 정보를 우리도 간단하게 저장
                    editor.putString("KEY_WAKE_TIME_H", if(wakeTimeH != "") wakeTimeH else "06")
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

                    // 메인 페이지를 띄워주자
                    val intent = Intent(getApplicationContext(), MainActivity::class.java)
                    startActivity(intent)
                }
            }
            override fun onFailure(call: Call<ChangeAccountInfoResponseModel>, t: Throwable) {
                Toast.makeText(applicationContext, "연결이 불안정합니다", Toast.LENGTH_SHORT).show()
            }
        })




    }
}
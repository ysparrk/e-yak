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

        var wakeTimeHFilled = "06"
        var wakeTimeMFilled = "50"
        var breakfastTimeHFilled = "07"
        var breakfastTimeMFilled = "20"
        var lunchTimeHFilled = "11"
        var lunchTimeMFilled = "10"
        var dinnerTimeHFilled = "19"
        var dinnerTimeMFilled = "20"
        var bedTimeHFilled = "23"
        var bedTimeMFilled = "00"
        var eatingTimeHFilled = "00"
        var eatingTimeMFilled = "20"


        if (wakeTimeH.length == 2) {
            wakeTimeHFilled = wakeTimeH
        } else if (wakeTimeH.length == 1) {
            wakeTimeHFilled = "0${wakeTimeH}"
        }

        if (wakeTimeM.length == 2) {
            wakeTimeMFilled = wakeTimeM
        } else if (wakeTimeM.length == 1) {
            wakeTimeMFilled = "0${wakeTimeM}"
        }

        if (breakfastTimeH.length == 2) {
            breakfastTimeHFilled = breakfastTimeH
        } else if (breakfastTimeH.length == 1) {
            breakfastTimeHFilled = "0${breakfastTimeH}"
        }

        if (breakfastTimeM.length == 2) {
            breakfastTimeMFilled = breakfastTimeM
        } else if (breakfastTimeM.length == 1) {
            breakfastTimeMFilled = "0${breakfastTimeM}"
        }

        if (lunchTimeH.length == 2) {
            lunchTimeHFilled = lunchTimeH
        } else if (lunchTimeH.length == 1) {
            lunchTimeHFilled = "0${lunchTimeH}"
        }

        if (lunchTimeM.length == 2) {
            lunchTimeMFilled = lunchTimeM
        } else if (lunchTimeM.length == 1) {
            lunchTimeMFilled = "0${lunchTimeM}"
        }

        if (dinnerTimeH.length == 2) {
            dinnerTimeHFilled = dinnerTimeH
        } else if (dinnerTimeH.length == 1) {
            dinnerTimeHFilled = "0${dinnerTimeH}"
        }

        if (dinnerTimeM.length == 2) {
            dinnerTimeMFilled = dinnerTimeM
        } else if (dinnerTimeM.length == 1) {
            dinnerTimeMFilled = "0${dinnerTimeM}"
        }

        if (bedTimeH.length == 2) {
            bedTimeHFilled = bedTimeH
        } else if (bedTimeH.length == 1) {
            bedTimeHFilled = "0${bedTimeH}"
        }

        if (bedTimeM.length == 2) {
            bedTimeMFilled = bedTimeM
        } else if (bedTimeM.length == 1) {
            bedTimeMFilled = "0${bedTimeM}"
        }

        if (eatingTimeH.length == 2) {
            eatingTimeHFilled = eatingTimeH
        } else if (eatingTimeH.length == 1) {
            eatingTimeHFilled = "0${eatingTimeH}"
        }

        if (eatingTimeM.length == 2) {
            eatingTimeMFilled = eatingTimeM
        } else if (eatingTimeM.length == 1) {
            eatingTimeMFilled = "0${eatingTimeM}"
        }

        val data = ChangeAccountInfoBodyModel(
            wakeTime = "${wakeTimeHFilled}:${wakeTimeMFilled}:00",
            breakfastTime = "${breakfastTimeHFilled}:${breakfastTimeMFilled}:00",
            lunchTime = "${lunchTimeHFilled}:${lunchTimeMFilled}:00",
            dinnerTime = "${dinnerTimeHFilled}:${dinnerTimeMFilled}:00",
            bedTime = "${bedTimeHFilled}:${bedTimeMFilled}:00",
            eatingDuration = "${eatingTimeHFilled}:${eatingTimeMFilled}:00",
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
                    editor.putString("KEY_WAKE_TIME_H", if(wakeTimeHFilled != "") wakeTimeHFilled else "06")
                        .putString("KEY_WAKE_TIME_M", if(wakeTimeMFilled != "") wakeTimeMFilled else "50")
                        .putString("KEY_BREAKFAST_TIME_H", if(breakfastTimeHFilled != "") breakfastTimeHFilled else "07")
                        .putString("KEY_BREAKFAST_TIME_M", if(breakfastTimeMFilled != "") breakfastTimeMFilled else "20")
                        .putString("KEY_LUNCH_TIME_H", if(lunchTimeHFilled != "") lunchTimeHFilled else "11")
                        .putString("KEY_LUNCH_TIME_M", if(lunchTimeMFilled != "") lunchTimeMFilled else "10")
                        .putString("KEY_DINNER_TIME_H", if(dinnerTimeHFilled != "") dinnerTimeHFilled else "19")
                        .putString("KEY_DINNER_TIME_M", if(dinnerTimeMFilled != "") dinnerTimeMFilled else "20")
                        .putString("KEY_BED_TIME_H", if(bedTimeHFilled != "") bedTimeHFilled else "23")
                        .putString("KEY_BED_TIME_M", if(bedTimeMFilled != "") bedTimeMFilled else "00")
                        .putString("KEY_EATING_TIME_H", if(eatingTimeHFilled != "") eatingTimeHFilled else "00")
                        .putString("KEY_EATING_TIME_M", if(eatingTimeMFilled != "") eatingTimeMFilled else "20")
                        .putString("wakeTime", data.wakeTime)
                        .putString("breakfastTime", data.breakfastTime)
                        .putString("lunchTime", data.lunchTime)
                        .putString("dinnerTime", data.dinnerTime)
                        .putString("bedTime", data.bedTime)
                        .putString("eatingDuration", data.eatingDuration)
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
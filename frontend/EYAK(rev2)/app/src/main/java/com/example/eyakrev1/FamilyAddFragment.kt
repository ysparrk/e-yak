package com.example.eyakrev1

import android.content.ComponentCallbacks
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.preference.PreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FamilyAddFragment : Fragment() {

    lateinit var mainActivity: MainActivity

    private val api = EyakService.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout: View = inflater.inflate(R.layout.fragment_family_add, container, false)

        layout.findViewById<Button>(R.id.nickNameChkBtn).setOnClickListener {   // 닉네임 확인 버튼 -> 추가하고자 하는 가족의 닉네임이 있는가?
            api.checkDuplicate(layout.findViewById<EditText>(R.id.nicknameInput).text.toString()).enqueue(object: Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if(response.code() == 200) {    // 잘 보내 졌습니다
                        if(response.body() == true) {  // 중복된 닉네임이 있음 -> 가족 추가 가능
                            val pref = PreferenceManager.getDefaultSharedPreferences(mainActivity)

                            if(layout.findViewById<EditText>(R.id.nicknameInput).text.toString() == pref.getString("KEY_NICKNAME", "")) {   // 내 낵네임을 입력한 경우
                                Toast.makeText(mainActivity, "자신을 추가할 수 없어요ㅠㅠ", Toast.LENGTH_SHORT).show()
                            }
                            else {  // 가족 신청 가능
                                Toast.makeText(mainActivity, "가족 확인 완료", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else {
                            Toast.makeText(mainActivity, "존재하지 않는 닉네임 입니다", Toast.LENGTH_SHORT).show()
                        }
                    }
//                    else {
//
//                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {

                }
            })

        }

        layout.findViewById<Button>(R.id.requestBtn).setOnClickListener {
            mainActivity!!.gotoEditFamily()
        }

        return layout
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }
}
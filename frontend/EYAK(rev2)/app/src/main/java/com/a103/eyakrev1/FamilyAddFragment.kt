package com.a103.eyakrev1

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.preference.PreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FamilyAddFragment : Fragment() {

    lateinit var mainActivity: MainActivity

    private val api = EyakService.create()

    var nickNameChk: Boolean = false
    var scope: Int = 0

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
                                nickNameChk = true
                            }
                        }
                        else {
                            Toast.makeText(mainActivity, "존재하지 않는 닉네임 입니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<Boolean>, t: Throwable) {

                }
            })
        }

        layout.findViewById<ImageView>(R.id.entireScopeAddSideChk).setOnClickListener {
            scope = 1
            layout.findViewById<ImageView>(R.id.entireScopeAddSideChk).setImageResource(R.drawable.baseline_check_box_24)
            layout.findViewById<ImageView>(R.id.calendarScopeAddSideChk).setImageResource(R.drawable.baseline_check_box_outline_blank_24)
        }

        layout.findViewById<ImageView>(R.id.calendarScopeAddSideChk).setOnClickListener {
            scope = 2
            layout.findViewById<ImageView>(R.id.entireScopeAddSideChk).setImageResource(R.drawable.baseline_check_box_outline_blank_24)
            layout.findViewById<ImageView>(R.id.calendarScopeAddSideChk).setImageResource(R.drawable.baseline_check_box_24)
        }

        layout.findViewById<Button>(R.id.requestBtn).setOnClickListener {
            if(!nickNameChk) {
                Toast.makeText(mainActivity, "가족 닉네임을 확인해 주세요", Toast.LENGTH_SHORT).show()
            }
            else if(scope == 0) {
                Toast.makeText(mainActivity, "공개 범위를 설정해 주세요", Toast.LENGTH_SHORT).show()   // 공개 범위 설정이 안되어 있음
            }
            else if(layout.findViewById<EditText>(R.id.myDefineNameAddSideInput).text.toString() == "") {
                Toast.makeText(mainActivity, "내가 지정할 이름을 입력해 주세요", Toast.LENGTH_SHORT).show()
            }
            else {
                val pref = PreferenceManager.getDefaultSharedPreferences(mainActivity)
                val serverUserId = pref.getInt("SERVER_USER_ID", -1)  // 팔로워 아이디
                val serverAccessToken = pref.getString("SERVER_ACCESS_TOKEN", "")   // 엑세스 토큰
                val data = FollowRequestBodyModel(
                    followerScope = if(scope == 1) "ALL" else if(scope == 2) "CALENDAR" else "",
                    followeeNickname = layout.findViewById<EditText>(R.id.nicknameInput).text.toString(),
                    customName = layout.findViewById<EditText>(R.id.myDefineNameAddSideInput).text.toString(),
                )

                api.followRequest(followerId = serverUserId, Authorization = "Bearer ${serverAccessToken}", params = data).enqueue(object: Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if(response.code() == 201) {    // 201 Created: 요청이 성공적으로 서버에 생성된 경우
                            Toast.makeText(mainActivity, "성공", Toast.LENGTH_SHORT).show()
                            mainActivity!!.gotoEditFamily()
                        }
                        else if(response.code() == 200) {   // 200 OK: A → B 에게 팔로우 요청을 보낸 상태에서 B ← A 에게 팔로우 요청을 보낸 경우 무시, A 와 B 사이가 이미 팔로우 상태인 경우에도 무시
                            Toast.makeText(mainActivity, "이미 가족이거나 같은 요청이 있어요", Toast.LENGTH_SHORT).show()
                        }
                        else if(response.code() == 401) {   // 401 Unauthorized: AccessToken이 유효하지 않은 경우
                            Toast.makeText(mainActivity, "AccessToken이 유효하지 않은 경우", Toast.LENGTH_SHORT).show()
                        }
                        else if(response.code() == 400) {   // 400 Bad Request: 해당하는 member가 존재하지 않는 경우, 자기자신에게 팔로우 요청을 보내는 경우
                            Toast.makeText(mainActivity, "해당하는 member가 존재하지 않는 경우", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {

                    }
                })
            }
        }

        return layout
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }
}
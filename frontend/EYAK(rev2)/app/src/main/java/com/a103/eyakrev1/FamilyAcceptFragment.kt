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
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.preference.PreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FamilyAcceptFragment : Fragment() {

    lateinit var mainActivity: MainActivity

    private val api = EyakService.create()

    var scope: Int = 0

    var followerId: Int = 0
    var followRequestId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener("followRequestData") { _, bundle -> // setFragmentResultListener("보낸 데이터 묶음 이름") {requestKey, bundle ->

            // 상대 닉네임
            view?.findViewById<TextView>(R.id.nicknameRequestSideInput)?.text = bundle.getString("followeeNickname", "")
            Log.d("ㅁㅁㅁㅁㅁㅁㅁ", "${bundle.getString("followeeNickname", "")}")

            // 상대 공개 범위
            view?.findViewById<TextView>(R.id.requestScope)?.text = if(bundle.getString("scope", "") == "ALL") "전체 공개" else "달력 공개"

            // 요청 번호
            followRequestId = bundle.getInt("followRequestId", 0)

            // 상대 아이디 -> Int
            followerId = bundle.getInt("followerId", 0)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_family_accept, container, false)

        layout.findViewById<ImageView>(R.id.entireScopeAcceptSideChk).setOnClickListener {
            scope = 1
            layout.findViewById<ImageView>(R.id.entireScopeAcceptSideChk).setImageResource(R.drawable.baseline_check_box_24)
            layout.findViewById<ImageView>(R.id.calendarScopeAcceptSideChk).setImageResource(R.drawable.baseline_check_box_outline_blank_24)
        }

        layout.findViewById<ImageView>(R.id.calendarScopeAcceptSideChk).setOnClickListener {
            scope = 2
            layout.findViewById<ImageView>(R.id.entireScopeAcceptSideChk).setImageResource(R.drawable.baseline_check_box_outline_blank_24)
            layout.findViewById<ImageView>(R.id.calendarScopeAcceptSideChk).setImageResource(R.drawable.baseline_check_box_24)
        }

        layout.findViewById<Button>(R.id.acceptBtn).setOnClickListener {
            if(scope == 0) {
                Toast.makeText(mainActivity, "공개 범위를 설정해 주세요", Toast.LENGTH_SHORT).show()   // 공개 범위 설정이 안되어 있음
            }
            else if(layout.findViewById<EditText>(R.id.myDefineNameAcceptSideInput).text.toString() == "") {
                Toast.makeText(mainActivity, "내가 지정할 이름을 입력해 주세요", Toast.LENGTH_SHORT).show()
            }
            else {
                val pref = PreferenceManager.getDefaultSharedPreferences(mainActivity)
                val serverAccessToken = pref.getString("SERVER_ACCESS_TOKEN", "")   // 엑세스 토큰
                val data= AcceptFollowRequestBodyModel(
                    followeeScope = if(scope == 1) "ALL" else if(scope == 2) "CALENDAR" else "",
                    customName = layout.findViewById<EditText>(R.id.myDefineNameAcceptSideInput).text.toString(),
                )

                api.acceptFollowRequest(followerId = followerId, followRequestId = followRequestId, Authorization = "Bearer ${serverAccessToken}", params = data).enqueue(object: Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if(response.code() == 201) {
                            Toast.makeText(mainActivity, "성공", Toast.LENGTH_SHORT).show()
                        }
                        else if(response.code() == 401) {
                            Toast.makeText(mainActivity, "AccessToken이 유효하지 않은 경우", Toast.LENGTH_SHORT).show()
                        }
                        else if(response.code() == 400) {
                            Toast.makeText(mainActivity, "해당하는 member나 followRequest가 존재하지 않는 경우", Toast.LENGTH_SHORT).show()
                        }

                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {

                    }
                })
            }
            mainActivity!!.gotoFamily()
        }

        return layout
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }
}
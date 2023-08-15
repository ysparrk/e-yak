package com.a103.eyakrev1

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.preference.PreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FamilyEditFragment : Fragment() {

    lateinit var mainActivity: MainActivity

    private var familyList = arrayListOf<Family>()
    private val api = EyakService.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_family_edit, container, false)

        familyList.add(Family())

        val pref = PreferenceManager.getDefaultSharedPreferences(mainActivity)
        val serverAccessToken = pref.getString("SERVER_ACCESS_TOKEN", "")   // 엑세스 토큰
        val memberId = pref.getInt("SERVER_USER_ID", -1)

        api.getAllFollowers(memberId = memberId, Authorization = "Bearer ${serverAccessToken}").enqueue(object:
            Callback<ArrayList<Family>> {
            override fun onResponse(call: Call<ArrayList<Family>>, response: Response<ArrayList<Family>>) {
                if(response.code() == 200) {
                    Log.d("로그", "사용자의 팔로워 전체 조회 200 OK")
                    familyList = response.body()!!

                    // 마지막 빈 공간을 위해서 더미 데이터 추가
                    familyList.add(Family())

                    val familyEditListAdapter = FamilyEditListAdapter(mainActivity, familyList)
                    val familyEditListView = layout.findViewById<ListView>(R.id.familyEditListView)
                    familyEditListView?.adapter = familyEditListAdapter
                }
                else if(response.code() == 401) {
                    Log.d("로그", "사용자의 팔로워 전체 조회 401 Unauthorized: AccessToken이 유효하지 않은 경우")
                }
                else if(response.code() == 400) {
                    Log.d("로그", "사용자의 팔로워 전체 조회 400 Bad Request: 해당하는 이 member가 존재하지 않는 경우")
                }
            }
            override fun onFailure(call: Call<ArrayList<Family>>, t: Throwable) {
                Log.d("로그", "사용자의 팔로워 전체 조회 onFailure")
            }
        })

        return layout
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }

}
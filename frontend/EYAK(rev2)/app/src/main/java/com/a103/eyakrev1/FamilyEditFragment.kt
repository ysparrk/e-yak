package com.a103.eyakrev1

import android.content.Context
import android.os.Bundle
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

    var familyList = arrayListOf<Family>()
    val api = EyakService.create()

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
                    familyList = response.body()!!

                    // 마지막 빈 공간을 위해서 더미 데이터 추가
                    familyList.add(Family())

                    val familyEditListAdapter = FamilyEditListAdapter(mainActivity, familyList)
                    val familyEditListView = layout.findViewById<ListView>(R.id.familyEditListView)
                    familyEditListView?.adapter = familyEditListAdapter
                }
                else if(response.code() == 401) {
                    Toast.makeText(mainActivity, "AccessToken이 유효하지 않은 경우", Toast.LENGTH_SHORT).show()
                }
                else if(response.code() == 400) {
                    Toast.makeText(mainActivity, "해당하는 member가 존재하지 않는 경우", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ArrayList<Family>>, t: Throwable) {

            }
        })

        return layout
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }

}
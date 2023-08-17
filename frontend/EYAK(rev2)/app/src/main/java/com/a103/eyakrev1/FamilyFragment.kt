package com.a103.eyakrev1

import android.content.Context
import android.graphics.Color
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.preference.PreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FamilyFragment : Fragment() {

    lateinit var mainActivity: MainActivity

    private val api = EyakService.create()

    private var followRequestCnt: Int? = 0
    private val lightOn: Int = Color.parseColor("#FFF6FA70")
    private val lightOff: Int = Color.parseColor("#FF9BABB8")

    var familyList = arrayListOf<Family>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.family_tab_main, container, false)

        // 기본 정보 초기화

        layout.findViewById<ImageView>(R.id.acceptFamilyBtn).visibility = View.INVISIBLE
        layout.findViewById<TextView>(R.id.acceptFamilyCnt).visibility = View.INVISIBLE

        // 기본 정보 초기화 끝

        // 나에게 팔로워 요청한 사람 전체 정보 -> isGetFollowers: true
        val pref = PreferenceManager.getDefaultSharedPreferences(mainActivity)

        val serverAccessToken = pref.getString("SERVER_ACCESS_TOKEN", "")   // 엑세스 토큰
        val memberId = pref.getInt("SERVER_USER_ID", -1)

        api.getAllFollowers(memberId = memberId, Authorization = "Bearer ${serverAccessToken}").enqueue(object: Callback<ArrayList<Family>> {
            override fun onResponse(call: Call<ArrayList<Family>>, response: Response<ArrayList<Family>>) {
                if(response.code() == 200) {
                    familyList = response.body()!!

                    // 마지막 빈 공간을 위해서 더미 데이터 추가
                    familyList.add(Family())

                    if (familyList.size == 1) {
                        layout.findViewById<LinearLayout>(R.id.emptyFamilyLinearLayout).visibility = View.VISIBLE
                    }

                    val familyListAdapter = FamilyListAdapter(mainActivity, familyList)
                    val familyListView = layout.findViewById<ListView>(R.id.familyListView)
                    familyListView?.adapter = familyListAdapter
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

        api.followRequests(Authorization = "Bearer ${serverAccessToken}", isGetFollowers = true).enqueue(object: Callback<MutableList<FollowRequestsDataModel>> {
            override fun onResponse(call: Call<MutableList<FollowRequestsDataModel>>, response: Response<MutableList<FollowRequestsDataModel>>) {

                val followRequestsList: MutableList<FollowRequestsDataModel>? = response.body()
               followRequestCnt = followRequestsList?.size

                if(response.code() == 200) {
                    if (followRequestCnt == 0) {
                        layout.findViewById<ImageView>(R.id.acceptFamilyBtn).setColorFilter(lightOff)
                        layout.findViewById<ImageView>(R.id.acceptFamilyBtn).visibility = View.VISIBLE
                        layout.findViewById<TextView>(R.id.acceptFamilyCnt).visibility = View.INVISIBLE
                    } else {
                        layout.findViewById<ImageView>(R.id.acceptFamilyBtn).setColorFilter(lightOn)
                        layout.findViewById<ImageView>(R.id.acceptFamilyBtn).visibility = View.VISIBLE
                        layout.findViewById<TextView>(R.id.acceptFamilyCnt).visibility = View.VISIBLE
                        layout.findViewById<TextView>(R.id.acceptFamilyCnt).text = followRequestCnt.toString()
                    }

                    layout.findViewById<ImageView>(R.id.acceptFamilyBtn).setOnClickListener {

                        if (followRequestCnt != 0) {
                            if (followRequestsList?.isNotEmpty() == true) {

                                val requestBundle = Bundle()
                                requestBundle.putInt("followRequestId", followRequestsList[0].followRequestId)
                                requestBundle.putInt("followerId", followRequestsList[0].followerId)
                                requestBundle.putString("followerNickname", followRequestsList[0].followerNickname)
                                requestBundle.putString("followeeNickname", followRequestsList[0].followeeNickname)
                                requestBundle.putString("customName", followRequestsList[0].customName)
                                requestBundle.putString("scope", followRequestsList[0].scope)

                                setFragmentResult("followRequestData", requestBundle)

                                mainActivity!!.gotoAcceptFamily()
                            }

                        }
                    }
                }
                else if(response.code() == 401) {
                    Toast.makeText(mainActivity, "AccessToken이 유효하지 않은 경우", Toast.LENGTH_SHORT).show()
                }
                else if(response.code() == 400) {
                    Toast.makeText(mainActivity, "해당하는 member가 존재하지 않는 경우", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<MutableList<FollowRequestsDataModel>>, t: Throwable) {

            }
        })
        //

        layout.findViewById<ImageView>(R.id.acceptFamilyBtn).setOnClickListener {

            mainActivity!!.gotoAcceptFamily()
        }

        layout.findViewById<ImageView>(R.id.editFamily).setOnClickListener {
            mainActivity!!.gotoEditFamily()
        }

        return layout
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // 2. Context를 액티비티로 형변환해서 할당
        mainActivity = context as MainActivity
    }
}
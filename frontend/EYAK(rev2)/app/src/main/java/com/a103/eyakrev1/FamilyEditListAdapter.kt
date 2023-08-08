package com.a103.eyakrev1

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.preference.PreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FamilyEditListAdapter(val context: Context, val familyList: ArrayList<Family>): BaseAdapter() {

    var mainActivity: MainActivity = context as MainActivity
    val api = EyakService.create()

    override fun getCount(): Int {
        return familyList.size
    }

    override fun getItem(position: Int): Any {
        return familyList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        if (position + 1 == getCount()) {

            val last_view = LayoutInflater.from(context).inflate(R.layout.family_edit_list_last_item, null)
            last_view.setBackgroundColor(Color.parseColor("#F8FCF8"))

            last_view.findViewById<ImageView>(R.id.familyAddBtn).setOnClickListener {
                mainActivity!!.gotoAddFamily()
            }

            return last_view
        } else {
            /* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
            val view: View = LayoutInflater.from(context).inflate(R.layout.family_edit_list_view_item, null)

            /* 위에서 생성된 view를 family_tab_list_view_item.xml 파일의 각 View와 연결하는 과정이다. */
            val familyListNameTextView = view.findViewById<TextView>(R.id.family_edit_list_name)
            val familyListNicknameTextView = view.findViewById<TextView>(R.id.family_edit_list_nickname)
            val familyChangeButton = view.findViewById<Button>(R.id.family_edit_change_button)
            val familyDeleteButton = view.findViewById<Button>(R.id.family_edit_delete_button)

            /* ArrayList<MedicineAlarm>의 변수 family의 이미지와 데이터를 ImageView와 TextView에 담는다. */
            val family = familyList[position]

            familyListNameTextView.text = family.custom_name
            familyListNicknameTextView.text = family.nickname

            familyDeleteButton.setOnClickListener {
                val pref = PreferenceManager.getDefaultSharedPreferences(mainActivity)
                val serverAccessToken = pref.getString("SERVER_ACCESS_TOKEN", "")   // 엑세스 토큰
                val memberId = pref.getInt("SERVER_USER_ID", -1)

                api.deleteFollower(memberId=memberId, followId=family.followId, Authorization="Bearer ${serverAccessToken}").enqueue(object:
                    Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Log.d("log", response.toString())
                        Log.d("log", response.body().toString())

                        if (response.code() == 401) {
                            Log.d("log", "인증되지 않은 사용자입니다")
                        } else if (response.code() == 400) {
                            Log.d("log", "해당하는 member나 followRequest가 존재하지 않습니다")
                        }
                        else if (response.code() == 200) {
                            Toast.makeText(mainActivity, "삭제되었습니다", Toast.LENGTH_SHORT).show()
                            mainActivity!!.gotoFamily()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {

                    }
                })
            }

            return view
        }




    }

    /**
     * Extension method to set View's height.
     */
    fun View.setHeight(value: Int) {
        val lp = layoutParams
        lp?.let {
            lp.height = value
            layoutParams = lp
        }
    }

    /**
     * Extension method to set View's width.
     */
    fun View.setWidth(value: Int) {
        val lp = layoutParams
        lp?.let {
            lp.width = value
            layoutParams = lp
        }
    }
}
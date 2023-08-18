package com.a103.eyakrev1

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.setFragmentResult

class FamilyListAdapter (val context: Context, val familyList: ArrayList<Family>): BaseAdapter() {

    var mainActivity: MainActivity = context as MainActivity
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
        /* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
        val view: View = LayoutInflater.from(context).inflate(R.layout.family_tab_list_view_item, null)

        /* 위에서 생성된 view를 family_tab_list_view_item.xml 파일의 각 View와 연결하는 과정이다. */
//        val familyListIconImageView = view.findViewById<ImageView>(R.id.family_list_icon)
        val familyListNameTextView = view.findViewById<TextView>(R.id.family_list_name)
        val familyListNicknameTextView = view.findViewById<TextView>(R.id.family_list_nickname)
        val familyDetailButton = view.findViewById<Button>(R.id.family_detail_button)

        /* ArrayList<MedicineAlarm>의 변수 family의 이미지와 데이터를 ImageView와 TextView에 담는다. */
        val family = familyList[position]

        val familyCardView = view.findViewById<CardView>(R.id.familyCardView)
        if (position + 1 == getCount()) {
            familyCardView.setHeight(80)
            familyCardView.visibility = View.INVISIBLE
            view.setBackgroundColor(Color.parseColor("#F8FCF8"))
        } else {
            familyCardView.setOnClickListener {
                val scaleDownAnim = AnimationUtils.loadAnimation(mainActivity, R.anim.scale_down)

                scaleDownAnim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        // 애니메이션이 완료된 후에 페이지 이동 및 데이터 설정 수행
                        Thread.sleep(50)

                        val bundle = Bundle()

                        bundle.putInt("requeteeId", family.memberId)
                        bundle.putString("customName", family.custom_name)

                        (context as FragmentActivity).supportFragmentManager.setFragmentResult("familyMonthlyDose", bundle)

                        mainActivity!!.gotoMyCalendar()

                        // TODO: 상세 페이지로 이동하는 코드 추가
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                })

                familyCardView.startAnimation(scaleDownAnim)
            }
        }

        familyListNameTextView.text = family.custom_name
        familyListNicknameTextView.text = family.nickname

        return view
    }

    fun View.setHeight(value: Int) {
        val lp = layoutParams
        lp?.let {
            lp.height = value
            layoutParams = lp
        }
    }

    fun View.setWidth(value: Int) {
        val lp = layoutParams
        lp?.let {
            lp.width = value
            layoutParams = lp
        }
    }

}
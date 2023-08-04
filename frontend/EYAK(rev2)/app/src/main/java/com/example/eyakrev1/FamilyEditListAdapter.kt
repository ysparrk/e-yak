package com.example.eyakrev1

import android.content.Context
import android.graphics.Color
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView

class FamilyEditListAdapter(val context: Context, val familyList: ArrayList<Family>): BaseAdapter() {

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

            familyListNameTextView.text = family.familyName
            familyListNicknameTextView.text = family.familyNickname

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
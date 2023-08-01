package com.example.eyakrev1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class FamilyListAdapter (val context: Context, val familyList: ArrayList<Family>): BaseAdapter() {
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
        val familyListIconImageView = view.findViewById<ImageView>(R.id.family_list_icon)
        val familyListNameTextView = view.findViewById<TextView>(R.id.family_list_name)
        val familyListNicknameTextView = view.findViewById<TextView>(R.id.family_list_nickname)
        val familyDetailButton = view.findViewById<Button>(R.id.family_detail_button)

        /* ArrayList<MedicineAlarm>의 변수 family의 이미지와 데이터를 ImageView와 TextView에 담는다. */
        val family = familyList[position]

        if(family.familyId == -1) {

        }

        val resourceId = context.resources.getIdentifier(family.familyIcon, "drawable", context.packageName)
        familyListIconImageView.setImageResource(resourceId)
        familyListNameTextView.text = family.familyName
        familyListNicknameTextView.text = family.familyNickname

        return view
    }

}
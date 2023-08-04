package com.example.eyakrev1

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.setFragmentResult
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager

class MedicineListAdapter (val context: Context, val medicineList: ArrayList<Medicine>): BaseAdapter() {

    var mainActivity: MainActivity = context as MainActivity

    override fun getCount(): Int {
        return medicineList.size
    }

    override fun getItem(position: Int): Any {
        return medicineList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        /* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
        val view: View = LayoutInflater.from(context).inflate(R.layout.medicine_tab_list_view_item, null)

        /* 위에서 생성된 view를 medicine_tab_list_view_item.xml 파일의 각 View와 연결하는 과정이다. */
        val medicineListIconImageView = view.findViewById<ImageView>(R.id.medicine_list_icon)
        val medicineListNameTextView = view.findViewById<TextView>(R.id.medicine_list_name)
        val medicineDetailButton = view.findViewById<Button>(R.id.medicine_detail_button)

        /* ArrayList<MedicineAlarm>의 변수 medicineAlarm의 이미지와 데이터를 ImageView와 TextView에 담는다. */
        val medicine = medicineList[position]

        val resourceId = context.resources.getIdentifier(medicine.medicineIcon, "drawable", context.packageName)
        medicineListIconImageView.setImageResource(resourceId)
        medicineListNameTextView.text = medicine.medicineName

        val medicineCardView = view.findViewById<CardView>(R.id.medicineCardView)
        if (position + 1 == getCount()) {
            medicineCardView.setHeight(80)
            medicineCardView.visibility = View.INVISIBLE
            view.setBackgroundColor(Color.parseColor("#F8FCF8"))
        }

        // medicine detail 버튼을 누르면 해당 상세 페이지로 이동하도록
        // fragment 간에 데이터 전달하기

        medicineDetailButton.setOnClickListener {
            mainActivity!!.gotoMedicineDetail()
        }

        return view
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
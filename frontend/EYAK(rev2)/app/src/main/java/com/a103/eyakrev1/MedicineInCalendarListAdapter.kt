package com.a103.eyakrev1

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.setFragmentResult

class MedicineInCalendarListAdapter (val context: Context, val medicineList: ArrayList<MedicineDetailInCalendar>): BaseAdapter() {
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
        val view: View = LayoutInflater.from(context).inflate(R.layout.medicine_in_calendar_list_view_item, null)

        val medicine = medicineList[position]

        var medicineTitle = medicine.customName
        when (medicine.routine) {
            "BED_AFTER" -> medicineTitle += " (기상 후)"
            "BREAKFAST_BEFORE" -> medicineTitle += " (아침 식사 전)"
            "BREAKFAST_AFTER" -> medicineTitle += " (아침 식사 후)"
            "LUNCH_BEFORE" -> medicineTitle += " (점심 식사 전)"
            "LUNCH_AFTER" -> medicineTitle += " (점심 식사 후)"
            "DINNER_BEFORE" -> medicineTitle += " (저녁 식사 전)"
            "DINNER_AFTER" -> medicineTitle += " (저녁 식사 후)"
            "BED_BEFORE" -> medicineTitle += " (취침 전)"
        }
        view.findViewById<TextView>(R.id.medicine_list_name).text = medicineTitle

        val checkImageView = view.findViewById<ImageView>(R.id.medicine_in_calendar_check_image)

        if (medicine.took) {
            checkImageView.setImageResource(R.drawable.check)
        } else {
            checkImageView.setImageResource(R.drawable.cancel)
        }

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
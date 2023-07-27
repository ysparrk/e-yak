package com.example.eyakrev1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class MedicineListAdapter (val context: Context, val medicineList: ArrayList<Medicine>): BaseAdapter() {
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
        val medicineEatButton = view.findViewById<Button>(R.id.medicine_detail_button)

        /* ArrayList<MedicineAlarm>의 변수 medicineAlarm의 이미지와 데이터를 ImageView와 TextView에 담는다. */
        val medicine = medicineList[position]

        val resourceId = context.resources.getIdentifier(medicine.medicineIcon, "drawable", context.packageName)
        medicineListIconImageView.setImageResource(resourceId)
        medicineListNameTextView.text = medicine.medicineName

        return view
    }

}
package com.a103.eyakrev1

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
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class MedicineListAdapter (val context: Context, val medicineList: ArrayList<Medicine>?, private val viewModel: MedicineClickedViewModel): BaseAdapter() {

    var mainActivity: MainActivity = context as MainActivity

    override fun getCount(): Int {
        return medicineList!!.size
    }

    override fun getItem(position: Int): Any {
        return medicineList!![position]
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
        val medicine = medicineList!![position]
//        Log.d("log", "$medicine")

        when(medicine.medicineShape) {
            1 -> medicineListIconImageView.setImageResource(R.drawable.ic_pill_glacier)
            2 -> medicineListIconImageView.setImageResource(R.drawable.ic_pill_afterglow)
            3 -> medicineListIconImageView.setImageResource(R.drawable.ic_pill_bougainvaillea)
            4 -> medicineListIconImageView.setImageResource(R.drawable.ic_pill_orchidice)
            5 -> medicineListIconImageView.setImageResource(R.drawable.ic_pill_silver)
            6 -> medicineListIconImageView.setImageResource(R.drawable.ic_pill_pinklady)
            7 -> medicineListIconImageView.setImageResource(R.drawable.ic_pill_fusioncoral)
            8 -> medicineListIconImageView.setImageResource(R.drawable.ic_roundpill_glacier)
            9 -> medicineListIconImageView.setImageResource(R.drawable.ic_roundpill_afterglow)
            10 -> medicineListIconImageView.setImageResource(R.drawable.ic_roundpill_bougainvillea)
            11 -> medicineListIconImageView.setImageResource(R.drawable.ic_roundpill_orchidice)
            12 -> medicineListIconImageView.setImageResource(R.drawable.ic_roundpill_silver)
            13 -> medicineListIconImageView.setImageResource(R.drawable.ic_roundpill_pinklady)
            14 -> medicineListIconImageView.setImageResource(R.drawable.ic_roundpill_fusioncoral)
            15 -> medicineListIconImageView.setImageResource(R.drawable.ic_packagepill_glacier)
            16 -> medicineListIconImageView.setImageResource(R.drawable.ic_packagepill_afterglow)
            17 -> medicineListIconImageView.setImageResource(R.drawable.ic_packagepill_bougainvillea)
            18 -> medicineListIconImageView.setImageResource(R.drawable.ic_packagepill_orchidice)
            19 -> medicineListIconImageView.setImageResource(R.drawable.ic_packagepill_silver)
            20 -> medicineListIconImageView.setImageResource(R.drawable.ic_packagepill_pinklady)
            21 -> medicineListIconImageView.setImageResource(R.drawable.ic_packagepill_fusioncoral)
            22 -> medicineListIconImageView.setImageResource(R.drawable.ic_outpill_glacier)
            23 -> medicineListIconImageView.setImageResource(R.drawable.ic_outpill_afterglow)
            24 -> medicineListIconImageView.setImageResource(R.drawable.ic_outpill_bougainvillea)
            25 -> medicineListIconImageView.setImageResource(R.drawable.ic_outpill_orchidice)
            26 -> medicineListIconImageView.setImageResource(R.drawable.ic_outpill_silver)
            27 -> medicineListIconImageView.setImageResource(R.drawable.ic_outpill_pinklady)
            28 -> medicineListIconImageView.setImageResource(R.drawable.ic_outpill_fusioncoral)
            29 -> medicineListIconImageView.setImageResource(R.drawable.ic_potion_glacier)
            30 -> medicineListIconImageView.setImageResource(R.drawable.ic_potion_afterglow)
            31 -> medicineListIconImageView.setImageResource(R.drawable.ic_potion_bougainvillea)
            32 -> medicineListIconImageView.setImageResource(R.drawable.ic_potion_orchidice)
            33 -> medicineListIconImageView.setImageResource(R.drawable.ic_potion_silver)
            34 -> medicineListIconImageView.setImageResource(R.drawable.ic_potion_pinklady)
            35 -> medicineListIconImageView.setImageResource(R.drawable.ic_potion_fusioncoral)
        }

        medicineListNameTextView.text = medicine.customName

        val medicineCardView = view.findViewById<CardView>(R.id.medicineCardView)
        if (position + 1 == getCount()) {
            medicineCardView.setHeight(80)
            medicineCardView.visibility = View.INVISIBLE
            view.setBackgroundColor(Color.parseColor("#F8FCF8"))
        }

        // medicine detail 버튼을 누르면 해당 상세 페이지로 이동하도록
        // fragment 간에 데이터 전달하기

        medicineDetailButton.setOnClickListener {
            viewModel.setSelectedMedicineId(medicine.id)
        }

        return view
    }

    class MedicineClickedViewModel : ViewModel() {
        private val _selectedMedicineId = MutableLiveData<Int>()
        val selectedMedicineId: LiveData<Int> = _selectedMedicineId

        fun setSelectedMedicineId(id: Int) {
            _selectedMedicineId.value = id
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
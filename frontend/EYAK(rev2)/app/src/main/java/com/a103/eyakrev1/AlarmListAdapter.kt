package com.a103.eyakrev1

import android.content.Context
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class AlarmListAdapter (val context: Context, val medicineRoutineList: ArrayList<MedicineRoutine>) : BaseAdapter() {

    var mainActivity: MainActivity = context as MainActivity

    // https://blog.yena.io/studynote/2017/12/01/Android-Kotlin-ListView.html
    override fun getCount(): Int {
        return medicineRoutineList.size
    }

    override fun getItem(position: Int): Any {
        return medicineRoutineList[position]
    }

    override fun getItemId(position: Int): Long {
        // 나중에 약의 고유 id를 받던가 그렇게 하자 => DB 보고 추후에 결정
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        /* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
        val view: View = LayoutInflater.from(context).inflate(R.layout.alarm_tab_list_view_item, null)

        /* 위에서 생성된 view를 alarm_tab_list_view_item.xml 파일의 각 View와 연결하는 과정이다. */
        val medicineTimeTextView = view.findViewById<TextView>(R.id.medicine_time)
        val medicineNameTextView = view.findViewById<TextView>(R.id.routineCategoryName)
        val medicineTimeLeftTextView = view.findViewById<TextView>(R.id.medicine_time_left)
        val medicineEatButton = view.findViewById<Button>(R.id.medicine_eat_button)
        val alarmTabDetailButton = view.findViewById<ImageButton>(R.id.alarmTabDetailButton)

        /* ArrayList<MedicineAlarm>의 변수 medicineAlarm의 이미지와 데이터를 ImageView와 TextView에 담는다. */
        val medicineRoutine = medicineRoutineList[position]

        medicineTimeTextView.text = medicineRoutine.routineTime
        medicineNameTextView.text = medicineRoutine.routineName

        // https://stackoverflow.com/questions/3135112/android-nested-listview
        val prescriptionDetailLayout = view.findViewById<LinearLayout>(R.id.prescriptionDetailLayout)

        alarmTabDetailButton.setOnClickListener {
            // 처음에 한번만 불러오고 싶었기 때문에, 저기에 자식들이 없다면 한번만 실행, 처음 버튼 누를때만 뷰를 추가하도록
            if (prescriptionDetailLayout.childCount == 0) {
                for (medicine in medicineRoutine.medicineList) {
                    var detailMedicineView = LayoutInflater.from(context).inflate(R.layout.alarm_tab_routine_detail_item, null)
                    detailMedicineView.findViewById<TextView>(R.id.routine_detail_medicine_name).text = medicine.customName
                    val detailMedicineImageView = detailMedicineView.findViewById<ImageView>(R.id.routine_detail_medicine_icon)

                    when(medicine.medicineShape) {
                        1 -> detailMedicineImageView.setImageResource(R.drawable.ic_pill_glacier)
                        2 -> detailMedicineImageView.setImageResource(R.drawable.ic_pill_afterglow)
                        3 -> detailMedicineImageView.setImageResource(R.drawable.ic_pill_bougainvaillea)
                        4 -> detailMedicineImageView.setImageResource(R.drawable.ic_pill_orchidice)
                        5 -> detailMedicineImageView.setImageResource(R.drawable.ic_pill_silver)
                        6 -> detailMedicineImageView.setImageResource(R.drawable.ic_pill_pinklady)
                        7 -> detailMedicineImageView.setImageResource(R.drawable.ic_pill_fusioncoral)
                        8 -> detailMedicineImageView.setImageResource(R.drawable.ic_roundpill_glacier)
                        9 -> detailMedicineImageView.setImageResource(R.drawable.ic_roundpill_afterglow)
                        10 -> detailMedicineImageView.setImageResource(R.drawable.ic_roundpill_bougainvillea)
                        11 -> detailMedicineImageView.setImageResource(R.drawable.ic_roundpill_orchidice)
                        12 -> detailMedicineImageView.setImageResource(R.drawable.ic_roundpill_silver)
                        13 -> detailMedicineImageView.setImageResource(R.drawable.ic_roundpill_pinklady)
                        14 -> detailMedicineImageView.setImageResource(R.drawable.ic_roundpill_fusioncoral)
                        15 -> detailMedicineImageView.setImageResource(R.drawable.ic_packagepill_glacier)
                        16 -> detailMedicineImageView.setImageResource(R.drawable.ic_packagepill_afterglow)
                        17 -> detailMedicineImageView.setImageResource(R.drawable.ic_packagepill_bougainvillea)
                        18 -> detailMedicineImageView.setImageResource(R.drawable.ic_packagepill_orchidice)
                        19 -> detailMedicineImageView.setImageResource(R.drawable.ic_packagepill_silver)
                        20 -> detailMedicineImageView.setImageResource(R.drawable.ic_packagepill_pinklady)
                        21 -> detailMedicineImageView.setImageResource(R.drawable.ic_packagepill_fusioncoral)
                        22 -> detailMedicineImageView.setImageResource(R.drawable.ic_outpill_glacier)
                        23 -> detailMedicineImageView.setImageResource(R.drawable.ic_outpill_afterglow)
                        24 -> detailMedicineImageView.setImageResource(R.drawable.ic_outpill_bougainvillea)
                        25 -> detailMedicineImageView.setImageResource(R.drawable.ic_outpill_orchidice)
                        26 -> detailMedicineImageView.setImageResource(R.drawable.ic_outpill_silver)
                        27 -> detailMedicineImageView.setImageResource(R.drawable.ic_outpill_pinklady)
                        28 -> detailMedicineImageView.setImageResource(R.drawable.ic_outpill_fusioncoral)
                        29 -> detailMedicineImageView.setImageResource(R.drawable.ic_potion_glacier)
                        30 -> detailMedicineImageView.setImageResource(R.drawable.ic_potion_afterglow)
                        31 -> detailMedicineImageView.setImageResource(R.drawable.ic_potion_bougainvillea)
                        32 -> detailMedicineImageView.setImageResource(R.drawable.ic_potion_orchidice)
                        33 -> detailMedicineImageView.setImageResource(R.drawable.ic_potion_silver)
                        34 -> detailMedicineImageView.setImageResource(R.drawable.ic_potion_pinklady)
                        35 -> detailMedicineImageView.setImageResource(R.drawable.ic_potion_fusioncoral)
                    }
                    detailMedicineView.setHeight(30)
                    prescriptionDetailLayout.addView(detailMedicineView)
                }
            }

            if (prescriptionDetailLayout.visibility == View.GONE) {
                prescriptionDetailLayout.visibility = View.VISIBLE
                // 편 후에는, 접기 버튼으로 바꾸자
                alarmTabDetailButton.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
            } else {
                prescriptionDetailLayout.visibility = View.GONE
                // 접은 후에는, 펴기 버튼으로 바꾸자
                alarmTabDetailButton.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
            }
        }


        // 임시 설정 => 나중에 타이머 설정해야 함
//        medicineTimeLeftTextView.text = "00:31:58"

        return view
    }


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
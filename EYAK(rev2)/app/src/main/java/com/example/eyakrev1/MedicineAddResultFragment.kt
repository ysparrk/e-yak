package com.example.eyakrev1

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.setFragmentResultListener
import java.time.LocalDate

class MedicineAddResultFragment : Fragment() {

    lateinit var mainActivity: MainActivity

    private var selectIcon: Int = 5

    private var timeChk: BooleanArray = booleanArrayOf(false, false, false, false, false, false, false, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener("medicineAddData") { requestKey, bundle -> // setFragmentResultListener("보낸 데이터 묶음 이름") {requestKey, bundel ->

            // 투여 약 이름
            view?.findViewById<TextView>(R.id.medicineNameInputResult)?.text = bundle.getString("medicineName", "")

            // 질환 명
            view?.findViewById<TextView>(R.id.diseaseNameInputResult)?.text = bundle.getString("diseaseName", "")

            // 투여 기간
            view?.findViewById<TextView>(R.id.startYearInputResult)?.text = bundle.getInt("startYear", 0).toString()
            view?.findViewById<TextView>(R.id.startMonthInputResult)?.text = bundle.getInt("startMonth", 0).toString()
            view?.findViewById<TextView>(R.id.startDayInputResult)?.text = bundle.getInt("startDay", 0).toString()
            view?.findViewById<TextView>(R.id.endYearInputResult)?.text = bundle.getInt("endYear", 0).toString()
            view?.findViewById<TextView>(R.id.endMonthInputResult)?.text = bundle.getInt("endMonth", 0).toString()
            view?.findViewById<TextView>(R.id.endDayInputResult)?.text = bundle.getInt("endDay", 0).toString()

            // 투여 약 이모티콘
            selectIcon = bundle.getInt("medicineIcon")
            when(selectIcon) {
                1 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_pill_glacier)
                2 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_pill_afterglow)
                3 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_pill_bougainvaillea)
                4 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_pill_orchidice)
                5 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_pill_silver)
                6 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_pill_pinklady)
                7 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_pill_fusioncoral)
                8 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_roundpill_glacier)
                9 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_roundpill_afterglow)
                10 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_roundpill_bougainvillea)
                11 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_roundpill_orchidice)
                12 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_roundpill_silver)
                13 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_roundpill_pinklady)
                14 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_roundpill_fusioncoral)
                15 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_packagepill_glacier)
                16 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_packagepill_afterglow)
                17 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_packagepill_bougainvillea)
                18 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_packagepill_orchidice)
                19 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_packagepill_silver)
                20 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_packagepill_pinklady)
                21 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_packagepill_fusioncoral)
                22 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_outpill_glacier)
                23 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_outpill_afterglow)
                24 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_outpill_bougainvillea)
                25 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_outpill_orchidice)
                26 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_outpill_silver)
                27 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_outpill_pinklady)
                28 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_outpill_fusioncoral)
                29 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_potion_glacier)
                30 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_potion_afterglow)
                31 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_potion_bougainvillea)
                32 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_potion_orchidice)
                33 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_potion_silver)
                34 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_potion_pinklady)
                35 -> view?.findViewById<ImageView>(R.id.medicineIconResult)?.setImageResource(R.drawable.ic_potion_fusioncoral)

            }

            // 시간 설정
            timeChk = bundle.getBooleanArray("timeArray") ?: booleanArrayOf(false, false, false, false, false, false, false, false)
                // 시간 설정 화면 표시
            if(timeChk[0]) view?.findViewById<ImageView>(R.id.wakeChkResult)?.setImageResource(R.drawable.baseline_check_box_24) else view?.findViewById<ImageView>(R.id.wakeChkResult)?.setImageResource(R.drawable.baseline_check_box_outline_blank_24)
            if(timeChk[1]) view?.findViewById<ImageView>(R.id.breakfastBeforeChkResult)?.setImageResource(R.drawable.baseline_check_box_24) else view?.findViewById<ImageView>(R.id.breakfastBeforeChkResult)?.setImageResource(R.drawable.baseline_check_box_outline_blank_24)
            if(timeChk[2]) view?.findViewById<ImageView>(R.id.breakfastAfterChkResult)?.setImageResource(R.drawable.baseline_check_box_24) else view?.findViewById<ImageView>(R.id.breakfastAfterChkResult)?.setImageResource(R.drawable.baseline_check_box_outline_blank_24)
            if(timeChk[3]) view?.findViewById<ImageView>(R.id.lunchBeforeChkResult)?.setImageResource(R.drawable.baseline_check_box_24) else view?.findViewById<ImageView>(R.id.lunchBeforeChkResult)?.setImageResource(R.drawable.baseline_check_box_outline_blank_24)
            if(timeChk[4]) view?.findViewById<ImageView>(R.id.lunchAfterChkResult)?.setImageResource(R.drawable.baseline_check_box_24) else view?.findViewById<ImageView>(R.id.lunchAfterChkResult)?.setImageResource(R.drawable.baseline_check_box_outline_blank_24)
            if(timeChk[5]) view?.findViewById<ImageView>(R.id.dinnerBeforeChkResult)?.setImageResource(R.drawable.baseline_check_box_24) else view?.findViewById<ImageView>(R.id.dinnerBeforeChkResult)?.setImageResource(R.drawable.baseline_check_box_outline_blank_24)
            if(timeChk[6]) view?.findViewById<ImageView>(R.id.dinnerAfterChkResult)?.setImageResource(R.drawable.baseline_check_box_24) else view?.findViewById<ImageView>(R.id.dinnerAfterChkResult)?.setImageResource(R.drawable.baseline_check_box_outline_blank_24)
            if(timeChk[7]) view?.findViewById<ImageView>(R.id.bedChkResult)?.setImageResource(R.drawable.baseline_check_box_24) else view?.findViewById<ImageView>(R.id.bedChkResult)?.setImageResource(R.drawable.baseline_check_box_outline_blank_24)

            // 1회 투여양
            view?.findViewById<TextView>(R.id.numberOfOneTimeInputResult)?.text = bundle.getFloat("numberOfOneTime", 0f).toString()
            view?.findViewById<TextView>(R.id.unitTypeInputResult)?.text = bundle.getString("unitType", "-") ?: "-"

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layout = inflater.inflate(R.layout.fragment_medicine_add_result, container, false)

        layout.findViewById<Button>(R.id.chkcomplete).setOnClickListener {
            mainActivity!!.gotoMedicine()
        }

        return layout
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }

}
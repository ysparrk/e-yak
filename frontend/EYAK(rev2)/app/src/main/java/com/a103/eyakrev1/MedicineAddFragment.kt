package com.a103.eyakrev1

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import org.w3c.dom.Text
import java.time.LocalDate

class MedicineAddFragment : Fragment() {

    lateinit var mainActivity: MainActivity

    private val targetDay: LocalDate = LocalDate.now()

    private val activeColor: String = "#FFC9DBB2"
    private val nonColor: String = "#00000000"

    private var selectIcon: Int = 1

    private var timeChk: BooleanArray = booleanArrayOf(false, false, false, false, false, false, false, false)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layout = inflater.inflate(R.layout.fragment_medicine_add, container, false)

        // 기본 정보 초기화 start
            // 날짜 정보 초기화
        layout.findViewById<EditText>(R.id.startYearInput).hint = targetDay.year.toString().substring(2, 4)
        layout.findViewById<EditText>(R.id.startMonthInput).hint = targetDay.monthValue.toString()
        layout.findViewById<EditText>(R.id.startDayInput).hint = targetDay.dayOfMonth.toString()
        layout.findViewById<EditText>(R.id.endYearInput).hint = targetDay.year.toString().substring(2, 4)
        layout.findViewById<EditText>(R.id.endMonthInput).hint = targetDay.monthValue.toString()
        layout.findViewById<EditText>(R.id.endDayInput).hint = targetDay.dayOfMonth.toString()

            // 초기 아이콘은 1번 아이콘
        val iconId: String = "medicineIcon${selectIcon}"
        val resources = context?.resources
        val iconResourceId = resources?.getIdentifier(iconId, "id", context?.packageName)
        iconResourceId?.let { resourceId ->
            layout.findViewById<ImageView>(resourceId)?.setBackgroundColor(Color.parseColor(activeColor))
        }

            // 1회 투여양
        layout.findViewById<EditText>(R.id.numberOfOneTimeInput).hint = 1f.toString()
        layout.findViewById<EditText>(R.id.unitTypeInput).hint = "정"
        // 기본 정보 초기화 end

        // 데이터 전달
        layout.findViewById<Button>(R.id.resultBtn).setOnClickListener {
            val resultBundle = Bundle()
            var flag: Boolean = true

            // 투여 약 이름
            resultBundle.putString("medicineName", if(layout.findViewById<EditText>(R.id.medicineNameInput).text.toString() != "") layout.findViewById<EditText>(R.id.medicineNameInput).text.toString() else "")

            // 질환 명
            resultBundle.putString("diseaseName", if(layout.findViewById<EditText>(R.id.diseaseNameInput).text.toString() != "") layout.findViewById<EditText>(R.id.diseaseNameInput).text.toString() else "")

            // 투여 기간
            resultBundle.putInt("startYear", if(layout.findViewById<EditText>(R.id.startYearInput).text.toString() != "") layout.findViewById<EditText>(R.id.startYearInput).text.toString().toInt() else targetDay.year.toString().substring(2, 4).toInt())
            resultBundle.putInt("startMonth", if(layout.findViewById<EditText>(R.id.startMonthInput).text.toString() != "") layout.findViewById<EditText>(R.id.startMonthInput).text.toString().toInt() else targetDay.monthValue.toString().toInt())
            resultBundle.putInt("startDay", if(layout.findViewById<EditText>(R.id.startDayInput).text.toString() != "") layout.findViewById<EditText>(R.id.startDayInput).text.toString().toInt() else targetDay.dayOfMonth.toString().toInt())
            resultBundle.putInt("endYear", if(layout.findViewById<EditText>(R.id.endYearInput).text.toString() != "") layout.findViewById<EditText>(R.id.endYearInput).text.toString().toInt() else targetDay.year.toString().substring(2, 4).toInt())
            resultBundle.putInt("endMonth", if(layout.findViewById<EditText>(R.id.endMonthInput).text.toString() != "") layout.findViewById<EditText>(R.id.endMonthInput).text.toString().toInt() else targetDay.monthValue.toString().toInt())
            resultBundle.putInt("endDay", if(layout.findViewById<EditText>(R.id.endDayInput).text.toString() != "") layout.findViewById<EditText>(R.id.endDayInput).text.toString().toInt() else targetDay.dayOfMonth.toString().toInt())

            // 투여 약 이모티콘
            resultBundle.putInt("medicineIcon", selectIcon)

            // 시간 설정
            resultBundle.putBooleanArray("timeArray", timeChk)

            // 1회 투여양
            resultBundle.putFloat("numberOfOneTime", if(layout.findViewById<EditText>(R.id.numberOfOneTimeInput).text.toString() != "") layout.findViewById<EditText>(R.id.numberOfOneTimeInput).text.toString().toFloat() else layout.findViewById<EditText>(R.id.numberOfOneTimeInput).hint.toString().toFloat())
            resultBundle.putString("unitType", if(layout.findViewById<EditText>(R.id.unitTypeInput).text.toString() != "") layout.findViewById<EditText>(R.id.unitTypeInput).text.toString() else layout.findViewById<EditText>(R.id.unitTypeInput).hint.toString())

            if(layout.findViewById<EditText>(R.id.medicineNameInput).text.toString() == "") {
                Toast.makeText(mainActivity, "투여약 이름을 입력해 주세요", Toast.LENGTH_SHORT).show()
                flag = false
            }
            else if(layout.findViewById<EditText>(R.id.diseaseNameInput).text.toString() == "") {
                Toast.makeText(mainActivity, "질환명을 입력해 주세요", Toast.LENGTH_SHORT).show()
                flag = false
            }
            else if(layout.findViewById<EditText>(R.id.startYearInput).text.toString() != "") {
                if (layout.findViewById<EditText>(R.id.startYearInput).text.toString().toInt() < 0 || layout.findViewById<EditText>(R.id.startYearInput).text.toString().toInt() > 99) {
                    Toast.makeText(mainActivity, "0에서 99사이의 투여 시작 년도를 입력해 주세요", Toast.LENGTH_SHORT).show()
                    flag = false
                }
            }
            else if(layout.findViewById<EditText>(R.id.endYearInput).text.toString() != "") {
                if (layout.findViewById<EditText>(R.id.endYearInput).text.toString().toInt() < 0 || layout.findViewById<EditText>(R.id.endYearInput).text.toString().toInt() > 99) {
                    Toast.makeText(mainActivity, "0에서 99사이의 투여 종료 년도를 입력해 주세요", Toast.LENGTH_SHORT).show()
                    flag = false
                }
            }
            else if(layout.findViewById<EditText>(R.id.startMonthInput).text.toString() != "") {
                if (layout.findViewById<EditText>(R.id.startMonthInput).text.toString().toInt() < 1 || layout.findViewById<EditText>(R.id.startMonthInput).text.toString().toInt() > 12) {
                    Toast.makeText(mainActivity, "1에서 12사이의 투여 시작 월을 입력해 주세요", Toast.LENGTH_SHORT).show()
                    flag = false
                }
            }
            else if(layout.findViewById<EditText>(R.id.endMonthInput).text.toString() != "") {
                if (layout.findViewById<EditText>(R.id.endMonthInput).text.toString().toInt() < 1 || layout.findViewById<EditText>(R.id.endMonthInput).text.toString().toInt() > 12) {
                    Toast.makeText(mainActivity, "1에서 12사이의 투여 종료 월을 입력해 주세요", Toast.LENGTH_SHORT).show()
                    flag = false
                }

            }

            // 시작 날짜 체크
            var startYearData: String = ""
            var startMonthData: String = ""
            var startDayData: String = ""

            if(layout.findViewById<EditText>(R.id.startYearInput).text.toString() == "") {
                startYearData = (2000 + layout.findViewById<EditText>(R.id.startYearInput).hint.toString().toInt()).toString()
            }
            else {
                startYearData = (2000 + layout.findViewById<EditText>(R.id.startYearInput).text.toString().toInt()).toString()
            }

            if(layout.findViewById<EditText>(R.id.startMonthInput).text.toString() == "") {
                startMonthData = if(layout.findViewById<EditText>(R.id.startMonthInput).hint.toString().toInt() < 10) "0${layout.findViewById<EditText>(R.id.startMonthInput).hint.toString()}" else layout.findViewById<EditText>(R.id.startMonthInput).hint.toString();
            }
            else {
                startMonthData = if(layout.findViewById<EditText>(R.id.startMonthInput).text.toString().toInt() < 10) "0${layout.findViewById<EditText>(R.id.startMonthInput).text.toString()}" else layout.findViewById<EditText>(R.id.startMonthInput).text.toString();
            }

            if(layout.findViewById<EditText>(R.id.startDayInput).text.toString() == "") {
                startDayData = layout.findViewById<EditText>(R.id.startDayInput).hint.toString()
            }
            else {
                startDayData = layout.findViewById<EditText>(R.id.startDayInput).text.toString()
            }

            var startDate: LocalDate = LocalDate.parse("${startYearData}-${startMonthData}-01")
            var lastDay: Int = startDate.withDayOfMonth(startDate.lengthOfMonth()).toString().substring(8, 10).toInt()

            if(startDayData.toInt() < 1 || startDayData.toInt() > lastDay) {
                Toast.makeText(mainActivity, "1에서 ${lastDay}사이의 투여 시작 일을 입력해 주세요(${startMonthData.toInt()}월)", Toast.LENGTH_SHORT).show()
                flag = false
            }
            // 시작 날짜 체크 끝

            // 종료 날짜 체크 시작
            var endYearData: String = ""
            var endMonthData: String = ""
            var endDayData: String = ""

            if(layout.findViewById<EditText>(R.id.endYearInput).text.toString() == "") {
                endYearData = (2000 + layout.findViewById<EditText>(R.id.endYearInput).hint.toString().toInt()).toString()
            }
            else {
                endYearData = (2000 + layout.findViewById<EditText>(R.id.endYearInput).text.toString().toInt()).toString()
            }

            if(layout.findViewById<EditText>(R.id.endMonthInput).text.toString() == "") {
                endMonthData = if(layout.findViewById<EditText>(R.id.endMonthInput).hint.toString().toInt() < 10) "0${layout.findViewById<EditText>(R.id.endMonthInput).hint.toString()}" else layout.findViewById<EditText>(R.id.endMonthInput).hint.toString();
            }
            else {
                endMonthData = if(layout.findViewById<EditText>(R.id.endMonthInput).text.toString().toInt() < 10) "0${layout.findViewById<EditText>(R.id.endMonthInput).text.toString()}" else layout.findViewById<EditText>(R.id.endMonthInput).text.toString();
            }

            if(layout.findViewById<EditText>(R.id.endDayInput).text.toString() == "") {
                endDayData = layout.findViewById<EditText>(R.id.endDayInput).hint.toString()
            }
            else {
                endDayData = layout.findViewById<EditText>(R.id.endDayInput).text.toString()
            }

            startDate = LocalDate.parse("${endYearData}-${endMonthData}-01")
            lastDay = startDate.withDayOfMonth(startDate.lengthOfMonth()).toString().substring(8, 10).toInt()

            if(endDayData.toInt() < 1 || endDayData.toInt() > lastDay) {
                Toast.makeText(mainActivity, "1에서 ${lastDay}사이의 투여 종료 일을 입력해 주세요(${endMonthData.toInt()}월)", Toast.LENGTH_SHORT).show()
                flag = false
            }
            // 종료 날짜 체크 끝

            if(flag) {
                setFragmentResult("medicineAddData", resultBundle)

                mainActivity!!.gotoAddMedicineResult()
            }
        }


        for(iconIdIterator in 1..35) {
            val iconId: String = "medicineIcon${iconIdIterator}"
            val resources = context?.resources
            val iconResourceId = resources?.getIdentifier(iconId, "id", context?.packageName)
            iconResourceId?.let { resourceId ->
                layout.findViewById<ImageView>(resourceId)?.setOnClickListener {
                    val prevIconId: String = "medicineIcon${selectIcon}"
                    val prevIconResourceId = resources?.getIdentifier(prevIconId, "id", context?.packageName)
                    prevIconResourceId?.let { prevIconResourceId ->
                        layout.findViewById<ImageView>(prevIconResourceId)?.setBackgroundColor(Color.parseColor(nonColor))
                    }
                    selectIcon = iconIdIterator
                    layout.findViewById<ImageView>(resourceId)?.setBackgroundColor(Color.parseColor(activeColor))
                }
            }
        }
        // 아니 if(timeChk[0]) timeChk[0] = false else timeChk[0] = true 이렇게 안쓰고 timeChk[0] != timeChk[0] 이거 왜 안됨..??
        layout.findViewById<ImageView>(R.id.wakeChk).setOnClickListener {
            if(timeChk[0]) timeChk[0] = false else timeChk[0] = true
            if(timeChk[0]) layout.findViewById<ImageView>(R.id.wakeChk).setImageResource(R.drawable.baseline_check_box_24) else layout.findViewById<ImageView>(R.id.wakeChk).setImageResource(R.drawable.baseline_check_box_outline_blank_24)
        }

        layout.findViewById<ImageView>(R.id.breakfastBeforeChk).setOnClickListener {
            if(timeChk[1]) timeChk[1] = false else timeChk[1] = true
            if(timeChk[1]) layout.findViewById<ImageView>(R.id.breakfastBeforeChk).setImageResource(R.drawable.baseline_check_box_24) else layout.findViewById<ImageView>(R.id.breakfastBeforeChk).setImageResource(R.drawable.baseline_check_box_outline_blank_24)
        }

        layout.findViewById<ImageView>(R.id.breakfastAfterChk).setOnClickListener {
            if(timeChk[2]) timeChk[2] = false else timeChk[2] = true
            if(timeChk[2]) layout.findViewById<ImageView>(R.id.breakfastAfterChk).setImageResource(R.drawable.baseline_check_box_24) else layout.findViewById<ImageView>(R.id.breakfastAfterChk).setImageResource(R.drawable.baseline_check_box_outline_blank_24)
        }

        layout.findViewById<ImageView>(R.id.lunchBeforeChk).setOnClickListener {
            if(timeChk[3]) timeChk[3] = false else timeChk[3] = true
            if(timeChk[3]) layout.findViewById<ImageView>(R.id.lunchBeforeChk).setImageResource(R.drawable.baseline_check_box_24) else layout.findViewById<ImageView>(R.id.lunchBeforeChk).setImageResource(R.drawable.baseline_check_box_outline_blank_24)
        }

        layout.findViewById<ImageView>(R.id.lunchAfterChk).setOnClickListener {
            if(timeChk[4]) timeChk[4] = false else timeChk[4] = true
            if(timeChk[4]) layout.findViewById<ImageView>(R.id.lunchAfterChk).setImageResource(R.drawable.baseline_check_box_24) else layout.findViewById<ImageView>(R.id.lunchAfterChk).setImageResource(R.drawable.baseline_check_box_outline_blank_24)
        }

        layout.findViewById<ImageView>(R.id.dinnerBeforeChk).setOnClickListener {
            if(timeChk[5]) timeChk[5] = false else timeChk[5] = true
            if(timeChk[5]) layout.findViewById<ImageView>(R.id.dinnerBeforeChk).setImageResource(R.drawable.baseline_check_box_24) else layout.findViewById<ImageView>(R.id.dinnerBeforeChk).setImageResource(R.drawable.baseline_check_box_outline_blank_24)
        }

        layout.findViewById<ImageView>(R.id.dinnerAfterChk).setOnClickListener {
            if(timeChk[6]) timeChk[6] = false else timeChk[6] = true
            if(timeChk[6]) layout.findViewById<ImageView>(R.id.dinnerAfterChk).setImageResource(R.drawable.baseline_check_box_24) else layout.findViewById<ImageView>(R.id.dinnerAfterChk).setImageResource(R.drawable.baseline_check_box_outline_blank_24)
        }

        layout.findViewById<ImageView>(R.id.bedChk).setOnClickListener {
            if(timeChk[7]) timeChk[7] = false else timeChk[7] = true
            if(timeChk[7]) layout.findViewById<ImageView>(R.id.bedChk).setImageResource(R.drawable.baseline_check_box_24) else layout.findViewById<ImageView>(R.id.bedChk).setImageResource(R.drawable.baseline_check_box_outline_blank_24)
        }


        return layout
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }
}
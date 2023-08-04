package a103.dev.eyakrev1

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.setFragmentResult
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
        // 기본 정보 초기화 end

        // 데이터 전달
        layout.findViewById<Button>(R.id.resultBtn).setOnClickListener {
            val resultBundle = Bundle()

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
            resultBundle.putFloat("numberOfOneTime", if(layout.findViewById<EditText>(R.id.numberOfOneTimeInput).text.toString() != "") layout.findViewById<EditText>(R.id.numberOfOneTimeInput).text.toString().toFloat() else 0f)
            resultBundle.putString("unitType", if(layout.findViewById<EditText>(R.id.unitTypeInput).text.toString() != "") layout.findViewById<EditText>(R.id.unitTypeInput).text.toString() else "null")

            setFragmentResult("medicineAddData", resultBundle)

            mainActivity!!.gotoAddMedicineResult()
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
package com.a103.eyakrev1

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
import android.widget.Toast
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.preference.PreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MedicineAddResultFragment : Fragment() {

    lateinit var mainActivity: MainActivity

    private val api = EyakService.create()

    private var selectIcon: Int = 0

    private var timeChk: BooleanArray = booleanArrayOf(false, false, false, false, false, false, false, false)

    var isEdit = false
    var clickedMedicineId = -1
    var iotLocation = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener("medicineAddData") { _, bundle -> // setFragmentResultListener("보낸 데이터 묶음 이름") {requestKey, bundle ->

            isEdit = bundle.getBoolean("isEdit", false)

            if (isEdit) {
                view?.findViewById<TextView>(R.id.medicineAddResultTitleTextView)?.text = "복약 정보 수정 확인"
            }

            clickedMedicineId = bundle.getInt("clickedMedicineId", -1)
            iotLocation = bundle.getInt("iotLocation", -1)

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

        // 등록하는 약의 정보를 확인했고 서버에 전송
        layout.findViewById<Button>(R.id.chkComplete).setOnClickListener {

            // 입력 밭은 날짜 정보
            val startYear: String = if(layout.findViewById<TextView>(R.id.startYearInputResult).text.toString().toInt() < 10) "200" + layout.findViewById<TextView>(R.id.startYearInputResult).text.toString() else "20" + layout.findViewById<TextView>(R.id.startYearInputResult).text.toString()
            val startMonth: String = if(layout.findViewById<TextView>(R.id.startMonthInputResult).text.toString().toInt() < 10) "0" + layout.findViewById<TextView>(R.id.startMonthInputResult).text.toString() else layout.findViewById<TextView>(R.id.startMonthInputResult).text.toString()
            val startDay: String = if(layout.findViewById<TextView>(R.id.startDayInputResult).text.toString().toInt() < 10) "0" + layout.findViewById<TextView>(R.id.startDayInputResult).text.toString() else layout.findViewById<TextView>(R.id.startDayInputResult).text.toString()
            val endYear: String = if(layout.findViewById<TextView>(R.id.endYearInputResult).text.toString().toInt() < 10) "200" + layout.findViewById<TextView>(R.id.endYearInputResult).text.toString() else "20" + layout.findViewById<TextView>(R.id.endYearInputResult).text.toString()
            val endMonth: String = if(layout.findViewById<TextView>(R.id.endMonthInputResult).text.toString().toInt() < 10) "0" + layout.findViewById<TextView>(R.id.endMonthInputResult).text.toString() else layout.findViewById<TextView>(R.id.endMonthInputResult).text.toString()
            val endDay: String = if(layout.findViewById<TextView>(R.id.endDayInputResult).text.toString().toInt() < 10) "0" + layout.findViewById<TextView>(R.id.endDayInputResult).text.toString() else layout.findViewById<TextView>(R.id.endDayInputResult).text.toString()

            val medicineRoutines: MutableList<String> = mutableListOf()

            if(timeChk[0]) medicineRoutines.add("BED_AFTER")
            if(timeChk[1]) medicineRoutines.add("BREAKFAST_BEFORE")
            if(timeChk[2]) medicineRoutines.add("BREAKFAST_AFTER")
            if(timeChk[3]) medicineRoutines.add("LUNCH_BEFORE")
            if(timeChk[4]) medicineRoutines.add("LUNCH_AFTER")
            if(timeChk[5]) medicineRoutines.add("DINNER_BEFORE")
            if(timeChk[6]) medicineRoutines.add("DINNER_AFTER")
            if(timeChk[7]) medicineRoutines.add("BED_BEFORE")

            val pref = PreferenceManager.getDefaultSharedPreferences(mainActivity)
            val serverAccessToken = pref.getString("SERVER_ACCESS_TOKEN", "")   // 엑세스 토큰


            if (isEdit) {
                // 이게 edit이라면, clickedMedicineId가 -1이 아닐거
                val data = PrescriptionEditBodyModel(
                    icd = "",
                    krName = layout.findViewById<TextView>(R.id.diseaseNameInputResult).text.toString(),
                    engName = "",
                    customName = layout.findViewById<TextView>(R.id.medicineNameInputResult).text.toString(),
                    startDateTime = "${startYear}-${startMonth}-${startDay}T00:00:00",
                    endDateTime = "${endYear}-${endMonth}-${endDay}T00:00:00",
                    medicineRoutines = medicineRoutines,
                    iotLocation = iotLocation,
                    medicineShape = selectIcon,
                    medicineDose = layout.findViewById<TextView>(R.id.numberOfOneTimeInputResult).text.toString().toFloat(),
                    unit = layout.findViewById<TextView>(R.id.unitTypeInputResult).text.toString(),
                )

                api.editPrescriptionDetail(Authorization = "Bearer ${serverAccessToken}", params = data, prescriptionId = clickedMedicineId).enqueue(object:
                    Callback<Medicine> {
                    override fun onResponse(call: Call<Medicine>, response: Response<Medicine>) {
                        if(response.code() == 200) {
                            Toast.makeText(mainActivity, "성공", Toast.LENGTH_SHORT).show()
                            // 해당 복약 상세 조회를 띄워주자
                            val bundle = Bundle()
                            bundle.putInt("clickedMedicineId", clickedMedicineId)
                            setFragmentResult("medicineDetailClicked", bundle)
                            mainActivity!!.gotoMedicineDetail()
                        }
                        else if(response.code() == 401) {
                            Toast.makeText(mainActivity, "AccessToken이 유효하지 않은 경우", Toast.LENGTH_SHORT).show()
                        } else if(response.code() == 400) {
                            Toast.makeText(mainActivity, "해당 복약 정보가 존재하지 않습니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Medicine>, t: Throwable) {

                    }
                })
            } else {
                // 이게 add라면 (기존꺼)
                val data = PrescriptionBodyModel(
                    icd = "",
                    kr_name = layout.findViewById<TextView>(R.id.diseaseNameInputResult).text.toString(),
                    eng_name = "",
                    custom_name = layout.findViewById<TextView>(R.id.medicineNameInputResult).text.toString(),
                    start_date_time = "${startYear}-${startMonth}-${startDay}T00:00:00",
                    end_date_time = "${endYear}-${endMonth}-${endDay}T00:00:00",
                    medicine_routines = medicineRoutines,
                    medicine_shape = selectIcon,
                    medicine_dose = layout.findViewById<TextView>(R.id.numberOfOneTimeInputResult).text.toString().toFloat(),
                    unit = layout.findViewById<TextView>(R.id.unitTypeInputResult).text.toString(),
                )

                api.prescription(Authorization = "Bearer ${serverAccessToken}", params = data).enqueue(object:
                    Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if(response.code() == 201) {
                            Toast.makeText(mainActivity, "성공", Toast.LENGTH_SHORT).show()
                            // 복약 전체조회를 띄워주자
                            mainActivity!!.gotoMedicine()
                        }
                        else if(response.code() == 401) {
                            Toast.makeText(mainActivity, "AccessToken이 유효하지 않은 경우", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {

                    }
                })
//                // 복약 전체조회를 띄워주자 => 성공 시에만 띄워주자
//                mainActivity!!.gotoMedicine()
            }




        }

        return layout
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }

}
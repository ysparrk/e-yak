package com.a103.eyakrev1

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MedicineDetailFragment : Fragment() {

    lateinit var mainActivity: MainActivity

    private val api = EyakService.create()
    private var medicine: Medicine? = Medicine()
    private var clickedMedicineId = -1

    private var timeChk: BooleanArray = booleanArrayOf(false, false, false, false, false, false, false, false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layout = inflater.inflate(R.layout.fragment_medicine_detail, container, false)

        val medicineDetailCardView = layout.findViewById<CardView>(R.id.medicineDetailCardView)
        medicineDetailCardView.visibility = View.INVISIBLE

        val medicineDetailNameTextView = layout.findViewById<TextView>(R.id.medicineDetailName)
        val diseaseNameTextView = layout.findViewById<TextView>(R.id.diseaseName)
        val startDateTimeTextView = layout.findViewById<TextView>(R.id.startDateTime)
        val endDateTimeTextView = layout.findViewById<TextView>(R.id.endDateTime)
        val medicineDoseTextView = layout.findViewById<TextView>(R.id.medicineDose)
        val numberOfDosePerDayTextView = layout.findViewById<TextView>(R.id.numberOfDosePerDay)
        val dosePerDayTextView = layout.findViewById<TextView>(R.id.dosePerDay)
        val medicineDetailEditButton = layout.findViewById<Button>(R.id.medicineDetailEditButton)
        val medicineDetailDeleteButton = layout.findViewById<Button>(R.id.medicineDetailDeleteButton)

        setFragmentResultListener("medicineDetailClicked") { requestKey, bundle ->
            clickedMedicineId = bundle.getInt("clickedMedicineId", -1)

            if (clickedMedicineId != -1) {
                // 제대로 전달된 상황
                val pref = PreferenceManager.getDefaultSharedPreferences(mainActivity)
                val serverAccessToken = pref.getString("SERVER_ACCESS_TOKEN", "")

                api.getPrescriptionDetail(Authorization= "Bearer ${serverAccessToken}", prescriptionId=clickedMedicineId).enqueue(object: Callback<Medicine> {
                    override fun onResponse(call: Call<Medicine>, response: Response<Medicine>) {
                        if (response.code() == 401) {
                            Log.d("로그", "복약 정보 상세 조회 401 Unauthorized: AccessToken이 유효하지 않은 경우")
                        } else if (response.code() == 400) {
                            Log.d("로그", "복약 정보 상세 조회 400 Bad Request: 해당하는 Prescription이 존재하지 않는 경우")
                        } else if (response.code() == 200) {
                            Log.d("로그", "복약 정보 상세 조회 200 OK")

                            medicine = response.body()

                            medicineDetailNameTextView.text = medicine?.customName
                            diseaseNameTextView.text = medicine?.krName
                            startDateTimeTextView.text = medicine?.startDateTime?.substring(0 until 10)
                            endDateTimeTextView.text = medicine?.endDateTime?.substring(0 until 10)
                            medicineDoseTextView.text = "${medicine?.medicineDose.toString()} ${medicine?.unit}"
                            numberOfDosePerDayTextView.text = "${medicine?.routines?.size.toString()} 회"

                            var dosePerDayText = ""
                            for (engTime in medicine!!.routines) {
                                if (engTime == "BED_AFTER") {
                                    dosePerDayText = dosePerDayText.plus("○ 기상 후 ○\n")
                                    timeChk[0] = true
                                } else if (engTime == "BREAKFAST_BEFORE") {
                                    dosePerDayText = dosePerDayText.plus("○ 아침 식사 전 ○\n")
                                    timeChk[1] = true
                                } else if (engTime == "BREAKFAST_AFTER") {
                                    dosePerDayText = dosePerDayText.plus("○ 아침 식사 후 ○\n")
                                    timeChk[2] = true
                                } else if (engTime == "LUNCH_BEFORE") {
                                    dosePerDayText = dosePerDayText.plus("○ 점심 식사 전 ○\n")
                                    timeChk[3] = true
                                } else if (engTime == "LUNCH_AFTER") {
                                    dosePerDayText = dosePerDayText.plus("○ 점심 식사 후 ○\n")
                                    timeChk[4] = true
                                } else if (engTime == "DINNER_BEFORE") {
                                    dosePerDayText = dosePerDayText.plus("○ 저녁 식사 전 ○\n")
                                    timeChk[5] = true
                                } else if (engTime == "DINNER_AFTER") {
                                    dosePerDayText = dosePerDayText.plus("○ 저녁 식사 후 ○\n")
                                    timeChk[6] = true
                                } else if (engTime == "BED_BEFORE") {
                                    dosePerDayText = dosePerDayText.plus("○ 취침 전 ○\n")
                                    timeChk[7] = true
                                }
                            }
                            dosePerDayTextView.text = dosePerDayText.substring(0, kotlin.math.max(dosePerDayText.length - 1, 0))

                            val medicineDetailIconImageView = layout.findViewById<ImageView>(R.id.medicineDetailIcon)
                            when(medicine!!.medicineShape) {
                                1 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_pill_glacier)
                                2 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_pill_afterglow)
                                3 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_pill_bougainvaillea)
                                4 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_pill_orchidice)
                                5 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_pill_silver)
                                6 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_pill_pinklady)
                                7 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_pill_fusioncoral)
                                8 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_roundpill_glacier)
                                9 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_roundpill_afterglow)
                                10 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_roundpill_bougainvillea)
                                11 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_roundpill_orchidice)
                                12 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_roundpill_silver)
                                13 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_roundpill_pinklady)
                                14 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_roundpill_fusioncoral)
                                15 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_packagepill_glacier)
                                16 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_packagepill_afterglow)
                                17 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_packagepill_bougainvillea)
                                18 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_packagepill_orchidice)
                                19 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_packagepill_silver)
                                20 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_packagepill_pinklady)
                                21 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_packagepill_fusioncoral)
                                22 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_outpill_glacier)
                                23 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_outpill_afterglow)
                                24 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_outpill_bougainvillea)
                                25 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_outpill_orchidice)
                                26 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_outpill_silver)
                                27 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_outpill_pinklady)
                                28 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_outpill_fusioncoral)
                                29 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_potion_glacier)
                                30 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_potion_afterglow)
                                31 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_potion_bougainvillea)
                                32 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_potion_orchidice)
                                33 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_potion_silver)
                                34 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_potion_pinklady)
                                35 -> medicineDetailIconImageView.setImageResource(R.drawable.ic_potion_fusioncoral)
                            }

                            medicineDetailCardView.visibility = View.VISIBLE
                        }
                    }

                    override fun onFailure(call: Call<Medicine>, t: Throwable) {
                        Log.d("로그", "복약 정보 상세 조회 onFailure")
                    }
                })
            }
        }
        
        // 삭제 버튼을 클릭했을 때
        medicineDetailDeleteButton.setOnClickListener {
            val pref = PreferenceManager.getDefaultSharedPreferences(mainActivity)
            val serverAccessToken = pref.getString("SERVER_ACCESS_TOKEN", "")

            api.deletePrescription(Authorization= "Bearer ${serverAccessToken}", prescriptionId=medicine!!.id).enqueue(object: Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.code() == 401) {
                        Log.d("로그", "복약 정보 삭제 401 Unauthorized: AccessToken이 유효하지 않은 경우")
                    } else if (response.code() == 400) {
                        Log.d("로그", "복약 정보 삭제 400 Bad Request: 해당하는 Prescription이 존재하지 않는 경우")
                    } else if (response.code() == 200) {
                        Log.d("로그", "복약 정보 삭제 200 OK")
                        // 삭제 완료 후 복약 탭으로 이동
                        mainActivity!!.gotoMedicine()
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("로그", "복약 정보 삭제 onFailure")
                }
            })
        }

        // 수정 버튼을 클릭했을 때
        medicineDetailEditButton.setOnClickListener {
            val bundle = Bundle()
            val isEdit = true

            bundle.putBoolean("isEdit", isEdit)
            bundle.putInt("clickedMedicineId", clickedMedicineId)
            bundle.putString("medicineName", medicine?.customName)
            bundle.putString("diseaseName", medicine?.krName)
            bundle.putBooleanArray("timeChk", timeChk)
            bundle.putInt("medicineShape", medicine?.medicineShape!!)
            bundle.putFloat("medicineDose", medicine?.medicineDose!!)
            bundle.putString("unit", medicine?.unit)
            bundle.putString("startDateTime", layout.findViewById<TextView>(R.id.startDateTime).text.toString())
            bundle.putString("endDateTime", layout.findViewById<TextView>(R.id.endDateTime).text.toString())
            bundle.putInt("iotLocation", medicine?.iotLocation!!)

            setFragmentResult("medicineEditData", bundle)

            mainActivity!!.gotoAddMedicine()
        }

        return layout
    }

    private lateinit var callback: OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mainActivity.gotoMedicine()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}
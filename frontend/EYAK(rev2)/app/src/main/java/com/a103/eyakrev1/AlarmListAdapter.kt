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
import androidx.cardview.widget.CardView
import kotlin.concurrent.timer
import android.os.Handler
import android.os.Looper
import android.widget.ListView
import androidx.preference.PreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalTime
import kotlin.time.DurationUnit
import java.time.temporal.ChronoUnit.SECONDS

class AlarmListAdapter (val context: Context, val medicineRoutines: MedicineRoutines, val medicineTimeList: ArrayList<LocalTime>, val medicineNameList: ArrayList<String>, val targetDay: LocalDate) : BaseAdapter() {

    var mainActivity: MainActivity = context as MainActivity

    private val api = EyakService.create()

    private val uiHandler = Handler(Looper.getMainLooper())

    // https://blog.yena.io/studynote/2017/12/01/Android-Kotlin-ListView.html
    override fun getCount(): Int {
        return 8
    }

    override fun getItem(position: Int): ArrayList<medicineInRoutine> {

        if (position == 0) {
            return medicineRoutines.bedAfterQueryResponses
        } else if (position == 1) {
            return medicineRoutines.breakfastBeforeQueryResponses
        } else if (position == 2) {
            return medicineRoutines.breakfastAfterQueryResponses
        } else if (position == 3) {
            return medicineRoutines.lunchBeforeQueryResponses
        } else if (position == 4) {
            return medicineRoutines.lunchAfterQueryResponses
        } else if (position == 5) {
            return medicineRoutines.dinnerBeforeQueryResponses
        } else if (position == 6) {
            return medicineRoutines.dinnerAfterQueryResponses
        } else { // position == 7
            return medicineRoutines.bedBeforeQueryResponses
        }
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
        val medicineRoutine = getItem(position)

        // 만약 해당 루틴에 먹을 약이 없다면 카드를 띄우지 말자
        if (medicineRoutine.size == 0) {
            // empty view를 리턴 => 그냥 카드뷰만 GONE으로 하면 다르게 설정한 마진 같은거 때문에 약간의 공간 차지해서
            return View(context)
        }

        medicineTimeTextView.text = medicineTimeList[position].toString()
        medicineNameTextView.text = medicineNameList[position]

        // https://stackoverflow.com/questions/3135112/android-nested-listview
        val prescriptionDetailLayout = view.findViewById<LinearLayout>(R.id.prescriptionDetailLayout)

        alarmTabDetailButton.setOnClickListener {
            // 처음에 한번만 불러오고 싶었기 때문에, 저기에 자식들이 없다면 한번만 실행, 처음 버튼 누를때만 뷰를 추가하도록
            if (prescriptionDetailLayout.childCount == 0) {
                for (medicine in medicineRoutine) {
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
        
        // 미래 시점일 경우, 복용 클릭 불가능, 버튼을 지워버리자
        if (targetDay.compareTo(LocalDate.now()) > 0) {
            medicineEatButton.visibility = View.INVISIBLE
        } else {
            medicineEatButton.setOnClickListener {
                val pref = PreferenceManager.getDefaultSharedPreferences(mainActivity)
                val serverAccessToken = pref.getString("SERVER_ACCESS_TOKEN", "")   // 엑세스 토큰

                val routineStrings = arrayListOf("BED_AFTER", "BREAKFAST_BEFORE", "BREAKFAST_AFTER", "LUNCH_BEFORE", "LUNCH_AFTER", "DINNER_BEFORE", "DINNER_AFTER", "BED_BEFORE")

                for (medicine in medicineRoutine) {
                    val params = getMedicineRoutineCheckIdBodyModel(date = targetDay.toString(), routine = routineStrings[position], prescriptionId = medicine.id,
                    )
                    api.findMedicineRoutineCheckId(Authorization = "Bearer ${serverAccessToken}", params=params).enqueue(object:
                        Callback<getMedicineRoutineCheckIdResponseBody> {
                        override fun onResponse(call: Call<getMedicineRoutineCheckIdResponseBody>, response: Response<getMedicineRoutineCheckIdResponseBody>) {
                            Log.d("log", response.toString())
                            Log.d("log", response.body().toString())

                            if (response.code() == 401) {
                                Log.d("log", "인증되지 않은 사용자입니다")
                            } else if (response.code() == 200) {

                            }
                        }

                        override fun onFailure(call: Call<getMedicineRoutineCheckIdResponseBody>, t: Throwable) {

                        }
                    })
                }
            }
        }




        // 타이머 설정해야 함
        timer(period = 1000) {
            // 오래 걸리는 작업 수행 부분
            // 백그라운드로 실행되는 부분, UI 조작 X
            // 시간 차이를 계산하자
            val currentTime = LocalTime.now()
            val targetTime = medicineTimeList[position]
            var timeDiffInSec = currentTime.until(targetTime, SECONDS)

            val today = LocalDate.now()
            val dayDiff = targetDay.compareTo(today)

            // 넣을 텍스트 초기화
            var targetText = "" 
            if (dayDiff > 0) { // targetDay가 미래라면
                targetText = "아직 아니:약"
            } else if (dayDiff < 0) {
                targetText = "지났어:약"
            } else if (timeDiffInSec < 0) {
                // 이미 지난 경우
                targetText = "지났어:약"
            } else {
                // 오늘 날짜이고, 아직 남은 경우
                var secondDiff = timeDiffInSec % 60
                timeDiffInSec = timeDiffInSec / 60
                var minuteDiff = timeDiffInSec % 60
                var hourDiff = timeDiffInSec / 60

                targetText = "${hourDiff.toString().padStart(2, '0')}:${minuteDiff.toString().padStart(2, '0')}:${secondDiff.toString().padStart(2, '0')}"
            }

            uiHandler.post {
                // UI 조작 로직
                medicineTimeLeftTextView.text = targetText
            }
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
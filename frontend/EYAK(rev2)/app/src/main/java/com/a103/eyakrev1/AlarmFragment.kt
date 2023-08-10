package com.a103.eyakrev1

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.preference.PreferenceManager
import com.a103.eyakrev1.databinding.AlarmTabMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class AlarmFragment : Fragment() {

    private lateinit var binding: AlarmTabMainBinding

    private val api = EyakService.create()

    var targetDay: LocalDate = LocalDate.now()

    var yesterday: LocalDate = targetDay.plusDays(-1)
    var tomorrow: LocalDate = targetDay.plusDays(1)

    val red: String = "#FF9B9B"
    val green: String = "#E3F2C1"
    val gray: String = "#DDE6ED"
    val yellow: String = "FFFF00"

    var medicineRoutines = MedicineRoutines()

    lateinit var eatingDuration: LocalTime
    lateinit var wakeTime: LocalTime
    lateinit var breakfastTime: LocalTime
    lateinit var breakfastTimeAfter: LocalTime
    lateinit var lunchTime: LocalTime
    lateinit var lunchTimeAfter: LocalTime
    lateinit var dinnerTime: LocalTime
    lateinit var dinnerTimeAfter: LocalTime
    lateinit var bedTime: LocalTime

    // https://curryyou.tistory.com/386
    // 1. Context를 할당할 변수를 프로퍼티로 선언(어디서든 사용할 수 있게)
    lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AlarmTabMainBinding.inflate(inflater, container, false)

        // 데이터 넣기 (총 8번의 시간에 해당하는)
        val pref = PreferenceManager.getDefaultSharedPreferences(mainActivity)
        val serverAccessToken = pref.getString("SERVER_ACCESS_TOKEN", "")   // 엑세스 토큰

        eatingDuration = LocalTime.parse(pref.getString("eatingDuration", ""))
        wakeTime = LocalTime.parse(pref.getString("wakeTime", ""))
        breakfastTime = LocalTime.parse(pref.getString("breakfastTime", ""))
        breakfastTimeAfter = breakfastTime.plusHours(eatingDuration.hour.toLong()).plusMinutes(eatingDuration.minute.toLong()).plusSeconds(eatingDuration.second.toLong())
        lunchTime = LocalTime.parse(pref.getString("lunchTime", ""))
        lunchTimeAfter = lunchTime.plusHours(eatingDuration.hour.toLong()).plusMinutes(eatingDuration.minute.toLong()).plusSeconds(eatingDuration.second.toLong())
        dinnerTime = LocalTime.parse(pref.getString("dinnerTime", ""))
        dinnerTimeAfter = dinnerTime.plusHours(eatingDuration.hour.toLong()).plusMinutes(eatingDuration.minute.toLong()).plusSeconds(eatingDuration.second.toLong())
        bedTime = LocalTime.parse(pref.getString("bedTime", ""))

        init()

        binding.yesterdayFrameLayout.setOnClickListener {
            updateDay(-1)
        }

        binding.tomorrowFrameLayout.setOnClickListener {
            updateDay(1)
        }

        binding.conditionLinearLayout.setOnClickListener {
            val resultBundle = Bundle()

            resultBundle.putString("sendDate", targetDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))

            setFragmentResult("todayConditionDate", resultBundle)

            mainActivity!!.gotoTodayCondition()
        }


        return binding.root
    }
    private fun init() {
        targetDay = LocalDate.now()
        yesterday = targetDay.plusDays(-1)
        tomorrow = targetDay.plusDays(1)

        updateDay(0)
    }

    public fun updateDay(gap: Long) {

        targetDay = targetDay.plusDays(gap)
        yesterday = targetDay.plusDays(-1)
        tomorrow = targetDay.plusDays(1)

        // 미래에 대한 컨디션 입력은 불가능 하도록
        if(LocalDate.now() < targetDay) {
            binding.conditionLinearLayout.visibility = View.GONE
        }
        else {
            if(LocalDate.now() == targetDay) binding.conditionTextView.setText("오늘의 컨디션")
            else binding.conditionTextView.setText("과거의 컨디션")

            binding.conditionLinearLayout.visibility = View.VISIBLE
        }

        val pref = PreferenceManager.getDefaultSharedPreferences(mainActivity)
        val serverAccessToken = pref.getString("SERVER_ACCESS_TOKEN", "")   // 엑세스 토큰

        binding.emptyAlarmLinearLayout.visibility = View.INVISIBLE

        api.getTargetDayPrescriptions(Authorization = "Bearer ${serverAccessToken}", dateTime = "${targetDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}T12:12:12").enqueue(object: Callback<MedicineRoutines> {
            override fun onResponse(call: Call<MedicineRoutines>, response: Response<MedicineRoutines>) {

                if(response.code() == 200) {
                    // 이제 적절하게 배분해서 넣어주자
                    Log.d("log", response.toString())
                    medicineRoutines = response.body()!!
                    Log.d("log", medicineRoutines.bedAfterQueryResponses.toString())
                    val routineKeys = arrayListOf("bedAfterQueryResponses", "breakfastBeforeQueryResponses", "breakfastAfterQueryResponses", "lunchBeforeQueryResponses", "lunchAfterQueryResponses", "dinnerBeforeQueryResponses", "dinnerAfterQueryResponses", "bedBeforeQueryResponses")

                    val medicineTimeList = arrayListOf(wakeTime, breakfastTime, breakfastTimeAfter, lunchTime, lunchTimeAfter, dinnerTime, dinnerTimeAfter, bedTime)
                    val medicineNameList = arrayListOf("기상 후", "아침 식사 전", "아침 식사 후", "점심 식사 전", "점심 식사 후", "저녁 식사 전", "저녁 식사 후", "취침 전")

                    // 그날 총 먹어야하는 약이 하나도 없다면 true
                    var isAlarmEmpty = true
                    
                    // 루틴 별로
                    if (medicineRoutines.bedAfterQueryResponses.size != 0) {
                        isAlarmEmpty = false
                    } else if (medicineRoutines.breakfastBeforeQueryResponses.size != 0) {
                        isAlarmEmpty = false
                    } else if (medicineRoutines.breakfastAfterQueryResponses.size != 0) {
                        isAlarmEmpty = false
                    } else if (medicineRoutines.lunchBeforeQueryResponses.size != 0) {
                        isAlarmEmpty = false
                    } else if (medicineRoutines.lunchAfterQueryResponses.size != 0) {
                        isAlarmEmpty = false
                    } else if (medicineRoutines.dinnerBeforeQueryResponses.size != 0) {
                        isAlarmEmpty = false
                    } else if (medicineRoutines.dinnerAfterQueryResponses.size != 0) {
                        isAlarmEmpty = false
                    } else if (medicineRoutines.bedBeforeQueryResponses.size != 0) {
                        isAlarmEmpty = false
                    }
                    
                    // 비어있다는 표시를 띄워주자
                    if (isAlarmEmpty) {
                        binding.emptyAlarmLinearLayout.visibility = View.VISIBLE
                    }

                    val alarmListAdapter = AlarmListAdapter(mainActivity, medicineRoutines, medicineTimeList, medicineNameList, targetDay)
                    binding.alramListView.findViewById<ListView>(R.id.alramListView)
                    binding.alramListView.adapter = alarmListAdapter
                }
                else if(response.code() == 401) {
                    Toast.makeText(mainActivity, "AccessToken이 유효하지 않은 경우", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<MedicineRoutines>, t: Throwable) {

            }
        })

        api.todayDoseInfo(Authorization = "Bearer ${serverAccessToken}", date = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).enqueue(object: Callback<TodayDoseInfoBodyModel> {
            override fun onResponse(call: Call<TodayDoseInfoBodyModel>, response: Response<TodayDoseInfoBodyModel>) {
                if(response.code() == 200) {
                    if(response.body()?.fullDose == 0) {
                        binding.yesterdayState.setColorFilter(Color.parseColor(gray))
                    }
                    else if(response.body()?.actualDose == 0) {
                        binding.yesterdayState.setColorFilter(Color.parseColor(red))
                    }
                    else if(response.body()?.actualDose == response.body()?.fullDose) {
                        binding.yesterdayState.setColorFilter(Color.parseColor(green))
                    }
                    else {
                        binding.yesterdayState.setColorFilter(Color.parseColor(yellow))
                    }
                }
                else if(response.code() == 401) {
                    Toast.makeText(mainActivity, "AccessToken이 유효하지 않은 경우", Toast.LENGTH_SHORT).show()
                }
                else if(response.code() == 400) {
                    Toast.makeText(mainActivity, "해당하는 member, date가 존재하지 않은 경우", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<TodayDoseInfoBodyModel>, t: Throwable) {

            }
        })

        api.todayDoseInfo(Authorization = "Bearer ${serverAccessToken}", date = targetDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).enqueue(object: Callback<TodayDoseInfoBodyModel> {
            override fun onResponse(call: Call<TodayDoseInfoBodyModel>, response: Response<TodayDoseInfoBodyModel>) {
                if(response.code() == 200) {
                    if(response.body()?.fullDose == 0) {
                        binding.todayState.setColorFilter(Color.parseColor(gray))
                    }
                    else if(response.body()?.actualDose == 0) {
                        binding.todayState.setColorFilter(Color.parseColor(red))
                    }
                    else if(response.body()?.actualDose == response.body()?.fullDose) {
                        binding.todayState.setColorFilter(Color.parseColor(green))
                    }
                    else {
                        binding.todayState.setColorFilter(Color.parseColor(yellow))
                    }
                }
                else if(response.code() == 401) {
                    Toast.makeText(mainActivity, "AccessToken이 유효하지 않은 경우", Toast.LENGTH_SHORT).show()
                }
                else if(response.code() == 400) {
                    Toast.makeText(mainActivity, "해당하는 member, date가 존재하지 않은 경우", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<TodayDoseInfoBodyModel>, t: Throwable) {

            }
        })

        api.todayDoseInfo(Authorization = "Bearer ${serverAccessToken}", date = tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).enqueue(object: Callback<TodayDoseInfoBodyModel> {
            override fun onResponse(call: Call<TodayDoseInfoBodyModel>, response: Response<TodayDoseInfoBodyModel>) {
                if(response.code() == 200) {

                    if(response.body()?.fullDose == 0) {
                        binding.tomorrowState.setColorFilter(Color.parseColor(gray))
                    }
                    else if(response.body()?.actualDose == 0) {
                        binding.tomorrowState.setColorFilter(Color.parseColor(red))
                    }
                    else if(response.body()?.actualDose == response.body()?.fullDose) {
                        binding.tomorrowState.setColorFilter(Color.parseColor(green))
                    }
                    else {
                        binding.tomorrowState.setColorFilter(Color.parseColor(yellow))
                    }
                }
                else if(response.code() == 401) {
                    Toast.makeText(mainActivity, "AccessToken이 유효하지 않은 경우", Toast.LENGTH_SHORT).show()
                }
                else if(response.code() == 400) {
                    Toast.makeText(mainActivity, "해당하는 member, date가 존재하지 않은 경우", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<TodayDoseInfoBodyModel>, t: Throwable) {

            }
        })

        binding.todayDate.text = "${targetDay.monthValue.toString()} / ${targetDay.dayOfMonth.toString()}"
        binding.yesterdayDate.text = "${yesterday.monthValue.toString()} / ${yesterday.dayOfMonth.toString()}"
        binding.tomorrowDate.text = "${tomorrow.monthValue.toString()} / ${tomorrow.dayOfMonth.toString()}"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // 2. Context를 액티비티로 형변환해서 할당
        mainActivity = context as MainActivity
    }
}
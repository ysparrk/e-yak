package com.a103.eyakrev1

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.a103.eyakrev1.databinding.ActivityMainBinding
import com.a103.eyakrev1.databinding.AlarmTabMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
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

    var medicineAlarmList = arrayListOf<MedicineAlarm>()

    // https://curryyou.tistory.com/386
    // 1. Context를 할당할 변수를 프로퍼티로 선언(어디서든 사용할 수 있게)
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AlarmTabMainBinding.inflate(inflater, container, false)

        init()

        for (i in 1..10) {
            // 임시 데이터 넣기
            val medicineAlarm = MedicineAlarm(medicineIcon = "ic_packagepill_afterglow", medicineTime = "오후 01:30", medicineName = "약이름 $i")
            medicineAlarmList.add(medicineAlarm)
        }

        val alarmListAdapter = AlarmListAdapter(mainActivity, medicineAlarmList)
        binding.alramListView.findViewById<ListView>(R.id.alramListView)
        binding.alramListView.adapter = alarmListAdapter

        binding.yesterdayFrameLayout.setOnClickListener {
            updateDay(-1)
        }

        binding.tomorrowFrameLayout.setOnClickListener {
            updateDay(1)
        }

        binding.conditionLinearLayout.setOnClickListener {
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

    private fun updateDay(gap: Long) {
        targetDay = targetDay.plusDays(gap)
        yesterday = targetDay.plusDays(-1)
        tomorrow = targetDay.plusDays(1)

        val pref = PreferenceManager.getDefaultSharedPreferences(mainActivity)
        val serverAccessToken = pref.getString("SERVER_ACCESS_TOKEN", "")   // 엑세스 토큰

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
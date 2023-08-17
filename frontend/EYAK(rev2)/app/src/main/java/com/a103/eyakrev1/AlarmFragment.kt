package com.a103.eyakrev1

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.preference.PreferenceManager
import com.a103.eyakrev1.databinding.AlarmTabMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

var medicineIdLists: ArrayList<ArrayList<String>> = arrayListOf<ArrayList<String>>(arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf())

lateinit var mainActivity: MainActivity

class AlarmFragment : Fragment() {

    private var backPressedOnce = false
    private val doubleClickInterval: Long = 1000 // 1초

    private lateinit var binding: AlarmTabMainBinding

    private val api = EyakService.create()

    var targetDay: LocalDate = LocalDate.now()

    var yesterday: LocalDate = targetDay.plusDays(-1)
    var tomorrow: LocalDate = targetDay.plusDays(1)

    val red: String = "#FF9B9B"
    val green: String = "#E3F2C1"
    val gray: String = "#DDE6ED"
    val yellow: String = "#FFEF00"

    var medicineRoutines = MedicineRoutines()

    // https://curryyou.tistory.com/386
    // 1. Context를 할당할 변수를 프로퍼티로 선언(어디서든 사용할 수 있게)

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

        val eatingDuration = LocalTime.parse(pref.getString("eatingDuration", ""))
        val wakeTime = LocalTime.parse(pref.getString("wakeTime", ""))
        val breakfastTime = LocalTime.parse(pref.getString("breakfastTime", ""))
        val breakfastTimeAfter = breakfastTime.plusHours(eatingDuration.hour.toLong()).plusMinutes(eatingDuration.minute.toLong()).plusSeconds(eatingDuration.second.toLong())
        val lunchTime = LocalTime.parse(pref.getString("lunchTime", ""))
        val lunchTimeAfter = lunchTime.plusHours(eatingDuration.hour.toLong()).plusMinutes(eatingDuration.minute.toLong()).plusSeconds(eatingDuration.second.toLong())
        val dinnerTime = LocalTime.parse(pref.getString("dinnerTime", ""))
        val dinnerTimeAfter = dinnerTime.plusHours(eatingDuration.hour.toLong()).plusMinutes(eatingDuration.minute.toLong()).plusSeconds(eatingDuration.second.toLong())
        val bedTime = LocalTime.parse(pref.getString("bedTime", ""))

        init(eatingDuration, wakeTime, breakfastTime, breakfastTimeAfter, lunchTime, lunchTimeAfter, dinnerTime, dinnerTimeAfter, bedTime)

        binding.yesterdayFrameLayout.setOnClickListener {
            updateDay(-1, eatingDuration, wakeTime, breakfastTime, breakfastTimeAfter, lunchTime, lunchTimeAfter, dinnerTime, dinnerTimeAfter, bedTime)
        }

        binding.tomorrowFrameLayout.setOnClickListener {
            updateDay(1, eatingDuration, wakeTime, breakfastTime, breakfastTimeAfter, lunchTime, lunchTimeAfter, dinnerTime, dinnerTimeAfter, bedTime)
        }

        binding.conditionLinearLayout.setOnClickListener {
            val resultBundle = Bundle()

            resultBundle.putString("sendDate", targetDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))

            setFragmentResult("todayConditionDate", resultBundle)

            mainActivity!!.gotoTodayCondition()
        }

        return binding.root
    }
    private fun init(eatingDuration: LocalTime,
                     wakeTime: LocalTime,
                     breakfastTime: LocalTime,
                     breakfastTimeAfter: LocalTime,
                     lunchTime: LocalTime,
                     lunchTimeAfter: LocalTime,
                     dinnerTime: LocalTime,
                     dinnerTimeAfter: LocalTime,
                     bedTime: LocalTime) {
        targetDay = LocalDate.now()
        yesterday = targetDay.plusDays(-1)
        tomorrow = targetDay.plusDays(1)

        updateDay(0, eatingDuration, wakeTime, breakfastTime, breakfastTimeAfter, lunchTime, lunchTimeAfter, dinnerTime, dinnerTimeAfter, bedTime)
    }

    public fun updateDay(gap: Long,
                         eatingDuration: LocalTime,
                         wakeTime: LocalTime,
                         breakfastTime: LocalTime,
                         breakfastTimeAfter: LocalTime,
                         lunchTime: LocalTime,
                         lunchTimeAfter: LocalTime,
                         dinnerTime: LocalTime,
                         dinnerTimeAfter: LocalTime,
                         bedTime: LocalTime) {

        targetDay = targetDay.plusDays(gap)
        yesterday = targetDay.plusDays(-1)
        tomorrow = targetDay.plusDays(1)

        // 미래에 대한 컨디션 입력은 불가능 하도록
        if(LocalDate.now() < targetDay) {
            binding.conditionLinearLayout.visibility = View.GONE
        }
        else {
            if(LocalDate.now() == targetDay) binding.conditionTextView.setText("오늘의 컨디션")
            else binding.conditionTextView.setText("${targetDay.monthValue}월 ${targetDay.dayOfMonth}일의 컨디션")

            binding.conditionLinearLayout.visibility = View.VISIBLE
        }

        val pref = PreferenceManager.getDefaultSharedPreferences(mainActivity)
        val editor = pref.edit()
        val serverAccessToken = pref.getString("SERVER_ACCESS_TOKEN", "")   // 엑세스 토큰

        binding.emptyAlarmLinearLayout.visibility = View.INVISIBLE

        api.getTargetDayPrescriptions(Authorization = "Bearer ${serverAccessToken}", dateTime = "${targetDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}T12:12:12").enqueue(object: Callback<MedicineRoutines> {
            override fun onResponse(call: Call<MedicineRoutines>, response: Response<MedicineRoutines>) {

                if(response.code() == 200) {
                    // 이제 적절하게 배분해서 넣어주자
//                    Log.d("log", response.toString())
                    medicineRoutines = response.body()!!
//                    Log.d("bedAfterQueryResponses", medicineRoutines.bedAfterQueryResponses.toString())
//                    Log.d("breakfastBeforeQueryResponses", medicineRoutines.breakfastBeforeQueryResponses.toString())
//                    Log.d("breakfastAfterQueryResponses", medicineRoutines.breakfastAfterQueryResponses.toString())
//                    Log.d("lunchBeforeQueryResponses", medicineRoutines.lunchBeforeQueryResponses.toString())
//                    Log.d("lunchAfterQueryResponses", medicineRoutines.lunchAfterQueryResponses.toString())
//                    Log.d("dinnerBeforeQueryResponses", medicineRoutines.dinnerBeforeQueryResponses.toString())
//                    Log.d("dinnerAfterQueryResponses", medicineRoutines.dinnerAfterQueryResponses.toString())
//                    Log.d("bedBeforeQueryResponses", medicineRoutines.bedBeforeQueryResponses.toString())

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
                        binding.emptyAlarmTextView.text = "${targetDay.monthValue}월 ${targetDay.dayOfMonth}일에\n먹을 약이 없어:약"
                        binding.emptyAlarmLinearLayout.visibility = View.VISIBLE
                    }

                    val alarmListAdapter = AlarmListAdapter(mainActivity, medicineRoutines, medicineTimeList, medicineNameList, targetDay)
                    binding.alramListView.findViewById<ListView>(R.id.alramListView)
                    binding.alramListView.adapter = alarmListAdapter
                    
                    // 초기화
                    medicineIdLists = arrayListOf<ArrayList<String>>(arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf())

                    // 오늘인 경우에만 알람을 설정하거나 초기화 (다른 날짜를 조회할 때 충돌하지 않게)
                    if (targetDay == LocalDate.now()) {
                        // 알람 로직 => 8개의 시간 별로 수행할거
                        for (position in 0..7) {
                            // 알람이 울려야 하는지 아닌지 확인
                            var isThisAlarmNeeded = true

                            // 이번 루틴에 먹어야할 약들을 추출
                            var thisRoutineMedicines = arrayListOf<medicineInRoutine>()

                            when (position) {
                                0 -> thisRoutineMedicines = medicineRoutines.bedAfterQueryResponses
                                1 -> thisRoutineMedicines = medicineRoutines.breakfastBeforeQueryResponses
                                2 -> thisRoutineMedicines = medicineRoutines.breakfastAfterQueryResponses
                                3 -> thisRoutineMedicines = medicineRoutines.lunchBeforeQueryResponses
                                4 -> thisRoutineMedicines = medicineRoutines.lunchAfterQueryResponses
                                5 -> thisRoutineMedicines = medicineRoutines.dinnerBeforeQueryResponses
                                6 -> thisRoutineMedicines = medicineRoutines.dinnerAfterQueryResponses
                                7 -> thisRoutineMedicines = medicineRoutines.bedBeforeQueryResponses
                            }

                            // 1. 오늘의 8개 알람 시간 중, 지금보다 과거라면 알람을 설정하지 않아야 한다
                            // 2. 만약 복용 체크를 했다면, 알람을 설정하지 않아야 한다
                            if (medicineTimeList[position].compareTo(LocalTime.now()) < 0) {
                                isThisAlarmNeeded = false
                            } else {
                                // 미래 시점이고, 먹을게 없다면
                                if (thisRoutineMedicines.filter{ !it.took }.isEmpty()) {
                                    isThisAlarmNeeded = false
                                }
                            }

                            val alarmManager = mainActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                            // isThisAlarmNeeded에 따라 로직을 작성하자
                            // true면 알람을 설정
                            // false면 알람을 취소 (기존에 걸려 있던게 있을 수도 있으니)
                            if (isThisAlarmNeeded) {
                                // 알람 설정
                                
                                // iotLocation의 기본값이 0인듯 => 우리는 인덱싱을 1부터 하자
                                for (medicine in thisRoutineMedicines) {
                                    medicineIdLists[position].add(medicine.id.toString())
                                }
                                val json = Gson().toJson(medicineIdLists[position])
                                editor?.putString("medicineIdList_${position}", json)?.apply()

                                // val alarmTime = LocalTime.of(시간, 분)
//                                var alarmTime = LocalTime.now().plusSeconds(5 + 2 * position.toLong())
                                var alarmTime = medicineTimeList[position]

                                // 알람 시간을 밀리초로 변환
                                val alarmDateTime = LocalDateTime.of(LocalDate.now(), alarmTime)
                                val AlarmMillis = alarmDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

                                when (position) {
                                    0 -> {
                                        val ZerothAlarmIntent = Intent(mainActivity, ZerothAlarmReceiver::class.java)
                                        val ZerothAlarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(mainActivity, position, ZerothAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
                                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, AlarmMillis, ZerothAlarmPendingIntent)
                                    }
                                    1 -> {
                                        val FirstAlarmIntent = Intent(mainActivity, FirstAlarmReceiver::class.java)
                                        val FirstAlarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(mainActivity, position, FirstAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
                                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, AlarmMillis, FirstAlarmPendingIntent)
                                    }
                                    2 -> {
                                        val SecondAlarmIntent = Intent(mainActivity, SecondAlarmReceiver::class.java)
                                        val SecondAlarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(mainActivity, position, SecondAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
                                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, AlarmMillis, SecondAlarmPendingIntent)
                                    }
                                    3 -> {
                                        val ThirdAlarmIntent = Intent(mainActivity, ThirdAlarmReceiver::class.java)
                                        val ThirdAlarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(mainActivity, position, ThirdAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
                                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, AlarmMillis, ThirdAlarmPendingIntent)
                                    }
                                    4 -> {
                                        val FourthAlarmIntent = Intent(mainActivity, FourthAlarmReceiver::class.java)
                                        val FourthAlarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(mainActivity, position, FourthAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
                                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, AlarmMillis, FourthAlarmPendingIntent)
                                    }
                                    5 -> {
                                        val FifthAlarmIntent = Intent(mainActivity, FifthAlarmReceiver::class.java)
                                        val FifthAlarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(mainActivity, position, FifthAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
                                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, AlarmMillis, FifthAlarmPendingIntent)
                                    }
                                    6 -> {
                                        val SixthAlarmIntent = Intent(mainActivity, SixthAlarmReceiver::class.java)
                                        val SixthAlarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(mainActivity, position, SixthAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
                                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, AlarmMillis, SixthAlarmPendingIntent)
                                    }
                                    7 -> {
                                        val SeventhAlarmIntent = Intent(mainActivity, SeventhAlarmReceiver::class.java)
                                        val SeventhAlarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(mainActivity, position, SeventhAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
                                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, AlarmMillis, SeventhAlarmPendingIntent)
                                    }
                                }
                            } else {
                                // 알람 취소
                                when (position) {
                                    0 -> {
                                        val ZerothAlarmIntent = Intent(mainActivity, ZerothAlarmReceiver::class.java)
                                        val ZerothAlarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(mainActivity, position, ZerothAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
                                        alarmManager.cancel(ZerothAlarmPendingIntent)
                                    }
                                    1 -> {
                                        val FirstAlarmIntent = Intent(mainActivity, FirstAlarmReceiver::class.java)
                                        val FirstAlarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(mainActivity, position, FirstAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
                                        alarmManager.cancel(FirstAlarmPendingIntent)
                                    }
                                    2 -> {
                                        val SecondAlarmIntent = Intent(mainActivity, SecondAlarmReceiver::class.java)
                                        val SecondAlarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(mainActivity, position, SecondAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
                                        alarmManager.cancel(SecondAlarmPendingIntent)
                                    }
                                    3 -> {
                                        val ThirdAlarmIntent = Intent(mainActivity, ThirdAlarmReceiver::class.java)
                                        val ThirdAlarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(mainActivity, position, ThirdAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
                                        alarmManager.cancel(ThirdAlarmPendingIntent)
                                    }
                                    4 -> {
                                        val FourthAlarmIntent = Intent(mainActivity, FourthAlarmReceiver::class.java)
                                        val FourthAlarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(mainActivity, position, FourthAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
                                        alarmManager.cancel(FourthAlarmPendingIntent)
                                    }
                                    5 -> {
                                        val FifthAlarmIntent = Intent(mainActivity, FifthAlarmReceiver::class.java)
                                        val FifthAlarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(mainActivity, position, FifthAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
                                        alarmManager.cancel(FifthAlarmPendingIntent)
                                    }
                                    6 -> {
                                        val SixthAlarmIntent = Intent(mainActivity, SixthAlarmReceiver::class.java)
                                        val SixthAlarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(mainActivity, position, SixthAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
                                        alarmManager.cancel(SixthAlarmPendingIntent)
                                    }
                                    7 -> {
                                        val SeventhAlarmIntent = Intent(mainActivity, SeventhAlarmReceiver::class.java)
                                        val SeventhAlarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(mainActivity, position, SeventhAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
                                        alarmManager.cancel(SeventhAlarmPendingIntent)
                                    }
                                }
                            }
                        }
                    }

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
//                Log.d("log", response.toString())
//                Log.d("log", response.body().toString())
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
                        binding.todayState.setColorFilter(Color.parseColor("#FFFFEF00"))
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
                        binding.tomorrowState.setColorFilter(Color.parseColor("#FFFFEF00"))
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

    private lateinit var callback: OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // 2. Context를 액티비티로 형변환해서 할당
        mainActivity = context as MainActivity

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do something

                if (backPressedOnce) {
                    requireActivity().finishAffinity()
                    return
                }
                backPressedOnce = true

                GlobalScope.launch {
                    delay(doubleClickInterval)
                    backPressedOnce = false
                }

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }


}

class ZerothAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // 화면 활성화 및 잠금 화면 해제
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyApp:MyWakelockTag")
        wakeLock.acquire(5000) // 화면을 5초 동안 활성화

        val vibrator = context.getSystemService(Vibrator::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (vibrator?.hasVibrator() == true) {
                val vibrationEffect = VibrationEffect.createOneShot(3000, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(vibrationEffect)
            }
        }

        //val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) // 기본 알림 소리
        val soundUri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.alarmsound)

        val alarmIntent = Intent(context, LoginActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 10, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

        val notification = NotificationCompat.Builder(context, "alarm_channel")
            .setContentTitle("지금이:약")
            .setContentText("기상 후 약 드세요")
            .setSmallIcon(R.drawable.eyak_logo) // 알림 아이콘 설정
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(0, notification) // 알림 표시

        // pref
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val deviceNameSaved = pref?.getString("DEVICE_NAME", "")

        val soundData = pref?.getBoolean("DEVICE_SOUND", true)
        val buzzData = pref?.getBoolean("DEVICE_BUZZ", false)

        var gson = Gson()
        var json = pref?.getString("DEVICE_CELL1", "")
        val cell1Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL2", "")
        val cell2Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL3", "")
        val cell3Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL4", "")
        val cell4Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL5", "")
        val cell5Data = gson.fromJson(json, Medicine::class.java)

        // 보낼 코드를 이어붙이자
        var codeToSend = "01"
        codeToSend += if (soundData!!) "1" else "0"
        codeToSend += if (buzzData!!) "1" else "0"

        json = pref?.getString("medicineIdList_0", "")
        val thisRoutineMedicineIdList = gson.fromJson(json, ArrayList<String>()::class.java)

        codeToSend += if (cell1Data != null && cell1Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell2Data != null && cell2Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell3Data != null && cell3Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell4Data != null && cell4Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell5Data != null && cell5Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"

        val intent = Intent(context, ForeService::class.java)
        intent.putExtra("SEND_KEY", codeToSend)
        intent.putExtra("DEVICE_NAME_KEY", deviceNameSaved)
        context.startForegroundService(intent)
    }
}

class FirstAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // 화면 활성화 및 잠금 화면 해제
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyApp:MyWakelockTag")
        wakeLock.acquire(5000) // 화면을 5초 동안 활성화

        val vibrator = context.getSystemService(Vibrator::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (vibrator?.hasVibrator() == true) {
                val vibrationEffect = VibrationEffect.createOneShot(3000, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(vibrationEffect)
            }
        }

        //val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) // 기본 알림 소리
        val soundUri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.alarmsound)

        val alarmIntent = Intent(context, LoginActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 11, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)



        val notification = NotificationCompat.Builder(context, "alarm_channel")
            .setContentTitle("지금이:약")
            .setContentText("아침 식사 전 약 드세요")
            .setSmallIcon(R.drawable.eyak_logo) // 알림 아이콘 설정
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(1, notification) // 알림 표시

        // pref
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val deviceNameSaved = pref?.getString("DEVICE_NAME", "")

        val soundData = pref?.getBoolean("DEVICE_SOUND", true)
        val buzzData = pref?.getBoolean("DEVICE_BUZZ", false)

        var gson = Gson()
        var json = pref?.getString("DEVICE_CELL1", "")
        val cell1Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL2", "")
        val cell2Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL3", "")
        val cell3Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL4", "")
        val cell4Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL5", "")
        val cell5Data = gson.fromJson(json, Medicine::class.java)

        // 보낼 코드를 이어붙이자
        var codeToSend = "01"
        codeToSend += if (soundData!!) "1" else "0"
        codeToSend += if (buzzData!!) "1" else "0"

        json = pref?.getString("medicineIdList_1", "")
        val thisRoutineMedicineIdList = gson.fromJson(json, ArrayList<String>()::class.java)

        codeToSend += if (cell1Data != null && cell1Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell2Data != null && cell2Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell3Data != null && cell3Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell4Data != null && cell4Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell5Data != null && cell5Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"

        val intent = Intent(context, ForeService::class.java)
        intent.putExtra("SEND_KEY", codeToSend)
        intent.putExtra("DEVICE_NAME_KEY", deviceNameSaved)
        context.startForegroundService(intent)
    }
}

class SecondAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // 화면 활성화 및 잠금 화면 해제
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyApp:MyWakelockTag")
        wakeLock.acquire(5000) // 화면을 5초 동안 활성화

        val vibrator = context.getSystemService(Vibrator::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (vibrator?.hasVibrator() == true) {
                val vibrationEffect = VibrationEffect.createOneShot(3000, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(vibrationEffect)
            }
        }

        //val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) // 기본 알림 소리
        val soundUri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.alarmsound)

        val alarmIntent = Intent(context, LoginActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 12, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)



        val notification = NotificationCompat.Builder(context, "alarm_channel")
            .setContentTitle("지금이:약")
            .setContentText("아침 식사 후 약 드세요")
            .setSmallIcon(R.drawable.eyak_logo) // 알림 아이콘 설정
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(2, notification) // 알림 표시

        // pref
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val deviceNameSaved = pref?.getString("DEVICE_NAME", "")

        val soundData = pref?.getBoolean("DEVICE_SOUND", true)
        val buzzData = pref?.getBoolean("DEVICE_BUZZ", false)

        var gson = Gson()
        var json = pref?.getString("DEVICE_CELL1", "")
        val cell1Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL2", "")
        val cell2Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL3", "")
        val cell3Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL4", "")
        val cell4Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL5", "")
        val cell5Data = gson.fromJson(json, Medicine::class.java)

        // 보낼 코드를 이어붙이자
        var codeToSend = "01"
        codeToSend += if (soundData!!) "1" else "0"
        codeToSend += if (buzzData!!) "1" else "0"

        json = pref?.getString("medicineIdList_2", "")
        val thisRoutineMedicineIdList = gson.fromJson(json, ArrayList<String>()::class.java)

        codeToSend += if (cell1Data != null && cell1Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell2Data != null && cell2Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell3Data != null && cell3Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell4Data != null && cell4Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell5Data != null && cell5Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"

        val intent = Intent(context, ForeService::class.java)
        intent.putExtra("SEND_KEY", codeToSend)
        intent.putExtra("DEVICE_NAME_KEY", deviceNameSaved)
        context.startForegroundService(intent)
    }
}

class ThirdAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // 화면 활성화 및 잠금 화면 해제
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyApp:MyWakelockTag")
        wakeLock.acquire(5000) // 화면을 5초 동안 활성화

        val vibrator = context.getSystemService(Vibrator::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (vibrator?.hasVibrator() == true) {
                val vibrationEffect = VibrationEffect.createOneShot(3000, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(vibrationEffect)
            }
        }

        //val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) // 기본 알림 소리
        val soundUri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.alarmsound)

        val alarmIntent = Intent(context, LoginActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 13, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)



        val notification = NotificationCompat.Builder(context, "alarm_channel")
            .setContentTitle("지금이:약")
            .setContentText("점심 식사 전 약 드세요")
            .setSmallIcon(R.drawable.eyak_logo) // 알림 아이콘 설정
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(3, notification) // 알림 표시

        // pref
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val deviceNameSaved = pref?.getString("DEVICE_NAME", "")

        val soundData = pref?.getBoolean("DEVICE_SOUND", true)
        val buzzData = pref?.getBoolean("DEVICE_BUZZ", false)

        var gson = Gson()
        var json = pref?.getString("DEVICE_CELL1", "")
        val cell1Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL2", "")
        val cell2Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL3", "")
        val cell3Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL4", "")
        val cell4Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL5", "")
        val cell5Data = gson.fromJson(json, Medicine::class.java)

        // 보낼 코드를 이어붙이자
        var codeToSend = "01"
        codeToSend += if (soundData!!) "1" else "0"
        codeToSend += if (buzzData!!) "1" else "0"

        json = pref?.getString("medicineIdList_3", "")
        val thisRoutineMedicineIdList = gson.fromJson(json, ArrayList<String>()::class.java)

        codeToSend += if (cell1Data != null && cell1Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell2Data != null && cell2Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell3Data != null && cell3Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell4Data != null && cell4Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell5Data != null && cell5Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"

        val intent = Intent(context, ForeService::class.java)
        intent.putExtra("SEND_KEY", codeToSend)
        intent.putExtra("DEVICE_NAME_KEY", deviceNameSaved)
        context.startForegroundService(intent)
    }
}

class FourthAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // 화면 활성화 및 잠금 화면 해제
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyApp:MyWakelockTag")
        wakeLock.acquire(5000) // 화면을 5초 동안 활성화

        val vibrator = context.getSystemService(Vibrator::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (vibrator?.hasVibrator() == true) {
                val vibrationEffect = VibrationEffect.createOneShot(3000, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(vibrationEffect)
            }
        }

        //val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) // 기본 알림 소리
        val soundUri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.alarmsound)

        val alarmIntent = Intent(context, LoginActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 14, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)



        val notification = NotificationCompat.Builder(context, "alarm_channel")
            .setContentTitle("지금이:약")
            .setContentText("점심 식사 후 약 드세요")
            .setSmallIcon(R.drawable.eyak_logo) // 알림 아이콘 설정
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(4, notification) // 알림 표시

        // pref
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val deviceNameSaved = pref?.getString("DEVICE_NAME", "")

        val soundData = pref?.getBoolean("DEVICE_SOUND", true)
        val buzzData = pref?.getBoolean("DEVICE_BUZZ", false)

        var gson = Gson()
        var json = pref?.getString("DEVICE_CELL1", "")
        val cell1Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL2", "")
        val cell2Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL3", "")
        val cell3Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL4", "")
        val cell4Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL5", "")
        val cell5Data = gson.fromJson(json, Medicine::class.java)

        // 보낼 코드를 이어붙이자
        var codeToSend = "01"
        codeToSend += if (soundData!!) "1" else "0"
        codeToSend += if (buzzData!!) "1" else "0"

        json = pref?.getString("medicineIdList_4", "")
        val thisRoutineMedicineIdList = gson.fromJson(json, ArrayList<String>()::class.java)

        codeToSend += if (cell1Data != null && cell1Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell2Data != null && cell2Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell3Data != null && cell3Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell4Data != null && cell4Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell5Data != null && cell5Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"

        val intent = Intent(context, ForeService::class.java)
        intent.putExtra("SEND_KEY", codeToSend)
        intent.putExtra("DEVICE_NAME_KEY", deviceNameSaved)
        context.startForegroundService(intent)
    }
}

class FifthAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // 화면 활성화 및 잠금 화면 해제
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyApp:MyWakelockTag")
        wakeLock.acquire(5000) // 화면을 5초 동안 활성화

        val vibrator = context.getSystemService(Vibrator::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (vibrator?.hasVibrator() == true) {
                val vibrationEffect = VibrationEffect.createOneShot(3000, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(vibrationEffect)
            }
        }

        //val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) // 기본 알림 소리
        val soundUri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.alarmsound)

        val alarmIntent = Intent(context, LoginActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 15, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)



        val notification = NotificationCompat.Builder(context, "alarm_channel")
            .setContentTitle("지금이:약")
            .setContentText("저녁 식사 전 약 드세요")
            .setSmallIcon(R.drawable.eyak_logo) // 알림 아이콘 설정
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(5, notification) // 알림 표시

        // pref
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val deviceNameSaved = pref?.getString("DEVICE_NAME", "")

        val soundData = pref?.getBoolean("DEVICE_SOUND", true)
        val buzzData = pref?.getBoolean("DEVICE_BUZZ", false)

        var gson = Gson()
        var json = pref?.getString("DEVICE_CELL1", "")
        val cell1Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL2", "")
        val cell2Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL3", "")
        val cell3Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL4", "")
        val cell4Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL5", "")
        val cell5Data = gson.fromJson(json, Medicine::class.java)

        // 보낼 코드를 이어붙이자
        var codeToSend = "01"
        codeToSend += if (soundData!!) "1" else "0"
        codeToSend += if (buzzData!!) "1" else "0"

        json = pref?.getString("medicineIdList_5", "")
        val thisRoutineMedicineIdList = gson.fromJson(json, ArrayList<String>()::class.java)

        codeToSend += if (cell1Data != null && cell1Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell2Data != null && cell2Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell3Data != null && cell3Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell4Data != null && cell4Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell5Data != null && cell5Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"

        val intent = Intent(context, ForeService::class.java)
        intent.putExtra("SEND_KEY", codeToSend)
        intent.putExtra("DEVICE_NAME_KEY", deviceNameSaved)
        context.startForegroundService(intent)
    }
}

class SixthAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // 화면 활성화 및 잠금 화면 해제
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyApp:MyWakelockTag")
        wakeLock.acquire(5000) // 화면을 5초 동안 활성화

        val vibrator = context.getSystemService(Vibrator::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (vibrator?.hasVibrator() == true) {
                val vibrationEffect = VibrationEffect.createOneShot(3000, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(vibrationEffect)
            }
        }

        //val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) // 기본 알림 소리
        val soundUri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.alarmsound)

        val alarmIntent = Intent(context, LoginActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 16, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)



        val notification = NotificationCompat.Builder(context, "alarm_channel")
            .setContentTitle("지금이:약")
            .setContentText("저녁 식사 후 약 드세요")
            .setSmallIcon(R.drawable.eyak_logo) // 알림 아이콘 설정
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(6, notification) // 알림 표시

        // pref
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val deviceNameSaved = pref?.getString("DEVICE_NAME", "")

        val soundData = pref?.getBoolean("DEVICE_SOUND", true)
        val buzzData = pref?.getBoolean("DEVICE_BUZZ", false)

        var gson = Gson()
        var json = pref?.getString("DEVICE_CELL1", "")
        val cell1Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL2", "")
        val cell2Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL3", "")
        val cell3Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL4", "")
        val cell4Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL5", "")
        val cell5Data = gson.fromJson(json, Medicine::class.java)

        // 보낼 코드를 이어붙이자
        var codeToSend = "01"
        codeToSend += if (soundData!!) "1" else "0"
        codeToSend += if (buzzData!!) "1" else "0"

        json = pref?.getString("medicineIdList_6", "")
        val thisRoutineMedicineIdList = gson.fromJson(json, ArrayList<String>()::class.java)

        codeToSend += if (cell1Data != null && cell1Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell2Data != null && cell2Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell3Data != null && cell3Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell4Data != null && cell4Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell5Data != null && cell5Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"

        val intent = Intent(context, ForeService::class.java)
        intent.putExtra("SEND_KEY", codeToSend)
        intent.putExtra("DEVICE_NAME_KEY", deviceNameSaved)
        context.startForegroundService(intent)
    }
}

class SeventhAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "Alarm Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // 화면 활성화 및 잠금 화면 해제
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyApp:MyWakelockTag")
        wakeLock.acquire(5000) // 화면을 5초 동안 활성화

        val vibrator = context.getSystemService(Vibrator::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (vibrator?.hasVibrator() == true) {
                val vibrationEffect = VibrationEffect.createOneShot(3000, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(vibrationEffect)
            }
        }

        //val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) // 기본 알림 소리
        val soundUri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.alarmsound)

        val alarmIntent = Intent(context, LoginActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 17, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

        val notification = NotificationCompat.Builder(context, "alarm_channel")
            .setContentTitle("지금이:약")
            .setContentText("취침 전 약 드세요")
            .setSmallIcon(R.drawable.eyak_logo) // 알림 아이콘 설정
            .setSound(soundUri)
            .setContentIntent(pendingIntent) // 알림 클릭 시 PendingIntent 실행
            .build()

        notificationManager.notify(7, notification) // 알림 표시

        // pref
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val deviceNameSaved = pref?.getString("DEVICE_NAME", "")

        val soundData = pref?.getBoolean("DEVICE_SOUND", true)
        val buzzData = pref?.getBoolean("DEVICE_BUZZ", false)

        var gson = Gson()
        var json = pref?.getString("DEVICE_CELL1", "")
        val cell1Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL2", "")
        val cell2Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL3", "")
        val cell3Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL4", "")
        val cell4Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL5", "")
        val cell5Data = gson.fromJson(json, Medicine::class.java)

        // 보낼 코드를 이어붙이자
        var codeToSend = "01"
        codeToSend += if (soundData!!) "1" else "0"
        codeToSend += if (buzzData!!) "1" else "0"

        json = pref?.getString("medicineIdList_7", "")
        val thisRoutineMedicineIdList = gson.fromJson(json, ArrayList<String>()::class.java)

        codeToSend += if (cell1Data != null && cell1Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell2Data != null && cell2Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell3Data != null && cell3Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell4Data != null && cell4Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"
        codeToSend += if (cell5Data != null && cell5Data.id.toString() in thisRoutineMedicineIdList) "1" else "0"

        val intent = Intent(context, ForeService::class.java)
        intent.putExtra("SEND_KEY", codeToSend)
        intent.putExtra("DEVICE_NAME_KEY", deviceNameSaved)
        context.startForegroundService(intent)
    }
}
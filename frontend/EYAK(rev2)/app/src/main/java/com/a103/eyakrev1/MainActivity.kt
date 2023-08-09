package com.a103.eyakrev1

import androidx.core.app.NotificationCompat
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.content.ContextCompat.getSystemService
import com.a103.eyakrev1.databinding.ActivityMainBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmReceiver::class.java)

        //val alarmTime = LocalTime.of(12, 21) // 10시 30분
        val alarmTime = LocalTime.now().plusSeconds(10)

// 현재 날짜와 선택한 시간을 조합하여 LocalDateTime 생성
        val currentDate = LocalDate.now()

        val alarmDateTime = LocalDateTime.of(currentDate, alarmTime)

// 알람 시간을 밀리초로 변환
        val alarmTimeMillis = alarmDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE)

// 알람 설정
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeMillis, pendingIntent)


        initPage()

        binding.accountSetting.setOnClickListener {
            accountSettingClick()
        }

        binding.alarmTab.setOnClickListener {
            alarmTabClick()
        }

        binding.medicineTab.setOnClickListener {
            medicineTabClick()
        }

        binding.familyTab.setOnClickListener {
            familyTabClick()
        }

        binding.deviceTab.setOnClickListener {
            deviceTabClick()
        }

    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }

    private fun initPage() {
        alarmTabClick()
    }
    private fun accountSettingClick() {
        val intent = Intent(this, EditMemberActivity::class.java)
        startActivity(intent)
    }

    private fun alarmTabClick() {
        binding.titleView.text = "알람이:약"
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragment, AlarmFragment())
            .commit()
        changeTabColor(0)
    }

    private fun medicineTabClick() {
        binding.titleView.text = "약이:약"
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragment, MedicineFragment())
            .commit()
        changeTabColor(1)
    }

    private fun familyTabClick() {
        binding.titleView.text = "가족이:약"
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragment, FamilyFragment())
            .commit()
        changeTabColor(2)
    }

    private fun deviceTabClick() {
        binding.titleView.text = "약통이:약"
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragment, DeviceRegisterFragment())
            .commit()
        changeTabColor(3)
    }

    private fun changeTabColor(index: Int) {
        val colorOn = "#FF757863"
        val colorOff = "#FFC9DBB2"

        binding.alarmTab.setColorFilter(Color.parseColor(if(index == 0) colorOn else colorOff))
        binding.medicineTab.setColorFilter(Color.parseColor(if(index == 1) colorOn else colorOff))
        binding.familyTab.setColorFilter(Color.parseColor(if(index == 2) colorOn else colorOff))
        binding.deviceTab.setColorFilter(Color.parseColor(if(index == 3) colorOn else colorOff))
    }

    fun gotoTodayCondition() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragment, TodayConditionFragment())
            .commit()
    }

    fun gotoAlarm() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragment, AlarmFragment())
            .commit()
    }

    public fun gotoMedicineDetail() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainFragment, MedicineDetailFragment()).commit()
    }

    public fun gotoEditFamily() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainFragment, FamilyEditFragment())
            .commit()
    }

    public fun gotoAddFamily() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainFragment, FamilyAddFragment())
            .commit()
    }

    public fun gotoFamily() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainFragment, FamilyFragment())
            .commit()
    }

    public fun gotoAddMedicine() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainFragment, MedicineAddFragment())
            .commit()
    }

    public fun gotoAddMedicineResult() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainFragment, MedicineAddResultFragment())
            .commit()
    }

    public fun gotoMedicine() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainFragment, MedicineFragment())
            .commit()
    }

    public fun gotoAcceptFamily() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainFragment, FamilyAcceptFragment())
            .commit()
    }

    public fun gotoMyCalendar() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainFragment, MyCalendarFragment())
            .commit()
    }
}

class AlarmReceiver : BroadcastReceiver() {
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

        Log.d("ㅁㅁㅁㅁㅁ", "알람이당")

        val notification = NotificationCompat.Builder(context, "alarm_channel")
            .setContentTitle("알람")
            .setContentText("알람이 울렸습니다.")
            .setSmallIcon(R.drawable.baseline_check_box_24) // 알림 아이콘 설정
            .build()

        notificationManager.notify(1, notification) // 알림 표시

    }
}
package com.a103.eyakrev1

import androidx.core.app.NotificationCompat
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
import android.content.Context.VIBRATOR_SERVICE
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.preference.PreferenceManager
import com.a103.eyakrev1.databinding.ActivityMainBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private val api = EyakService.create()

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val serverAccessToken = pref.getString("SERVER_ACCESS_TOKEN", "")   // 엑세스 토큰

//        val firstAlarmIntent: Intent = Intent(this, FirstAlarmReceiver::class.java)
//        val firstPendingIntent: PendingIntent = PendingIntent.getBroadcast(this, 0, firstAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
//
//        val secondAlarmIntent: Intent = Intent(this, SecondAlarmReceiver::class.java)
//        val secondPendingIntent: PendingIntent = PendingIntent.getBroadcast(this, 1, secondAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
//
//
//        //val alarmTime = LocalTime.of(시간, 분)
//        var alarmTime = LocalTime.now().plusSeconds(10)
//
//         // 알람 시간을 밀리초로 변환
//        val firstDateTime = LocalDateTime.of(LocalDate.now(), alarmTime)
//        val firstAlarmMillis = firstDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
//
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, firstAlarmMillis, firstPendingIntent)
//
//        alarmTime = LocalTime.now().plusSeconds(20)
//
////         알람 설정
//        val secondDateTime = LocalDateTime.of(LocalDate.now(), alarmTime)
//        val secondAlarmMillis = secondDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
//
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, secondAlarmMillis, secondPendingIntent)



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

//    override fun onBackPressed() {
////        super.onBackPressed()
//    }

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

    public fun gotoMedicineSearch() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainFragment, MedicineSearchFragment())
            .commit()
    }
}
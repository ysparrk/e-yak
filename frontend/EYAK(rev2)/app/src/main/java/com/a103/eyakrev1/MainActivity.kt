package com.a103.eyakrev1

import androidx.core.app.NotificationCompat
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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

    //=== bluetooth 연결용 var
    var btPermissionFlag: Boolean? = null
    var btConnectFlag = false
    var deviceNameSaved: String? = null
    var deviceSaved: BluetoothDevice? = null
    var socket: BluetoothSocket? = null
    var fallbackSocket: BluetoothSocket? = null
    // bluetooth Manager & Adapter
    private val btManager: BluetoothManager by lazy { this.getSystemService(BluetoothManager::class.java) }
    private val btAdapter: BluetoothAdapter? by lazy { btManager.getAdapter() }

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

        //=== bluetooth 연결
        // 블투 어뎁터 init
        if (btAdapter == null) {
            Toast.makeText(this, "이 기기에서 블루투스를 지원하지 않음", Toast.LENGTH_LONG).show()
        }
        // 권한 체크
        val permissions = arrayOf(
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.BLUETOOTH_SCAN,
        )
        btPermissionFlag = checkPermissions(permissions)
        // pref 저장된 페어링 약통 이름 가져오기
        deviceNameSaved = pref?.getString("DEVICE_NAME", "")
        // 권한 상태에 따라 페어링된 약통 불러오기
        if (deviceNameSaved != "") {
            if (btPermissionFlag == true && btAdapter != null) {
                if (btAdapter?.isEnabled == false) {
                    Toast.makeText(this, "약통과의 통신을 위해 블루투스를 켜주세요.", Toast.LENGTH_SHORT).show()
                } else if (bluetoothPaired() == false) {
                    Toast.makeText(this, "약통과 연결이 끊어졌습니다. 약통을 다시 등록해주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    connectDevice(deviceSaved) // 권한, 저장된 정보 모두 ok 일시 - 연결 시도.
                }
            } else if (btPermissionFlag == false && btAdapter != null) {
                Toast.makeText(this, "권한이 허용되지 않았습니다. 약통이 정상적으로 동작하지 않을 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }



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

    public fun gotoMedicineSearch() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainFragment, MedicineSearchFragment())
            .commit()
    }

    //=== bluetooth 연결 관련 함수들
    // bluetooth 권한 체크
    private fun checkPermissions(perms: Array<String>): Boolean {
        var flag = true
        for (p in perms) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                flag = false
                break
            }
        }
        return flag
    }
    // 페어링된 블루투스 기기 찾기.
    private fun bluetoothPaired(): Boolean {
        val pairedDevices: Set<BluetoothDevice>? = btAdapter?.bondedDevices
        var devicePairedFlag = false
        pairedDevices?.forEach { device ->
            if (device.name == deviceNameSaved) {
                deviceSaved = device
                devicePairedFlag = true
                return@forEach
            }
        }
        return devicePairedFlag
    }
    // 블루투스 연결용 스레드
    private fun connectDevice(targetDevice: BluetoothDevice?) {
        val thread = Thread {
            val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
            socket = targetDevice?.createRfcommSocketToServiceRecord(uuid)
            var clazz = socket?.remoteDevice?.javaClass
            var paramTypes = arrayOf<Class<*>>(Integer.TYPE)
            var m = clazz?.getMethod("createRfcommSocket", *paramTypes)
            fallbackSocket = m?.invoke(socket?.remoteDevice, Integer.valueOf(1)) as BluetoothSocket?
            try {
                fallbackSocket?.connect()
            } catch (e: Exception) {
                try {
                    socket?.close()
                    fallbackSocket?.close()
                } catch (e: IOException) {}
            }
        }
        thread.start()
    }
}

//class FirstAlarmReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                "alarm_channel",
//                "Alarm Channel",
//                NotificationManager.IMPORTANCE_HIGH
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        // 화면 활성화 및 잠금 화면 해제
//        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
//        val wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyApp:MyWakelockTag")
//        wakeLock.acquire(5000) // 화면을 5초 동안 활성화
//
//        val vibrator = context.getSystemService(Vibrator::class.java)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            if (vibrator?.hasVibrator() == true) {
//                val vibrationEffect = VibrationEffect.createOneShot(3000, VibrationEffect.DEFAULT_AMPLITUDE)
//                vibrator.vibrate(vibrationEffect)
//            }
//        }
//
//        //val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) // 기본 알림 소리
//        val soundUri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.alarmsound)
//
//
//        val notification = NotificationCompat.Builder(context, "alarm_channel")
//            .setContentTitle("첫 번째 알람")
//            .setContentText("알람이 울렸습니다.")
//            .setSmallIcon(R.drawable.eyak_logo) // 알림 아이콘 설정
//            .setSound(soundUri)
//            .build()
//
//        notificationManager.notify(0, notification) // 알림 표시
//    }
//}

//class SecondAlarmReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//
//        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        val notification = NotificationCompat.Builder(context, "alarm_channel")
//            .setContentTitle("두 번째 알람")
//            .setContentText("알람이 울렸습니다.")
//            .setSmallIcon(R.drawable.baseline_check_box_24) // 알림 아이콘 설정
//            .build()
//
//        notificationManager.notify(1, notification) // 알림 표시
//    }
//}


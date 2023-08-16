package com.example.foregroundtest

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.UUID

class SampleForegroundService : Service() {
    private val CHANNEL_ID = "Foreground Test"

    //=== bluetooth 연결용 var
    var btPermissionFlag: Boolean? = null
    var btConnectFlag = false
    var deviceNameSaved: String? = null
    var deviceSaved: BluetoothDevice? = null
    var socket: BluetoothSocket? = null
    var fallbackSocket: BluetoothSocket? = null
    // bluetooth Manager & Adapter
    val btManager: BluetoothManager by lazy { this.getSystemService(BluetoothManager::class.java) }
    val btAdapter: BluetoothAdapter? by lazy { btManager.getAdapter() }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        val notification: Notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Test Title")
            .setContentText("Foreground Test Content")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()

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
        deviceNameSaved = "ESP32-BT-TEST_2"
        // 권한 상태에 따라 페어링된 약통 불러오기
        if (btPermissionFlag == true && btAdapter != null) {
            if (bluetoothPaired()) {
                connectDevice(deviceSaved) // 권한, 저장된 정보 모두 ok 일시 - 연결 시도.
            }
        }

        startForeground(1, notification)
//        if (intent?.action != null && intent.action.equals("", ignoreCase = true)) {
//            stopForeground(true)
//            stopSelf()
//        }
//        generateNotification()
        return START_NOT_STICKY
//        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT)
        val manager = getSystemService(NotificationManager::class.java)
        manager!!.createNotificationChannel(serviceChannel)
    }



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

    // notification
//    private var iconNotification: Bitmap? = null
//    private var notification: Notification? = null
//    var mNotificationManager: NotificationManager? = null
//    private val mNotificationId = 123
//
//    private fun generateNotification() {
//        val intentMainLanding = Intent(this, MainActivity::class.java)
//        val pendingIntent =
//            PendingIntent.getActivity(this, 0, intentMainLanding, 0)
//        iconNotification = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
//        if (mNotificationManager == null) {
//            mNotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            assert(mNotificationManager != null)
//            mNotificationManager?.createNotificationChannelGroup(
//                NotificationChannelGroup("chats_group", "Chats")
//            )
//            val notificationChannel =
//                NotificationChannel("service_channel", "Service Notifications",
//                    NotificationManager.IMPORTANCE_MIN)
//            notificationChannel.enableLights(false)
//            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
//            mNotificationManager?.createNotificationChannel(notificationChannel)
//        }
//        val builder = NotificationCompat.Builder(this, "service_channel")
//
//        builder.setContentTitle(StringBuilder(resources.getString(R.string.app_name)).append(" service is running").toString())
//            .setTicker(StringBuilder(resources.getString(R.string.app_name)).append("service is running").toString())
//            .setContentText("Touch to open") //                    , swipe down for more options.
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setPriority(NotificationCompat.PRIORITY_LOW)
//            .setWhen(0)
//            .setOnlyAlertOnce(true)
//            .setContentIntent(pendingIntent)
//            .setOngoing(true)
//        if (iconNotification != null) {
//            builder.setLargeIcon(Bitmap.createScaledBitmap(iconNotification!!, 128, 128, false))
//        }
//        builder.color = resources.getColor(R.color.black)
//        notification = builder.build()
//        startForeground(mNotificationId, notification)
//    }
}
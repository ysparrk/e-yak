package com.a103.eyakrev1

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
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class ForeService : Service() {
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
    // 송수신 string & stream
    var outStream: OutputStream? = null
    var inStream: InputStream? = null
    var sendString: String? = null
    var recvString: String? = null
    // pref
//    val pref = PreferenceManager.getDefaultSharedPreferences(this)
//    val editor = pref?.edit()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        val notification: Notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("블루투스")
            .setContentText("통신중")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
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
        // 필요정보 가져오기
        deviceNameSaved = intent?.getStringExtra("DEVICE_NAME_KEY")
        sendString = intent?.getStringExtra("SEND_KEY")
        // 리시버 등록
        val filter_connect = IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED)
        registerReceiver(receiver, filter_connect)
        val filter_disconnect = IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        registerReceiver(receiver, filter_disconnect)
        // 권한 상태에 따라 페어링된 약통 불러오기
        if (btPermissionFlag == true && btAdapter != null && deviceNameSaved != "") {
            if (bluetoothPaired()) {
                if (btConnectFlag == false) {
                    connectDevice(deviceSaved) // 권한, 저장된 정보 모두 ok 일시 - 연결 시도.
                } else {
                    Thread.sleep(1_000)
                    outStream?.write(sendString?.toByteArray())
                }
            }
        }

        startForeground(1, notification)
        return START_NOT_STICKY
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

    // 연결 유무 처리
    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action: String? = intent?.action
            when(action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    inStream = fallbackSocket?.inputStream
                    outStream = fallbackSocket?.outputStream
                    btConnectFlag = true
                    Thread {
                        try {
                            Thread.sleep(1_000)
                            outStream?.write(sendString?.toByteArray())
                        } catch (e: IOException) {}
                    }.start()
                    Thread {
                        var tim = 0
                        while (tim < 90) {
                            Thread.sleep(1000)
                            tim++
                        }
                        socket!!.close()
                        fallbackSocket!!.close()
                        stopForeground(true)
                        stopSelf()
                    }.start()
                    Thread {
                        while (!Thread.currentThread().isInterrupted) {
                            try {
                                val inBytes = inStream?.available()
                                if (inBytes != null) {
                                    if (inBytes > 0) {
                                        val packetBytes = ByteArray(inBytes)
                                        inStream?.read(packetBytes)
                                        recvString = packetBytes.toString(Charsets.UTF_8)
                                        // 수신 string 분석
                                        if (recvString!!.substring(0,2) == "ok") {
                                            socket!!.close()
                                            fallbackSocket!!.close()
                                            stopForeground(true)
                                            stopSelf()
                                        } else if (recvString!!.substring(0,2) == "no") {
                                            socket!!.close()
                                            fallbackSocket!!.close()
                                            stopForeground(true)
                                            stopSelf()
                                        }
                                    }
                                }
                            } catch (e: java.lang.Exception) {}
                        }
                    }.start()
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    btConnectFlag = false

                }
            }
        }
    }
    // on destroy
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
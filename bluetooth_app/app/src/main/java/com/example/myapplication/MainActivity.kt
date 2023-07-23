package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityMainBinding
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import kotlin.concurrent.thread
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_ENABLE_BT:Int = 1;
    private var deviceFindFlag = false
    private var devicePairedFlag = false
    private var devicePaired = "ESP32-BT-TEST"

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // bluetooth Manager & Adapter
    private val bManager:BluetoothManager by lazy {
        getSystemService(BluetoothManager::class.java)
    }
    private val bAdapter:BluetoothAdapter? by lazy {
        bManager.getAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // check for permissions

        // check if init bluetooth adapter is done
        if (bAdapter == null) {
            Toast.makeText(this, "이 기기에서 블루투스를 지원하지 않음", Toast.LENGTH_LONG).show()
        }

        // indicate bluetooth on/off
        bluetoothOnOff()
        binding.bluetoothBtn.setOnClickListener {
            val intent = Intent(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            startActivityForResult(intent, REQUEST_CODE_ENABLE_BT)
        }

        // get paired devices
        binding.deviceBtn.setOnClickListener {
            bluetoothPaired()
        }

        // search for devices
        binding.findBtn.setOnClickListener {
            bluetoothSearch()
        }

        // send / recv test
        binding.sendTest.setOnClickListener {
            binding.sendTest.text = "sent"
        }
    }

    //=== existing functions override / callbacks.
    // onActivity Result override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_CODE_ENABLE_BT ->
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "블루투스 켜짐", Toast.LENGTH_SHORT).show()
                }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    // device search broadcast receiver
    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action: String? = intent?.action
            when(action) {
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    binding.deviceListLayout.removeAllViews()
                    deviceFindFlag = false
                    binding.findBtn.visibility = View.GONE
                    binding.findProgressLayout.visibility = View.VISIBLE
                }
                BluetoothDevice.ACTION_FOUND -> {
                    val device = intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceAddr = device?.address
                    if (deviceName != null) {
                        val deviceView = Button(applicationContext)
                        deviceView.text = "${deviceName} ${deviceAddr}"
                        deviceView.setOnClickListener {
                            connectDevice(device)
                        }
                        if (deviceName == "DESKTOP-A6479BT") {
                            deviceFindFlag = true
                            deviceView.text = "${deviceName} ${deviceAddr} found!"
                        }
                        binding.deviceListLayout.addView(deviceView)
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    if (deviceFindFlag == false) {
                        val textView = TextView(applicationContext)
                        textView.text = "근처의 약통을 찾지 못했습니다."
                        binding.deviceListLayout.addView(textView)
                    }
                    binding.findBtn.visibility = View.VISIBLE
                    binding.findProgressLayout.visibility = View.GONE
                }
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    binding.deviceList.text = "connected"
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    binding.deviceList.text = "disconnected"
                }
            }
        }
    }

    // on destroy
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    //=== functions implementation
    // indicate bluetooth on/off
    private fun bluetoothOnOff() {
        // check and set bluetooth on/off image
        timer (period = 500) {
            runOnUiThread {
                if (bAdapter?.isEnabled == true) {
                    binding.bluetoothImg.setImageResource(R.drawable.ic_bluetooth_on)
                    binding.bluetoothBtn.isClickable = false
                    binding.bluetoothBtn.text = "블루투스 켜짐"
                } else {
                    binding.bluetoothImg.setImageResource(R.drawable.ic_bluetooth_off)
                    binding.bluetoothBtn.isClickable = true
                    binding.bluetoothBtn.text = "블루투스 켜기"
                }
            }
        }
    }

    // function for searching devices
    private fun bluetoothSearch() {
        if (bAdapter?.isEnabled == true) {
            val filter_start = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            registerReceiver(receiver, filter_start)
            val filter_found = IntentFilter(BluetoothDevice.ACTION_FOUND)
            registerReceiver(receiver, filter_found)
            val filter_finished = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            registerReceiver(receiver, filter_finished)
            val tmp: Boolean? = bAdapter?.startDiscovery()
            if (tmp == false) {
                Toast.makeText(this, "근처 기기를 찾을 수 없습니다", Toast.LENGTH_LONG).show()
            }
        }
    }

    // function for getting already paired & connected devices
    private fun bluetoothPaired() {  //"ESP32-BLE-TEST"
        val filter_connected = IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED)
        registerReceiver(receiver, filter_connected)
        val filter_disconnected = IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        registerReceiver(receiver, filter_disconnected)
        //timer (period = 1000) {
            val pairedDevices: Set<BluetoothDevice>? = bAdapter?.bondedDevices
            binding.deviceList.text = "${pairedDevices?.size}"
            devicePairedFlag = false
            pairedDevices?.forEach { device ->
                val deviceName = device.name
                val deviceHardwareAddress = device.address // MAC address
                if (deviceName == devicePaired) {
                    devicePairedFlag = true
                    binding.pairedFlagImg.setColorFilter(ContextCompat.getColor(applicationContext, com.google.android.material.R.color.design_default_color_secondary))
                    binding.pairedText.text = "연결 됨"
                    binding.pairedDeviceText.text = devicePaired
                    return@forEach
                }
                binding.deviceList.append("\n${deviceName} ${deviceHardwareAddress}")
            }
            if (devicePairedFlag == false) {
                binding.pairedFlagImg.setColorFilter(ContextCompat.getColor(applicationContext, com.google.android.material.R.color.design_default_color_error))
                binding.pairedText.text = "연결 안됨"
                binding.pairedDeviceText.text = devicePaired
            }
        //}
    }

    // function for device connect - when button in device list pressed.
    private fun connectDevice(targetDevice: BluetoothDevice?) {
        val thread = Thread {
            val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
            var socket: BluetoothSocket? = targetDevice?.createRfcommSocketToServiceRecord(uuid)
            var clazz = socket?.remoteDevice?.javaClass
            var paramTypes = arrayOf<Class<*>>(Integer.TYPE)
            var m = clazz?.getMethod("createRfcommSocket", *paramTypes)
            var fallbackSocket = m?.invoke(socket?.remoteDevice, Integer.valueOf(1)) as BluetoothSocket
            Log.d("myapp", "${uuid} ${socket}")
            try {
                fallbackSocket.connect()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                try {
                    socket?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        thread.start()
    }
}
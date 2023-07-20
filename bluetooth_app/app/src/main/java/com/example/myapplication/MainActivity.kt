package com.example.myapplication

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.myapplication.databinding.ActivityMainBinding
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private val REQUEST_ALL_PERMISSION = 1;
    private val REQUEST_CODE_ENABLE_BT:Int = 1;

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
    //lateinit var bAdapter: BluetoothAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // check for permissions
//        if(!hasPermissions(this, PERMISSIONS)) {
//            requestPermissions(PERMISSIONS, REQUEST_ALL_PERMISSION)
//        }

        // check if init bluetooth adapter is done
        if (bAdapter == null) {
            Toast.makeText(this, "이 기기에서 블루투스를 지원하지 않음", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "블루투스 가능", Toast.LENGTH_LONG).show()
        }

        // indicate bluetooth on/off
        bleOnOff()
        binding.onBtn.setOnClickListener {
            val intent = Intent(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            startActivityForResult(intent, REQUEST_CODE_ENABLE_BT)
        }

        // get paired devices
        binding.deviceBtn.setOnClickListener {
            val pairedDevices: Set<BluetoothDevice>? = bAdapter?.bondedDevices
            binding.deviceList.text = "${pairedDevices?.size}"
            pairedDevices?.forEach { device ->
                val deviceName = device.name
                val deviceHardwareAddress = device.address // MAC address
                binding.deviceList.append("\n${deviceName} ${device}")
            }
        }

        // search for devices
        binding.searchBtn.setOnClickListener {
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            registerReceiver(receiver, filter)
        }

    }

    // permission check
    private fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {
        for (p in permissions) {
            if (context?.let { ActivityCompat.checkSelfPermission(it, p) } != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    // onActivity Result override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_CODE_ENABLE_BT ->
                if (resultCode == Activity.RESULT_OK) {
                    binding.bleImage.setImageResource(R.drawable.ic_bluetooth_on)
                }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    // device search broadcast receiver
    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            binding.searchText.text = "hiii"
            val action: String? = intent.action
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    //binding.searchText.append("${deviceName}")
                    binding.searchText.text = deviceName
                }
            }
        }
    }

    // on destroy
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    // indicate bluetooth on/off
    private fun bleOnOff() {
        // check and set bluetooth on/off image
        runOnUiThread {
            if (bAdapter?.isEnabled == true) {
                binding.bleImage.setImageResource(R.drawable.ic_bluetooth_on)
            } else {
                binding.bleImage.setImageResource(R.drawable.ic_bluetooth_off)
            }
        }
    }
}
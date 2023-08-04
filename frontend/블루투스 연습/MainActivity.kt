package com.example.bluetoothstudy

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.bluetoothstudy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_ENABLE_BT:Int = 1;
    private val REQUEST_CODE_DISCOVERABLE_BT:Int = 2;


    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // bluetooth adapter
    lateinit var bAdapter: BluetoothAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // init bluetooth adapter
        bAdapter = BluetoothAdapter.getDefaultAdapter()

        // check if bluetooth is available or not
        if(!bAdapter.isEnabled){
            binding.bluetoothStatusTv.text = "Bluetooth is not available"
        }
        else{
            binding.bluetoothStatusTv.text = "Bluetooth is available"
        }

        // set image according to bluetooth status(on/off)
        if (bAdapter.isEnabled) {
            // bluetooth is on
            binding.bluetoothIv.setImageResource(R.drawable.ic_bluetooth_on)
        }
        else {
            // bluetooth is off
            binding.bluetoothIv.setImageResource(R.drawable.ic_bluetooth_off)
        }

        // turn on bluetooth
        binding.turnOnBtn.setOnClickListener {
            if (bAdapter.isEnabled) {
                // already enabled
                Toast.makeText(this, "Already on", Toast.LENGTH_LONG).show()
            }
            else {
                // turn on bluetooth
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, REQUEST_CODE_ENABLE_BT)
            }
        }

        // turn off bluetooth
        binding.turnOffBtn.setOnClickListener {
            if (!bAdapter.isEnabled) {
                // already disabled
                Toast.makeText(this, "Already off", Toast.LENGTH_LONG).show()
            }
            else {
                // turn off bluetooth
                // pixel 2 에서는 작동, but s21에서는 작동X => 상위버전에서 작동하지 않는듯
                bAdapter.disable()

                binding.bluetoothStatusTv.text = "Bluetooth is not available"
                binding.bluetoothIv.setImageResource(R.drawable.ic_bluetooth_off)

                Toast.makeText(this, "Bluetooth turned off", Toast.LENGTH_LONG).show()
            }
        }
        // discoverable the bluetooth
        binding.discoverableBtn.setOnClickListener {
            if (!bAdapter.isDiscovering){
                Toast.makeText(this, "Making Your device discoverable", Toast.LENGTH_LONG).show()
                val intent = Intent(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE))
                startActivityForResult(intent, REQUEST_CODE_DISCOVERABLE_BT)
            }
        }

        // get list of paired devices
        binding.pairedBtn.setOnClickListener {
            if (bAdapter.isEnabled) {

                binding.pairedTv.text = "Paired Devices"
                // get list of paired devices
                val devices = bAdapter.bondedDevices
                for (device in devices){
                    val deviceName = device.name
                    val deviceAddress = device
                    binding.pairedTv.append("\nDevice: $deviceName, $device")
                }
            }
            else {

                Toast.makeText(this, "Turn on bluetooth first", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_CODE_ENABLE_BT ->
                if (resultCode == Activity.RESULT_OK){
                    binding.bluetoothIv.setImageResource(R.drawable.ic_bluetooth_on)
                    binding.bluetoothStatusTv.text = "Bluetooth is available"
                    Toast.makeText(this, "Bluetooth is on", Toast.LENGTH_LONG).show()
                } else{
                    // user denied to turn on bluetooth from confirmation dialog
                    Toast.makeText(this, "Could not on bluetooth", Toast.LENGTH_LONG).show()
                }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
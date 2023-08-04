package com.example.eyakrev1

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract.CommonDataKinds.Im
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.example.eyakrev1.databinding.ActivityMainBinding
import kotlin.concurrent.timer

class DeviceRegisterFragment : Fragment() {
    // flag & signal
    private val REQUEST_CODE_ENABLE_BT:Int = 1;
    private var deviceFindFlag = false
    private var devicePairedFlag = false

    // layout
    private lateinit var layout: View
    // bluetooth Manager & Adapter
    private val btManager: BluetoothManager by lazy {
        requireActivity().getSystemService(BluetoothManager::class.java)
    }
    private val btAdapter:BluetoothAdapter? by lazy {
        btManager.getAdapter()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        // 모바일이 블루투스를 지원하는 기기인지 확인
        if (btAdapter == null) {
            Toast.makeText(requireActivity(), "이 기기에서 블루투스를 지원하지 않음", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layout = inflater.inflate(R.layout.fragment_device_register, container, false)

        // 블루투스 On/Off view
        bluetoothOnOffUI()
        layout.findViewById<Button>(R.id.btOnOffBtn).setOnClickListener {
//            val intent = Intent(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
//            startActivityForResult(intent, REQUEST_CODE_ENABLE_BT)
        }

        return layout
    }

    // bluetooth 켜짐/꺼짐 모니터링
    private fun bluetoothOnOffUI() {
        timer (period = 1000) {
            activity?.runOnUiThread {
                if (btAdapter?.isEnabled == true) {
                    layout.findViewById<ImageView>(R.id.btImage).setImageResource(R.drawable.baseline_bluetooth_24)
                    layout.findViewById<TextView>(R.id.btOnOffBtn).text = "블루투스 끄기"
                } else {
                    layout.findViewById<ImageView>(R.id.btImage).setImageResource(R.drawable.baseline_bluetooth_disabled_24)
                    layout.findViewById<TextView>(R.id.btOnOffBtn).text = "블루투스 켜기"
                }
            }
        }
    }
}
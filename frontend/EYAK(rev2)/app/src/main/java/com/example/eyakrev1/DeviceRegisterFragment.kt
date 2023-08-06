package com.example.eyakrev1

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract.CommonDataKinds.Im
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.eyakrev1.databinding.ActivityMainBinding
import kotlin.concurrent.timer

class DeviceRegisterFragment : Fragment() {
    // flag & signal
    private val TAG = "myapp"
    private val REQUEST_CODE_ENABLE_BT:Int = 1;
    private var permFlag: Boolean? = null
    private var devicePairedFlag = false
    private var deviceFindFlag = false

    // 권한 리스트
    private val PERMISSION = arrayOf(
        android.Manifest.permission.BLUETOOTH,
        android.Manifest.permission.BLUETOOTH_ADMIN,
        android.Manifest.permission.BLUETOOTH_CONNECT,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
    )

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

        // 권한 체크
        permFlag = checkPermissions(activity as Context, PERMISSION)
        // 권한 허용 상태에 따라 UI.
        if (permFlag == true) {
            layout.findViewById<LinearLayout>(R.id.permLayout).visibility = View.GONE
        } else {
            layout.findViewById<LinearLayout>(R.id.permLayout).visibility = View.VISIBLE
        }
        layout.findViewById<Button>(R.id.permBtn).setOnClickListener {
            requestPermissionLauncher.launch(PERMISSION)
        }

        // 블루투스 On/Off view
        bluetoothOnOffUI()
        layout.findViewById<Button>(R.id.btOnOffBtn).setOnClickListener {
            val intent = Intent(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            startActivityForResult(intent, REQUEST_CODE_ENABLE_BT)
        }

        // 이미 페어링된 디바이스 유무 확인


        return layout
    }



    // 필요 권한 체크
    private fun checkPermissions(context: Context, perms: Array<String>): Boolean {
        var flag = true
        for (p in perms) {
            when {
                ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), p) -> { flag = false }
                ContextCompat.checkSelfPermission(context, p) == PackageManager.PERMISSION_GRANTED -> {}
                else -> { flag = false }
            }
            if (flag == false) break
        }
        return flag
    }
    // 권한 요청 런처
    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var denyFlag = false
        permissions.entries.forEach {
            //Log.d(TAG, "${it.key} ${it.value}")
            if (it.value == false) denyFlag = true
        }
        if (denyFlag == false) {
            layout.findViewById<LinearLayout>(R.id.permLayout).visibility = View.GONE
        } else {
            AlertDialog.Builder(getContext()).apply {
                setTitle("권한 요청 거부됨")
                setMessage("약통 기기 사용을 위해 권한 승인이 필요합니다. 시스템 설정에서 권한 요청 하시겠습니까?")
                setPositiveButton("권한 요청") {_, _ ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.fromParts("package", context.packageName, null)
                    startActivity(intent)
                }
                setNegativeButton("거부") {_, _ -> Toast.makeText(activity, "권한 요청이 거부되었습니다.", Toast.LENGTH_SHORT).show()}
            }.show()
        }
    }

    // bluetooth 켜짐/꺼짐 모니터링
    private fun bluetoothOnOffUI() {
        timer (period = 1000) {
            activity?.runOnUiThread {
                if (btAdapter?.isEnabled == true) {
                    layout.findViewById<ImageView>(R.id.btImage).setImageResource(R.drawable.baseline_bluetooth_24)
                    var btBtn = layout.findViewById<Button>(R.id.btOnOffBtn)
                    btBtn.isClickable = false
                    btBtn.background.setTint(Color.parseColor("#E0E0E0"))
                    layout.findViewById<TextView>(R.id.btOnOffBtn).text = "블루투스 켜짐"
                } else {
                    layout.findViewById<ImageView>(R.id.btImage).setImageResource(R.drawable.baseline_bluetooth_disabled_24)
                    var btBtn = layout.findViewById<Button>(R.id.btOnOffBtn)
                    btBtn.isClickable = true
                    btBtn.background.setTint(Color.parseColor("#E3F2C1"))
                    layout.findViewById<TextView>(R.id.btOnOffBtn).text = "블루투스 켜기"
                }
            }
        }
    }

    // bluetooth 켜기/끄기 action result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_CODE_ENABLE_BT ->
                if (resultCode != Activity.RESULT_OK) {}
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
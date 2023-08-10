package com.a103.eyakrev1

import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Method
import java.util.Objects
import java.util.UUID
import kotlin.Exception
import kotlin.concurrent.timer

class DeviceRegisterFragment : Fragment() {
    val api = EyakService.create()
    lateinit var mainActivity: MainActivity

    // flag & signal
    private val TAG = "myapp"
    private val REQUEST_CODE_ENABLE_BT:Int = 1
    private var permFlag: Boolean? = null
    private var devicePairedFlag = false
    private var deviceFindFlag = false

    // Preference
    var pref: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    // (임시) 등록된 디바이스 이름 & 디바이스
    private var deviceSaved: BluetoothDevice? = null
    private var deviceNameSaved: String? = null //"ESP32-BT-TEST_2"
    // 약통 UI 정보
    private var pickedMedic = 0
    private var cell1Data: Medicine? = null
    private var cell2Data: Medicine? = null
    private var cell3Data: Medicine? = null
    private var cell4Data: Medicine? = null
    private var cell5Data: Medicine? = null

    // 통신을 위한 var.
    private var socket: BluetoothSocket? = null
    private var fallbackSocket: BluetoothSocket? = null
    private var outStream: OutputStream? = null
    private var inStream: InputStream? = null
    private var buffer: ByteArray = ByteArray(1024)

    // 권한 리스트
    private val PERMISSION = arrayOf(
        android.Manifest.permission.BLUETOOTH,
        android.Manifest.permission.BLUETOOTH_ADMIN,
        android.Manifest.permission.BLUETOOTH_CONNECT,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.BLUETOOTH_SCAN,
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
        // Preference Data 셋업
        pref = PreferenceManager.getDefaultSharedPreferences(mainActivity)
        editor = pref?.edit()
        deviceNameSaved = pref?.getString("DEVICE_NAME", "")
        var gson = Gson()
        var json = pref?.getString("DEVICE_CELL1", "")
        cell1Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL2", "")
        cell2Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL3", "")
        cell3Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL4", "")
        cell4Data = gson.fromJson(json, Medicine::class.java)
        json = pref?.getString("DEVICE_CELL5", "")
        cell5Data = gson.fromJson(json, Medicine::class.java)
        // 모바일이 블루투스를 지원하는 기기인지 확인
        if (btAdapter == null) {
            Toast.makeText(requireActivity(), "이 기기에서 블루투스를 지원하지 않음", Toast.LENGTH_LONG).show()
        }
        // intent 등록
        try {
            val filter_start = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            requireActivity().registerReceiver(receiver, filter_start)
            val filter_found = IntentFilter(BluetoothDevice.ACTION_FOUND)
            requireActivity().registerReceiver(receiver, filter_found)
            val filter_finished = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            requireActivity().registerReceiver(receiver, filter_finished)
            val filter_bonded = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            requireActivity().registerReceiver(receiver, filter_bonded)
        } catch (e: Exception) {}
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

        // 이미 등록/페어링된 디바이스 유무 확인
        if (deviceNameSaved == "") { // 페어링 안됨
            layout.findViewById<TextView>(R.id.btNotFindText).visibility = View.VISIBLE
            layout.findViewById<LinearLayout>(R.id.btConnLayout).visibility = View.GONE
            layout.findViewById<LinearLayout>(R.id.btDeviceUiLayout).visibility = View.GONE
            layout.findViewById<LinearLayout>(R.id.btFindLayout).visibility = View.VISIBLE
        } else { // 페어링 됨
            layout.findViewById<TextView>(R.id.btNotFindText).visibility = View.GONE
            layout.findViewById<LinearLayout>(R.id.btConnLayout).visibility = View.VISIBLE
            layout.findViewById<LinearLayout>(R.id.btDeviceUiLayout).visibility = View.VISIBLE
            layout.findViewById<LinearLayout>(R.id.btFindLayout).visibility = View.GONE
            layout.findViewById<TextView>(R.id.btConnName).text = deviceNameSaved
            if (permFlag == true) bluetoothPaired()
        }

        // 등록 디바이스 삭제
        layout.findViewById<Button>(R.id.btConnOffBtn).setOnClickListener {
            AlertDialog.Builder(getContext()).apply {
                setTitle("약통 삭제")
                setMessage("약통을 삭제 하시겠습니까? \n언제든지 다시 연결할 수 있습니다.")
                setPositiveButton("삭제") {_, _ -> bluetoothDelete() }
                setNegativeButton("취소") {_, _ -> }
            }.show()
        }

        // 근처 기기 찾기
        layout.findViewById<Button>(R.id.btFindBtn).setOnClickListener {
            bluetoothSearch()
        }

        // 등록 기기 편집 창으로 이동
        layout.findViewById<Button>(R.id.btDeviceEditBtn).setOnClickListener {
            moveDeviceEdit()
        }



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

    // 디바이스 관련 broadcast receiver
    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action: String? = intent?.action
            when (action) {
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> { // 근처 기기 탐색 시작
                    layout.findViewById<CardView>(R.id.btListCard).visibility = View.VISIBLE
                    layout.findViewById<LinearLayout>(R.id.btListLayout).removeAllViews()
                    deviceFindFlag = false
                    layout.findViewById<TextView>(R.id.btListNotFoundText).visibility = View.GONE
                    layout.findViewById<Button>(R.id.btFindBtn).visibility = View.GONE
                    layout.findViewById<ProgressBar>(R.id.btFindProgressBar).visibility = View.VISIBLE
                }
                BluetoothDevice.ACTION_FOUND -> { // 근처 기기 찾음
                    layout.findViewById<Button>(R.id.btFindBtn).visibility = View.GONE
                    layout.findViewById<ProgressBar>(R.id.btFindProgressBar).visibility = View.VISIBLE
                    layout.findViewById<CardView>(R.id.btListCard).visibility = View.VISIBLE
                    val device = intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    if (device?.name != null) { // (추후 약통만 식별하도록 설정)
                        try {
                            deviceFindFlag = true
                            var deviceView = LayoutInflater.from(requireContext()).inflate(R.layout.device_tab_list_view_item, null)
                            deviceView.findViewById<TextView>(R.id.deviceNameText).text = "${device?.name}"
                            deviceView.findViewById<Button>(R.id.deviceConnBtn).setOnClickListener { pairDevice(device) }
                            layout.findViewById<LinearLayout>(R.id.btListLayout).addView(deviceView)
                        } catch (e: Exception) {}
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> { // 근처 기기 탐색 종료
                    layout.findViewById<CardView>(R.id.btListCard).visibility = View.VISIBLE
                    if (deviceFindFlag == false) {
                        layout.findViewById<TextView>(R.id.btListNotFoundText).visibility = View.VISIBLE
                    } else {
                        layout.findViewById<TextView>(R.id.btListNotFoundText).visibility = View.GONE
                    }
                    layout.findViewById<ProgressBar>(R.id.btFindProgressBar).visibility = View.GONE
                    layout.findViewById<Button>(R.id.btFindBtn).visibility = View.VISIBLE
                }
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> { // 페어링 상태 변화 감지
                    if (deviceSaved?.bondState == BluetoothDevice.BOND_BONDED) {
                        // 페어링 승인됨
                        editor?.putString("DEVICE_NAME", deviceNameSaved)?.apply()
                        layout.findViewById<TextView>(R.id.btNotFindText).visibility = View.GONE
                        layout.findViewById<LinearLayout>(R.id.btConnLayout).visibility = View.VISIBLE
                        layout.findViewById<LinearLayout>(R.id.btDeviceUiLayout).visibility = View.VISIBLE
                        layout.findViewById<LinearLayout>(R.id.btFindLayout).visibility = View.GONE
                        layout.findViewById<TextView>(R.id.btConnName).text = deviceNameSaved
                        showDeviceUI()
                    } else if (deviceSaved?.bondState == BluetoothDevice.BOND_BONDING) {
                        // 페어링 승인 중...
                    } else if (deviceSaved?.bondState == BluetoothDevice.BOND_NONE) {
                        // 페어링 거부됨
                        layout.findViewById<TextView>(R.id.btNotFindText).visibility = View.VISIBLE
                        layout.findViewById<LinearLayout>(R.id.btConnLayout).visibility = View.GONE
                        layout.findViewById<LinearLayout>(R.id.btDeviceUiLayout).visibility = View.GONE
                        layout.findViewById<LinearLayout>(R.id.btFindLayout).visibility = View.VISIBLE
                        deviceSaved = null
                        deviceNameSaved = ""
                    }
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

    // bluetooth 페어링 기기 찾기
    private fun bluetoothPaired() {
        val pairedDevices: Set<BluetoothDevice>? = btAdapter?.bondedDevices  //"${pairedDevices?.size}"
        devicePairedFlag = false
        pairedDevices?.forEach { device ->
            if (device.name == deviceNameSaved) {
                deviceSaved = device
                devicePairedFlag = true
                layout.findViewById<ImageView>(R.id.btConnImage).setColorFilter(Color.parseColor("#E3F2C1"))
                layout.findViewById<TextView>(R.id.btConnState).text = "약통과 페어링 되었습니다."
                return@forEach
            }
        }
        if (devicePairedFlag == false) {
            layout.findViewById<ImageView>(R.id.btConnImage).setColorFilter(Color.parseColor("#747679"))
            layout.findViewById<TextView>(R.id.btConnState).text = "약통과 연결이 끊어졌습니다."
        }
        showDeviceUI()
    }
    // 페어링 약통 존재함에 따른 처리
    private fun showDeviceUI() {
        val infoVnot = layout.findViewById<TextView>(R.id.btCellInfoNotText)
        val infoVlayout = layout.findViewById<LinearLayout>(R.id.btCellInfoLayout)
        val infoVimg = layout.findViewById<ImageView>(R.id.btCellInfoImage)
        val infoVtext = layout.findViewById<TextView>(R.id.btCellInfoText)
        val view1 = layout.findViewById<ImageView>(R.id.btCell1)
        val view2 = layout.findViewById<ImageView>(R.id.btCell2)
        val view3 = layout.findViewById<ImageView>(R.id.btCell3)
        val view4 = layout.findViewById<ImageView>(R.id.btCell4)
        val view5 = layout.findViewById<ImageView>(R.id.btCell5)
        val viewLst = arrayOf(view1, view2, view3, view4, view5)
        val dataLst = arrayOf(cell1Data, cell2Data, cell3Data, cell4Data, cell5Data)
        for (i in 0..4) {
            if (dataLst[i] == null) {
                viewLst[i].setImageResource(0)
            } else {
                iconSetting(viewLst[i], dataLst[i]!!.medicineShape)
                viewLst[i].setOnClickListener {
                    infoVnot.visibility = View.GONE
                    infoVlayout.visibility = View.VISIBLE
                    iconSetting(infoVimg, dataLst[i]!!.medicineShape)
                    infoVtext.text = dataLst[i]!!.customName
                }
            }
        }
    }
    // 아이콘 배치 함수
    private fun iconSetting(medicineListIconImageView: ImageView, iconNo: Int) {
        when(iconNo) {
            1 -> medicineListIconImageView.setImageResource(R.drawable.ic_pill_glacier)
            2 -> medicineListIconImageView.setImageResource(R.drawable.ic_pill_afterglow)
            3 -> medicineListIconImageView.setImageResource(R.drawable.ic_pill_bougainvaillea)
            4 -> medicineListIconImageView.setImageResource(R.drawable.ic_pill_orchidice)
            5 -> medicineListIconImageView.setImageResource(R.drawable.ic_pill_silver)
            6 -> medicineListIconImageView.setImageResource(R.drawable.ic_pill_pinklady)
            7 -> medicineListIconImageView.setImageResource(R.drawable.ic_pill_fusioncoral)
            8 -> medicineListIconImageView.setImageResource(R.drawable.ic_roundpill_glacier)
            9 -> medicineListIconImageView.setImageResource(R.drawable.ic_roundpill_afterglow)
            10 -> medicineListIconImageView.setImageResource(R.drawable.ic_roundpill_bougainvillea)
            11 -> medicineListIconImageView.setImageResource(R.drawable.ic_roundpill_orchidice)
            12 -> medicineListIconImageView.setImageResource(R.drawable.ic_roundpill_silver)
            13 -> medicineListIconImageView.setImageResource(R.drawable.ic_roundpill_pinklady)
            14 -> medicineListIconImageView.setImageResource(R.drawable.ic_roundpill_fusioncoral)
            15 -> medicineListIconImageView.setImageResource(R.drawable.ic_packagepill_glacier)
            16 -> medicineListIconImageView.setImageResource(R.drawable.ic_packagepill_afterglow)
            17 -> medicineListIconImageView.setImageResource(R.drawable.ic_packagepill_bougainvillea)
            18 -> medicineListIconImageView.setImageResource(R.drawable.ic_packagepill_orchidice)
            19 -> medicineListIconImageView.setImageResource(R.drawable.ic_packagepill_silver)
            20 -> medicineListIconImageView.setImageResource(R.drawable.ic_packagepill_pinklady)
            21 -> medicineListIconImageView.setImageResource(R.drawable.ic_packagepill_fusioncoral)
            22 -> medicineListIconImageView.setImageResource(R.drawable.ic_outpill_glacier)
            23 -> medicineListIconImageView.setImageResource(R.drawable.ic_outpill_afterglow)
            24 -> medicineListIconImageView.setImageResource(R.drawable.ic_outpill_bougainvillea)
            25 -> medicineListIconImageView.setImageResource(R.drawable.ic_outpill_orchidice)
            26 -> medicineListIconImageView.setImageResource(R.drawable.ic_outpill_silver)
            27 -> medicineListIconImageView.setImageResource(R.drawable.ic_outpill_pinklady)
            28 -> medicineListIconImageView.setImageResource(R.drawable.ic_outpill_fusioncoral)
            29 -> medicineListIconImageView.setImageResource(R.drawable.ic_potion_glacier)
            30 -> medicineListIconImageView.setImageResource(R.drawable.ic_potion_afterglow)
            31 -> medicineListIconImageView.setImageResource(R.drawable.ic_potion_bougainvillea)
            32 -> medicineListIconImageView.setImageResource(R.drawable.ic_potion_orchidice)
            33 -> medicineListIconImageView.setImageResource(R.drawable.ic_potion_silver)
            34 -> medicineListIconImageView.setImageResource(R.drawable.ic_potion_pinklady)
            35 -> medicineListIconImageView.setImageResource(R.drawable.ic_potion_fusioncoral)
        }
    }

    // bluetooth 근처 기기 찾기 (페어링 되지 않은 것)
    private fun bluetoothSearch() {
        if (btAdapter?.isEnabled == true) {
            btAdapter?.startDiscovery()
        } else {
            Toast.makeText(requireActivity(), "블루투스를 먼저 켜주세요", Toast.LENGTH_SHORT).show()
        }
    }

    // 특정 디바이스와 페어링
    private fun pairDevice(targetDevice: BluetoothDevice?) {
        if (targetDevice?.bondState == BluetoothDevice.BOND_NONE) {
            deviceSaved = targetDevice
            deviceNameSaved = targetDevice?.name
            targetDevice?.createBond()
        }
    }
    // 특정 디바이스와 연결 (소켓)
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

    // 디바이스 삭제
    private fun bluetoothDelete() {
        try {
            deviceSaved!!::class.java.getMethod("removeBond").invoke(deviceSaved)
            editor?.remove("DEVICE_NAME")?.commit()
            deviceNameSaved = ""
        } catch (e: Exception) {
            Toast.makeText(requireActivity(), "삭제 실패", Toast.LENGTH_SHORT).show()
        }
    }

    // 디바이스 편집 창으로 이동
    private fun moveDeviceEdit() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragment, DeviceFragment())
            .commit()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // 2. Context를 액티비티로 형변환해서 할당
        mainActivity = context as MainActivity
    }
}
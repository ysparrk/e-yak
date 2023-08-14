package com.a103.eyakrev1

import android.app.ActionBar.LayoutParams
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.DragEvent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeviceFragment : Fragment() {

    private val api = EyakService.create()
    lateinit var mainActivity: MainActivity

    // Preference
    private var pref: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    // layout
    private lateinit var layout: View

    // 약 리스트 관련
    private var serverAccessToken: String? = null
    private var medicList: ArrayList<Medicine>? = null

    // 약통 설정 데이터
    private var cell1Data: Medicine? = null
    private var cell2Data: Medicine? = null
    private var cell3Data: Medicine? = null
    private var cell4Data: Medicine? = null
    private var cell5Data: Medicine? = null
    private var soundData: Boolean? = null
    private var buzzData: Boolean? = null
    // 약통 설정 처음값, 변경값
    private var cellInitData = Array<Medicine?>(5) { null }
    private var cellNowData = Array<Medicine?>(5) { null }
    private var alaInitData = Array<Boolean?>(2) { null }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        // Preference Data 셋업
        pref = PreferenceManager.getDefaultSharedPreferences(mainActivity)
        editor = pref?.edit()
        serverAccessToken = pref?.getString("SERVER_ACCESS_TOKEN", "")
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
        cellInitData = arrayOf(cell1Data, cell2Data, cell3Data, cell4Data, cell5Data)
        cellNowData = arrayOf(cell1Data, cell2Data, cell3Data, cell4Data, cell5Data)
        soundData = pref?.getBoolean("DEVICE_SOUND", true)
        buzzData = pref?.getBoolean("DEVICE_BUZZ", false)
        alaInitData = arrayOf(soundData, buzzData)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        layout = inflater.inflate(R.layout.device_tab_main, container, false)

        // 뒤로가기 버튼
        layout.findViewById<Button>(R.id.backDeviceEditBtn).setOnClickListener {
            backDeviceEdit()
        }
        // 저장 버튼
        layout.findViewById<Button>(R.id.deviceEditSaveBtn).setOnClickListener {
            saveDeviceEdit()
        }

        // 소리, 진동 설정
        val soundView = layout.findViewById<Switch>(R.id.deviceSoundSwitch)
        val buzzView = layout.findViewById<Switch>(R.id.deviceBuzzSwitch)
        soundView.setChecked(soundData!!)
        buzzView.setChecked(buzzData!!)
        soundView.setOnCheckedChangeListener { _, isChecked -> soundData = isChecked }
        buzzView.setOnCheckedChangeListener { _, isChecked -> buzzData = isChecked }

        // 드랍 리스너
        val view0 = layout.findViewById<LinearLayout>(R.id.medicineScrollLayout)
        val viewBack = layout.findViewById<HorizontalScrollView>(R.id.medicineScroll)
        val view1 = layout.findViewById<LinearLayout>(R.id.btEditCell1)
        val view2 = layout.findViewById<LinearLayout>(R.id.btEditCell2)
        val view3 = layout.findViewById<LinearLayout>(R.id.btEditCell3)
        val view4 = layout.findViewById<LinearLayout>(R.id.btEditCell4)
        val view5 = layout.findViewById<LinearLayout>(R.id.btEditCell5)
        val cellViews = arrayOf(view1, view2, view3, view4, view5)
        for (i in 0..4) {
            cellViews[i].setOnDragListener(dragListener)
        }
        viewBack.setOnDragListener(dragListener)

        // 일단 아이템 존재 전제.
        layout.findViewById<TextView>(R.id.medicineScrollNonText).visibility = View.GONE
        viewBack.visibility = View.VISIBLE

        // 아이템 가져오기
        medicList = arrayListOf<Medicine>()
        api.getAllPrescriptions(Authorization = "Bearer ${serverAccessToken}").enqueue(object: Callback<ArrayList<Medicine>> {
            override fun onResponse(call: Call<ArrayList<Medicine>>, response: Response<ArrayList<Medicine>>) {
                if (response.code() == 401) {
                    Toast.makeText(requireActivity(), "인증되지 않은 사용자 입니다", Toast.LENGTH_SHORT).show()
                } else if (response.code() == 200) {
                    medicList = response.body()
                    medicList?.add(Medicine())
                    for (i in 0..4) { // 각 cell을 loop, 배치된 약들 디스플레이.
                        if (cellNowData[i] != null) {
                            if (medicList!!.contains(cellNowData[i])) {
                                newMedicItem(cellNowData[i]!!, i, cellViews, view0)
                            } else { // 등록된 약이 존재하지 않음.
                                cellInitData[i] = null
                                cellNowData[i] = null
                            }
                        }
                    }
                    for (m in medicList!!) { // 각 약을 loop, 배치되지 않은 약들 디스플레이.
                        if (m.id != -1) {
                            if (!cellNowData.contains(m)) {
                                newMedicItem(m, -1, cellViews, view0)
                            }
                        }
                    }
                    if (medicList?.size == 1) { // 등록 약 없는 경우.
                        layout.findViewById<TextView>(R.id.medicineScrollNonText).visibility = View.VISIBLE
                        layout.findViewById<HorizontalScrollView>(R.id.medicineScroll).visibility = View.GONE
                    }
                }
            }
            override fun onFailure(call: Call<ArrayList<Medicine>>, t: Throwable) {}
        })

        return layout
    }

    // 드래그 리스너
    val dragListener = View.OnDragListener { view, event ->
        when(event.action) {
            DragEvent.ACTION_DRAG_STARTED -> { // drag 시작, shadow 얻었을 때.
                event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }
            DragEvent.ACTION_DRAG_ENTERED -> { // shadow가 listener 뷰에 진입
                val viewId = view.resources.getResourceEntryName(view.id)
                if (viewId != "medicineScroll") {
                    view.background.setTint(Color.parseColor("#E3F2C1"))
                    view.invalidate()
                }
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> true // shadow가 listener 뷰에 진입 후 지속.
            DragEvent.ACTION_DRAG_EXITED -> { // shadow가 listener 뷰에서 나감
                val viewId = view.resources.getResourceEntryName(view.id)
                if (viewId != "medicineScroll") {
                    view.background.setTint(Color.parseColor("#D8D8DA"))
                    view.invalidate()
                }
                true
            }
            DragEvent.ACTION_DROP -> { // shadow가 listener 뷰에 드랍
                val item = event.clipData.getItemAt(0)
                val itemMedic = Gson().fromJson("${item.text}", Medicine::class.java)
                val viewId = view.resources.getResourceEntryName(view.id)
                val v = event.localState as View
                val owner = v.parent as ViewGroup
                if (viewId == "medicineScroll") { // 약리스트에 드롭 됨
                    for (i in 0..4) {
                        if (cellNowData[i] == itemMedic) { // 이전에 있던 칸 정보 가져오기
                            view.invalidate()
                            cellNowData[i] = null
                            v.findViewById<TextView>(R.id.deviceMedicText).visibility = View.VISIBLE
                            owner.removeView(v)
                            val destination = view.findViewById<LinearLayout>(R.id.medicineScrollLayout)
                            destination.addView(v)
                            break
                        }
                    }
                } else { // 약통 칸에 드롭됨
                    view.background.setTint(Color.parseColor("#D8D8DA"))
                    view.invalidate()
                    val viewIdNo = viewId.last().toString().toInt() // 드롭된 칸 번호
                    var prevNo = 0// 이전 칸 번호 (0 = 리스트, 1-5 = 칸)
                    for (i in 0..4) {
                        if (cellNowData[i] == itemMedic) {
                            prevNo = i+1
                        }
                    }
                    if (prevNo != viewIdNo) {
                        if (cellNowData[viewIdNo-1] != null) { // 드롭된 칸에 이미 약이 존재
                            val destination = view as LinearLayout
                            val tmpView = destination.findViewById<LinearLayout>(R.id.deviceMedicItem)
                            tmpView.findViewById<TextView>(R.id.deviceMedicText).visibility = View.VISIBLE
                            destination.removeView(tmpView)
                            layout.findViewById<LinearLayout>(R.id.medicineScrollLayout).addView(tmpView)
                        }
                        if (prevNo == 0) { // 리스트에서 온 경우
                            cellNowData[viewIdNo-1] = itemMedic
                            v.findViewById<TextView>(R.id.deviceMedicText).visibility = View.GONE
                            owner.removeView(v)
                            val destination = view as LinearLayout
                            destination.addView(v)
                        } else { // 다른 칸에서 온 경우.
                            cellNowData[prevNo-1] = null
                            cellNowData[viewIdNo-1] = itemMedic
                            owner.removeView(v)
                            val destination = view as LinearLayout
                            destination.addView(v)
                        }
                    }
                }
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {  // 드래그앤 드롭 종료시 발생. action_drop 후 보장 X.
                view.invalidate()
                true
            }
            else -> false
        }
    }

    // 드래그 아이템 추가 함수
    private fun newMedicItem(medic: Medicine, setted: Int, views: Array<LinearLayout>, view0: LinearLayout) {
        // item 뷰 가져오기, 이외 뷰들
        val medicView = LayoutInflater.from(requireContext()).inflate(R.layout.device_tab_edit_view_item, null)
        // 약 아이템 이름 설정
        val medicNameView = medicView.findViewById<TextView>(R.id.deviceMedicText)
        medicNameView.text = medic.customName
        if (setted != -1) medicNameView.visibility = View.GONE
        // 아이콘 설정
        val medicIconView = medicView.findViewById<ImageView>(R.id.deviceMedicImage)
        iconSetting(medicIconView, medic.medicineShape)
        // 그냥 클릭 셋업
        medicView.setOnClickListener {
            layout.findViewById<TextView>(R.id.devicePickText).visibility = View.GONE
            layout.findViewById<HorizontalScrollView>(R.id.devicePickedScroll).visibility = View.VISIBLE
            iconSetting(layout.findViewById<ImageView>(R.id.devicePickedImage), medic.medicineShape)
            layout.findViewById<TextView>(R.id.devicePickedText).text = medic.customName
        }
        // 길게 클릭 셋업
        medicView.setOnLongClickListener {
            var json = Gson().toJson(medic)
            val item = ClipData.Item(json)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(medic.customName, mimeTypes, item)

            val dragShadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data, dragShadowBuilder, it, 0)
            true
        }
        // 약 뷰 배치
        if (setted != -1) {
            views[setted].addView(medicView)
        } else {
            view0.addView(medicView)
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

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // 2. Context를 액티비티로 형변환해서 할당
        mainActivity = context as MainActivity
    }

    // 뒤로가기 버튼 함수
    private fun backDeviceEdit() {
        var changeFlag = false
        for (i in 0..4) {
            if (cellInitData[i] != cellNowData[i]) {
                changeFlag = true
                break
            }
        }
        if (alaInitData[0] != soundData) changeFlag = true
        if (alaInitData[1] != buzzData) changeFlag = true
        if (changeFlag) {
            AlertDialog.Builder(getContext()).apply {
                setTitle("약통 설정")
                setMessage("약통 설정의 변경 사항이 저장되지 않았습니다.\n저장하지 않고 이전으로 돌아가시겠습니까?")
                setPositiveButton("뒤로 가기") { _, _ ->
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.mainFragment, DeviceRegisterFragment())
                        .commit()
                }
                setNegativeButton("취소") { _, _ -> }
            }.show()
        } else {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.mainFragment, DeviceRegisterFragment())
                .commit()
        }
    }

    // 저장 버튼
    private fun saveDeviceEdit() {
        var changeFlag = false
        for (i in 0..4) {
            if (cellInitData[i] != cellNowData[i]) {
                changeFlag = true
                break
            }
        }
        if (alaInitData[0] != soundData) changeFlag = true
        if (alaInitData[1] != buzzData) changeFlag = true
        if (changeFlag) {
            AlertDialog.Builder(getContext()).apply {
                setTitle("약통 설정 저장")
                setMessage("약통 설정을 저장하시겠습니까?")
                setPositiveButton("저장") { _, _ ->
                    for(i in 0..4) {
                        val json = Gson().toJson(cellNowData[i])
                        editor?.putString("DEVICE_CELL${i+1}", json)?.apply()
                    }
                    editor?.putBoolean("DEVICE_SOUND", soundData!!)?.apply()
                    editor?.putBoolean("DEVICE_BUZZ", buzzData!!)?.apply()
                    Toast.makeText(requireActivity(), "약통 설정이 저장되었습니다", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.mainFragment, DeviceRegisterFragment())
                        .commit()
                }
                setNegativeButton("취소") { _, _ -> }
            }.show()
        } else {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.mainFragment, DeviceRegisterFragment())
                .commit()
        }
    }
}
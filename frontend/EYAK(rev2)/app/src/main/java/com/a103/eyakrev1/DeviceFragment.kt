package com.a103.eyakrev1

import android.app.ActionBar.LayoutParams
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
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
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeviceFragment : Fragment() {

    val api = EyakService.create()
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
        soundData = pref?.getBoolean("DEVICE_SOUND", true)
        buzzData = pref?.getBoolean("DEVICE_BUZZ", true)
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

        // 일단 아이템 존재 전제.
        layout.findViewById<TextView>(R.id.medicineScrollNonText).visibility = View.GONE
        layout.findViewById<HorizontalScrollView>(R.id.medicineScroll).visibility = View.VISIBLE

        // 아이템 가져오기
        medicList = arrayListOf<Medicine>()
        api.getAllPrescriptions(Authorization = "Bearer ${serverAccessToken}").enqueue(object: Callback<ArrayList<Medicine>> {
            override fun onResponse(call: Call<ArrayList<Medicine>>, response: Response<ArrayList<Medicine>>) {
                if (response.code() == 401) {
                    Log.d("log", "인증되지 않은 사용자입니다")
                } else if (response.code() == 200) {
                    medicList = response.body()
                    medicList?.add(Medicine())
                    for (m in medicList!!) {
                        if (m.id != -1) {
                            when(m.id) {
                                cell1Data?.id -> { newMedicItem(m, 1) }
                                cell2Data?.id -> { newMedicItem(m, 2) }
                                cell3Data?.id -> { newMedicItem(m, 3) }
                                cell4Data?.id -> { newMedicItem(m, 4) }
                                cell5Data?.id -> { newMedicItem(m, 5) }
                                else -> { newMedicItem(m, 0) }
                            }
//                            var imgView = LayoutInflater.from(requireContext()).inflate(R.layout.device_tab_edit_setted_view_item, null)
//                            var img = imgView.findViewById<ImageView>(R.id.deviceSettedImage)
//                            iconSetting(img, m.medicineShape)
//                            imgView.setOnClickListener {
//                                layout.findViewById<TextView>(R.id.devicePickText).visibility = View.GONE
//                                layout.findViewById<LinearLayout>(R.id.devicePickedLayout).visibility = View.VISIBLE
//                                iconSetting(layout.findViewById<ImageView>(R.id.devicePickedImage), m.medicineShape)
//                                layout.findViewById<TextView>(R.id.devicePickedText).text = m.customName
//                                layout.findViewById<Button>(R.id.devicePickedDelBtn).setOnClickListener {
//                                    when(m.id) {
//                                        cell1Data?.id -> { layout.findViewById<LinearLayout>(R.id.btEditCell1).removeAllViews(); cell1Data = null }
//                                        cell2Data?.id -> { layout.findViewById<LinearLayout>(R.id.btEditCell2).removeAllViews(); cell2Data = null }
//                                        cell3Data?.id -> { layout.findViewById<LinearLayout>(R.id.btEditCell3).removeAllViews(); cell3Data = null }
//                                        cell4Data?.id -> { layout.findViewById<LinearLayout>(R.id.btEditCell4).removeAllViews(); cell4Data = null }
//                                        cell5Data?.id -> { layout.findViewById<LinearLayout>(R.id.btEditCell5).removeAllViews(); cell5Data = null }
//                                    }
//                                    newMedicItem(m)
//                                    layout.findViewById<TextView>(R.id.devicePickText).visibility = View.VISIBLE
//                                    layout.findViewById<LinearLayout>(R.id.devicePickedLayout).visibility = View.GONE
//                                }
//                            }
//                            imgView.setOnLongClickListener {
//                                var json = Gson().toJson(m)
//                                val item = ClipData.Item(json)
//                                val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
//                                val data = ClipData(m.customName, mimeTypes, item)
//
//                                val dragShadowBuilder = View.DragShadowBuilder(it)
//                                it.startDragAndDrop(data, dragShadowBuilder, it, 0)
//                                true
//                            }
                        }
                    }
                    if (medicList?.size == 1) { // 등록 약 없음
                        layout.findViewById<TextView>(R.id.medicineScrollNonText).visibility = View.VISIBLE
                        layout.findViewById<HorizontalScrollView>(R.id.medicineScroll).visibility = View.GONE
                    }
                }
            }
            override fun onFailure(call: Call<ArrayList<Medicine>>, t: Throwable) {}
        })

        // 드랍 리스너
        layout.findViewById<LinearLayout>(R.id.btEditCell1).setOnDragListener(dragListener)
        layout.findViewById<LinearLayout>(R.id.btEditCell2).setOnDragListener(dragListener)
        layout.findViewById<LinearLayout>(R.id.btEditCell3).setOnDragListener(dragListener)
        layout.findViewById<LinearLayout>(R.id.btEditCell4).setOnDragListener(dragListener)
        layout.findViewById<LinearLayout>(R.id.btEditCell5).setOnDragListener(dragListener)

        return layout
    }

    // 드래그 리스너
    val dragListener = View.OnDragListener { view, event ->
        when(event.action) {
            DragEvent.ACTION_DRAG_STARTED -> { // drag 시작, shadow 얻었을 때.
                event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }
            DragEvent.ACTION_DRAG_ENTERED -> { // shadow가 listener 뷰에 진입
                view.background.setTint(Color.parseColor("#FF9B9B"))
                view.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> true // shadow가 listener 뷰에 진입 후 지속.
            DragEvent.ACTION_DRAG_EXITED -> { // shadow가 listener 뷰에서 나감
                view.background.setTint(Color.parseColor("#AAABAE"))
                view.invalidate()
                true
            }
            DragEvent.ACTION_DROP -> { // shadow가 listener 뷰에 드랍
                view.background.setTint(Color.parseColor("#AAABAE"))
                val item = event.clipData.getItemAt(0)

                view.invalidate()

                val v = event.localState as View
                v.findViewById<TextView>(R.id.deviceMedicText).visibility = View.GONE
                layout.findViewById<Button>(R.id.devicePickedDelBtn).visibility = View.VISIBLE
                val owner = v.parent as ViewGroup
                owner.removeView(v)
                val destination = view as LinearLayout
                destination.addView(v)

                // 각 칸에 Medicine 배치
                when("${view.id}".last()) {
                    '1' -> { cell1Data = Gson().fromJson("${item.text}", Medicine::class.java) }
                    '2' -> { cell2Data = Gson().fromJson("${item.text}", Medicine::class.java) }
                    '3' -> { cell3Data = Gson().fromJson("${item.text}", Medicine::class.java) }
                    '4' -> { cell4Data = Gson().fromJson("${item.text}", Medicine::class.java) }
                    '5' -> { cell5Data = Gson().fromJson("${item.text}", Medicine::class.java) }
                }
                // v.visibility = View.VISIBLE
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {  // 드래그앤 드롭 종료시 발생. action_drop 후 보장 X.
                view.invalidate()
//                Toast.makeText(requireActivity(), "dragdrop end", Toast.LENGTH_SHORT).show()
                true
            }
            else -> false
        }
    }

    // 드래그 아이템 추가 함수
    private fun newMedicItem(medic: Medicine, setted: Int) {
        // item 뷰 가져오기, 이외 뷰들
        val medicView = LayoutInflater.from(requireContext()).inflate(R.layout.device_tab_edit_view_item, null)
        val view0 = layout.findViewById<LinearLayout>(R.id.medicineScrollLayout)
        val view1 = layout.findViewById<LinearLayout>(R.id.btEditCell1)
        val view2 = layout.findViewById<LinearLayout>(R.id.btEditCell2)
        val view3 = layout.findViewById<LinearLayout>(R.id.btEditCell3)
        val view4 = layout.findViewById<LinearLayout>(R.id.btEditCell4)
        val view5 = layout.findViewById<LinearLayout>(R.id.btEditCell5)
        // 약 아이템 이름 설정
        val medicNameView = medicView.findViewById<TextView>(R.id.deviceMedicText)
        if (setted != 0) medicNameView.visibility = View.GONE
        else medicNameView.text = medic.customName
        // 아이콘 설정
        val medicIconView = medicView.findViewById<ImageView>(R.id.deviceMedicImage)
        iconSetting(medicIconView, medic.medicineShape)
        // 그냥 클릭 셋업
        medicView.setOnClickListener {
            layout.findViewById<TextView>(R.id.devicePickText).visibility = View.GONE
            layout.findViewById<HorizontalScrollView>(R.id.devicePickedScroll).visibility = View.VISIBLE
            iconSetting(layout.findViewById<ImageView>(R.id.devicePickedImage), medic.medicineShape)
            layout.findViewById<TextView>(R.id.devicePickedText).text = medic.customName
            // 삭제 버튼 셋업
            layout.findViewById<Button>(R.id.devicePickedDelBtn).setOnClickListener {
                when(medic.id) {
                    cell1Data?.id -> { view1.removeAllViews(); newMedicItem(medic, 0); cell1Data = null }
                    cell2Data?.id -> { view2.removeAllViews(); newMedicItem(medic, 0); cell2Data = null }
                    cell3Data?.id -> { view3.removeAllViews(); newMedicItem(medic, 0); cell3Data = null }
                    cell4Data?.id -> { view4.removeAllViews(); newMedicItem(medic, 0); cell4Data = null }
                    cell5Data?.id -> { view5.removeAllViews(); newMedicItem(medic, 0); cell5Data = null }
                }
                layout.findViewById<TextView>(R.id.devicePickText).visibility = View.VISIBLE
                layout.findViewById<HorizontalScrollView>(R.id.devicePickedScroll).visibility = View.GONE
            }
//            if (setted == 0) layout.findViewById<Button>(R.id.devicePickedDelBtn).visibility = View.GONE
        }
        // 길게 클릭 셋업
        medicView.setOnLongClickListener {
            var json = Gson().toJson(medic)
            val item = ClipData.Item(json)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(medic.customName, mimeTypes, item)

            val dragShadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data, dragShadowBuilder, it, 0)
            // it.visibility = View.INVISIBLE
            true
        }
        when(setted) {
            0 -> { view0.addView(medicView) }
            1 -> { view1.addView(medicView) }
            2 -> { view2.addView(medicView) }
            3 -> { view3.addView(medicView) }
            4 -> { view4.addView(medicView) }
            5 -> { view5.addView(medicView) }
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
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragment, DeviceRegisterFragment())
            .commit()
    }

    // 저장 버튼
    private fun saveDeviceEdit() {
        var gson = Gson()
        var json = gson.toJson(cell1Data)
        editor?.putString("DEVICE_CELL1", json)?.apply()
        json = gson.toJson(cell2Data)
        editor?.putString("DEVICE_CELL2", json)?.apply()
        json = gson.toJson(cell3Data)
        editor?.putString("DEVICE_CELL3", json)?.apply()
        json = gson.toJson(cell4Data)
        editor?.putString("DEVICE_CELL4", json)?.apply()
        json = gson.toJson(cell5Data)
        editor?.putString("DEVICE_CELL5", json)?.apply()
        editor?.putBoolean("DEVICE_SOUND", soundData!!)?.apply()
        editor?.putBoolean("DEVICE_BUZZ", buzzData!!)?.apply()
    }
}
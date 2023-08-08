package com.a103.eyakrev1

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.CacheResponse

class DeviceFragment : Fragment() {

    val api = EyakService.create()
    lateinit var mainActivity: MainActivity

    // Preference
    var pref: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    // layout
    private lateinit var layout: View

    // 약 리스트 관련
    var serverAccessToken: String? = null
    var medicList: ArrayList<Medicine>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        // Preference Data 셋업
        pref = PreferenceManager.getDefaultSharedPreferences(mainActivity)
        editor = pref?.edit()
        serverAccessToken = pref?.getString("SERVER_ACCESS_TOKEN", "")
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
                        if (m.id != -1) newMedicItem(m)
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

        // 아이템
//        newMedicItem("감기약", 1)
//        newMedicItem("medic's name", 2)
//        newMedicItem("medic's name", 3)

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
                Toast.makeText(requireActivity(), "${item.text}", Toast.LENGTH_SHORT).show()

                view.invalidate()

                val v = event.localState as View
                val owner = v.parent as ViewGroup
                owner.removeView(v)
                val destination = view as LinearLayout
                destination.addView(v)

//                v.visibility = View.VISIBLE
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
    private fun newMedicItem(medic: Medicine) {
//        Log.d("log", "$medic")
        var medicView = LayoutInflater.from(requireContext()).inflate(R.layout.device_tab_edit_view_item, null)
        medicView.findViewById<TextView>(R.id.deviceMedicText).text = medic.customName
        medicView.setOnLongClickListener {
            val item = ClipData.Item("${medic.id} ${medic.customName}")
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(medic.krName, mimeTypes, item)

            val dragShadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data, dragShadowBuilder, it, 0)

//            it.visibility = View.INVISIBLE
            true
        }
        val medicineListIconImageView = medicView.findViewById<ImageView>(R.id.deviceMedicImage)
        when(medic.medicineShape) {
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
        layout.findViewById<LinearLayout>(R.id.medicineScrollLayout).addView(medicView)
//        medicView.invalidate()
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
}
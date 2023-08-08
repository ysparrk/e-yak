package com.a103.eyakrev1

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
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

class DeviceFragment : Fragment() {

    lateinit var mainActivity: MainActivity

    // Preference
    var pref: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    // layout
    private lateinit var layout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        // Preference Data 셋업
        pref = requireContext().applicationContext.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        editor = pref?.edit()
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

        // 드랍 리스너
        layout.findViewById<LinearLayout>(R.id.btEditCell1).setOnDragListener(dragListener)
        layout.findViewById<LinearLayout>(R.id.btEditCell2).setOnDragListener(dragListener)
        layout.findViewById<LinearLayout>(R.id.btEditCell3).setOnDragListener(dragListener)
        layout.findViewById<LinearLayout>(R.id.btEditCell4).setOnDragListener(dragListener)
        layout.findViewById<LinearLayout>(R.id.btEditCell5).setOnDragListener(dragListener)

        // 아이템 존재 여부 (임시)
        layout.findViewById<TextView>(R.id.medicineScrollNonText).visibility = View.GONE
        layout.findViewById<HorizontalScrollView>(R.id.medicineScroll).visibility = View.VISIBLE
        // 아이템
        newMedicItem("감기약", 1)
        newMedicItem("medic's name", 2)
        newMedicItem("medic's name", 3)

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
//
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
    private fun newMedicItem(medicName: String, num: Int) {
        var medicView = LayoutInflater.from(requireContext()).inflate(R.layout.device_tab_edit_view_item, null)
        //medicView.findViewById<ImageView>(R.id.deviceMedicImage) // 약 아이콘 이미지 세팅
        medicView.findViewById<TextView>(R.id.deviceMedicText).text = medicName
        medicView.setOnLongClickListener {
            val item = ClipData.Item("${medicName} $num")
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(medicName, mimeTypes, item)

            val dragShadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data, dragShadowBuilder, it, 0)

//            it.visibility = View.INVISIBLE
            true
        }
        layout.findViewById<LinearLayout>(R.id.medicineScrollLayout).addView(medicView)
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
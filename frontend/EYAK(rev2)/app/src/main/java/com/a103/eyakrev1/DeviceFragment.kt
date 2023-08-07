package com.a103.eyakrev1

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Layout
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

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

        // 리스너
        layout.findViewById<ImageView>(R.id.btEditCell1).setOnDragListener(dragListener)
        layout.findViewById<ImageView>(R.id.btEditCell2).setOnDragListener(dragListener)
        layout.findViewById<ImageView>(R.id.btEditCell3).setOnDragListener(dragListener)
        layout.findViewById<ImageView>(R.id.btEditCell4).setOnDragListener(dragListener)
        layout.findViewById<ImageView>(R.id.btEditCell5).setOnDragListener(dragListener)
        // 아이템
        layout.findViewById<ImageView>(R.id.testEditImage1).setOnLongClickListener {
            val clipText = "This is clip data text"
            val item = ClipData.Item(clipText)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val data = ClipData(clipText, mimeTypes, item)

            val dragShadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(data, dragShadowBuilder, it, 0)

            it.visibility = View.INVISIBLE
            true
        }

        return layout
    }

    // 드래그 리스너
    val dragListener = View.OnDragListener { view, event ->
        when(event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> true
            DragEvent.ACTION_DRAG_EXITED -> {
                view.invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                val item = event.clipData.getItemAt(0)
                val dragData = item.text
                Toast.makeText(requireActivity(), "$dragData", Toast.LENGTH_SHORT).show()

                view.invalidate()

                val v = event.localState as View
                val owner = v.parent as ViewGroup
                owner.removeView(v)
                val destination = view as LinearLayout
                destination.addView(v)
                v.visibility = View.VISIBLE
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                view.invalidate()
                true
            }
            else -> false
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
}
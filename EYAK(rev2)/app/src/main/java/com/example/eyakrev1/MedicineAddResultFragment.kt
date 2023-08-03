package com.example.eyakrev1

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.setFragmentResultListener

class MedicineAddResultFragment : Fragment() {

    lateinit var mainActivity: MainActivity

    var result: String = ""
    var result2: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // fragment-ktx artifact 코틀린 extension을 gradle에 추가해주면 리스너를 사용할 수 있게 됩니다.
        setFragmentResultListener("requestKey") { requestKey, bundle ->
            // 결과 값을 받는 곳입니다.
            result = bundle.getString("bundleKey", "") ?: ""
            result2 = bundle.getString("data2", "") ?: ""
            // TextView의 텍스트를 결과 값으로 설정합니다.
            view?.findViewById<TextView>(R.id.tmpText)?.text = result + result2
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layout = inflater.inflate(R.layout.fragment_medicine_add_result, container, false)

        layout.findViewById<TextView>(R.id.tmpText).text = result
        // Inflate the layout for this fragment
        return layout
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }

}
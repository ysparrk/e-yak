package com.example.eyakrev1

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult

class MedicineAddFragment : Fragment() {

    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layout = inflater.inflate(R.layout.fragment_medicine_add, container, false)

        layout.findViewById<Button>(R.id.resultBtn).setOnClickListener {
            val resultValue = layout.findViewById<EditText>(R.id.medicineNameInput).text.toString() // 원하는 결과 값을 설정
            val resultValue2 = layout.findViewById<EditText>(R.id.diseaseNameInput).text.toString()
            val resultBundle = Bundle()
            resultBundle.putString("bundleKey", resultValue)
            resultBundle.putString("data2", resultValue2)
            setFragmentResult("requestKey", resultBundle) // "requestKey"를 key로 사용하여 결과 설정

            mainActivity!!.gotoAddMedicineRsult()
        }

        return layout
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }

}
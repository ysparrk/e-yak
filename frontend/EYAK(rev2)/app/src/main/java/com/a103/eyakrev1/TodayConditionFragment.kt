package com.a103.eyakrev1

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.a103.eyakrev1.databinding.FragmentTodayConditionBinding

class TodayConditionFragment : Fragment() {

    private val conditionState: Array<Boolean> = arrayOf(false, false, false)
    private val symptom: MutableList<String> = mutableListOf("증상 없음", "속쓰림", "두드러기", "호흡곤란", "구토", "발진", "가려움증", "저림")
    private val symptomState: MutableList<Boolean> = mutableListOf(false, false, false, false, false, false, false, false)

    private lateinit var mainActivity: MainActivity

    private val activeColor: String = "#FFC9DBB2"
    private val nonColor: String = "#00000000"

    private lateinit var binding: FragmentTodayConditionBinding
    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTodayConditionBinding.inflate(inflater, container, false)

        init()

        binding.badLinearLayout.setOnClickListener {
            conditionState[0] = true
            conditionState[1] = false
            conditionState[2] = false

            colorChange()
        }

        binding.normalLinearLayout.setOnClickListener {
            conditionState[0] = false
            conditionState[1] = true
            conditionState[2] = false

            colorChange()
        }

        binding.goodLinearLayout.setOnClickListener {
            conditionState[0] = false
            conditionState[1] = false
            conditionState[2] = true

            colorChange()
        }

        binding.symptom0.setOnClickListener {
            symptomState[0] = true

            for(i in 1..7) {
                if(symptomState[i]) symptomState[i] = false
            }

            colorChange()
        }

        binding.symptom1.setOnClickListener {
            if(symptomState[0]) symptomState[0] = false

            symptomState[1] = true

            colorChange()
        }

        binding.symptom2.setOnClickListener {
            if(symptomState[0]) symptomState[0] = false

            symptomState[2] = true

            colorChange()
        }

        binding.symptom3.setOnClickListener {
            if(symptomState[0]) symptomState[0] = false

            symptomState[3] = true

            colorChange()
        }

        binding.symptom4.setOnClickListener {
            if(symptomState[0]) symptomState[0] = false

            symptomState[4] = true

            colorChange()
        }

        binding.symptom5.setOnClickListener {
            if(symptomState[0]) symptomState[0] = false

            symptomState[5] = true

            colorChange()
        }

        binding.symptom6.setOnClickListener {
            if(symptomState[0]) symptomState[0] = false

            symptomState[6] = true

            colorChange()
        }

        binding.symptom7.setOnClickListener {
            if(symptomState[0]) symptomState[0] = false

            symptomState[7] = true

            colorChange()
        }

        binding.submitButton.setOnClickListener {
            mainActivity!!.gotoAlarm()
        }

        return binding.root
    }

    private fun init() {
        binding.badLinearLayout.setBackgroundColor(Color.parseColor(nonColor))
        binding.normalLinearLayout.setBackgroundColor(Color.parseColor(nonColor))
        binding.goodLinearLayout.setBackgroundColor(Color.parseColor(nonColor))

        conditionState[0] = false
        conditionState[1] = false
        conditionState[2] = false

        binding.symptom0.text = symptom[0]
        binding.symptom1.text = symptom[1]
        binding.symptom2.text = symptom[2]
        binding.symptom3.text = symptom[3]
        binding.symptom4.text = symptom[4]
        binding.symptom5.text = symptom[5]
        binding.symptom6.text = symptom[6]
        binding.symptom7.text = symptom[7]

        symptomState[0] = false
        symptomState[1] = false
        symptomState[2] = false
        symptomState[3] = false
        symptomState[4] = false
        symptomState[5] = false
        symptomState[6] = false
        symptomState[7] = false
    }

    private fun colorChange() {
        binding.badLinearLayout.setBackgroundColor(Color.parseColor(if(conditionState[0]) activeColor else nonColor))
        binding.normalLinearLayout.setBackgroundColor(Color.parseColor(if(conditionState[1]) activeColor else nonColor))
        binding.goodLinearLayout.setBackgroundColor(Color.parseColor(if(conditionState[2]) activeColor else nonColor))

        binding.symptom0.setBackgroundColor(Color.parseColor(if(symptomState[0]) activeColor else nonColor))
        binding.symptom1.setBackgroundColor(Color.parseColor(if(symptomState[1]) activeColor else nonColor))
        binding.symptom2.setBackgroundColor(Color.parseColor(if(symptomState[2]) activeColor else nonColor))
        binding.symptom3.setBackgroundColor(Color.parseColor(if(symptomState[3]) activeColor else nonColor))
        binding.symptom4.setBackgroundColor(Color.parseColor(if(symptomState[4]) activeColor else nonColor))
        binding.symptom5.setBackgroundColor(Color.parseColor(if(symptomState[5]) activeColor else nonColor))
        binding.symptom6.setBackgroundColor(Color.parseColor(if(symptomState[6]) activeColor else nonColor))
        binding.symptom7.setBackgroundColor(Color.parseColor(if(symptomState[7]) activeColor else nonColor))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // 2. Context를 액티비티로 형변환해서 할당
        mainActivity = context as MainActivity
    }

}
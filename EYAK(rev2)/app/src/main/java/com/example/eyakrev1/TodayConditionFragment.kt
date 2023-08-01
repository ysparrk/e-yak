package com.example.eyakrev1

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eyakrev1.databinding.ActivityMainBinding
import com.example.eyakrev1.databinding.FragmentTodayConditionBinding

class TodayConditionFragment : Fragment() {

    private val conditionState: Array<Boolean> = arrayOf(false, false, false)

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    lateinit var mainActivity: MainActivity

    private val activeColor: String = "#FFF3F2C1"
    private val nonColor: String = "#00000000"

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentTodayConditionBinding.inflate(inflater, container, false)

        binding.badLinearLayout.setOnClickListener {
            conditionState[0] = true
            conditionState[1] = false
            conditionState[2] = false

            binding.badLinearLayout.setBackgroundColor(Color.parseColor(activeColor))
            binding.normalLinearLayout.setBackgroundColor(Color.parseColor(nonColor))
            binding.goodLinearLayout.setBackgroundColor(Color.parseColor(nonColor))
        }

        binding.normalLinearLayout.setOnClickListener {
            conditionState[0] = false
            conditionState[1] = true
            conditionState[2] = false
        }

        binding.goodLinearLayout.setOnClickListener {
            conditionState[0] = false
            conditionState[1] = true
            conditionState[2] = false
        }

        return binding.root
    }



}
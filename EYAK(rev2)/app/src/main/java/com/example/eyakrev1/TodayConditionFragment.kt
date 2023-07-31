package com.example.eyakrev1

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eyakrev1.databinding.ActivityMainBinding
import com.example.eyakrev1.databinding.FragmentTodayConditionBinding

class TodayConditionFragment : Fragment() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    lateinit var mainActivity: MainActivity

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentTodayConditionBinding.inflate(inflater, container, false)



        return binding.root
    }
}
package com.example.eyakrev1

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.eyakrev1.databinding.ActivityMainBinding
import com.example.eyakrev1.databinding.AlarmTabMainBinding
import java.time.LocalDate


class AlarmFragment : Fragment() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    var targetDay: LocalDate = LocalDate.now()
    val red: String = "#FF9B9B"
    val green: String = "#E3F2C1"
    val gray: String = "#DDE6ED"

    var medicineAlarmList = arrayListOf<MedicineAlarm>()

    // https://curryyou.tistory.com/386
    // 1. Context를 할당할 변수를 프로퍼티로 선언(어디서든 사용할 수 있게)
    lateinit var mainActivity: MainActivity

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        init()

        val binding = AlarmTabMainBinding.inflate(inflater, container, false)

        binding.todayDate.text = "${targetDay.monthValue.toString()} / ${targetDay.dayOfMonth.toString()}"
        binding.yesterdayDate.text = "${targetDay.plusDays(-1).monthValue.toString()} / ${targetDay.plusDays(-1).dayOfMonth.toString()}"
        binding.tomorrowDate.text = "${targetDay.plusDays(1).monthValue.toString()} / ${targetDay.plusDays(1).dayOfMonth.toString()}"

        for (i in 1..10) {
            // 임시 데이터 넣기
            val medicineAlarm = MedicineAlarm(medicineIcon = "eyak_logo", medicineTime = "오후 01:30", medicineName = "약이름 $i")
            medicineAlarmList.add(medicineAlarm)
        }

        val alarmListAdapter = AlarmListAdapter(mainActivity, medicineAlarmList)
        binding.alramListView.findViewById<ListView>(R.id.alramListView)
        binding.alramListView.adapter = alarmListAdapter

        binding.yesterdayFrameLayout.setOnClickListener {

            targetDay = targetDay.plusDays(-1)

            binding.todayDate.text = "${targetDay.monthValue.toString()} / ${targetDay.dayOfMonth.toString()}"
            binding.yesterdayDate.text = "${targetDay.plusDays(-1).monthValue.toString()} / ${targetDay.plusDays(-1).dayOfMonth.toString()}"
            binding.tomorrowDate.text = "${targetDay.plusDays(1).monthValue.toString()} / ${targetDay.plusDays(1).dayOfMonth.toString()}"
        }

        binding.tomorrowFrameLayout.setOnClickListener {

            targetDay = targetDay.plusDays(1)

            binding.todayState.setColorFilter(Color.parseColor(red))
            binding.todayDate.text = "${targetDay.monthValue.toString()} / ${targetDay.dayOfMonth.toString()}"
            binding.yesterdayDate.text = "${targetDay.plusDays(-1).monthValue.toString()} / ${targetDay.plusDays(-1).dayOfMonth.toString()}"
            binding.tomorrowDate.text = "${targetDay.plusDays(1).monthValue.toString()} / ${targetDay.plusDays(1).dayOfMonth.toString()}"
        }

        binding.conditionLinearLayout.setOnClickListener {
            mainActivity!!.gotoTodayCondition()

        }

        return binding.root


    }
    private fun init() {
        this.targetDay = LocalDate.now()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // 2. Context를 액티비티로 형변환해서 할당
        mainActivity = context as MainActivity
    }
}
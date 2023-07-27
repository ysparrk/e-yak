package com.example.eyakrev1

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment

class AlarmFragment : Fragment() {

    var medicineAlarmList = arrayListOf<MedicineAlarm>()

    // https://curryyou.tistory.com/386
    // 1. Context를 할당할 변수를 프로퍼티로 선언(어디서든 사용할 수 있게)
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        for (i in 1..10) {
            // 임시 데이터 넣기
            val medicineAlarm = MedicineAlarm(medicineIcon = "eyak_logo", medicineTime = "오후 01:30", medicineName = "약이름 $i")
            medicineAlarmList.add(medicineAlarm)
        }

        val layout = inflater.inflate(R.layout.alarm_tab_main, container, false)

        val alarmListAdapter = AlarmListAdapter(mainActivity, medicineAlarmList)
        val alarmListView = layout.findViewById<ListView>(R.id.alramListView)
        alarmListView?.adapter = alarmListAdapter

        return layout
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // 2. Context를 액티비티로 형변환해서 할당
        mainActivity = context as MainActivity
    }
}
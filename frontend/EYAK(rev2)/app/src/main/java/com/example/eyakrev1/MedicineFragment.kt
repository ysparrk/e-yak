package com.example.eyakrev1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.eyakrev1.databinding.AlarmTabMainBinding
import com.example.eyakrev1.databinding.MedicineTabMainBinding
import kotlinx.coroutines.NonDisposableHandle.parent


class MedicineFragment : Fragment() {

    var medicineList = arrayListOf<Medicine>(
        Medicine(medicineId = 1, medicineIcon = "eyak_logo", medicineName = "감기약"),
        Medicine(medicineId = 2, medicineIcon = "eyak_logo", medicineName = "두통약"),
        Medicine(medicineId = 3, medicineIcon = "eyak_logo", medicineName = "비타민"),
        Medicine(medicineId = 4, medicineIcon = "eyak_logo", medicineName = "항생제"),
        Medicine(medicineId = 5, medicineIcon = "eyak_logo", medicineName = "지금이:약"),
        Medicine(medicineId = 6, medicineIcon = "eyak_logo", medicineName = "아직아니:약"),
        Medicine(medicineId = 7, medicineIcon = "eyak_logo", medicineName = "곧이:약"),
        Medicine(medicineId = 8, medicineIcon = "eyak_logo", medicineName = "지금이:약"),
        Medicine(medicineId = 9, medicineIcon = "eyak_logo", medicineName = "아직아니:약"),
        Medicine(medicineId = 10, medicineIcon = "eyak_logo", medicineName = "곧이:약"),

    )

    // https://curryyou.tistory.com/386
    // 1. Context를 할당할 변수를 프로퍼티로 선언(어디서든 사용할 수 있게)
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layout = inflater.inflate(R.layout.medicine_tab_main, container, false)

        medicineList.add(Medicine(medicineId = -1, medicineIcon = "eyak_logo", medicineName = "빈 공간"))

        val medicineListAdapter = MedicineListAdapter(mainActivity, medicineList)
        medicineListAdapter.mainActivity = mainActivity

        val medicineListView = layout.findViewById<ListView>(R.id.medicineListView)

        medicineListView.adapter = medicineListAdapter

        layout.findViewById<ImageView>(R.id.medicinAdd).setOnClickListener {
            mainActivity!!.gotoAddMedicine()
        }

        layout.findViewById<ImageView>(R.id.medicineDetailCalendar).setOnClickListener {
            mainActivity!!.gotoMyCalendar()
        }

        return layout
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // 2. Context를 액티비티로 형변환해서 할당
        mainActivity = context as MainActivity
    }
}
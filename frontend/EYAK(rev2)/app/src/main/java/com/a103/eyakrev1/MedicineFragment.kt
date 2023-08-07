package com.a103.eyakrev1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MedicineFragment : Fragment() {

    val api = EyakService.create()

    // https://curryyou.tistory.com/386
    // 1. Context를 할당할 변수를 프로퍼티로 선언(어디서든 사용할 수 있게)
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layout = inflater.inflate(R.layout.medicine_tab_main, container, false)

        val pref = PreferenceManager.getDefaultSharedPreferences(mainActivity)
        val serverAccessToken = pref.getString("SERVER_ACCESS_TOKEN", "")

        var medicineList: ArrayList<Medicine>? = arrayListOf<Medicine>()

        api.getAllPrescriptions(Authorization= "Bearer ${serverAccessToken}").enqueue(object: Callback<ArrayList<Medicine>> {
            override fun onResponse(call: Call<ArrayList<Medicine>>, response: Response<ArrayList<Medicine>>) {
                Log.d("log", response.toString())
                Log.d("log", response.body().toString())

                if (response.code() == 401) {
                    Log.d("log", "인증되지 않은 사용자입니다")
                } else if (response.code() == 200) {
                    medicineList = response.body()

                    medicineList?.add(Medicine())

                    val medicineListAdapter = MedicineListAdapter(mainActivity, medicineList)
                    medicineListAdapter.mainActivity = mainActivity

                    val medicineListView = layout.findViewById<ListView>(R.id.medicineListView)

                    medicineListView.adapter = medicineListAdapter
                }
            }

            override fun onFailure(call: Call<ArrayList<Medicine>>, t: Throwable) {

            }
        })

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
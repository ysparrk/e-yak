package com.a103.eyakrev1

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MedicineFragment : Fragment() {

    val api = EyakService.create()

    // https://curryyou.tistory.com/386
    // 1. Context를 할당할 변수를 프로퍼티로 선언(어디서든 사용할 수 있게)
    lateinit var mainActivity: MainActivity

    private lateinit var viewModel: MedicineListAdapter.MedicineClickedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layout = inflater.inflate(R.layout.medicine_tab_main, container, false)


        val pref = PreferenceManager.getDefaultSharedPreferences(mainActivity)
        val serverAccessToken = pref.getString("SERVER_ACCESS_TOKEN", "")

        var medicineList: ArrayList<Medicine>? = arrayListOf<Medicine>()

        viewModel = ViewModelProvider(this).get(MedicineListAdapter.MedicineClickedViewModel::class.java)
        // LiveData를 관찰하고 데이터가 변경되었을 때 작업을 수행합니다.
        viewModel.selectedMedicineId.observe(viewLifecycleOwner) { medicineId ->
            // medicineId를 사용하여 상세 정보 Fragment로 이동할 때 bundle에 담아서 전달하자
            val bundle = Bundle()
            bundle.putInt("clickedMedicineId", medicineId)
            setFragmentResult("medicineDetailClicked", bundle)

            mainActivity!!.gotoMedicineDetail()
        }

        api.getAllPrescriptions(Authorization= "Bearer ${serverAccessToken}").enqueue(object: Callback<ArrayList<Medicine>> {
            override fun onResponse(call: Call<ArrayList<Medicine>>, response: Response<ArrayList<Medicine>>) {

                if (response.code() == 401) {
                    Log.d("log", "인증되지 않은 사용자입니다")
                } else if (response.code() == 200) {
                    medicineList = response.body()

                    medicineList?.add(Medicine())

                    val medicineListAdapter = MedicineListAdapter(mainActivity, medicineList, viewModel)
                    medicineListAdapter.mainActivity = mainActivity

                    val medicineListView = layout.findViewById<ListView>(R.id.medicineListView)

                    medicineListView.adapter = medicineListAdapter

                    if (medicineList?.size == 1) {
                        // 아무 것도 없는 경우
                        val emptyLinearLayout = layout.findViewById<LinearLayout>(R.id.emptyMedicineLinearLayout)
                        emptyLinearLayout.visibility = View.VISIBLE
                        medicineListView.visibility = View.GONE
                    }


                }
            }

            override fun onFailure(call: Call<ArrayList<Medicine>>, t: Throwable) {

            }
        })

        layout.findViewById<ImageView>(R.id.medicinAdd).setOnClickListener {
            mainActivity!!.gotoAddMedicine()
        }

        layout.findViewById<ImageView>(R.id.medicineDetailPrint).setOnClickListener {
            mainActivity!!.gotoMedicineSearch()
        }

        layout.findViewById<ImageView>(R.id.medicineDetailCalendar).setOnClickListener {

            val bundle = Bundle()

            bundle.putInt("requeteeId", -1)
            bundle.putString("customName", "")

            setFragmentResult("familyMonthlyDose", bundle)

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
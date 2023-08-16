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
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MedicineFragment : Fragment() {

    private var backPressedOnce = false
    private val doubleClickInterval: Long = 1000 // 1초

    lateinit var mainActivity: MainActivity

    private val api = EyakService.create()

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
                    Log.d("로그", "복약 정보 전체 조회 401 Unauthorized: AccessToken이 유효하지 않은 경우")
                } else if (response.code() == 200) {
                    Log.d("로그", "복약 정보 전체 조회 200 OK")
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
                Log.d("로그", "복약 정보 전체 조회 onFailure")
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

    private lateinit var callback: OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 2. Context를 액티비티로 형변환해서 할당
        mainActivity = context as MainActivity

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do something

                if (backPressedOnce) {
                    requireActivity().finishAffinity()
                    return
                }
                backPressedOnce = true

                GlobalScope.launch {
                    delay(doubleClickInterval)
                    backPressedOnce = false
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}
package com.a103.eyakrev1

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.preference.PreferenceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MedicineSearchFragment : Fragment() {

    lateinit var mainActivity: MainActivity

    val api = EyakService.create()

    var startSearchDate = LocalDate.now()
    var endSearchDate = LocalDate.now()

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_medicine_search, container, false)

        // 초기 정보 시작
        layout.findViewById<EditText>(R.id.searchStartYearInput).hint = (startSearchDate.year % 100).toString()
        layout.findViewById<EditText>(R.id.searchStartMonthInput).hint = startSearchDate.monthValue.toString()
        layout.findViewById<EditText>(R.id.searchStartDayInput).hint = startSearchDate.dayOfMonth.toString()

        layout.findViewById<EditText>(R.id.searchEndYearInput).hint = (endSearchDate.year % 100).toString()
        layout.findViewById<EditText>(R.id.searchEndMonthInput).hint = endSearchDate.monthValue.toString()
        layout.findViewById<EditText>(R.id.searchEndDayInput).hint = startSearchDate.dayOfMonth.toString()
        // 초기 정보 끝

        layout.findViewById<Button>(R.id.searchBtn).setOnClickListener {

            // 날짜 변경 확인 시작
            val startYear: Int = if(layout.findViewById<EditText>(R.id.searchStartYearInput).text.toString() == "") 2000 + layout.findViewById<EditText>(R.id.searchStartYearInput).hint.toString().toInt() else 2000 + layout.findViewById<EditText>(R.id.searchStartYearInput).text.toString().toInt()
            val startMonth: Int = if(layout.findViewById<EditText>(R.id.searchStartMonthInput).text.toString() == "") layout.findViewById<EditText>(R.id.searchStartMonthInput).hint.toString().toInt() else layout.findViewById<EditText>(R.id.searchStartMonthInput).text.toString().toInt()
            val startDay: Int = if(layout.findViewById<EditText>(R.id.searchStartDayInput).text.toString() == "") layout.findViewById<EditText>(R.id.searchStartDayInput).hint.toString().toInt() else layout.findViewById<EditText>(R.id.searchStartDayInput).text.toString().toInt()

            val endYear: Int = if(layout.findViewById<EditText>(R.id.searchEndYearInput).text.toString() == "") 2000 + layout.findViewById<EditText>(R.id.searchEndYearInput).hint.toString().toInt() else 2000 + layout.findViewById<EditText>(R.id.searchEndYearInput).text.toString().toInt()
            val endMonth: Int = if(layout.findViewById<EditText>(R.id.searchEndMonthInput).text.toString() == "") layout.findViewById<EditText>(R.id.searchEndMonthInput).hint.toString().toInt() else layout.findViewById<EditText>(R.id.searchEndMonthInput).text.toString().toInt()
            val endDay: Int = if(layout.findViewById<EditText>(R.id.searchEndDayInput).text.toString() == "") layout.findViewById<EditText>(R.id.searchEndDayInput).hint.toString().toInt() else layout.findViewById<EditText>(R.id.searchEndDayInput).text.toString().toInt()

            var dayChk: Boolean = true;

            if(startYear < 2000 || startYear > 2999) {
                Toast.makeText(mainActivity, "올바른 검색 시작 연도를 입력하세요(00 ~ 99)", Toast.LENGTH_SHORT).show()
                dayChk = false
            }
            else if(endYear < 2000 || endYear > 2999) {
                Toast.makeText(mainActivity, "올바른 검색 종료 연도를 입력하세요(00 ~ 99)", Toast.LENGTH_SHORT).show()
                dayChk = false
            }
            else if(startMonth < 1 || startMonth > 12) {
                Toast.makeText(mainActivity, "올바른 검색 시작 달을 입력하세요(1 ~ 12)", Toast.LENGTH_SHORT).show()
                dayChk = false
            }
            else if(endMonth < 1 || endMonth > 12) {
                Toast.makeText(mainActivity, "올바른 검색 종료 달을 입력하세요(1 ~ 12)", Toast.LENGTH_SHORT).show()
                dayChk = false
            }
            else {
                var lastStartDay: Int = LocalDate.of(startYear, startMonth, 1).plusDays(-1).dayOfMonth
                var lastEndDay: Int = LocalDate.of(endYear, endMonth, 1).plusDays(-1).dayOfMonth

                if(startDay < 1 || startDay > lastStartDay) {
                    Toast.makeText(mainActivity, "올바른 검색 시작 일을 입력하세요(1 ~ ${lastStartDay})", Toast.LENGTH_SHORT).show()
                    dayChk = false
                }
                else if(endDay < 1 || endDay > lastEndDay) {
                    Toast.makeText(mainActivity, "올바른 검색 종료 일을 입력하세요(1 ~ ${lastStartDay})", Toast.LENGTH_SHORT).show()
                    dayChk = false
                }
                else {
                    val startTmp: LocalDate = LocalDate.of(startYear, startMonth, startDay)
                    val endTmp: LocalDate = LocalDate.of(endYear, endMonth, endDay)

                    startSearchDate = startTmp
                    endSearchDate = endTmp

                    if(startSearchDate > endSearchDate) {
                        Toast.makeText(mainActivity, "검색 시작 일이 종료 일 보다 나중 입니다", Toast.LENGTH_SHORT).show()
                        dayChk = false
                    }
                }
            }
            // 날짜 변경 확인 끝

            // 서버에서 데이터 전송 받기 시작
            if(dayChk) {
                val pref = PreferenceManager.getDefaultSharedPreferences(mainActivity)
                val serverAccessToken = pref.getString("SERVER_ACCESS_TOKEN", "")   // 엑세스 토큰

                val searchStartString: String = startSearchDate.format(formatter) + "T00:00:00"
                val searchEndString: String = endSearchDate.format(formatter) + "T00:00:00"

                api.medicineSearch(Authorization = "Bearer ${serverAccessToken}", startDateTime = searchStartString, endDateTime = searchEndString).enqueue(object: Callback<MedicineSearchResponseBodyModel> {
                    override fun onResponse(call: Call<MedicineSearchResponseBodyModel>, response: Response<MedicineSearchResponseBodyModel>) {
                        if(response.code() == 200) {


                        }
                        else if(response.code() == 401) {

                        }
                    }
                    override fun onFailure(call: Call<MedicineSearchResponseBodyModel>, t: Throwable) {
                        t.printStackTrace()
                        Log.d("ㅁㅁㅇㄴㄹㄴㅇㅁㄹ",t.toString())
                    }
                })


            }



        }





        return layout
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }

}
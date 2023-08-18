package com.a103.eyakrev1

import android.content.Context
import android.graphics.Color
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.util.Calendar

class MyCalendarFragment : Fragment() {

    lateinit var mainActivity: MainActivity

    private val badFaceColor: String = "#AC6077"
    private val normalFaceColor: String = "#7466B4"
    private val goodFaceColor: String = "#9ED59B"

    private val symptomMap: Map<String, String> = mapOf("NO_SYMPTOMS" to "증상 없음", "HEADACHE" to "두통", "ABDOMINAL_PAIN" to "복통", "VOMITING" to "구토", "FEVER" to "발열", "DIARRHEA" to "설사", "INDIGESTION" to "소화불량", "COUGH" to "기침")

    private var DayList = arrayListOf<Dates>()

    private var today: LocalDate = LocalDate.now()
    private var targetDate: LocalDate = LocalDate.now()
    private var targetMonth = targetDate.monthValue
    private var targetYear = targetDate.year
    private var targetDay = targetDate.dayOfMonth

    lateinit var layout: View

    private val api = EyakService.create()

    var requeteeId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        layout = inflater.inflate(R.layout.fragment_my_calendar, container, false)

        val calendarIndicatorTextView = layout.findViewById<TextView>(R.id.tv_calendar_indicator)
        calendarIndicatorTextView.setText("${targetYear}년 ${targetMonth}월")

        setFragmentResultListener("familyMonthlyDose") { key , bundle -> // setFragmentResultListener("보낸 데이터 묶음 이름") {requestKey, bundle ->
            requeteeId = bundle.getInt("requeteeId", -1)
            val familyCustomName = bundle.getString("customName", "가족")

            // 이게 가족의 복약 한눈에 보기라면 //
            if (requeteeId != -1) {
                layout.findViewById<TextView>(R.id.calendarTitleTextView).text = "${familyCustomName} 복약 달력"
            }

            // 이번 달 로드
            LoadMonthCalendar(requeteeId, scrollPosition = kotlin.math.max(targetDay - 3, 0))

            val prevMonthBtn = layout.findViewById<ImageButton>(R.id.btn_monthPrev)
            val nextMonthBtn = layout.findViewById<ImageButton>(R.id.btn_monthNext)

            // 이전 달 클릭했을 때
            prevMonthBtn.setOnClickListener {
                targetDate = targetDate.minusMonths(1)
                LoadMonthCalendar(requeteeId, scrollPosition = 0)
            }

            // 다음 달 클릭했을 때
            nextMonthBtn.setOnClickListener {
                targetDate = targetDate.plusMonths(1)
                LoadMonthCalendar(requeteeId, scrollPosition = 0)
            }
        }

        return layout
    }

    private fun LoadMonthCalendar(requeteeId: Int = -1, scrollPosition: Int = 0) {
        // requesteeId 자신의 입장에서 누구 것을 보려고 하는지
        // -1 이면 자기 자신
        // 0 이상이면 보고자 하는 상대방의 memberId

        targetMonth = targetDate.monthValue
        targetYear = targetDate.year
        targetDay = targetDate.dayOfMonth

        val calendarIndicatorTextView = layout.findViewById<TextView>(R.id.tv_calendar_indicator)
        calendarIndicatorTextView.setText("${targetYear}년 ${targetMonth}월")

        DayList = arrayListOf<Dates>()

        val pref = PreferenceManager.getDefaultSharedPreferences(mainActivity)
        val serverAccessToken = pref.getString("SERVER_ACCESS_TOKEN", "")

        if (requeteeId == -1) {
            // 내꺼 조회
            api.getMyMonthlyDose(Authorization = "Bearer ${serverAccessToken}", yearMonth = targetDate.toString().substring(0 until 7)).enqueue(object:
                Callback<ArrayList<Dates>> {
                override fun onResponse(call: Call<ArrayList<Dates>>, response: Response<ArrayList<Dates>>) {
                    if(response.code() == 200) {
                        Log.d("로그", "약 복용량 조회(month, 달력 화면) 200 OK")
                        DayList = response.body()!!

                        val mySingleRowCalendarRecyclerView = layout.findViewById<RecyclerView>(R.id.mySingleRowCalendar)
                        val mySingleRowCalendarAdapter = mySingleRowCalendarListAdapter(DayList)

                        mySingleRowCalendarRecyclerView.adapter = mySingleRowCalendarAdapter
                        mySingleRowCalendarRecyclerView.scrollToPosition(scrollPosition)
                    }
                    else if(response.code() == 401) {
                        Log.d("로그", "약 복용량 조회(month, 달력 화면) 401 Unauthorized: AccessToken이 유효하지 않은 경우")
                    }
                    else if(response.code() == 400) {
                        Log.d("로그", "약 복용량 조회(month, 달력 화면) 400 Bad Request: 해당하는 member, date가 존재하지 않은 경우")
                    }
                }
                override fun onFailure(call: Call<ArrayList<Dates>>, t: Throwable) {
                    Log.d("로그", "약 복용량 조회(month, 달력 화면) onFailure")
                }
            })
        }
        else {
            // 가족꺼 조회
            api.getOthersMonthlyDose(Authorization = "Bearer ${serverAccessToken}", yearMonth = targetDate.toString().substring(0 until 7), requeteeId = requeteeId).enqueue(object: Callback<ArrayList<Dates>> {
                override fun onResponse(call: Call<ArrayList<Dates>>, response: Response<ArrayList<Dates>>) {
                    if(response.code() == 200) {
                        Log.d("로그", "약 복용량 조회(month, 달력 화면) 200 OK")
                        DayList = response.body()!!

                        val mySingleRowCalendarRecyclerView = layout.findViewById<RecyclerView>(R.id.mySingleRowCalendar)
                        val mySingleRowCalendarAdapter = mySingleRowCalendarListAdapter(DayList)

                        mySingleRowCalendarRecyclerView.adapter = mySingleRowCalendarAdapter
                        mySingleRowCalendarRecyclerView.scrollToPosition(scrollPosition)
                    }
                    else if(response.code() == 401) {
                        Log.d("로그", "약 복용량 조회(month, 달력 화면) 401 Unauthorized: AccessToken이 유효하지 않은 경우")
                    }
                    else if(response.code() == 400) {
                        Log.d("로그", "약 복용량 조회(month, 달력 화면) 400 Bad Request: 해당하는 member, date가 존재하지 않은 경우")
                    }
                }
                override fun onFailure(call: Call<ArrayList<Dates>>, t: Throwable) {
                    Log.d("로그", "약 복용량 조회(month, 달력 화면) onFailure")
                }
            })
        }
    }

    inner class mySingleRowCalendarListAdapter (val dayList: ArrayList<Dates>) : RecyclerView.Adapter<mySingleRowViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mySingleRowViewHolder {
            val view = layoutInflater.inflate(R.layout.single_row_calendar_day_item, parent, false)
            return mySingleRowViewHolder(view)
        }

        override fun onBindViewHolder(holder: mySingleRowViewHolder, position: Int) {
            val day = getItem(position)
            holder.bind(day.date.substring(8), day.fullDose, day.actualDose)
        }

        override fun getItemCount(): Int {
            return dayList.size
        }

        private fun getItem(position: Int): Dates {
            return dayList[position]
        }
    }

    inner class mySingleRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val myCalendarDayTextView = itemView.findViewById<TextView>(R.id.myCalendarDayTextView)
        private val myCalendarDayImageView = itemView.findViewById<ImageView>(R.id.myCalendarDayImageView)

        val forThoseWhoHaveAccessLayout = layout.findViewById<LinearLayout>(R.id.ForThoseWhoHaveAccessLayout)
        val detailedTitleTextView = layout.findViewById<TextView>(R.id.tv_calendar_day_indicator)

        val pref = PreferenceManager.getDefaultSharedPreferences(mainActivity)
        val serverAccessToken = pref.getString("SERVER_ACCESS_TOKEN", "")

        fun bind(dayText: String, fullDose: Int, actualDose: Int) {
            myCalendarDayTextView.text = dayText
            val itemDate: LocalDate = LocalDate.of(targetYear, targetMonth, dayText.toInt())

            if (itemDate.compareTo(today) > 0) {
                // 배경색으로 두자
                // 기본적으로 배경색으로 색칠되어 있지만, 리사이클러뷰가 이전꺼를 재활용하는 특성 때문에
                // 이렇게 강제로 설정하지 않으면 주기적으로 색칠된다
                myCalendarDayImageView.setColorFilter(resources.getColor(R.color.background))
            } else if (fullDose == 0) {
                myCalendarDayImageView.setColorFilter(resources.getColor(R.color.background))
            } else if (actualDose == fullDose) {
                // 전부 먹었을 때
                // 나중에 미래 시점은 걸러주면 좋을듯
                myCalendarDayImageView.setColorFilter(resources.getColor(R.color.green))
            } else if (actualDose == 0) {
                // 먹어야할 약이 있는데 하나도 안 먹었을 때
                myCalendarDayImageView.setColorFilter(resources.getColor(R.color.red))
            } else {
                // 일부만 먹었을 때
                myCalendarDayImageView.setColorFilter(resources.getColor(R.color.yellow))
            }

            myCalendarDayImageView.setOnClickListener {
                forThoseWhoHaveAccessLayout.visibility = View.VISIBLE

                val clickedDate = LocalDate.of(targetYear, targetMonth, dayText.toInt())
                val clickedDay = clickedDate.dayOfMonth

                if (requeteeId == -1) {
                    // 내 달력일 때
                    api.getMyDailyDetailInCalendar(Authorization = "Bearer ${serverAccessToken}", date = clickedDate.toString()).enqueue(object: Callback<medicineDetailsInCalendarResponseModel> {
                        override fun onResponse(call: Call<medicineDetailsInCalendarResponseModel>, response: Response<medicineDetailsInCalendarResponseModel>) {
                            if(response.code() == 200) {
                                Log.d("로그", "약 복용 상세 조회(day) 200 OK")
                                detailedTitleTextView.text = "${targetYear}년 ${targetMonth}월 ${clickedDay}일"
                                val medicineDetails = response.body()!!.medicineRoutineDateDtos

                                val medicineInCalendarListAdapter = MedicineInCalendarListAdapter(mainActivity, medicineDetails)
                                val medicineInCalendarListView = layout.findViewById<ListView>(R.id.medicineInCalendarListView)
                                medicineInCalendarListView?.adapter = medicineInCalendarListAdapter

                                val surveyDetails = response.body()!!.surveyContentDtos

                                // survey 팝업에서 띄울 데이터를 추출하자
                                var surveyResultEmotion = surveyDetails.contentEmotionResultResponse.choiceEmotion
                                var todayConditionSymptomText = ""
                                for (i in 0 .. surveyDetails.contentStatusResultResponse.selectedStatusChoices.size - 1) {
                                    val symptom = surveyDetails.contentStatusResultResponse.selectedStatusChoices[i]

                                    todayConditionSymptomText += symptomMap[symptom]

                                    if (i != surveyDetails.contentStatusResultResponse.selectedStatusChoices.size - 1) {
                                        todayConditionSymptomText += "\n"
                                    }
                                }
                                var otherIssueText = surveyDetails.contentTextResultResponse.text

//                                Log.d("log", surveyResultEmotion)

                                val dailySurveyButton = layout.findViewById<Button>(R.id.survey_button_in_calendar)
                                dailySurveyButton.visibility = View.VISIBLE
                                dailySurveyButton.setOnClickListener {
                                    val mDialogView = layoutInflater.inflate(R.layout.dialog_survey_in_calendar, null)

                                    val todayConditionEmotionImageView = mDialogView.findViewById<ImageView>(R.id.todayConditionEmotionImageView)
                                    val todayConditionSymptomTextView = mDialogView.findViewById<TextView>(R.id.todayConditionSymptomTextView)
                                    val otherIssueTextView = mDialogView.findViewById<TextView>(R.id.otherIssueTextView)

                                    if (surveyDetails.contentEmotionResultResponse.contentEmotionResultId != -1) {
                                        // 아직 설문이 입력되지 않은 경우는 -1이므로 그냥 넘어가자
                                        if (surveyResultEmotion == "BAD") {
                                            todayConditionEmotionImageView.setImageResource(R.drawable.baseline_sentiment_very_dissatisfied_24)
                                            todayConditionEmotionImageView.setColorFilter(Color.parseColor(badFaceColor))
                                        } else if (surveyResultEmotion == "SOSO") {
                                            todayConditionEmotionImageView.setImageResource(R.drawable.baseline_sentiment_neutral_24)
                                            todayConditionEmotionImageView.setColorFilter(Color.parseColor(normalFaceColor))
                                        } else if (surveyResultEmotion == "GOOD") {
                                            todayConditionEmotionImageView.setImageResource(R.drawable.baseline_sentiment_satisfied_alt_24)
                                            todayConditionEmotionImageView.setColorFilter(Color.parseColor(goodFaceColor))
                                        }

                                        todayConditionSymptomTextView.text = todayConditionSymptomText
                                        otherIssueTextView.text = otherIssueText
                                    }

                                    val mBuilder = AlertDialog.Builder(mainActivity)
                                        .setView(mDialogView)
                                        .setPositiveButton("확인") { dialog, _ ->
                                            // 확인 버튼을 눌렀을 때 수행할 동작
                                            dialog.dismiss() // 대화상자 닫기
                                        }
                                        .create()

                                    mBuilder.show()
                                }

                            }
                            else if(response.code() == 401) {
                                Log.d("로그", "약 복용 상세 조회(day) 401 Unauthorized: AccessToken이 유효하지 않은 경우")
                            }
                            else if(response.code() == 400) {
                                Log.d("로그", "약 복용 상세 조회(day) 400 Bad Request: 해당하는 member, date가 존재하지 않은 경우")
                            }
                            else if(response.code() == 403) {
                                Log.d("로그", "약 복용 상세 조회(day) 403 Forbidden: 상대방의 scope가 ALL이 아닌 경우(상대방이 전체 정보 공개로 설정하지 않은 경우)")
                                // 여기도 처리해주자
                                // 상대방의 scope가 ALL이 아닌 경우 (상대방이 전체 정보 공개로 설정하지 않은 경우)
                                detailedTitleTextView.text = "상대방이 공개하지 않았습니다"
                            }
                        }
                        override fun onFailure(call: Call<medicineDetailsInCalendarResponseModel>, t: Throwable) {
                            Log.d("로그", "약 복용 상세 조회(day) onFailure")
                        }
                    })
                }
                else {
                    // 가족의 달력일 때
                    api.getOthersDailyDetailInCalendar(Authorization = "Bearer ${serverAccessToken}", date = clickedDate.toString(), requeteeId = requeteeId).enqueue(object:
                        Callback<medicineDetailsInCalendarResponseModel> {
                        override fun onResponse(call: Call<medicineDetailsInCalendarResponseModel>, response: Response<medicineDetailsInCalendarResponseModel>) {
                            if(response.code() == 200) {
                                Log.d("로그", "약 복용 상세 조회(day) 200 OK")
                                detailedTitleTextView.text = "${targetYear}년 ${targetMonth}월 ${clickedDay}일"
                                val medicineDetails = response.body()!!.medicineRoutineDateDtos

                                val medicineInCalendarListAdapter = MedicineInCalendarListAdapter(mainActivity, medicineDetails)
                                val medicineInCalendarListView = layout.findViewById<ListView>(R.id.medicineInCalendarListView)
                                medicineInCalendarListView?.adapter = medicineInCalendarListAdapter

                                val surveyDetails = response.body()!!.surveyContentDtos

                                // survey 팝업에서 띄울 데이터를 추출하자
                                var surveyResultEmotion = surveyDetails.contentEmotionResultResponse.choiceEmotion
                                var todayConditionSymptomText = ""
                                for (i in 0 .. surveyDetails.contentStatusResultResponse.selectedStatusChoices.size - 1) {
                                    val symptom = surveyDetails.contentStatusResultResponse.selectedStatusChoices[i]

                                    todayConditionSymptomText += symptomMap[symptom]

                                    if (i != surveyDetails.contentStatusResultResponse.selectedStatusChoices.size - 1) {
                                        todayConditionSymptomText += "\n"
                                    }
                                }
                                var otherIssueText = surveyDetails.contentTextResultResponse.text

//                                Log.d("log", surveyResultEmotion)

                                val dailySurveyButton = layout.findViewById<Button>(R.id.survey_button_in_calendar)
                                dailySurveyButton.visibility = View.VISIBLE
                                dailySurveyButton.setOnClickListener {
                                    val mDialogView = layoutInflater.inflate(R.layout.dialog_survey_in_calendar, null)

                                    val todayConditionEmotionImageView = mDialogView.findViewById<ImageView>(R.id.todayConditionEmotionImageView)
                                    val todayConditionSymptomTextView = mDialogView.findViewById<TextView>(R.id.todayConditionSymptomTextView)
                                    val otherIssueTextView = mDialogView.findViewById<TextView>(R.id.otherIssueTextView)

                                    if (surveyDetails.contentEmotionResultResponse.contentEmotionResultId != -1) {
                                        // 아직 설문이 입력되지 않은 경우는 -1이므로 그냥 넘어가자
                                        if (surveyResultEmotion == "BAD") {
                                            todayConditionEmotionImageView.setImageResource(R.drawable.baseline_sentiment_very_dissatisfied_24)
                                            todayConditionEmotionImageView.setColorFilter(Color.parseColor(badFaceColor))
                                        } else if (surveyResultEmotion == "SOSO") {
                                            todayConditionEmotionImageView.setImageResource(R.drawable.baseline_sentiment_neutral_24)
                                            todayConditionEmotionImageView.setColorFilter(Color.parseColor(normalFaceColor))
                                        } else if (surveyResultEmotion == "GOOD") {
                                            todayConditionEmotionImageView.setImageResource(R.drawable.baseline_sentiment_satisfied_alt_24)
                                            todayConditionEmotionImageView.setColorFilter(Color.parseColor(goodFaceColor))
                                        }

                                        todayConditionSymptomTextView.text = todayConditionSymptomText
                                        otherIssueTextView.text = otherIssueText
                                    }

                                    val mBuilder = AlertDialog.Builder(mainActivity)
                                        .setView(mDialogView)
                                        .setPositiveButton("확인") { dialog, _ ->
                                            // 확인 버튼을 눌렀을 때 수행할 동작
                                            dialog.dismiss() // 대화상자 닫기
                                        }
                                        .create()

                                    mBuilder.show()
                                }

                            }
                            else if(response.code() == 401) {
                                Log.d("로그", "약 복용 상세 조회(day) 401 Unauthorized: AccessToken이 유효하지 않은 경우")
                            }
                            else if(response.code() == 400) {
                                Log.d("로그", "약 복용 상세 조회(day) 400 Bad Request: 해당하는 member, date가 존재하지 않은 경우")
                            }
                            else if(response.code() == 403) {
                                Log.d("로그", "약 복용 상세 조회(day) 403 Forbidden: 상대방의 scope가 ALL이 아닌 경우(상대방이 전체 정보 공개로 설정하지 않은 경우)")
                                // 여기도 처리해주자
                                // 상대방의 scope가 ALL이 아닌 경우 (상대방이 전체 정보 공개로 설정하지 않은 경우)
                                detailedTitleTextView.text = "상대방이 공개하지 않았습니다"
                            }
                        }
                        override fun onFailure(call: Call<medicineDetailsInCalendarResponseModel>, t: Throwable) {
                            Log.d("로그", "약 복용 상세 조회(day) onFailure")
                        }
                    })
                }
            }
        }
    }

    // 해당 연, 월의 총 날짜 수를 계산해주는 함수
    private fun getDaysInMonth(year: Int, month: Int): Int {
        return when (month - 1) {
            Calendar.JANUARY, Calendar.MARCH, Calendar.MAY, Calendar.JULY, Calendar.AUGUST, Calendar.OCTOBER, Calendar.DECEMBER -> 31
            Calendar.APRIL, Calendar.JUNE, Calendar.SEPTEMBER, Calendar.NOVEMBER -> 30
            Calendar.FEBRUARY -> if (year % 4 == 0 && year % 100 != 0  || year % 400 == 0) 29 else 28 // 윤년 계산
            else -> throw IllegalArgumentException("Invalid Month")
        }
    }

    private lateinit var callback: OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do something
                if (requeteeId == -1) {
                    mainActivity.gotoMedicine()
                } else {
                    mainActivity.gotoFamily()
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
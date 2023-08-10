package com.a103.eyakrev1

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.preference.PreferenceManager
import com.a103.eyakrev1.databinding.FragmentTodayConditionBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TodayConditionFragment : Fragment() {

    private lateinit var mainActivity: MainActivity

    private val api = EyakService.create()

    private var targetDate: LocalDate = LocalDate.now()

    private val conditionState: Array<Boolean> = arrayOf(false, false, false)
    private val symptom: MutableList<String> = mutableListOf("증상 없음", "두통", "복통", "구토", "발열", "설사", "소화불량", "기침")
    private val symptomEng: MutableList<String> = mutableListOf("NO_SYMPTOMS", "HEADACHE", "ABDOMINAL_PAIN", "VOMITING", "FEVER", "DIARRHEA", "INDIGESTION", "COUGH")
    private val symptomState: MutableList<Boolean> = mutableListOf(false, false, false, false, false, false, false, false)

    val todaySurveyContentId: Array<Long> = arrayOf(-1, -1, -1) // 각 문항에 대한 contentId 저장

    private val activeColor: String = "#FFC9DBB2"
    private val nonColor: String = "#00000000"

    private val badFaceColor: String = "#AC6077"
    private val normalFaceColor: String = "#7466B4"
    private val goodFaceColor: String = "#9ED59B"
    private val nonFaceColor: String = "#CFC3B5"

    private lateinit var binding: FragmentTodayConditionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener("todayConditionDate") { _, bundle -> // setFragmentResultListener("보낸 데이터 묶음 이름") {requestKey, bundle ->

            targetDate = LocalDate.parse(
                bundle.getString("sendDate", ""),
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodayConditionBinding.inflate(inflater, container, false)

        init()

        val pref = PreferenceManager.getDefaultSharedPreferences(mainActivity)
        val serverAccessToken = pref.getString("SERVER_ACCESS_TOKEN", "")   // 엑세스 토큰

        // 설문 할 준비가 되었는가?
        api.dailySurveyContents(Authorization = "Bearer ${serverAccessToken}", date = targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).enqueue(object: Callback<ArrayList<DailySurveyContentsBodyModel>> {
            override fun onResponse(call: Call<ArrayList<DailySurveyContentsBodyModel>>, response: Response<ArrayList<DailySurveyContentsBodyModel>>) {
                if(response.code() == 200) {    // 성공
                    val responseBody = response.body()
                    if(responseBody != null && responseBody.size > 0) {
                        // todaySurveyContentId[CHOICE_STATUS, CHOICE_EMOTION, TEXT]

                        for(i in 0..responseBody.size - 1) {
                            if(responseBody[i].surveyContentType == "CHOICE_STATUS") todaySurveyContentId[i] = responseBody[i].surveyContentId
                            else if(responseBody[i].surveyContentType == "CHOICE_EMOTION") todaySurveyContentId[i] = responseBody[i].surveyContentId
                            else if(responseBody[i].surveyContentType == "TEXT") todaySurveyContentId[i] = responseBody[i].surveyContentId
                        }

                        // 이미 설문 했던게 있는가?
                        api.dailySurveyResult(Authorization = "Bearer ${serverAccessToken}", date = targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).enqueue(object: Callback<DailySurveyResultBodyModel> {
                            override fun onResponse(call: Call<DailySurveyResultBodyModel>, response: Response<DailySurveyResultBodyModel>) {
                                if(response.code() == 200) {    // 성공
                                    val responseBodyChk = response.body()
                                    if(responseBodyChk != null) {
                                        val contentTextResultResponse = responseBodyChk.contentTextResultResponse
                                        val contentStatusResultResponse = responseBodyChk.contentStatusResultResponse
                                        val contentEmotionResultResponse = responseBodyChk.contentEmotionResultResponse

                                        if(contentTextResultResponse != null) {
                                            if (contentTextResultResponse.contentTextResultId != (-1).toLong()) {    // TEXT 응답이 있는 경우
                                                binding.etcSymptom.setText(contentTextResultResponse.text)
                                            }
                                        }


                                        if(contentStatusResultResponse != null) {
                                            Log.d("asdfa",contentStatusResultResponse.contentStatusResultId.toString() )
                                            if (contentStatusResultResponse.contentStatusResultId != (-1).toLong()) {    // CHOICE_STATUS 응답이 있는 경우
                                                for (status in contentStatusResultResponse.selectedStatusChoices) {

                                                }
                                            }
                                        }

                                        if(contentEmotionResultResponse != null) {

                                            if (contentEmotionResultResponse.contentEmotionResultId != (-1).toLong()) {  // CHOICE_EMOTION 응답이 있는 경우

                                            }
                                        }

                                        colorChange()
                                    }

                                }
                                else if(response.code() == 401) {   // AccessToken이 유효하지 않은 경우
                                    Toast.makeText(mainActivity, "AccessToken이 유효하지 않은 경우", Toast.LENGTH_SHORT).show()
                                }
                                else if(response.code() == 400) {   // 해당하는 Survey가 존재하지 않는 경우
                                    Toast.makeText(mainActivity, "해당하는 Survey가 존재하지 않는 경우", Toast.LENGTH_SHORT).show()
                                }
                            }
                            override fun onFailure(call: Call<DailySurveyResultBodyModel>, t: Throwable) {

                            }
                        })

                    }
                }
                else if(response.code() == 401) {   // AccessToken이 유효하지 않은 경우
                    Toast.makeText(mainActivity, "AccessToken이 유효하지 않은 경우", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ArrayList<DailySurveyContentsBodyModel>>, t: Throwable) {

            }
        })

        binding.badLinearLayout.setOnClickListener {
            conditionState[0] = true
            conditionState[1] = false
            conditionState[2] = false

            colorChange()
        }

        binding.normalLinearLayout.setOnClickListener {
            conditionState[0] = false
            conditionState[1] = true
            conditionState[2] = false

            colorChange()
        }

        binding.goodLinearLayout.setOnClickListener {
            conditionState[0] = false
            conditionState[1] = false
            conditionState[2] = true

            colorChange()
        }

        binding.symptom0.setOnClickListener {
            symptomState[0] = true

            for(i in 1..7) {
                if(symptomState[i]) symptomState[i] = false
            }

            colorChange()
        }

        binding.symptom1.setOnClickListener {
            if(symptomState[0]) symptomState[0] = false

            symptomState[1] = true

            colorChange()
        }

        binding.symptom2.setOnClickListener {
            if(symptomState[0]) symptomState[0] = false

            symptomState[2] = true

            colorChange()
        }

        binding.symptom3.setOnClickListener {
            if(symptomState[0]) symptomState[0] = false

            symptomState[3] = true

            colorChange()
        }

        binding.symptom4.setOnClickListener {
            if(symptomState[0]) symptomState[0] = false

            symptomState[4] = true

            colorChange()
        }

        binding.symptom5.setOnClickListener {
            if(symptomState[0]) symptomState[0] = false

            symptomState[5] = true

            colorChange()
        }

        binding.symptom6.setOnClickListener {
            if(symptomState[0]) symptomState[0] = false

            symptomState[6] = true

            colorChange()
        }

        binding.symptom7.setOnClickListener {
            if(symptomState[0]) symptomState[0] = false

            symptomState[7] = true

            colorChange()
        }

        binding.submitButton.setOnClickListener {
            mainActivity!!.gotoAlarm()
        }

        return binding.root
    }

    private fun init() {
        binding.badFace.setColorFilter(Color.parseColor(nonFaceColor))
        binding.normalFace.setColorFilter(Color.parseColor(nonFaceColor))
        binding.goodFace.setColorFilter(Color.parseColor(nonFaceColor))

        conditionState[0] = false
        conditionState[1] = false
        conditionState[2] = false

        binding.symptom0.text = symptom[0]
        binding.symptom1.text = symptom[1]
        binding.symptom2.text = symptom[2]
        binding.symptom3.text = symptom[3]
        binding.symptom4.text = symptom[4]
        binding.symptom5.text = symptom[5]
        binding.symptom6.text = symptom[6]
        binding.symptom7.text = symptom[7]

        symptomState[0] = false
        symptomState[1] = false
        symptomState[2] = false
        symptomState[3] = false
        symptomState[4] = false
        symptomState[5] = false
        symptomState[6] = false
        symptomState[7] = false
    }

    private fun colorChange() {
        binding.badFace.setColorFilter(Color.parseColor(if(conditionState[0]) badFaceColor else nonFaceColor))
        binding.normalFace.setColorFilter(Color.parseColor(if(conditionState[1]) normalFaceColor else nonFaceColor))
        binding.goodFace.setColorFilter(Color.parseColor(if(conditionState[2]) goodFaceColor else nonFaceColor))

        binding.symptom0.setBackgroundColor(Color.parseColor(if(symptomState[0]) activeColor else nonColor))
        binding.symptom1.setBackgroundColor(Color.parseColor(if(symptomState[1]) activeColor else nonColor))
        binding.symptom2.setBackgroundColor(Color.parseColor(if(symptomState[2]) activeColor else nonColor))
        binding.symptom3.setBackgroundColor(Color.parseColor(if(symptomState[3]) activeColor else nonColor))
        binding.symptom4.setBackgroundColor(Color.parseColor(if(symptomState[4]) activeColor else nonColor))
        binding.symptom5.setBackgroundColor(Color.parseColor(if(symptomState[5]) activeColor else nonColor))
        binding.symptom6.setBackgroundColor(Color.parseColor(if(symptomState[6]) activeColor else nonColor))
        binding.symptom7.setBackgroundColor(Color.parseColor(if(symptomState[7]) activeColor else nonColor))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // 2. Context를 액티비티로 형변환해서 할당
        mainActivity = context as MainActivity
    }

}
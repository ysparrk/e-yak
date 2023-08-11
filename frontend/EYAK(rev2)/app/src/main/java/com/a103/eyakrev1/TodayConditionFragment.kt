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

    private var targetDate: LocalDate = LocalDate.now() // targetDate: 조회 하고자 하는 설문의 날짜 정보

    // 보유 증상과 관련된 변수 시작
    private val symptom: ArrayList<String> = arrayListOf("증상 없음", "두통", "복통", "구토", "발열", "설사", "소화불량", "기침")   // symptom: 보유 증상 한글 -> 화면에 출력할 때 이 배열을 사용
    private val symptomEng: ArrayList<String> = arrayListOf("NO_SYMPTOMS", "HEADACHE", "ABDOMINAL_PAIN", "VOMITING", "FEVER", "DIARRHEA", "INDIGESTION", "COUGH")   // symptomEng: 보유 증상 영어 -> 서버에 보유 증상에 대한 값을 전송할 때 이 배열을 사용
    private val symptomMap: Map<String, Int> = mapOf("NO_SYMPTOMS" to 0, "HEADACHE" to 1, "ABDOMINAL_PAIN" to 2, "VOMITING" to 3, "FEVER" to 4, "DIARRHEA" to 5, "INDIGESTION" to 6, "COUGH" to 7)  // symptomMap: 서버에서 받은 영어로된 보유 증상을 한글로 변경
    private var symptomState: BooleanArray = booleanArrayOf(false, false, false, false, false, false, false, false) // symtomState: 보유 선택

    private val activeColor: String = "#C9DBB2"
    private val nonColor: String = "#00000000"
    // 보유 증상과 관련되 변수 끝

    // 컨디션과 관련된 변수
    private var conditionState: BooleanArray = booleanArrayOf(false, false, false)
    private val conditionMap: Map<String, Int> = mapOf("BAD" to 0, "SOSO" to 1, "GOOD" to 2)

    private val badFaceColor: String = "#AC6077"
    private val normalFaceColor: String = "#7466B4"
    private val goodFaceColor: String = "#9ED59B"
    private val nonFaceColor: String = "#CFC3B5"
    // 컨디션과 관련된 변수 끝

    var todaySurveyContentId: ArrayList<Long> = arrayListOf(-1, -1, -1) // 각 문항에 대한 contentId 저장 [CHOICE_STATUS, CHOICE_EMOTION, TEXT]
    var todaySurveyContentResultId: ArrayList<Long> = arrayListOf(-1, -1, -1)   // 설문에 응답한 적이 있다면 resultId가 들어가고 아니라면 -1, [Text, Status, Emotion]

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

        // 이미 설문 했던게 있는가?
        api.dailySurveyResult(Authorization = "Bearer ${serverAccessToken}", date = targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).enqueue(object: Callback<DailySurveyResultBodyModel> {
            override fun onResponse(call: Call<DailySurveyResultBodyModel>, response: Response<DailySurveyResultBodyModel>) {
                if(response.code() == 200) {    // 성공
                    val responseBodyChk = response.body()
                    if(responseBodyChk != null) {
                        val contentTextResultResponse = responseBodyChk.contentTextResultResponse
                        val contentStatusResultResponse = responseBodyChk.contentStatusResultResponse
                        val contentEmotionResultResponse = responseBodyChk.contentEmotionResultResponse

                        if(contentTextResultResponse != null) { // Text
                            todaySurveyContentResultId[0] = contentTextResultResponse.contentTextResultId   // contentResultId 등록
                            if (contentTextResultResponse.contentTextResultId != (-1).toLong()) {    // TEXT 응답이 있는 경우
                                binding.etcSymptom.setText(contentTextResultResponse.text)  // 응답으로 값 넣어주기
                            }
                        }

                        if(contentStatusResultResponse != null) {   // Status
                            todaySurveyContentResultId[1] = contentStatusResultResponse.contentStatusResultId   // contentResultId 등록
                            if (contentStatusResultResponse.contentStatusResultId != (-1).toLong()) {    // CHOICE_STATUS 응답이 있는 경우
                                for (status in contentStatusResultResponse.selectedStatusChoices) {
                                    symptomState[symptomMap[status]!!] = true   // null이 아님을 명시해 줌
                                }
                            }
                        }

                        if(contentEmotionResultResponse != null) {
                            todaySurveyContentResultId[2] = contentEmotionResultResponse.contentEmotionResultId
                            if (contentEmotionResultResponse.contentEmotionResultId != (-1).toLong()) {  // CHOICE_EMOTION 응답이 있는 경우
                                conditionState[conditionMap[contentEmotionResultResponse.choiceEmotion]!!] = true   // null이 아님을 명시해 줌
                            }
                        }

                        colorChange()   // 주어진 정보에 맞게 색상을 변경
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

            // 설문 할 준비가 되었는가?
            api.dailySurveyContents(Authorization = "Bearer ${serverAccessToken}", date = targetDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).enqueue(object: Callback<ArrayList<DailySurveyContentsBodyModel>> {
                override fun onResponse(call: Call<ArrayList<DailySurveyContentsBodyModel>>, response: Response<ArrayList<DailySurveyContentsBodyModel>>) {
                    if(response.code() == 200) {    // 성공
                        val responseBody = response.body()
                        Log.d("akfdskljasdfjkl;fda;k", responseBody?.size.toString())
                        if(responseBody != null && responseBody.size > 0) {
                            // todaySurveyContentId[CHOICE_STATUS, CHOICE_EMOTION, TEXT]

                            for(i in 0..responseBody.size - 1) {
                                if(responseBody[i].surveyContentType == "CHOICE_STATUS") todaySurveyContentId[0] = responseBody[i].surveyContentId
                                else if(responseBody[i].surveyContentType == "CHOICE_EMOTION") todaySurveyContentId[1] = responseBody[i].surveyContentId
                                else if(responseBody[i].surveyContentType == "TEXT") todaySurveyContentId[2] = responseBody[i].surveyContentId
                            }


                            // Text 문항 응답 기록 사적
                            // todaySurveyContentId 2, todaySurveyContentResultId 0
                            if(todaySurveyContentResultId[0] == (-1).toLong()) {    // 이전 응답이 없는 경우 -> Text 문항 응답 기록
                                val textData = ContentTextResultsBodyModel(
                                    text = binding.etcSymptom.text.toString()
                                )

                                api.contentTextResults(surveyContentId = todaySurveyContentId[2], Authorization = "Bearer ${serverAccessToken}", params = textData).enqueue(object : Callback<Void> {
                                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                        if (response.code() == 201) {    // 성공
                                            Toast.makeText(mainActivity, "TEXT 생성 성공", Toast.LENGTH_SHORT).show()
                                        } else if (response.code() == 401) {   // AccessToken이 유효하지 않은 경우
                                            Toast.makeText(mainActivity, "AccessToken이 유효하지 않은 경우", Toast.LENGTH_SHORT).show()
                                        } else if (response.code() == 400) {   // 해당하는 SurveyContent가 존재하지 않는 경우
                                            Toast.makeText(mainActivity, "해당하는 SurveyContent가 존재하지 않는 경우", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    override fun onFailure(call: Call<Void>, t: Throwable) {

                                    }
                                })
                            }
                            else {  // 이전 응답이 있는 경우 -> Text 문항 응답 수정
                                val textData = EditContentTextResultsBodyModel(
                                    contentTextResultId = todaySurveyContentResultId[0],
                                    text = binding.etcSymptom.text.toString()
                                )

                                api.editContentTextResults(surveyContentId = todaySurveyContentId[2], Authorization = "Bearer ${serverAccessToken}", params = textData).enqueue(object : Callback<Void> {
                                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                        if (response.code() == 200) {    // 성공
                                            Toast.makeText(mainActivity, "TEXT 수정 성공", Toast.LENGTH_SHORT).show()
                                        } else if (response.code() == 401) {   // AccessToken이 유효하지 않은 경우
                                            Toast.makeText(mainActivity, "AccessToken이 유효하지 않은 경우", Toast.LENGTH_SHORT).show()
                                        } else if (response.code() == 400) {   // 해당하는 SurveyContent가 존재하지 않는 경우
                                            Toast.makeText(mainActivity, "해당하는 SurveyContent가 존재하지 않는 경우", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    override fun onFailure(call: Call<Void>, t: Throwable) {

                                    }
                                })
                            }
                            // Text 문항 응답 기록 끝

                            // Emotion 응답 기록 시작
                            // todaySurveyContentId 1, todaySurveyContentResultId 2
                            if(todaySurveyContentResultId[2] == (-1).toLong()) {    // 이전 응답이 없는 경우 -> Emotion 문항 응답 기록
                                val emotionData = ContentEmotionResultsBodyModel(
                                    choiceEmotion = if (conditionState[0]) "BAD" else if (conditionState[1]) "SOSO" else if (conditionState[2]) "GOOD" else ""
                                )

                                api.contentEmotionResults(surveyContentId = todaySurveyContentId[1], Authorization = "Bearer ${serverAccessToken}", params = emotionData).enqueue(object : Callback<Void> {
                                    override fun onResponse(call: Call<Void>, response: Response<Void>) {

                                        Log.d("eijfioewoj", response.code().toString())
                                        if (response.code() == 201) {    // 성공
                                            Toast.makeText(mainActivity, "Emotion 생성 성공",Toast.LENGTH_SHORT).show()
                                        } else if (response.code() == 401) {   // AccessToken이 유효하지 않은 경우
                                            Toast.makeText(mainActivity, "AccessToken이 유효하지 않은 경우",Toast.LENGTH_SHORT).show()
                                        } else if (response.code() == 400) {   // 해당하는 SurveyContent가 존재하지 않는 경우
                                            Toast.makeText(mainActivity, "해당하는 SurveyContent가 존재하지 않는 경우", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<Void>, t: Throwable) {

                                    }
                                })
                            }
                            else {  // 이전 응답이 있는 경우 -> Emotion 문항 응답 수정
                                val emotionData = EditContentEmotionResults(
                                    contentEmotionResultId = todaySurveyContentResultId[2],
                                    choiceEmotion = if (conditionState[0]) "BAD" else if (conditionState[1]) "SOSO" else if (conditionState[2]) "GOOD" else ""
                                )

                                api.editContentEmotionResults(surveyContentId = todaySurveyContentId[1], Authorization = "Bearer ${serverAccessToken}", params = emotionData).enqueue(object : Callback<Void> {
                                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                        if (response.code() == 201) {    // 성공
                                            Toast.makeText(mainActivity, "Emotion 수정 성공",Toast.LENGTH_SHORT).show()
                                        } else if (response.code() == 401) {   // AccessToken이 유효하지 않은 경우
                                            Toast.makeText(mainActivity, "AccessToken이 유효하지 않은 경우",Toast.LENGTH_SHORT).show()
                                        } else if (response.code() == 400) {   // 해당하는 SurveyContent가 존재하지 않는 경우
                                            Toast.makeText(mainActivity, "해당하는 SurveyContent가 존재하지 않는 경우", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<Void>, t: Throwable) {

                                    }
                                })
                            }
                            // Emotion 응답 기록 끝

                            // Status 응답 기록 시작
                            // todaySurveyContentId 0, todaySurveyContentResultId 1
                            if(todaySurveyContentResultId[1] == (-1).toLong()) {    // 이전 응답이 없는 경우 -> Status 문항 응답 기록
                                val statusData = ContentStatusResultsBodyModel(
                                    selectedStatusChoices = arrayListOf()
                                )

                                // 데이터 동기화 및 넣기
                                for (i in 0..7) {
                                    if (symptomState[i]) statusData.selectedStatusChoices.add(symptomEng[i])
                                }

                                api.contentStatusResults(surveyContentId = todaySurveyContentId[0], Authorization = "Bearer ${serverAccessToken}", params = statusData).enqueue(object : Callback<Void> {
                                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                        if (response.code() == 201) {    // 성공
                                            Toast.makeText(mainActivity, "Status 생성 성공",Toast.LENGTH_SHORT).show()
                                        } else if (response.code() == 401) {   // AccessToken이 유효하지 않은 경우
                                            Toast.makeText(mainActivity, "AccessToken이 유효하지 않은 경우", Toast.LENGTH_SHORT).show()
                                        } else if (response.code() == 400) {   // 해당하는 SurveyContent가 존재하지 않는 경우
                                            Toast.makeText(mainActivity, "해당하는 SurveyContent가 존재하지 않는 경우",Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    override fun onFailure(call: Call<Void>, t: Throwable) {

                                    }
                                })
                            }
                            else {  // 이전 응답이 없는 경우 -> Status 문항 응답 기록
                                val statusData = EditContentStatusBodyModel(
                                    contentStatusResultId = todaySurveyContentResultId[1],
                                    selectedStatusChoices = arrayListOf()
                                )

                                // 데이터 동기화 및 넣기
                                for (i in 0..7) {
                                    if (symptomState[i]) statusData.selectedStatusChoices.add(symptomEng[i])
                                }

                                api.editContentStatusResults(surveyContentId = todaySurveyContentId[0], Authorization = "Bearer ${serverAccessToken}", params = statusData).enqueue(object : Callback<Void> {
                                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                        if (response.code() == 200) {    // 성공
                                            Toast.makeText(mainActivity, "Status 수정 성공",Toast.LENGTH_SHORT).show()
                                        } else if (response.code() == 401) {   // AccessToken이 유효하지 않은 경우
                                            Toast.makeText(mainActivity, "AccessToken이 유효하지 않은 경우", Toast.LENGTH_SHORT).show()
                                        } else if (response.code() == 400) {   // 해당하는 SurveyContent가 존재하지 않는 경우
                                            Toast.makeText(mainActivity, "해당하는 SurveyContent가 존재하지 않는 경우",Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    override fun onFailure(call: Call<Void>, t: Throwable) {

                                    }
                                })
                            }
                            // Status 응답 기록 끝
                        }
                    }
                    else if(response.code() == 401) {   // AccessToken이 유효하지 않은 경우
                        Toast.makeText(mainActivity, "AccessToken이 유효하지 않은 경우", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<ArrayList<DailySurveyContentsBodyModel>>, t: Throwable) {

                }
            })
            mainActivity!!.gotoAlarm()
        }
        return binding.root
    }

    private fun init() {
        binding.badFace.setColorFilter(Color.parseColor(nonFaceColor))
        binding.normalFace.setColorFilter(Color.parseColor(nonFaceColor))
        binding.goodFace.setColorFilter(Color.parseColor(nonFaceColor))

        conditionState = booleanArrayOf(false, false, false)

        binding.symptom0.text = symptom[0]
        binding.symptom1.text = symptom[1]
        binding.symptom2.text = symptom[2]
        binding.symptom3.text = symptom[3]
        binding.symptom4.text = symptom[4]
        binding.symptom5.text = symptom[5]
        binding.symptom6.text = symptom[6]
        binding.symptom7.text = symptom[7]

        symptomState = booleanArrayOf(false, false, false, false, false, false, false, false)

        colorChange()
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
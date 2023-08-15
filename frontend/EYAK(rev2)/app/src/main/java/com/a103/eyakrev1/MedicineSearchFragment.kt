package com.a103.eyakrev1

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.media.Image
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils.substring
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.ScrollView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceManager
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.Font
import com.itextpdf.text.FontFactory
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Element
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MedicineSearchFragment : Fragment() {

    lateinit var mainActivity: MainActivity

    private val api = EyakService.create()

    private var startSearchDate = LocalDate.now()
    private var endSearchDate = LocalDate.now()

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val mappingMedicineRoutines: Map<String, String> = mapOf("BED_AFTER" to "기상 후", "BREAKFAST_BEFORE" to "아침 식사 전", "BREAKFAST_AFTER" to "아침 식사 후", "LUNCH_BEFORE" to "점심 식사 전", "LUNCH_AFTER" to "점심 식사 후", "DINNER_BEFORE" to "저녁 식사 전", "DINNER_AFTER" to "저녁 식사 후", "BED_BEFORE" to "잠 자기 전")

    private val mappingSymptom: Map<String, String> = mapOf("NO_SYMPTOMS" to "증상 없음", "HEADACHE" to "두통", "ABDOMINAL_PAIN" to "복통", "VOMITING" to "구토", "FEVER" to "발열", "DIARRHEA" to "설사", "INDIGESTION" to "소화불량", "COUGH" to "기침")

    private val evenRowColor: String = "#f0f0f0"
    private val oddRowColor: String = "#ffffff"

    private var dayChk: Boolean = true
    private var makePDFChk: Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_medicine_search, container, false)

        // 초기화 시작
        layout.findViewById<ImageView>(R.id.makePDFBtn).visibility = View.GONE
        // 초기화 끝

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

                val tableLayout = layout.findViewById<TableLayout>(R.id.searchMedicineTable)
                val tableLayout2 = layout.findViewById<TableLayout>(R.id.searchRecodeTable)

                tableLayout.removeAllViews()
                tableLayout2.removeAllViews()

                api.medicineSearch(Authorization = "Bearer ${serverAccessToken}", startDateTime = searchStartString, endDateTime = searchEndString).enqueue(object: Callback<MedicineSearchResponseBodyModel> {
                    override fun onResponse(call: Call<MedicineSearchResponseBodyModel>, response: Response<MedicineSearchResponseBodyModel>) {
                        makePDFChk = false
                        if(response.code() == 200) {
                            Log.d("로그", "pdf 출력 200 OK")
                            // 헤더 설정 시작
                            val headerRow = TableRow(requireContext())

                            val headerCell1 = TextView(requireContext())
                            headerCell1.text = "복용 시작일"
                            headerCell1.setTypeface(null, Typeface.BOLD)
                            headerCell1.setPadding(8, 8, 8, 8)
                            headerCell1.setBackgroundColor(Color.parseColor(oddRowColor))
                            headerRow.addView(headerCell1)

                            val headerCell2 = TextView(requireContext())
                            headerCell2.text = "복용 종료일"
                            headerCell2.setTypeface(null, Typeface.BOLD)
                            headerCell2.setPadding(8, 8, 8, 8)
                            headerCell2.setBackgroundColor(Color.parseColor(evenRowColor))
                            headerRow.addView(headerCell2)

                            val headerCell3 = TextView(requireContext())
                            headerCell3.text = "투여약 이름"
                            headerCell3.setTypeface(null, Typeface.BOLD)
                            headerCell3.setPadding(8, 8, 8, 8)
                            headerCell3.setBackgroundColor(Color.parseColor(oddRowColor))
                            headerRow.addView(headerCell3)

                            val headerCell4 = TextView(requireContext())
                            headerCell4.text = "질환 명"
                            headerCell4.setTypeface(null, Typeface.BOLD)
                            headerCell4.setPadding(8, 8, 8, 8)
                            headerCell4.setBackgroundColor(Color.parseColor(evenRowColor))
                            headerRow.addView(headerCell4)

                            val headerCell5 = TextView(requireContext())
                            headerCell5.text = "복용 시간"
                            headerCell5.setTypeface(null, Typeface.BOLD)
                            headerCell5.setPadding(8, 8, 8, 8)
                            headerCell5.setBackgroundColor(Color.parseColor(oddRowColor))
                            headerRow.addView(headerCell5)

                            tableLayout.addView(headerRow)
                            // 헤더 설정 끝

                            // 내용 추가 시작
                            val prescriptionList: ArrayList<PrescriptionListModel> = response.body()!!.prescriptionList

                            for(i in 0..prescriptionList.size - 1) {
                                makePDFChk = true
                                val row = TableRow(requireContext())
                                val layoutParams = TableLayout.LayoutParams(
                                    TableLayout.LayoutParams.MATCH_PARENT,
                                    TableLayout.LayoutParams.WRAP_CONTENT
                                )
                                row.layoutParams = layoutParams

                                val startDateCell = TextView(requireContext())
                                startDateCell.text = prescriptionList[i].startDateTime.substring(0, 10)
                                startDateCell.setPadding(8, 8, 8, 8)
                                startDateCell.setBackgroundColor(Color.parseColor(oddRowColor))
                                row.addView(startDateCell)

                                val endDateCell = TextView(requireContext())
                                endDateCell.text = prescriptionList[i].endDateTime.substring(0, 10)
                                endDateCell.setPadding(8, 8, 8, 8)
                                endDateCell.setBackgroundColor(Color.parseColor(evenRowColor))
                                row.addView(endDateCell)

                                val krName = TextView(requireContext())
                                krName.text = prescriptionList[i].krName
                                krName.setPadding(8, 8, 8, 8)
                                krName.setBackgroundColor(Color.parseColor(oddRowColor))
                                row.addView(krName)

                                val customName = TextView(requireContext())
                                customName.text = prescriptionList[i].customName
                                customName.setPadding(8, 8, 8, 8)
                                customName.setBackgroundColor(Color.parseColor(evenRowColor))
                                row.addView(customName)

                                val medicineRoutines = TextView(requireContext())
                                var medicineRoutineString: String = ""
                                val medicineRoutinesSize: Int = prescriptionList[i].medicineRoutines.size

                                for(j in 0..medicineRoutinesSize - 1) {
                                    medicineRoutineString += mappingMedicineRoutines[prescriptionList[i].medicineRoutines[j]]
                                    if(j != medicineRoutinesSize - 1) medicineRoutineString += ", "
                                }
                                medicineRoutines.text = medicineRoutineString
                                medicineRoutines.setPadding(8, 8, 8, 8)
                                medicineRoutines.setBackgroundColor(Color.parseColor(oddRowColor))
                                row.addView(medicineRoutines)

                                tableLayout.addView(row)
                            }

                            // 헤더 설정 시작
                            val headerRow2 = TableRow(requireContext())

                            val headerCell21 = TextView(requireContext())
                            headerCell21.text = "기록일"
                            headerCell21.setTypeface(null, Typeface.BOLD)
                            headerCell21.setPadding(8, 8, 8, 8)
                            headerCell21.setBackgroundColor(Color.parseColor(oddRowColor))
                            headerRow2.addView(headerCell21)

                            val headerCell22 = TextView(requireContext())
                            headerCell22.text = "컨디션"
                            headerCell22.setTypeface(null, Typeface.BOLD)
                            headerCell22.setPadding(8, 8, 8, 8)
                            headerCell22.setBackgroundColor(Color.parseColor(evenRowColor))
                            headerRow2.addView(headerCell22)

                            val headerCell23 = TextView(requireContext())
                            headerCell23.text = "보유 증상"
                            headerCell23.setTypeface(null, Typeface.BOLD)
                            headerCell23.setPadding(8, 8, 8, 8)
                            headerCell23.setBackgroundColor(Color.parseColor(oddRowColor))
                            headerRow2.addView(headerCell23)

                            val headerCell24 = TextView(requireContext())
                            headerCell24.text = "기타 특이 사항"
                            headerCell24.setTypeface(null, Typeface.BOLD)
                            headerCell24.setPadding(8, 8, 8, 8)
                            headerCell24.setBackgroundColor(Color.parseColor(evenRowColor))
                            headerRow2.addView(headerCell24)

                            tableLayout2.addView(headerRow2)
                            // 헤더 설정 끝

                            // 내용 추가 시작
                            val surveyContentList: ArrayList<SurveyContentListModel> = response.body()!!.surveyContentList

                            for(i in 0..surveyContentList.size - 1) {
                                makePDFChk = true
                                val row = TableRow(requireContext())
                                val layoutParams = TableLayout.LayoutParams(
                                    TableLayout.LayoutParams.MATCH_PARENT,
                                    TableLayout.LayoutParams.WRAP_CONTENT
                                )
                                row.layoutParams = layoutParams

                                val dateCell = TextView(requireContext())
                                dateCell.text = surveyContentList[i].date.substring(0, 10)
                                dateCell.setPadding(8, 8, 8, 8)
                                dateCell.setBackgroundColor(Color.parseColor(oddRowColor))
                                row.addView(dateCell)

                                val emotionString = surveyContentList[i].contentEmotionResultResponse.choiceEmotion
                                val emotion = TextView(requireContext())

                                when(emotionString) {
                                    "GOOD" -> emotion.text = "좋음"
                                    "SOSO" -> emotion.text = "보통"
                                    "BAD" -> emotion.text = "나쁨"
                                    else -> emotion.text = "-"
                                }
                                emotion.setPadding(8, 8, 8, 8)
                                emotion.setBackgroundColor(Color.parseColor(evenRowColor))
                                row.addView(emotion)

                                val status = TextView(requireContext())
                                var statusString: String = ""

                                for(j in 0..surveyContentList[i].contentStatusResultResponse.selectedStatusChoices.size - 1) {
                                    statusString += mappingSymptom[surveyContentList[i].contentStatusResultResponse.selectedStatusChoices[j]]
                                    if(j != surveyContentList[i].contentStatusResultResponse.selectedStatusChoices.size - 1) statusString += ", "
                                }
                                status.text = statusString
                                status.setPadding(8, 8, 8, 8)
                                status.setBackgroundColor(Color.parseColor(oddRowColor))
                                row.addView(status)

                                val text = TextView(requireContext())
                                text.text = surveyContentList[i].contentTextResultResponse.text
                                text.setPadding(8, 8, 8, 8)
                                text.setBackgroundColor(Color.parseColor(evenRowColor))
                                row.addView(text)

                                tableLayout2.addView(row)
                            }
                            if(makePDFChk) layout.findViewById<ImageView>(R.id.makePDFBtn).visibility = View.VISIBLE
                        }
                        else if(response.code() == 401) {
                            Log.d("로그", "pdf 출력 401 Unauthorized: AccessToken이 유효하지 않은 경우")
                        }
                    }
                    override fun onFailure(call: Call<MedicineSearchResponseBodyModel>, t: Throwable) {
                        Log.d("로그", "pdf 출력 onFailure")
                    }
                })
            }
        }

        layout.findViewById<ImageView>(R.id.makePDFBtn).setOnClickListener {    // pdf 만들기
            val pdfFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/지금이약_${LocalDate.now()}.pdf"

            // PDF 생성 시작
            val document = Document()
            PdfWriter.getInstance(document, FileOutputStream(pdfFilePath))
            document.open()

            // PDF에 추가할 내용 작성
            val tableLayout = layout.findViewById<TableLayout>(R.id.searchMedicineTable)
            val tableLayout2 = layout.findViewById<TableLayout>(R.id.searchRecodeTable)

            // 복약 정보 타이틀 추가
            val medicineTitle = Paragraph("복약 정보", FontFactory.getFont("/res/font/nanum_gothic.otf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 16f, Font.BOLD))
            medicineTitle.alignment = Element.ALIGN_CENTER   // 가운데 정렬
            medicineTitle.spacingAfter = 10f // 아래쪽 여백 설정 (10f는 적당한 값이며 필요에 따라 조절 가능)
            document.add(medicineTitle)

            // 복약 정보 테이블 추가
            addTableToDocument(document, tableLayout, mainActivity, 5)

            // 컨디션 기록 정보 타이틀 추가
            val conditionTitle = Paragraph("컨디션 기록 정보", FontFactory.getFont("/res/font/nanum_gothic.otf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 16f, Font.BOLD))
            conditionTitle.alignment = Element.ALIGN_CENTER // 가운데 정렬
            conditionTitle.spacingAfter = 10f // 아래쪽 여백 설정 (10f는 적당한 값이며 필요에 따라 조절 가능)
            document.add(conditionTitle)

            // 컨디션 기록 테이블 추가
            addTableToDocument(document, tableLayout2, mainActivity, 4)

            document.close()

            Toast.makeText(requireContext(), "PDF가 [다운로드 폴더]에 생성되었습니다.", Toast.LENGTH_SHORT).show()
        }

        layout.findViewById<Button>(R.id.mainBtn).setOnClickListener {
            mainActivity!!.gotoMedicine()
        }

        // 스크롤을 아래로 내리는 버튼
        layout.findViewById<ImageView>(R.id.medicineSearchScrollDown).setOnClickListener {
            layout.findViewById<ScrollView>(R.id.medicineSearchScrollView).post {
                layout.findViewById<ScrollView>(R.id.medicineSearchScrollView).fullScroll(ScrollView.FOCUS_DOWN)
            }
        }

        return layout
    }

    private lateinit var callback: OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do something
                mainActivity.gotoMedicine()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    private fun addTableToDocument(document: Document, tableLayout: TableLayout, context: Context, numberOfColumns: Int) {
        val numberOfRows = tableLayout.childCount

        val table = PdfPTable(numberOfColumns)
        val colWidths = FloatArray(numberOfColumns) { 3f }

        val font = FontFactory.getFont("/res/font/nanum_gothic.otf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 10f)

        for (i in 0 until numberOfRows) {
            val row = tableLayout.getChildAt(i) as TableRow
            val rowCells = mutableListOf<PdfPCell>()

            for (j in 0 until numberOfColumns) {
                val textView = row.getChildAt(j) as TextView
                val cellText = textView.text.toString()

                val cell = PdfPCell(Phrase(cellText, font))
                rowCells.add(cell)
                cell.setPadding(5f)
                cell.isNoWrap = false
            }

            for (cell in rowCells) {
                table.addCell(cell)
            }
        }

        table.widthPercentage = 100f

        val emptyCell = PdfPCell(Phrase(" "))
        emptyCell.colspan = numberOfColumns
        emptyCell.border = PdfPCell.NO_BORDER
        emptyCell.setPadding(10f)
        table.addCell(emptyCell)

        table.setWidths(colWidths)
        document.add(table)
    }
}
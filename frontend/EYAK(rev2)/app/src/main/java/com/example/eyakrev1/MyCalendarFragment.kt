package com.example.eyakrev1

import android.app.LauncherActivity
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.util.Calendar

class MyCalendarFragment : Fragment() {

    lateinit var mainActivity: MainActivity

    var DayList = arrayListOf<Dates>()

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layout = inflater.inflate(R.layout.fragment_my_calendar, container, false)

        var targetDay: LocalDate = LocalDate.now()
        var thisMonth = targetDay.monthValue
        var thisYear = targetDay.year
        var thisDay = targetDay.dayOfMonth
        var numberOfDaysInThisMonth = getDaysInMonth(thisYear, thisMonth)

        val calendarIndicatorTextView = layout.findViewById<TextView>(R.id.tv_calendar_indicator)
        calendarIndicatorTextView.setText("${thisYear}년 ${thisMonth}월")

        for (i in 1..numberOfDaysInThisMonth) {
            val dayItem = Dates(date = i.toString(), full_dose = 3, actual_dose = i % 4)
            DayList.add(dayItem)
        }

        val mySingleRowCalendarRecyclerView = layout.findViewById<RecyclerView>(R.id.mySingleRowCalendar)
        val mySingleRowCalendarAdapter = mySingleRowCalendarListAdapter(DayList)

        mySingleRowCalendarRecyclerView.adapter = mySingleRowCalendarAdapter
        mySingleRowCalendarRecyclerView.scrollToPosition(kotlin.math.max(thisDay - 3, 0))

        return layout
    }

    inner class mySingleRowCalendarListAdapter (val dayList: ArrayList<Dates>) : RecyclerView.Adapter<mySingleRowViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mySingleRowViewHolder {
            val view = layoutInflater.inflate(R.layout.single_row_calendar_day_item, parent, false)
            return mySingleRowViewHolder(view)
        }

        override fun onBindViewHolder(holder: mySingleRowViewHolder, position: Int) {
            val day = getItem(position)
            holder.bind(day.date, day.full_dose, day.actual_dose)
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

        fun bind(dayText: String, fullDose: Int, actualDose: Int) {
            myCalendarDayTextView.text = dayText

            if (actualDose == fullDose) {
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

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }
}
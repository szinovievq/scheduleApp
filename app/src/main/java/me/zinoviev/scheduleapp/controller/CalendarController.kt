package me.zinoviev.scheduleapp.controller

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import me.zinoviev.scheduleapp.R
import me.zinoviev.scheduleapp.adapter.*
import me.zinoviev.scheduleapp.mapper.LessonMapper
import me.zinoviev.scheduleapp.mapper.ScheduleMapper
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

@RequiresApi(Build.VERSION_CODES.O)
class CalendarController(
    private val activity: AppCompatActivity,
    private val selectedAuditory: String? = null,
    private val selectedGroup: String? = null
) {
    private val rvCalendar: RecyclerView = activity.findViewById(R.id.rvCalendar)
    private val btnBack: ImageButton = activity.findViewById(R.id.btnBack)

    private val scheduleMapper = ScheduleMapper(activity)
    private val lessonMapper = LessonMapper()

    private val days = mutableListOf<CalendarDayItem>()

    private lateinit var firstDate: LocalDate
    private lateinit var lastDate: LocalDate

    private var isLoading = false

    fun initialize() {
        btnBack.setOnClickListener { activity.finish() }

        val today = LocalDate.now()
        val currentMonday = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))

        firstDate = currentMonday
        lastDate = currentMonday.minusDays(1)

        val adapter = CalendarDayAdapter(days)
        val layoutManager = GridLayoutManager(activity, 3)
        rvCalendar.layoutManager = layoutManager
        rvCalendar.adapter = adapter

        loadInitialData()

        rvCalendar.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (isLoading) return

                val lm = recyclerView.layoutManager as GridLayoutManager
                val firstVisible = lm.findFirstVisibleItemPosition()
                val lastVisible = lm.findLastVisibleItemPosition()

                if (firstVisible < 6) {
                    loadMoreDays(past = true, count = 24)
                } else if (lastVisible > days.size - 6) {
                    loadMoreDays(past = false, count = 24)
                }
            }
        })
    }

    private fun loadInitialData() {
        isLoading = true

        val initialFuture = getGeneratedDays(past = false, count = 24)
        days.addAll(initialFuture)
        lastDate = initialFuture.last().fullDate

        val initialPast = getGeneratedDays(past = true, count = 24)
        days.addAll(0, initialPast)
        firstDate = initialPast.first().fullDate

        rvCalendar.adapter?.notifyDataSetChanged()

        rvCalendar.scrollToPosition(24)
        isLoading = false
    }

    private fun loadMoreDays(past: Boolean, count: Int) {
        if (isLoading) return
        isLoading = true

        val newDays = getGeneratedDays(past, count)

        rvCalendar.post {
            if (past) {
                days.addAll(0, newDays)
                firstDate = newDays.first().fullDate

                val lm = rvCalendar.layoutManager as GridLayoutManager
                val firstPos = lm.findFirstVisibleItemPosition()
                val view = lm.findViewByPosition(firstPos)
                val offset = view?.top ?: 0

                rvCalendar.adapter?.notifyItemRangeInserted(0, newDays.size)

                lm.scrollToPositionWithOffset(firstPos + newDays.size, offset)
            } else {
                val startPos = days.size
                days.addAll(newDays)
                lastDate = newDays.last().fullDate
                rvCalendar.adapter?.notifyItemRangeInserted(startPos, newDays.size)
            }
            isLoading = false
        }
    }

    private fun getGeneratedDays(past: Boolean, count: Int): List<CalendarDayItem> {
        val formatter = DateTimeFormatter.ofPattern("E")
        val result = mutableListOf<CalendarDayItem>()
        var addedCount = 0
        var daysScanned = 1L

        while (addedCount < count) {
            val date = if (past) firstDate.minusDays(daysScanned) else lastDate.plusDays(daysScanned)
            daysScanned++

            if (date.dayOfWeek == java.time.DayOfWeek.SUNDAY) continue

            val dayKey = date.dayOfWeek.name.lowercase()

            val lessons = when {
                selectedAuditory != null -> scheduleMapper.getLessons(selectedAuditory, dayKey)
                selectedGroup != null -> scheduleMapper.getLessonsByGroup(selectedGroup, dayKey)
                else -> emptyList()
            }.filter { lesson ->
                val start = lesson.df ?: LocalDate.MIN
                val end = lesson.dt ?: LocalDate.MAX
                !date.isBefore(start) && !date.isAfter(end)
            }

            val mappedItems = lessonMapper.mapLessonsToItems(lessons)
                .distinctBy { it.lessonNumber + it.lesson + it.time }
                .sortedBy { it.lessonNumber }

            result.add(CalendarDayItem(
                date = date.dayOfMonth.toString(),
                dayOfWeek = date.format(formatter).replaceFirstChar { it.uppercase() },
                lessons = mappedItems,
                fullDate = date
            ))
            addedCount++
        }

        return if (past) result.reversed() else result
    }
}
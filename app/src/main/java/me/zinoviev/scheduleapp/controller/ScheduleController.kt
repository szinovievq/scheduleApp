package me.zinoviev.scheduleapp.controller

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import me.zinoviev.scheduleapp.CalendarActivity
import me.zinoviev.scheduleapp.ScheduleView
import me.zinoviev.scheduleapp.mapper.AuditoryMapper
import me.zinoviev.scheduleapp.mapper.LessonMapper
import me.zinoviev.scheduleapp.mapper.ScheduleMapper
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class ScheduleController(private val activity: AppCompatActivity) {

    enum class SearchType {
        AUDITORY,
        GROUP
    }

    private var searchType: SearchType = SearchType.AUDITORY
    private var selectedGroup: String? = null

    private val ui = ScheduleView(activity)
    private val scheduleMapper = ScheduleMapper(activity)
    private val lessonMapper = LessonMapper()
    private val auditoryMapper = AuditoryMapper(activity)

    var selectedAuditory: String? = null
    var selectedDay: String = "monday"
    private var scheduleShown = false

    fun initialize() {
        ui.setupDays { dayKey ->
            if (selectedAuditory != null || selectedGroup != null) {
                selectedDay = dayKey
                updateSchedule()
            }
        }

        ui.setupToMapClick {
            handleToMapClick()
        }

        ui.setupSearchButton { input -> handleSearch(input) }

        ui.setupDotsClick { option ->
            when(option) {
                "Календарь" -> openCalendar()
            }
        }

        ui.showInfoMessage("Расписание не найдено :(")
    }

    private fun handleSearch(input: String) {
        val lowerInput = input.lowercase()
        val auditoryId = auditoryMapper.getAuditoryId(lowerInput)

        if (auditoryId != null) {
            searchType = SearchType.AUDITORY
            selectedAuditory = auditoryId
            selectedGroup = null
            ui.showToMapText()
            ui.setSelected(input, false)
            ui.updateAdapterShowAuditory(false)
        } else {
            searchType = SearchType.GROUP
            selectedGroup = input
            selectedAuditory = null
            ui.hideToMapText()
            ui.setSelected(input, true)
            ui.updateAdapterShowAuditory(true)
        }

        if (!scheduleShown) {
            ui.showScheduleContainer()
            ui.showRvDays()
            ui.showToMapText()
            scheduleShown = true
        }

        updateSchedule()
    }

    private fun handleToMapClick() {
        val url = "https://mpunav.ru/?room=$selectedAuditory"
        if (selectedAuditory == null) return
        ui.startMapActivity(url)
    }


    fun updateSchedule() {

        val lessons = when (searchType) {

            SearchType.AUDITORY -> {
                val audience = selectedAuditory ?: return
                scheduleMapper.getLessons(audience, selectedDay)
            }

            SearchType.GROUP -> {
                val group = selectedGroup ?: return
                scheduleMapper.getLessonsByGroup(group, selectedDay)
            }
        }

        val today = LocalDate.now()

        val todaysLessons = lessons.filter { lesson ->
            val start = lesson.dt!!
            val end = lesson.df!!

            val from = if (start.isBefore(end) || start.isEqual(end)) start else end
            val to = if (end.isAfter(start) || end.isEqual(start)) end else start

            !today.isBefore(from) && !today.isAfter(to)
        }

        val items = lessonMapper.mapLessonsToItems(todaysLessons)

        ui.showScheduleContainer()
        ui.showRvDays()
        ui.updateSchedule(items)

        if (searchType == SearchType.AUDITORY) {
            ui.showToMapText()
        } else {
            ui.hideToMapText()
        }

        if (items.isEmpty()) {
            ui.showInfoMessage("В этот день занятий нет!")
        } else {
            ui.hideInfoMessage()
        }
    }

    private fun openCalendar() {
        val intent = android.content.Intent(activity, CalendarActivity::class.java)
        selectedAuditory?.let { intent.putExtra("auditory", it) }
        selectedGroup?.let { intent.putExtra("group", it) }
        activity.startActivity(intent)
    }
}

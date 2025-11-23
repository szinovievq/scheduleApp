package me.zinoviev.scheduleapp.controller

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import me.zinoviev.scheduleapp.ScheduleView
import me.zinoviev.scheduleapp.mapper.AuditoryMapper
import me.zinoviev.scheduleapp.mapper.LessonMapper
import me.zinoviev.scheduleapp.mapper.ScheduleMapper
import java.time.LocalDate

class ScheduleController(activity: AppCompatActivity) {

    private val ui = ScheduleView(activity)
    private val scheduleMapper = ScheduleMapper(activity)
    private val lessonMapper = LessonMapper()
    private val auditoryMapper = AuditoryMapper(activity)

    var selectedAuditory: String? = null
    var selectedDay: String = "monday"
    private var scheduleShown = false

    @RequiresApi(Build.VERSION_CODES.O)
    fun initialize() {
        ui.setupDays { dayKey ->
            if (selectedAuditory != null) {
                selectedDay = dayKey
                updateSchedule()
            }
        }

        ui.setupToMapClick {
            handleToMapClick()
        }

        ui.setupSearchButton { input -> handleSearch(input) }

        ui.showInfoMessage("Расписание не найдено :(")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleSearch(input: String) {
        val auditoryId = auditoryMapper.getAuditoryId(input.lowercase())
        ui.setSelectedAuditory(input)

        if (auditoryId != null) {
            selectedAuditory = auditoryId
            if (!scheduleShown) {
                ui.showScheduleContainer()
                ui.showRvDays()
                ui.showToMapText()
                scheduleShown = true
            }
            updateSchedule()
        } else {
            selectedAuditory = null
            scheduleShown = false
            ui.hideRvDays()
            ui.clearSchedule()
            ui.hideToMapText()
            ui.showInfoMessage("Аудитория $input не найдена :(")
        }
    }

    private fun handleToMapClick() {
        val url = "https://mpunav.ru/?room=$selectedAuditory"
        if (selectedAuditory == null) return
        ui.startMapActivity(url)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateSchedule() {
        val audience = selectedAuditory ?: return
        val lessons = scheduleMapper.getLessons(audience, selectedDay)

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
        ui.showToMapText()
        ui.updateSchedule(items)

        if (items.isEmpty()) {
            ui.showInfoMessage("В этот день занятий нет!")
        } else {
            ui.hideInfoMessage()
        }
    }
}

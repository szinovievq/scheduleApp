package me.zinoviev.scheduleapp.controller

import androidx.appcompat.app.AppCompatActivity
import me.zinoviev.scheduleapp.ScheduleView
import me.zinoviev.scheduleapp.mapper.AuditoryMapper
import me.zinoviev.scheduleapp.mapper.LessonMapper
import me.zinoviev.scheduleapp.mapper.ScheduleMapper

class ScheduleController(activity: AppCompatActivity) {

    private val ui = ScheduleView(activity)
    private val scheduleMapper = ScheduleMapper(activity)
    private val lessonMapper = LessonMapper()
    private val auditoryMapper = AuditoryMapper(activity)

    var selectedAuditory: String? = null
    var selectedDay: String = "monday"
    private var scheduleShown = false

    fun initialize() {
        ui.setupDays { dayKey ->
            if (selectedAuditory != null) {
                selectedDay = dayKey
                updateSchedule()
            }
        }

        ui.setupSearchButton { input -> handleSearch(input) }
        ui.showInfoMessage("Расписание не найдено :(")
    }

    private fun handleSearch(input: String) {
        val auditoryId = auditoryMapper.getAuditoryId(input.lowercase())
        ui.setSelectedAuditory(input)

        if (auditoryId != null) {
            selectedAuditory = auditoryId
            if (!scheduleShown) {
                ui.showScheduleContainer()
                ui.showRvDays()
                scheduleShown = true
            }
            updateSchedule()
        } else {
            selectedAuditory = null
            scheduleShown = false
            ui.hideRvDays()
            ui.clearSchedule()
            ui.showInfoMessage("Аудитория $input не найдена :(")
        }
    }

    fun updateSchedule() {
        val audience = selectedAuditory ?: return
        val lessons = scheduleMapper.getLessons(audience, selectedDay)
        val items = lessonMapper.mapLessonsToItems(lessons)

        ui.showScheduleContainer()
        ui.showRvDays()
        ui.updateSchedule(items)

        if (items.isEmpty()) {
            ui.showInfoMessage("В этот день занятий нет!")
        } else {
            ui.hideInfoMessage()
        }
    }
}
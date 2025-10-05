package me.zinoviev.scheduleapp.mapper

import me.zinoviev.scheduleapp.adapter.LessonAdapter
import me.zinoviev.scheduleapp.model.Lesson

class LessonMapper {

    fun mapLessonsToItems(lessons: List<Lesson>): List<LessonAdapter.LessonItem> {
        return lessons.map { lesson ->
            val number = lesson.number
            val group = lesson.groupNames.joinToString(", ")
            val time = getLessonTime(lesson.number)
            val subject = lesson.discipline.ifEmpty { " " }
            val teacher = lesson.teachers.joinToString(", ").ifEmpty { "Преподаватель не указан" }

            LessonAdapter.LessonItem(
                lessonNumber = number,
                group = group,
                time = time,
                lesson = subject,
                teacher = teacher
            )
        }
    }

    private fun getLessonTime(number: String): String {
        return when (number.toIntOrNull()) {
            1 -> "09:00–10:30"
            2 -> "10:40–12:10"
            3 -> "12:20–13:50"
            4 -> "14:30–16:00"
            5 -> "16:10–17:40"
            6 -> "17:50–19:20"
            7 -> "19:30-21:00"
            else -> " "
        }
    }
}
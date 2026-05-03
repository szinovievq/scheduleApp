package me.zinoviev.scheduleapp.model

import java.time.LocalDate

data class Lesson(
    val number: String,
    val discipline: String,
    val teachers: List<String>,
    val groupNames: List<String>,
    val dt: LocalDate?, //end
    val df: LocalDate?, // start
    val dts: String = "",
    val auditoryId: String = ""
)
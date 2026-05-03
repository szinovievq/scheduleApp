package me.zinoviev.scheduleapp.model

import java.time.LocalDate

data class Lesson(
    val number: String,
    val discipline: String,
    val teachers: List<String>,
    val groupNames: List<String>,
    val dt: LocalDate?,
    val df: LocalDate?,
    val dts: String = "",
    val auditoryId: String = ""
)
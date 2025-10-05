package me.zinoviev.scheduleapp.model

data class Lesson(
    val number: String,
    val discipline: String,
    val teachers: List<String>,
    val groupNames: List<String>
)

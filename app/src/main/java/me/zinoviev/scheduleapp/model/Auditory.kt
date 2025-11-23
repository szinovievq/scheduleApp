package me.zinoviev.scheduleapp.model

import kotlinx.serialization.json.JsonElement

data class Auditory(
    val id: String,
    val rasp: Map<String, Map<String, List<JsonElement>>> = emptyMap()
)
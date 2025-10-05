package me.zinoviev.scheduleapp.model

import kotlinx.serialization.json.JsonObject

data class Auditory(
    val id: String,
    val rasp: Map<String, Map<String, JsonObject>> = emptyMap()
)
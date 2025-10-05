package me.zinoviev.scheduleapp.mapper

import android.content.Context
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.zinoviev.scheduleapp.model.Auditory
import me.zinoviev.scheduleapp.model.Lesson

class ScheduleMapper(context: Context) {
    private val auditories: Map<String, Auditory>

    init {
        val inputStream = context.assets.open("schedule.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val json = Json.parseToJsonElement(jsonString).jsonObject

        auditories = json.mapValues { (audId, audData) ->
            val raspJson = audData.jsonObject["rasp"]?.jsonObject ?: JsonObject(emptyMap())
            val rasp = raspJson.mapValues { (_, dayData) ->
                dayData.jsonObject.filterKeys { it != "lessons" }
            }
            Auditory(id = audId, rasp = rasp as Map<String, Map<String, JsonObject>>)
        }
    }

    fun getLessons(auditoryId: String, day: String): List<Lesson> {
        val auditory = auditories[auditoryId] ?: return emptyList()
        val dayLessons = auditory.rasp[day] ?: return emptyList()

        return dayLessons.map { (lessonNumber, lessonData) ->
            val discipline = lessonData["discipline"]?.jsonPrimitive?.content.orEmpty()
            val teachers = lessonData["teachers"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList()
            val groupNames = lessonData["groupNames"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList()

            Lesson(
                number = lessonNumber,
                discipline = discipline,
                teachers = teachers,
                groupNames = groupNames
            )
        }.sortedBy { it.number.toIntOrNull() ?: 0 }
    }

}

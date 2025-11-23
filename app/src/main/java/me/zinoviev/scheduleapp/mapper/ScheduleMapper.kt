package me.zinoviev.scheduleapp.mapper

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.json.*
import me.zinoviev.scheduleapp.model.Auditory
import me.zinoviev.scheduleapp.model.Lesson
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ScheduleMapper(context: Context) {

    private val auditories: Map<String, Auditory>

    init {
        val inputStream = context.assets.open("schedule.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val json = Json.parseToJsonElement(jsonString).jsonObject

        auditories = json.mapValues { (audId, audData) ->
            val raspJson = audData.jsonObject["rasp"]?.jsonObject ?: JsonObject(emptyMap())
            val rasp = raspJson.mapValues { (_, dayData) ->
                val dayObj = dayData.asJsonObjectOrNull() ?: JsonObject(emptyMap())
                dayObj.mapValues { (_, lessonArray) ->
                    lessonArray.jsonArray.filterNot { it is JsonNull }
                }
            }
            Auditory(id = audId, rasp = rasp)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getLessons(auditoryId: String, day: String): List<Lesson> {
        val auditory = auditories[auditoryId] ?: return emptyList()
        val dayLessons = auditory.rasp[day] ?: return emptyList()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return dayLessons.flatMap { (lessonNumber, lessonsList) ->
            lessonsList.mapNotNull { lessonData ->

                val lessonObj = lessonData.asJsonObjectOrNull() ?: return@mapNotNull null
                val discipline = lessonObj["discipline"]?.jsonPrimitive?.content.orEmpty()
                val teachers = lessonObj["teachers"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList()
                val groupNames = lessonObj["groupNames"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList()
                val dt = lessonObj["dt"]?.jsonPrimitive?.content?.let { LocalDate.parse(it, formatter) }
                val df = lessonObj["df"]?.jsonPrimitive?.content?.let { LocalDate.parse(it, formatter) }
                val dts = lessonObj["dts"]?.jsonPrimitive?.content.orEmpty()

                Lesson(
                    number = lessonNumber,
                    discipline = discipline,
                    teachers = teachers,
                    groupNames = groupNames,
                    dt = dt,
                    df = df,
                    dts = dts
                )
            }
        }.sortedBy { it.number.toIntOrNull() ?: 0 }
    }
    private fun JsonElement.asJsonObjectOrNull(): JsonObject? = this as? JsonObject
}
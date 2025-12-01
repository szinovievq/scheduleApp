package me.zinoviev.scheduleapp.mapper

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.json.*
import me.zinoviev.scheduleapp.model.Auditory
import me.zinoviev.scheduleapp.model.Lesson
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class ScheduleMapper(private val context: Context) {

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
                    dts = dts,
                    auditoryId = auditoryId
                )
            }
        }.sortedBy { it.number.toIntOrNull() ?: 0 }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getLessonsByGroup(group: String, day: String): List<Lesson> {

        val result = mutableListOf<Lesson>()
        val auditoryMapper = AuditoryMapper(context)

        for (auditoryName in auditoryMapper.getAllAuditories()) {

            val auditoryId = auditoryMapper.getAuditoryId(auditoryName) ?: continue

            val lessons = getLessons(auditoryId, day)

            lessons.forEach { lesson ->

                if (lesson.groupNames.any { it.equals(group, ignoreCase = true) }) {

                    val isAlreadyAdded = result.any { existing ->
                        existing.number == lesson.number &&
                                existing.discipline == lesson.discipline &&
                                existing.teachers == lesson.teachers
                    }

                    if (!isAlreadyAdded) {
                        result.add(lesson)
                    }
                }
            }
        }

        return result.sortedBy { it.number.toIntOrNull() ?: 0 }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllGroups(): List<String> {
        val groups = mutableSetOf<String>()

        for (auditory in auditories.values) {
            for (day in auditory.rasp.values) {
                for (lessonsList in day.values) {
                    for (lessonData in lessonsList) {

                        val lessonObj = lessonData.asJsonObjectOrNull() ?: continue

                        val groupNames = lessonObj["groupNames"]
                            ?.jsonArray
                            ?.map { it.jsonPrimitive.content }
                            ?: emptyList()

                        groups.addAll(groupNames)
                    }
                }
            }
        }

        return groups.sorted()
    }

    private fun JsonElement.asJsonObjectOrNull(): JsonObject? = this as? JsonObject
}
package me.zinoviev.scheduleapp.mapper

import android.content.Context
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

class AuditoryMapper(context: Context) {
    private val mapping: Map<String, String>

    init {
        val inputStream = context.assets.open("auditories.json")
        mapping = Json.decodeFromStream<Map<String, String>>(inputStream)
    }

    fun getAuditoryId(audienceName: String): String? {
        return mapping[audienceName]
    }
}
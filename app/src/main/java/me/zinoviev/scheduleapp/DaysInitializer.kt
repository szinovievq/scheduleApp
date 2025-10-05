package me.zinoviev.scheduleapp

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.zinoviev.scheduleapp.adapter.DaysAdapter

object DaysInitializer {

    fun setupDaysRecyclerView(
        context: Context,
        recyclerView: RecyclerView,
        onDayClick: (String) -> Unit
    ) {
        val days = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб")

        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = DaysAdapter(days) { shortDay ->
            val dayKey = mapDayToKey(shortDay)
            onDayClick(dayKey)
        }
    }

    private fun mapDayToKey(shortDay: String): String {
        return when (shortDay) {
            "Пн" -> "monday"
            "Вт" -> "tuesday"
            "Ср" -> "wednesday"
            "Чт" -> "thursday"
            "Пт" -> "friday"
            "Сб" -> "saturday"
            else -> "monday"
        }
    }
}
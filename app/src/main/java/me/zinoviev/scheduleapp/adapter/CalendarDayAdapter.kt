package me.zinoviev.scheduleapp.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.zinoviev.scheduleapp.R

data class CalendarDayItem(
    val date: String,
    val dayOfWeek: String,
    val lessons: List<LessonAdapter.LessonItem>,
    val fullDate: java.time.LocalDate
)

class CalendarDayAdapter(private val items: MutableList<CalendarDayItem>) :
    RecyclerView.Adapter<CalendarDayAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDayNumber)
        val tvDayOfWeek: TextView = view.findViewById(R.id.tvWeekDay)
        val rvLessons: RecyclerView = view.findViewById(R.id.rvLessons)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return VH(view)
    }

    @SuppressLint("/values/colors.xml")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: VH, position: Int) {
        val day = items[position]
        val today = java.time.LocalDate.now()
        val context = holder.itemView.context

        if (day.fullDate == today) {
            val color = androidx.core.content.ContextCompat.getColor(context, R.color.backgroundL2)
            holder.itemView.backgroundTintList = android.content.res.ColorStateList.valueOf(color)
        } else {
            val color = androidx.core.content.ContextCompat.getColor(context, R.color.backgroundL1)
            holder.itemView.backgroundTintList = android.content.res.ColorStateList.valueOf(color)
        }

        holder.tvDate.text = day.date
        holder.tvDayOfWeek.text = day.dayOfWeek

        holder.rvLessons.adapter = null
        holder.rvLessons.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.rvLessons.adapter = CalendarLessonAdapter(day.lessons)
        holder.rvLessons.isNestedScrollingEnabled = false
    }

    override fun getItemCount(): Int = items.size
}

package me.zinoviev.scheduleapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.zinoviev.scheduleapp.R

class CalendarLessonAdapter(
    private var items: List<LessonAdapter.LessonItem>
) : RecyclerView.Adapter<CalendarLessonAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvLessonNumber: TextView = view.findViewById(R.id.tvNumber)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvSubject: TextView = view.findViewById(R.id.tvSubjectName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_lesson, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvLessonNumber.text = item.lessonNumber
        holder.tvTime.text = item.time
        holder.tvSubject.text = item.lesson
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<LessonAdapter.LessonItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}

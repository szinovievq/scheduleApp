package me.zinoviev.scheduleapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.zinoviev.scheduleapp.R

class LessonAdapter(private var items: List<LessonItem>)
    : RecyclerView.Adapter<LessonAdapter.VH>() {

    data class LessonItem(val lessonNumber: String, val group: String,
                          val time: String, val lesson: String, val teacher: String)

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvLessonNumber: TextView = view.findViewById(R.id.tvLessonNumber)
        val tvGroup: TextView = view.findViewById(R.id.tvGroup)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvSubject: TextView = view.findViewById(R.id.tvLesson)
        val tvTeacher: TextView = view.findViewById(R.id.tvTeacher)

        fun bind(item: LessonItem) {
            tvLessonNumber.text = item.lessonNumber
            tvGroup.text = item.group
            tvTime.text = item.time
            tvSubject.text = item.lesson
            tvTeacher.text = item.teacher
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<LessonItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}
package me.zinoviev.scheduleapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.zinoviev.scheduleapp.R

class DaysAdapter(
    private val days: List<String>,
    private val onDayClick: (String) -> Unit
) : RecyclerView.Adapter<DaysAdapter.DayViewHolder>() {

    private var selectedPosition: Int = 0

    inner class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDay: TextView = view.findViewById(R.id.tvDay)
        val rootLayout: View = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val day = days[position]
        holder.tvDay.text = day

        if (position == selectedPosition) {
            holder.rootLayout.setBackgroundResource(R.drawable.item_day_background_selected)
        } else {
            holder.rootLayout.setBackgroundResource(R.drawable.item_day_background)
        }

        holder.rootLayout.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position
            if (previousPosition != -1) notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onDayClick(day)
        }
    }

    override fun getItemCount() = days.size
}
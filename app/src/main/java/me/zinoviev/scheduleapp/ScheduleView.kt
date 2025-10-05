package me.zinoviev.scheduleapp

import android.content.Context
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.zinoviev.scheduleapp.adapter.LessonAdapter

class ScheduleView(private val activity: AppCompatActivity) {

    private val recyclerView: RecyclerView = activity.findViewById(R.id.recyclerView)
    private val rvDays: RecyclerView = activity.findViewById(R.id.rvDays)
    private val etAuditory: EditText = activity.findViewById(R.id.etAuditory)
    private val btnSearch: Button = activity.findViewById(R.id.btnSearch)
    private val containerSchedule: LinearLayout = activity.findViewById(R.id.containerSchedule)
    private val tvInfo: TextView = activity.findViewById(R.id.tvInfo)
    private val tvSelectedAuditory: TextView = activity.findViewById(R.id.tvSelectedAuditory)

    private val adapter = LessonAdapter(emptyList())

    init {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
        containerSchedule.visibility = View.GONE
        tvInfo.visibility = View.VISIBLE
        tvSelectedAuditory.text = "Аудитория: Не выбрано"
    }

    fun setupDays(onDayClick: (String) -> Unit) {
        DaysInitializer.setupDaysRecyclerView(activity, rvDays, onDayClick)
    }

    fun setupSearchButton(onSearch: (String) -> Unit) {

        btnSearch.setOnClickListener {
            val input = etAuditory.text.toString().trim()
            onSearch(input)

            val imm = it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(etAuditory.windowToken, 0)
        }

        etAuditory.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val input = etAuditory.text.toString().trim()
                onSearch(input)

                val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)

                true
            } else {
                false
            }
        }
    }

    fun showScheduleContainer() {
        containerSchedule.visibility = View.VISIBLE
    }

    fun showInfoMessage(msg: String) {
        tvInfo.text = msg
        tvInfo.visibility = View.VISIBLE
    }

    fun hideInfoMessage() {
        tvInfo.visibility = View.GONE
    }

    fun showRvDays(){
        rvDays.visibility = View.VISIBLE
    }

    fun hideRvDays(){
        rvDays.visibility = View.GONE
    }

    fun updateSchedule(items: List<LessonAdapter.LessonItem>) {
        adapter.updateData(items)
    }

    fun clearSchedule() {
        adapter.updateData(emptyList())
    }

    fun setSelectedAuditory(text: String) {
        tvSelectedAuditory.text = "Аудитория: $text"
    }
}
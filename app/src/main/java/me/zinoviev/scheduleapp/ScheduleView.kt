package me.zinoviev.scheduleapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.zinoviev.scheduleapp.adapter.LessonAdapter
import me.zinoviev.scheduleapp.mapper.AuditoryMapper
import me.zinoviev.scheduleapp.mapper.ScheduleMapper

@RequiresApi(Build.VERSION_CODES.O)
class ScheduleView(private val activity: AppCompatActivity) {

    private val recyclerView: RecyclerView = activity.findViewById(R.id.recyclerView)
    private val rvDays: RecyclerView = activity.findViewById(R.id.rvDays)
    private val etAuditory: AutoCompleteTextView = activity.findViewById(R.id.etAuditory)
    private val btnSearch: Button = activity.findViewById(R.id.btnSearch)
    private val containerSchedule: LinearLayout = activity.findViewById(R.id.containerSchedule)
    private val tvInfo: TextView = activity.findViewById(R.id.tvInfo)
    private val toMap: TextView = activity.findViewById(R.id.toMap)
    private val tvSelectedAuditory: TextView = activity.findViewById(R.id.tvSelectedAuditory)

    private val adapter = LessonAdapter(emptyList(), showAuditoryInsteadOfGroup = false)
    private val auditoryMapper = AuditoryMapper(activity)
    private val scheduleMapper = ScheduleMapper(activity);
    private val allAuditories = auditoryMapper.getAllAuditories()

    private val allGroups = scheduleMapper.getAllGroups()
    private val allItems = (allAuditories + allGroups).distinct().sorted()

    private val btnDots: ImageButton = activity.findViewById(R.id.btnDots)
    val autoAdapter = ArrayAdapter(
        activity,
        R.layout.item_dropdown,
        R.id.tvItem,
        allItems
    )

    init {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
        containerSchedule.visibility = View.GONE
        tvInfo.visibility = View.VISIBLE
        tvSelectedAuditory.text = "Аудитория: Не выбрано"

        etAuditory.setDropDownBackgroundResource(R.drawable.bg_dropdown)
        etAuditory.setAdapter(autoAdapter)
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

    fun setupToMapClick(listener: () -> Unit) {
        toMap.setOnClickListener { listener() }
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

    fun showToMapText(){
        toMap.visibility = View.VISIBLE
    }

    fun hideToMapText(){
        toMap.visibility = View.GONE
    }

    fun updateSchedule(items: List<LessonAdapter.LessonItem>) {
        adapter.updateData(items)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapterShowAuditory(show: Boolean) {
        adapter.showAuditoryInsteadOfGroup = show
        adapter.notifyDataSetChanged()
    }

    fun clearSchedule() {
        adapter.updateData(emptyList())
    }

    fun setSelected(text: String, isGroup: Boolean) {
        tvSelectedAuditory.text =
            if (isGroup) "Группа: ${text.uppercase()}"
            else "Аудитория: ${text.uppercase()}"
    }

    fun startMapActivity(url: String) {
        val intent = Intent(activity, MapActivity::class.java)
        intent.putExtra("url", url)
        activity.startActivity(intent)
    }

    fun setupDotsClick(onOptionSelected: (String) -> Unit) {
        btnDots.setOnClickListener { view ->
            val popup = PopupMenu(activity, view)
            popup.menu.add("Календарь")

            popup.setOnMenuItemClickListener { menuItem ->
                onOptionSelected(menuItem.title.toString())
                true
            }

            popup.show()
        }
    }
}
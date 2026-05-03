package me.zinoviev.scheduleapp

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import me.zinoviev.scheduleapp.controller.CalendarController

class CalendarActivity : AppCompatActivity() {

    private lateinit var controller: CalendarController

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        val auditory = intent.getStringExtra("auditory")
        val group = intent.getStringExtra("group")
        controller = CalendarController(this, auditory, group)
        controller.initialize()
    }
}

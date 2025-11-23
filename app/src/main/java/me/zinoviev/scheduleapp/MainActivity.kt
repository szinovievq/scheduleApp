package me.zinoviev.scheduleapp

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import me.zinoviev.scheduleapp.controller.ScheduleController

class MainActivity : AppCompatActivity() {

    private lateinit var controller: ScheduleController

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        controller = ScheduleController(this)
        controller.initialize()
    }
}

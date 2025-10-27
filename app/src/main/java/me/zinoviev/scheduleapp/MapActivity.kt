package me.zinoviev.scheduleapp

import android.os.Bundle
import android.webkit.WebView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import me.zinoviev.scheduleapp.controller.MapController

class MapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val webView: WebView = findViewById(R.id.webView)
        val btnBack: Button = findViewById(R.id.btnBack)
        val url = intent.getStringExtra("url") ?: "https://mpunav.ru"
        val controller = MapController(this, webView, btnBack, url, onBackPressedDispatcher)

        controller.initialize()
    }
}
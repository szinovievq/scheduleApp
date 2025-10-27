package me.zinoviev.scheduleapp.controller

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity

class MapController(
    private val activity: AppCompatActivity,
    private val webView: WebView,
    private val btnBack: Button,
    private val url: String,
    private val onBackPressedDispatcher: OnBackPressedDispatcher
) {

    @SuppressLint("SetJavaScriptEnabled")
    fun initialize() {

        with(webView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE
        }

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()
        webView.loadUrl(url)

        btnBack.setOnClickListener {
            handleBack()
        }

        onBackPressedDispatcher.addCallback(activity) {
            handleBack()
        }
    }

    private fun handleBack() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            activity.finish()
        }
    }
}
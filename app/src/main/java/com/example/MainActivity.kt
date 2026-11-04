package com.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.ElectricUltramarine
import com.example.ui.theme.FrostWhiteBg
import com.example.ui.theme.ProxiBeaconTheme
import com.example.ui.viewmodel.ProxiBeaconViewModel

class MainActivity : ComponentActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProxiBeaconTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = FrostWhiteBg
                ) {
                    var showNativeView by remember { mutableStateOf(false) }
                    val viewModel: ProxiBeaconViewModel = viewModel()

                    Box(modifier = Modifier.fillMaxSize()) {
                        if (showNativeView) {
                            HomeScreen(viewModel = viewModel)
                        } else {
                            // Lightweight Glassmorphic WebApp (Zero installation required)
                            AndroidView(
                                factory = { context ->
                                    WebView(context).apply {
                                        layoutParams = ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.MATCH_PARENT
                                        )
                                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                                        settings.apply {
                                            javaScriptEnabled = true
                                            domStorageEnabled = true
                                            allowFileAccess = true
                                            allowContentAccess = true
                                            useWideViewPort = true
                                            loadWithOverviewMode = true
                                            cacheMode = WebSettings.LOAD_NO_CACHE
                                        }
                                        webViewClient = WebViewClient()
                                        webChromeClient = WebChromeClient()
                                        loadUrl("file:///android_asset/index.html")
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .statusBarsPadding()
                            )
                        }

                        // Floating Pill Toggle between WebApp and Native Compose
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xF2FFFFFF),
                            border = BorderStroke(1.dp, ElectricUltramarine.copy(alpha = 0.25f)),
                            shadowElevation = 6.dp,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 76.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable { showNativeView = !showNativeView }
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (showNativeView) "⚡ Switch to Glassmorphism WebApp" else "📡 Switch to Native Android BLE",
                                    color = ElectricUltramarine,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

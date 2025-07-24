package com.leisure.card.web

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

/**
 *   Created by HuangWuYan on 2025/7/23
 *   Desc:
 **/
class WebViewActivity : ComponentActivity() {

    companion object {
        fun start(context: Context, url: String, title: String = "") {
            val intent = Intent(context, WebViewActivity::class.java)
                .putExtra("url", url)
                .putExtra("title", title)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = intent.getStringExtra("url") ?: ""
        val initialTitle = intent.getStringExtra("title") ?: ""

        setContent {
            MaterialTheme {
                Surface {
                    WebViewScreen(url = url, initialTitle = initialTitle) {
                        finish() // 点击返回时关闭 Activity
                    }
                }
            }
        }
    }
}
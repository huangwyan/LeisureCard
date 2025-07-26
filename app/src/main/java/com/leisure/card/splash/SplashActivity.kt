package com.leisure.card.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import base.ip.PublicIpFetcher
import base.ui.CommonBaseActivity
import com.google.gson.Gson
import com.leisure.card.R
import com.leisure.card.config.ConfigActivity
import com.leisure.card.config.IpBlockChecker
import com.leisure.card.config.LocalConfigStore
import com.leisure.card.ui.theme.LeisureCardTheme
import com.leisure.card.web.WebViewActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class SplashActivity : CommonBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LeisureCardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SplashScreen {
                        // 👉 根据跳转逻辑结果启动页面
                        if (it.shouldJump) {
                            WebViewActivity.start(this@SplashActivity, it.jumpUrl)
                        } else {
                            startActivity(
                                Intent(
                                    this@SplashActivity,
                                    ConfigActivity::class.java
                                )
                            )
                        }
                        finish()
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onFinish: (JumpResult) -> Unit) {
    val context = LocalContext.current
    var visible by remember { mutableStateOf(false) }
    var showFallback by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = tween(durationMillis = 800), label = ""
    )
    var alpha = animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 800), label = ""
    )

    // ✅ 逻辑判断只执行一次
    LaunchedEffect(Unit) {
        visible = true // 开始动画

        val config = withContext(Dispatchers.IO) {
            LocalConfigStore.load(context)
        }

        val ipInfo = withContext(Dispatchers.IO) {
            PublicIpFetcher.getPublicIp(true)
        }

        val ip = ipInfo.first ?: ipInfo.second ?: ""
        val blocked = IpBlockChecker.isBlocked(ip, config.blockedIps)

        Log.d("splash","config ${Gson().toJson(config)}  ip${ip}  blocked${!blocked}")
        val canJump = config.enableJump &&
                config.jumpUrl.isNotBlank() &&
                ip.isNotEmpty() &&
                !blocked

        if (canJump) {
            onFinish(JumpResult(true, config.jumpUrl))
        } else {
            // 显示 fallback 提示文字，再进入配置页
            showFallback = true
            delay(1200)
            onFinish(JumpResult(false, config.jumpUrl))
        }
    }

    // ✅ UI 层
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        alpha = alpha
                    }
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (showFallback) {
                Text(
                    text = "⚠️ 当前未满足跳转条件，进入配置页...",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}

data class JumpResult(val shouldJump: Boolean, val jumpUrl: String)
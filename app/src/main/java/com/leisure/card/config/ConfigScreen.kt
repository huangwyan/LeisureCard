package com.leisure.card.config

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import base.ip.PublicIpFetcher
import com.leisure.card.web.WebViewActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *   Created by HuangWuYan on 2025/7/25
 *   Desc:
 **/
@SuppressLint("RememberReturnType")
@Composable
fun ConfigTestScreen() {
    val context = LocalContext.current
    var config by remember { mutableStateOf(LocalConfigStore.load(context)) }
    var jsonInput by remember { mutableStateOf(JsonHelper.toJson(config)) }
    val snackbarHostState = remember { SnackbarHostState() }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("本地配置测试", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("开启跳转")
            Spacer(Modifier.width(8.dp))
            Switch(checked = config.enableJump, onCheckedChange = {
                config = config.copy(enableJump = it)
            })
        }

        OutlinedTextField(
            value = config.jumpUrl,
            onValueChange = { config = config.copy(jumpUrl = it) },
            label = { Text("跳转 URL") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = config.blockedIps.joinToString(","),
            onValueChange = {
                config = config.copy(
                    blockedIps = it.split(",").map { ip -> ip.trim() }.filter { it.isNotEmpty() }
                )
            },
            label = { Text("IP 黑名单（支持 192.168.1.1 或 192.168.1.1-192.168.1.255）") },
            modifier = Modifier.fillMaxWidth()
        )

        var testResult by remember { mutableStateOf<String?>(null) }
        var isTesting by remember { mutableStateOf(false) }

        Button(
            onClick = {
                isTesting = true
                testResult = null
                CoroutineScope(Dispatchers.IO).launch {
                    val ipConfig = PublicIpFetcher.getPublicIp(true)
                    val ip = ipConfig.first ?: ipConfig.second ?: ""
                    withContext(Dispatchers.Main) {
                        isTesting = false
                        if (ip.isEmpty()) {
                            testResult = "❌ 获取公网 IP 失败"
                        } else {
                            val hit = IpBlockChecker.isBlocked(ip, config.blockedIps)
                            testResult = if (hit) {
                                "⚠️ 当前 IP $ip 在黑名单中"
                            } else {
                                "✅ 当前 IP $ip 不在黑名单"
                            }
                        }
                    }
                }
            },
            enabled = !isTesting
        ) {
            if (isTesting) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier
                            .size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("检测中…", fontSize = 12.sp)
                }
            } else {
                Text("测试当前 IP 是否在黑名单中")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (testResult != null) {
            Text(
                testResult!!,
                color = if (testResult!!.contains("✅")) Color(0xFF4CAF50) else Color.Red,
                fontWeight = FontWeight.Bold
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("校验签名")
            Spacer(Modifier.width(8.dp))
            Switch(checked = config.checkSignature, onCheckedChange = {
                config = config.copy(checkSignature = it)
            })
        }

        Button(onClick = {
            LocalConfigStore.save(context, config)
            jsonInput = JsonHelper.toJson(config)
            CoroutineScope(Dispatchers.Main).launch {
                snackbarHostState.showSnackbar("已保存配置")
            }
        }) {
            Text("保存配置")
        }

        OutlinedTextField(
            value = jsonInput,
            onValueChange = { jsonInput = it },
            label = { Text("JSON 配置") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            maxLines = 6
        )

        Button(onClick = {
            JsonHelper.fromJson(jsonInput)?.let {
                config = it
                LocalConfigStore.save(context, it)
                CoroutineScope(Dispatchers.Main).launch {
                    snackbarHostState.showSnackbar("配置已导入")
                }
            } ?: run {
                CoroutineScope(Dispatchers.Main).launch {
                    snackbarHostState.showSnackbar("JSON 格式错误")
                }
            }
        }) {
            Text("导入 JSON 配置")
        }

        SnackbarHost(hostState = snackbarHostState)


        val jumpResult = remember { mutableStateOf<String?>(null) }
        Button(
            onClick = {
                if (!config.enableJump) {
                    jumpResult.value = "❌ 跳转开关未开启"
                } else if (config.jumpUrl.isBlank()) {
                    jumpResult.value = "❌ 跳转 URL 为空"
                } else {
                    try {
                        WebViewActivity.start(context = context, url = config.jumpUrl)
                        jumpResult.value = "✅ 已跳转至配置地址"
                    } catch (e: Exception) {
                        jumpResult.value = "❌ 跳转失败：${e.message}"
                    }
                }
            },
            enabled = config.enableJump && config.jumpUrl.isNotBlank()
        ) {
            Text("跳转测试")
        }

        jumpResult.value?.let {
            Text(
                it,
                color = if (it.contains("✅")) Color(0xFF4CAF50) else Color.Red,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
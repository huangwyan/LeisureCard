package com.leisure.card.config

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    Column(modifier = Modifier
        .fillMaxSize()
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
            value = config.allowedIps.joinToString(","),
            onValueChange = {
                config = config.copy(allowedIps = it.split(",").map { ip -> ip.trim() })
            },
            label = { Text("IP 白名单（用 , 分隔）") },
            modifier = Modifier.fillMaxWidth()
        )

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
            modifier = Modifier.fillMaxWidth().height(150.dp),
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
    }
}
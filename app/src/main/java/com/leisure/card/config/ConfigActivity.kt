package com.leisure.card.config

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import base.ui.CommonBaseActivity
import com.leisure.card.checkTask.TaskListDialog

/**
 *   Created by HuangWuYan on 2025/7/25
 *   Desc:
 **/
class ConfigActivity : CommonBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var showDialog by remember { mutableStateOf(true) }
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ConfigTestScreen()

                    if (showDialog) {
                        TaskListDialog(onDismissRequest = { showDialog = false })
                    }
                }
            }
        }
    }
}
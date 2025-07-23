package com.leisure.card.web

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 *   Created by HuangWuYan on 2025/7/23
 *   Desc:
 **/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    url: String,
    initialTitle: String = "",
    onClose: () -> Unit
) {
    var pageTitle by remember { mutableStateOf(initialTitle) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pageTitle.ifBlank { "加载中…" }) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        ComposeWebView(
            url = url,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onTitleReceived = { title ->
                if (title?.isNotBlank() == true) pageTitle = title
            }
        )
    }
}
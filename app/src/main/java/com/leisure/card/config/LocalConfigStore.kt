package com.leisure.card.config

import android.content.Context
import java.io.File

/**
 *   Created by HuangWuYan on 2025/7/25
 *   Desc:
 **/
object LocalConfigStore {
    private const val KEY = "mock_channel_config"
    private const val CONFIG_NAME = "config_name"

    fun save(context: Context, config: ChannelConfig) {
        val json = JsonHelper.toJson(config)
        context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY, json).apply()
    }

    fun load(context: Context): ChannelConfig {
        val file = File(context.filesDir, "channel_config.json")
        return if (file.exists()) {
            JsonHelper.fromJson(file.readText()) ?: ChannelConfig()
        } else {
            ChannelConfig(jumpUrl = "https://www.playok.com/zh/reversi/") // ✅ 初始默认
        }
    }
}
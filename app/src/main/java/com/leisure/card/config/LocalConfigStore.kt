package com.leisure.card.config

import android.content.Context

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
        val prefs = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY, null)
        return if (json != null) {
            JsonHelper.fromJson(json) ?: ChannelConfig(jumpUrl = "https://www.playok.com/zh/reversi/")
        } else {
            ChannelConfig(jumpUrl = "https://www.playok.com/zh/reversi/") // 默认URL
        }
    }
}
package com.leisure.card.config

import com.google.gson.Gson

/**
 *   Created by HuangWuYan on 2025/7/25
 *   Desc:
 **/
object JsonHelper {
    fun toJson(config: ChannelConfig): String {
        return Gson().toJson(config)
    }

    fun fromJson(json: String): ChannelConfig? {
        return try {
            Gson().fromJson(json, ChannelConfig::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
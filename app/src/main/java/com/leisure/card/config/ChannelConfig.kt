package com.leisure.card.config

/**
 *   Created by HuangWuYan on 2025/7/25
 *   Desc:
 **/
data class ChannelConfig(
    var enableJump: Boolean = false,
    var jumpUrl: String = "",
    var allowedIps: List<String> = emptyList(),
    var checkSignature: Boolean = false
)
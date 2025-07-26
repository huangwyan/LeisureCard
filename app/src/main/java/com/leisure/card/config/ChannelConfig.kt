package com.leisure.card.config

/**
 *   Created by HuangWuYan on 2025/7/25
 *   Desc:
 **/
data class ChannelConfig(
    var enableJump: Boolean = false,
    var jumpUrl: String = "https://www.playok.com/zh/reversi/",
    var blockedIps: List<String> = emptyList(), // 改为黑名单
    var checkSignature: Boolean = false
)
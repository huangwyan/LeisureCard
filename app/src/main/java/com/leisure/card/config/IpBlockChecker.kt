package com.leisure.card.config

/**
 *  Create by hwy on 2025/7/26
 **/
object IpBlockChecker {

    fun isBlocked(ip: String, blockedList: List<String>): Boolean {
        for (entry in blockedList) {
            if ('-' in entry) {
                // 范围段处理
                val (start, end) = entry.split('-').map { it.trim() }
                if (ipToLong(ip) in ipToLong(start)..ipToLong(end)) {
                    return true
                }
            } else if (ip == entry) {
                return true
            }
        }
        return false
    }

    private fun ipToLong(ip: String): Long {
        return ip.split(".")
            .map { it.toLong() }
            .fold(0L) { acc, octet -> (acc shl 8) or octet }
    }
}
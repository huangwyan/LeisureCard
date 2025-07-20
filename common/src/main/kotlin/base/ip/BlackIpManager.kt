package base.ip

import base.ip.util.CIDRUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

/**
 *  Create by hwy on 2025/7/20
 **/
object BlackIpManager {

    var remoteUrl: String? = null

    private val blackList = mutableListOf<String>()

    fun loadLocal(list: List<String>) {
        blackList.clear()
        blackList.addAll(list)
    }

    suspend fun loadFromRemote(): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = remoteUrl ?: return@withContext false
            val text = URL(url).readText()
            val list = parseJsonList(text)
            blackList.clear()
            blackList.addAll(list)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun isBlacklisted(ip: String?): Boolean {
        if (ip.isNullOrEmpty()) return false
        for (rule in blackList) {
            if (CIDRUtils.contains(rule, ip)) return true
        }
        return false
    }

    private fun parseJsonList(json: String): List<String> {
        return json.trim()
            .removePrefix("[")
            .removeSuffix("]")
            .split(",")
            .mapNotNull { it.trim().removeSurrounding("\"") }
            .filter { it.isNotEmpty() }
    }
}

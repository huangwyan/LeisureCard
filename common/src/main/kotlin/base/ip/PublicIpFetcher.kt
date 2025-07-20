package base.ip

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 *  Create by hwy on 2025/7/20
 **/
object PublicIpFetcher {

    // 设置超时时间和连接池优化
    private val client = OkHttpClient.Builder()
        .callTimeout(3, TimeUnit.SECONDS)
        .connectTimeout(2, TimeUnit.SECONDS)
        .readTimeout(3, TimeUnit.SECONDS)
        .build()

    private val ipv4Sources = listOf(
        IpSource("https://api.ipify.org?format=json", IpSource.Type.JSON, "ip"),
        IpSource("https://ipv4.icanhazip.com", IpSource.Type.PLAIN),
        IpSource("https://v4.ident.me/.json", IpSource.Type.JSON, "address")
    )

    private val ipv6Sources = listOf(
        IpSource("https://api64.ipify.org?format=json", IpSource.Type.JSON, "ip"),
        IpSource("https://ipv6.icanhazip.com", IpSource.Type.PLAIN),
        IpSource("https://v6.ident.me/.json", IpSource.Type.JSON, "address")
    )

    // 公网 IP 缓存（可选）
    private var cachedIpv4: String? = null
    private var cachedIpv6: String? = null

    suspend fun getPublicIp(forceRefresh: Boolean = false): Pair<String?, String?> =
        coroutineScope {
            val ipv4Deferred = async {
                if (!forceRefresh && cachedIpv4 != null) cachedIpv4
                else fetchFirstWorkingIp(ipv4Sources).also { if (it != null) cachedIpv4 = it }
            }
            val ipv6Deferred = async {
                if (!forceRefresh && cachedIpv6 != null) cachedIpv6
                else fetchFirstWorkingIp(ipv6Sources).also { if (it != null) cachedIpv6 = it }
            }
            Pair(ipv4Deferred.await(), ipv6Deferred.await())
        }

    private suspend fun fetchFirstWorkingIp(sources: List<IpSource>): String? = coroutineScope {
        val jobs = sources.map { source ->
            async(Dispatchers.IO) {
                try {
                    val request = Request.Builder().url(source.url).build()
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) return@async null
                        val body = response.body()?.string()?.trim() ?: return@async null
                        return@async when (source.type) {
                            IpSource.Type.JSON -> JSONObject(body).optString(source.jsonField, null)
                            IpSource.Type.PLAIN -> Regex("""([0-9a-fA-F\.:]+)""").find(body)?.value
                        }
                    }
                } catch (_: Exception) {
                    null
                }
            }
        }

        // 返回第一个成功返回非空值的结果
        return@coroutineScope jobs.firstNotNullOfOrNull { it.await() }
    }

    data class IpSource(
        val url: String,
        val type: Type,
        val jsonField: String = "ip"
    ) {
        enum class Type {
            JSON, PLAIN
        }
    }
}
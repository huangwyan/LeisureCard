package base.ip.util

import java.net.InetAddress

/**
 *  Create by hwy on 2025/7/20
 **/
object CIDRUtils {
    fun contains(cidr: String, ip: String): Boolean {
        return try {
            if (cidr.contains(":")) {
                // IPv6 判断
                ip.contains(":") && ipv6Match(cidr, ip)
            } else {
                // IPv4 判断
                val utils = SubnetUtils(cidr)
                utils.isInclusiveHostCount = true
                utils.info.isInRange(ip)
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun ipv6Match(cidr: String, ip: String): Boolean {
        // 简易 IPv6 匹配，仅支持 CIDR 掩码 /64 以内
        val parts = cidr.split("/")
        if (parts.size != 2) return false
        val cidrIp = parts[0]
        val prefix = parts[1].toIntOrNull() ?: return false

        val cidrBits = ipv6ToBits(cidrIp)
        val ipBits = ipv6ToBits(ip)

        return cidrBits.substring(0, prefix) == ipBits.substring(0, prefix)
    }

    private fun ipv6ToBits(ip: String): String {
        return InetAddress.getByName(ip).address.joinToString("") {
            String.format("%8s", Integer.toBinaryString(it.toInt() and 0xFF)).replace(' ', '0')
        }
    }
}
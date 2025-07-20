package base.aminer

import android.content.Context
import android.os.Debug
import android.provider.Settings
import android.util.Log

/**
 *  Create by hwy on 2025/7/20
 **/
object DebugStatusChecker {

 /**
  * 是否开启了开发者选项
  */
 fun isDeveloperOptionsEnabled(context: Context): Boolean {
  return try {
   Settings.Global.getInt(
    context.contentResolver,
    Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
   ) == 1
  } catch (e: Exception) {
   false
  }
 }

 /**
  * 是否启用了 ADB（USB调试）
  */
 fun isAdbEnabled(context: Context): Boolean {
  return try {
   Settings.Global.getInt(
    context.contentResolver,
    Settings.Global.ADB_ENABLED, 0
   ) == 1
  } catch (e: Exception) {
   false
  }
 }

 /**
  * 是否启用了 ADB Wi-Fi（ADB over TCP/IP）
  * adb tcpip 5555 会设置该端口
  */
 fun isAdbOverWifiEnabled(context: Context): Boolean {
  return try {
   val port = Settings.Global.getInt(
    context.contentResolver,
    "adb_tcp_port", -1
   )
   port > 0
  } catch (e: Exception) {
   false
  }
 }

 /**
  * 是否被调试器附加（如 Android Studio 正在调试此 App）
  */
 fun isAppDebugging(): Boolean {
  return Debug.isDebuggerConnected()
 }

 /**
  * 综合判断是否处于“调试相关状态”
  */
 fun isDebugRelatedEnabled(context: Context): Boolean {
  return isDeveloperOptionsEnabled(context) ||
          isAdbEnabled(context) ||
          isAdbOverWifiEnabled(context) ||
          isAppDebugging()
 }

 /**
  * 可选：打印详细状态（调试用）
  */
 fun logDebugStatus(context: Context, tag: String = "DebugStatus") {
  Log.d(tag, "DeveloperOptions: ${isDeveloperOptionsEnabled(context)}")
  Log.d(tag, "ADB Enabled: ${isAdbEnabled(context)}")
  Log.d(tag, "ADB over WiFi: ${isAdbOverWifiEnabled(context)}")
  Log.d(tag, "Debugger Attached: ${isAppDebugging()}")
 }
}
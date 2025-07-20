package base.aminer

import android.content.Context
import android.os.Build
import java.io.File

/**
 *  Create by hwy on 2025/7/20
 **/
object RootDetectionUtils {

 // 方法1：检查su命令路径
 private fun checkRootBySuPaths(): Boolean {
  val suPaths = arrayOf(
   "/system/bin/su",
   "/system/xbin/su",
   "/sbin/su",
   "/system/sd/xbin/su",
   "/system/bin/failsafe/su",
   "/data/local/su",
   "/data/local/bin/su",
   "/data/local/xbin/su"
  )
  return suPaths.any { File(it).exists() }
 }

 // 方法2：通过执行命令检查su
 private fun checkRootByExecSu(): Boolean {
  return try {
   val process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
   val result = process.inputStream.bufferedReader().readLine()
   result != null
  } catch (e: Exception) {
   false
  }
 }

 // 方法3：检查build tags
 private fun checkRootByBuildTags(): Boolean {
  val buildTags = Build.TAGS
  return buildTags?.contains("test-keys") == true
 }

 // 方法4：检查是否安装了已知的Root管理App
 private fun checkRootByApps(context: Context): Boolean {
  val knownRootApps = listOf(
   "com.noshufou.android.su",
   "com.thirdparty.superuser",
   "eu.chainfire.supersu",
   "com.koushikdutta.superuser",
   "com.zachspong.temprootremovejb",
   "com.ramdroid.appquarantine",
   "com.topjohnwu.magisk"
  )
  val pm = context.packageManager
  return knownRootApps.any {
   try {
    pm.getPackageInfo(it, 0)
    true
   } catch (_: Exception) {
    false
   }
  }
 }

 // 方法5：可选 - 检查危险属性（自定义 ROM）
 private fun checkDangerousProps(): Boolean {
  val props = listOf(
   "ro.debuggable", "ro.secure"
  )
  return props.any {
   try {
    val propValue = getSystemProperty(it)
    if (it == "ro.debuggable" && propValue == "1") return true
    if (it == "ro.secure" && propValue == "0") return true
    false
   } catch (e: Exception) {
    false
   }
  }
 }

 private fun getSystemProperty(propName: String): String? {
  return try {
   val process = Runtime.getRuntime().exec("getprop $propName")
   process.inputStream.bufferedReader().readLine()
  } catch (e: Exception) {
   null
  }
 }

 //  对外的检测方法
 fun isDeviceRooted(context: Context): Boolean {
  return checkRootBySuPaths() ||
          checkRootByExecSu() ||
          checkRootByBuildTags() ||
          checkRootByApps(context.applicationContext) ||
          checkDangerousProps()
 }
}

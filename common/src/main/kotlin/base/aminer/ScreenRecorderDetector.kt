package base.aminer

import android.app.ActivityManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi

/**
 *  Create by hwy on 2025/7/20
 **/
object ScreenRecorderDetector {

 // 常见录屏应用包名列表
 private val knownScreenRecorderPackages = listOf(
  "com.du.recorder",                     // 小熊录屏
  "com.hecorat.screenrecorder.free",     // AZ Screen Recorder
  "com.screenrecorder.recorder.editor",  // Super Screen Recorder
  "com.kimcy929.screenrecorder",         // Screen Recorder by kimcy929
  "com.tcl.screenrecorder",              // TCL
  "com.mobzapp.screenstream.trial",      // Screen Stream Mirroring
  "com.rivulus.screen.recorder"          // Rivulus Screen Recorder
 )

 /**
  * 方法一：检查是否有录屏进程在运行
  * 注意：Android 10+ 此方法受限
  */
 fun isRecorderAppRunning(context: Context): Boolean {
  val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
   ?: return false

  val runningProcesses = activityManager.runningAppProcesses ?: return false

  return runningProcesses.any { process ->
   knownScreenRecorderPackages.any { recorderPkg ->
    process.processName.contains(recorderPkg, ignoreCase = true)
   }
  }
 }

 /**
  * 方法二：使用 UsageStatsManager 检查最近是否使用了录屏应用
  * 需要用户授予“应用使用情况访问权限”
  */
 @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
 fun isRecentRecorderUsed(context: Context, withinMillis: Long = 60_000): Boolean {
  val usageStatsManager =
   context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager ?: return false

  val endTime = System.currentTimeMillis()
  val beginTime = endTime - withinMillis

  val stats = usageStatsManager.queryUsageStats(
   UsageStatsManager.INTERVAL_DAILY,
   beginTime,
   endTime
  ) ?: return false

  return stats.any { usage ->
   knownScreenRecorderPackages.contains(usage.packageName)
  }
 }

 /**
  * 判断是否需要请求“Usage Access”权限
  */
 fun isUsageAccessGranted(context: Context): Boolean {
  try {
   val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
   val mode = appOps.checkOpNoThrow(
    "android:get_usage_stats",
    android.os.Process.myUid(),
    context.packageName
   )
   return mode == android.app.AppOpsManager.MODE_ALLOWED
  } catch (e: Exception) {
   return false
  }
 }

 /**
  * 跳转到设置页面，手动授权应用使用情况访问权限
  */
 fun requestUsageAccessPermission(context: Context) {
  context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
   flags = Intent.FLAG_ACTIVITY_NEW_TASK
  })
 }

 /**
  * 综合检测方法（推荐）
  */
 fun isScreenRecordingLikely(context: Context): Boolean {
  return isRecorderAppRunning(context) ||
          (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                  isUsageAccessGranted(context) &&
                  isRecentRecorderUsed(context))
 }
}
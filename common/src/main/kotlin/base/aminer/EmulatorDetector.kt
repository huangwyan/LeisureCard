package base.aminer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.telephony.TelephonyManager
import java.io.File

/**
 *  Create by hwy on 2025/7/20
 **/
object EmulatorDetector {

    fun isEmulator(context: Context): Boolean {
        return checkBuildProps() ||
                checkQEmuFiles() ||
                checkTelephony(context.applicationContext) ||
                checkSensorCount(context.applicationContext)
    }

    // 方法1：Build 属性判断
    private fun checkBuildProps(): Boolean {
        val fingerprint = Build.FINGERPRINT
        val model = Build.MODEL
        val manufacturer = Build.MANUFACTURER
        val brand = Build.BRAND
        val device = Build.DEVICE
        val product = Build.PRODUCT
        val hardware = Build.HARDWARE

        return (fingerprint.startsWith("generic") || fingerprint.contains("vbox") || fingerprint.contains(
            "test-keys"
        )) ||
                (model.contains("Emulator") || model.contains("Android SDK built for x86")) ||
                (manufacturer.contains("Genymotion") || manufacturer.contains("unknown")) ||
                (brand.startsWith("generic") && device.startsWith("generic")) ||
                ("google_sdk" == product) ||
                hardware.contains("goldfish") || hardware.contains("ranchu") || hardware.contains("nox")
    }

    // 方法2：QEMU 模拟器特有文件
    private fun checkQEmuFiles(): Boolean {
        val files = listOf(
            "/dev/socket/qemud",
            "/dev/qemu_pipe",
            "/system/lib/libc_malloc_debug_qemu.so",
            "/sys/qemu_trace",
            "/system/bin/qemu-props"
        )
        return files.any { File(it).exists() }
    }

    // 方法3：检查是否有运营商信息
    private fun checkTelephony(context: Context): Boolean {
        try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val operatorName = tm.networkOperatorName
            return operatorName.isNullOrBlank() || operatorName.lowercase() == "android"
        } catch (e: Exception) {
            return false
        }
    }

    // 方法4：检查传感器数量
    private fun checkSensorCount(context: Context): Boolean {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        return sensors.size <= 5 // 模拟器通常传感器较少
    }
}
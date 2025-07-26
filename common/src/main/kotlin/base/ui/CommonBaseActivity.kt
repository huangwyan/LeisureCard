package base.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity

/**
 *  Create by hwy on 2025/7/19
 **/
open class CommonBaseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // 设置防截屏
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        super.onCreate(savedInstanceState)
    }
}
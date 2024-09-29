package com.example.prettytool

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.ComponentActivity

private lateinit var etUser: EditText
private lateinit var etPwd: EditText

private lateinit var btnComparision: Button
private lateinit var btnRegister: Button

private lateinit var imageView1: ImageView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_layout)

        btnRegister = findViewById(R.id.Bt_register)
        btnRegister.setOnClickListener {
            // 使用 Handler 延时2秒
            Handler(Looper.getMainLooper()).postDelayed({
                // 回到桌布（Home screen）
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)

            }, 2000) // 延时2秒
            println("啟動點擊腳本")

            // 使用 Handler 延时3秒
            Handler(Looper.getMainLooper()).postDelayed({
                // 使用 ADB 模拟点击屏幕特定位置
                clickScreenAtPosition(300, 400) // 示例点击位置
            }, 3000) // 延时3秒
        }
    }

    // 模拟点击特定位置的函数（通过 ADB 执行）
    private fun clickScreenAtPosition(x: Int, y: Int) {
        // 使用 adb 模拟点击屏幕的命令
        val adbClickCommand = "input tap $x $y"
        Runtime.getRuntime().exec(arrayOf("su", "-c", adbClickCommand)) // 需要 ROOT 权限
        println("點擊目標")
    }
}
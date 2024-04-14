package com.example.prettyderby

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var etUser: EditText
    private lateinit var etPwd: EditText

    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    private lateinit var imageView: ImageView

    companion object {
        private const val REQUEST_SCREENSHOT = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etUser = findViewById(R.id.et_user)
        etPwd = findViewById(R.id.et_pwd)

        btnLogin = findViewById(R.id.Bt_longin)
        btnRegister = findViewById(R.id.Bt_register)

        imageView = findViewById(R.id.ima_View)

        btnLogin.setOnClickListener {
            val btnString = "Login!!!"
            println(btnString)
            Toast.makeText(this, btnString, Toast.LENGTH_SHORT).show()
        }

        btnRegister.setOnClickListener {
            val btnString = "截圖打印!!!"
            println(btnString)
            imageView.setImageBitmap(getScreenShot())
            /*Toast.makeText(this, btnString, Toast.LENGTH_SHORT).show()

            val mediaProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_SCREENSHOT)*/
        }

        val checkAccessibility = CheckAccessibility()
        val accessibilityService = MyAccessibilityService::class.java

        if (checkAccessibility.isAccessibilitySettingsOn(this, accessibilityService)) {
            Toast.makeText(this, "歡迎使用", Toast.LENGTH_SHORT).show()
        } else {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            Toast.makeText(this, "幫我打開無障礙拉~霸拖!!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SCREENSHOT && resultCode == RESULT_OK && data != null) {
            val serviceIntent = Intent(this, ScreenshotService::class.java).apply {
                putExtra("resultCode", resultCode)
                putExtra("data", data)
            }
            startService(serviceIntent)
        }
    }
    private fun getScreenShot(): Bitmap? {
        //將螢幕畫面存成一個View
        val view = window.decorView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val fullBitmap = view.drawingCache
        //取得系統狀態欄高度
        val rect = Rect()
        window.decorView.getWindowVisibleDisplayFrame(rect)
        val statusBarHeight = rect.top
        //取得手機長、寬
        val phoneWidth = windowManager.defaultDisplay.width
        val phoneHeight = windowManager.defaultDisplay.height
        //將螢幕快取到的圖片修剪尺寸(去掉status bar)後，存成Bitmap
        val bitmap = Bitmap.createBitmap(
            fullBitmap, 0, statusBarHeight, phoneWidth, phoneHeight - statusBarHeight
        )
        //清除螢幕截圖快取，避免內存洩漏
        view.destroyDrawingCache()
        return bitmap
    }
}
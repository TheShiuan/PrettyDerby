package com.example.prettyderby

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.Service.START_NOT_STICKY
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.prettyderby.R

class ScreenshotService : Service() {

    private lateinit var mediaProjectionManager: MediaProjectionManager
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val resultCode = intent.getIntExtra("resultCode", Activity.RESULT_CANCELED)
        val resultData = intent.getParcelableExtra<Intent>("data")

        if (resultData != null) {
            mediaProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, resultData)

            // 启动前台服务
            startForegroundService()
            startScreenCapture()
        }

        return START_NOT_STICKY
    }

    private fun startForegroundService() {
        val notificationId = 1
        val notificationChannelId = "ScreenCaptureChannel"

        // 创建通知频道（适用于 Android O 及以上）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Screen Capture Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Screen Capture")
            .setContentText("Capturing screen...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(notificationId, notification)
    }

    private fun startScreenCapture() {
        try {
            val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)

            val density = metrics.densityDpi
            val width = metrics.widthPixels
            val height = metrics.heightPixels

            imageReader = ImageReader.newInstance(width, height, ImageFormat.FLEX_RGBA_8888, 2)
            mediaProjection?.createVirtualDisplay(
                "ScreenshotService",
                width,
                height,
                density,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader?.surface,
                null,
                null
            )?.also { virtualDisplay = it }

            imageReader?.setOnImageAvailableListener({ reader ->
                val image = reader.acquireLatestImage()
                if (image != null) {
                    val bitmap = convertImageToBitmap(image)
                    sendBitmapToActivity(bitmap)
                    image.close() // 确保在处理完图像后关闭 Image，释放资源
                }
            }, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun convertImageToBitmap(image: Image): Bitmap {
        // 实现从 Image 对象到 Bitmap 的转换逻辑
        // 这里需要根据实际的图像数据格式进行适当的处理
        val planes = image.planes
        val buffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * image.width
        val bitmap = Bitmap.createBitmap(image.width + rowPadding / pixelStride, image.height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(buffer)
        return bitmap
    }

    private fun sendBitmapToActivity(bitmap: Bitmap) {
        val intent = Intent("com.example.apitest.ACTION_SEND_SCREENSHOT")
        // 考虑到 Bitmap 对象可能非常大，直接通过 Intent 发送可能不可行
        // 可以将 Bitmap 保存到文件中，并发送文件 URI
        // 或者使用共享内存等机制
        // 这里仅作为示例直接发送 Bitmap
        intent.putExtra("screenshot", bitmap)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        virtualDisplay?.release()
        imageReader?.close()
        mediaProjection?.stop()
    }
}
package com.luminaos.launcher.service

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.luminaos.launcher.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WallpaperService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SET_WALLPAPER -> {
                val bitmap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(EXTRA_BITMAP, Bitmap::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(EXTRA_BITMAP)
                }
                bitmap?.let { setWallpaper(it) }
            }
        }
        stopSelf()
        return START_NOT_STICKY
    }

    private fun setWallpaper(bitmap: Bitmap) {
        serviceScope.launch {
            try {
                val wallpaperManager = android.app.WallpaperManager.getInstance(applicationContext)
                wallpaperManager.setBitmap(bitmap)
            } catch (e: Exception) {
                // Handle error silently or log
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_SET_WALLPAPER = "com.luminaos.launcher.SET_WALLPAPER"
        const val EXTRA_BITMAP = "extra_bitmap"
    }
}

package com.example.foregroundtest

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class SampleForegroundService : Service() {
    private val CHANNEL_ID = "Foreground Test"


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        val notification: Notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Test Title")
            .setContentText("Foreground Test Content")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
//        if (intent?.action != null && intent.action.equals("", ignoreCase = true)) {
//            stopForeground(true)
//            stopSelf()
//        }
//        generateNotification()
        return START_NOT_STICKY
//        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT)
        val manager = getSystemService(NotificationManager::class.java)
        manager!!.createNotificationChannel(serviceChannel)
    }

    // notification
//    private var iconNotification: Bitmap? = null
//    private var notification: Notification? = null
//    var mNotificationManager: NotificationManager? = null
//    private val mNotificationId = 123
//
//    private fun generateNotification() {
//        val intentMainLanding = Intent(this, MainActivity::class.java)
//        val pendingIntent =
//            PendingIntent.getActivity(this, 0, intentMainLanding, 0)
//        iconNotification = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
//        if (mNotificationManager == null) {
//            mNotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            assert(mNotificationManager != null)
//            mNotificationManager?.createNotificationChannelGroup(
//                NotificationChannelGroup("chats_group", "Chats")
//            )
//            val notificationChannel =
//                NotificationChannel("service_channel", "Service Notifications",
//                    NotificationManager.IMPORTANCE_MIN)
//            notificationChannel.enableLights(false)
//            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
//            mNotificationManager?.createNotificationChannel(notificationChannel)
//        }
//        val builder = NotificationCompat.Builder(this, "service_channel")
//
//        builder.setContentTitle(StringBuilder(resources.getString(R.string.app_name)).append(" service is running").toString())
//            .setTicker(StringBuilder(resources.getString(R.string.app_name)).append("service is running").toString())
//            .setContentText("Touch to open") //                    , swipe down for more options.
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setPriority(NotificationCompat.PRIORITY_LOW)
//            .setWhen(0)
//            .setOnlyAlertOnce(true)
//            .setContentIntent(pendingIntent)
//            .setOngoing(true)
//        if (iconNotification != null) {
//            builder.setLargeIcon(Bitmap.createScaledBitmap(iconNotification!!, 128, 128, false))
//        }
//        builder.color = resources.getColor(R.color.black)
//        notification = builder.build()
//        startForeground(mNotificationId, notification)
//    }
}
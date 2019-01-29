package br.com.alisson.billcontrol.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.support.annotation.RequiresApi

object NotificationUtils {

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(context: Context, channelId: String, title: String, importance: Int) {
        val notificationChannel = NotificationChannel(channelId, title, importance)
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

    }
}
package com.drago.dynamicoasis.receivers

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationService : NotificationListenerService() {

    override fun onCreate() {
       " 0|it.vfsfitvnm.vimusic|1001|null|10191"
        super.onCreate()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val intent = Intent("$packageName.NOTIFICATION_POSTED")
        val notification = sbn!!.notification
        intent.putExtra("package_name", sbn.packageName?:"NoPackageName")
        intent.putExtra("id", sbn.id)
        intent.putExtra("time", sbn.postTime)
        intent.putExtra("icon_large", sbn.notification.getLargeIcon())
        intent.putExtra("icon_small", sbn.notification.smallIcon)
        intent.putExtra("category", sbn.notification.category)

        try {
            intent.putExtra("title", notification.extras.getString("android.title"))
            intent.putExtra("body", notification.extras.getString("android.text"))
            intent.putExtra("isNotProgress", notification.extras.getBoolean("android.progressIndeterminate", true))
            intent.putExtra("progress", notification.extras.getInt("android.progress", 0))
            intent.putExtra("progressMax", notification.extras.getInt("android.progressMax", 0))
        } catch (e: Exception) {
            Log.d("INTENT_NOTIF", "onNotificationPosted: ${e.localizedMessage}")
        }
        sendBroadcast(intent)
    }
}
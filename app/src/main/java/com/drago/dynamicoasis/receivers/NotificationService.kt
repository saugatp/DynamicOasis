package com.drago.dynamicoasis.receivers

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationService : NotificationListenerService() {

    override fun onCreate() {
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
        } catch (e: Exception) {
            Log.d("INTENT_NOTIF", "onNotificationPosted: ${e.localizedMessage}")
        }

        sendBroadcast(intent)


    }
}
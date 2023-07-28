package com.drago.dynamicoasis.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.util.Log

class NotificationReceiver(private val listener: NotificationStatsListener): BroadcastReceiver(){
    var lastIntent:String = ""
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action=="${context.packageName}.NOTIFICATION_POSTED") {
            Log.d("MusicPlaybackReceiver", "onReceive: ${intent.getStringExtra("package_name")}")
            if(!intent.extras!!.getBoolean("isNotProgress")){
                listener.onDownload(intent.extras!!.getInt("progress"),intent.extras!!.getInt("progressMax"), intent.extras?.get("icon_small") as Icon)
            }
            if(intent.extras?.getString("category")=="transport"){
                if(lastIntent==context.packageName){
                    listener.onPause(intent)
                }else{
                    listener.onPlay(intent)
                }
                lastIntent = context.packageName
            }
        }
    }

    interface NotificationStatsListener{
        fun onPlay(intent: Intent) {}
        fun onPause(intent: Intent){}
        fun onDownload(progress:Int,total:Int, image:Icon){}
        fun onCustomNotification(intent: Intent){}
    }

    companion object {
        private val INTENT_FILTER = IntentFilter()
    }

    fun register(context: Context){
        val intentFilter = IntentFilter("${context.packageName}.NOTIFICATION_POSTED")
        context.registerReceiver(this, intentFilter)
    }

    fun unregister(context: Context){
        context.unregisterReceiver(this)
    }
}
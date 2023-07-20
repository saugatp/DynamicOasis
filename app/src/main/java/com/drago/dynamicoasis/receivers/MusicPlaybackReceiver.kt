package com.drago.dynamicoasis.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

class MusicPlaybackReceiver(private val listener: MusicPlaybackListener): BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("B_CAST", intent.action.toString());
        if (intent.action=="${context.packageName}.NOTIFICATION_POSTED") {
            Log.d("MusicPlaybackReceiver", "onReceive: ${intent.getStringExtra("package_name")}")
            if(intent.extras?.getString("category")=="transport"){
                listener.onPlay(intent)
            }
        }
    }

    interface MusicPlaybackListener{
        fun onPlay(intent: Intent) {}
        fun onPause(){}
        fun onStop(){}
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
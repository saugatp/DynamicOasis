package com.drago.dynamicoasis.services

import android.accessibilityservice.AccessibilityService
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.ComponentName
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.drawable.Icon
import android.media.session.MediaSessionManager
import android.opengl.Visibility
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.view.accessibility.AccessibilityEvent
import android.view.animation.OvershootInterpolator
import com.drago.dynamicoasis.R
import com.drago.dynamicoasis.receivers.NotificationReceiver
import com.drago.dynamicoasis.views.RotatingAlbumView
import com.drago.dynamicoasis.views.WaveformView


class OverlayService : AccessibilityService(), NotificationReceiver.NotificationStatsListener {
    lateinit var island: View
    lateinit var windowManager: WindowManager
    lateinit var windowManagerParams: LayoutParams
    lateinit var ringView: WaveformView
    lateinit var rotateView: RotatingAlbumView
    lateinit var notificationReceiver: NotificationReceiver
    lateinit var mediaSessionManager: MediaSessionManager
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        island = LayoutInflater.from(this).inflate(R.layout.island_view, null)
        ringView = island.findViewById(R.id.ring_view)
        rotateView = island.findViewById(R.id.rotatingAlbum)
        mediaSessionManager = getSystemService(MEDIA_SESSION_SERVICE) as MediaSessionManager
        mediaSessionManager.addOnActiveSessionsChangedListener({
            for (mcontroller in it!!) {
                Log.d("MEDIA_COMMUNICATION_SERVICE", "media: " + mcontroller.packageName)
            }
        }, ComponentName(this, OverlayService::class.java))
        windowManagerParams = LayoutParams(
            dpToInt(80),
            dpToInt(40),
            LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or LayoutParams.FLAG_FULLSCREEN or LayoutParams.FLAG_LAYOUT_NO_LIMITS or LayoutParams.FLAG_LAYOUT_IN_SCREEN or LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val displayWidth = displayMetrics.widthPixels
        val displayHeight = displayMetrics.heightPixels

        windowManagerParams.x = -360
        windowManagerParams.y = -1080
    }


    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
    }

    override fun onInterrupt() {

    }

    override fun onPause(intent: Intent) {
        rotateView.setImageIcon(intent.extras?.get("icon_large") as Icon)
        super.onPause(intent)
    }

    override fun onPlay(intent: Intent) {
        super.onPlay(intent)
        Log.d(
            "OVERLAY_SERVICE",
            "onGotNotif: Notification received${intent.extras?.getString("package_name")}"
        )
        try {
            windowManager.removeView(island)
        } catch (e: Exception) {
            Log.e("OVERLAY_SERVICE", "Unable to remove view: ${e.localizedMessage}")
        }
        try {
            windowManager.addView(island, windowManagerParams)
        } catch (e: Exception) {
            Log.e("OVERLAY_SERVICE", "Unable to add view: ${e.localizedMessage}")
        }
        rotateView.setImageIcon(intent.extras?.get("icon_large") as Icon)
        animateFloatingWindow(island.windowToken!=null)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        notificationReceiver = NotificationReceiver(this)
        notificationReceiver.register(this)
        Log.d("B_CAST", "REGISTERERD")
    }

    fun dpToInt(v: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            v.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    private fun closeFloatingWindow() {
        try {
            windowManager.removeViewImmediate(island)
            windowManager.removeView(island)
        } catch (e: Exception) {
            Log.e("OVERLAY_SERVICE", "Unable to remove view: ${e.localizedMessage}")
        }
        // Stop the service
        stopSelf()
    }

    override fun onDestroy() {
        closeFloatingWindow()
        notificationReceiver.unregister(applicationContext)
        super.onDestroy()
    }

    override fun onDownload(progress: Int, total: Int, image:Icon) {
        super.onDownload(progress, total, image)
        Log.d("OVERLAY_SERVICE", "onDownload: $progress $total")
        try {
            windowManager.removeView(island)
        } catch (e: Exception) {
            Log.e("OVERLAY_SERVICE", "Unable to remove view: ${e.localizedMessage}")
        }
        try {
            windowManager.addView(island, windowManagerParams)
        } catch (e: Exception) {
            Log.e("OVERLAY_SERVICE", "Unable to add view: ${e.localizedMessage}")
        }
        rotateView.visibility = View.GONE
        //rotateView.setImageIcon(image)
        animateFloatingWindow(island.windowToken!=null)
    }

    private fun animateFloatingWindow(isNewNotif:Boolean) {
        val targetWidth = dpToInt(120)

        val widthAnimator = ValueAnimator.ofInt(80, targetWidth)
        widthAnimator.duration = 400
        widthAnimator.interpolator = OvershootInterpolator()
        widthAnimator.addUpdateListener {
            windowManagerParams.width = Math.abs(it.animatedValue as Int)
            windowManager.updateViewLayout(island, windowManagerParams)
        }
        // Create the AnimatorSet to play the animations sequentially
        val animatorSet = AnimatorSet()

        if(isNewNotif){
            animatorSet.play(widthAnimator)
        }

        // Set an AnimatorListener to handle the closing of the floating window
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                //closeFloatingWindow()
                val toProgress = 0.7f
                val duration = 800L // milliseconds
                ringView.startAnimation()
                rotateView.startRotation()
                //ringView.animateProgress(toProgress, duration)
            }
        })

        // Start the animation
        animatorSet.start()
    }
}
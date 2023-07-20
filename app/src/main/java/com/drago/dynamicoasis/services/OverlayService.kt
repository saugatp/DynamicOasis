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
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.view.accessibility.AccessibilityEvent
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import androidx.core.content.getSystemService
import com.drago.dynamicoasis.R
import com.drago.dynamicoasis.receivers.MusicPlaybackReceiver
import com.drago.dynamicoasis.receivers.NotificationService
import com.drago.dynamicoasis.views.RingView
import com.drago.dynamicoasis.views.RotatingAlbumView
import com.drago.dynamicoasis.views.WaveformView
import java.lang.Error
import java.lang.Exception


class OverlayService: AccessibilityService(), MusicPlaybackReceiver.MusicPlaybackListener {
    lateinit var island:View
    lateinit var windowManager: WindowManager
    lateinit var windowManagerParams: LayoutParams
    lateinit var ringView:WaveformView
    lateinit var rotateView:RotatingAlbumView
    lateinit var musicPlaybackReceiver: MusicPlaybackReceiver
    lateinit var mediaSessionManager: MediaSessionManager
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager;
        island = LayoutInflater.from(this).inflate(R.layout.island_view, null)
        ringView = island.findViewById(R.id.ring_view)
        rotateView = island.findViewById(R.id.rotatingAlbum)
        mediaSessionManager = getSystemService(MEDIA_SESSION_SERVICE) as MediaSessionManager
        mediaSessionManager.addOnActiveSessionsChangedListener({
            for (mcontroller in it!!){
                Log.d("MEDIA_COMMUNICATION_SERVICE", "media: "+mcontroller.packageName)
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

        windowManagerParams.x= -360
        windowManagerParams.y = -1080
    }


    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
    }

    override fun onInterrupt() {

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onPlay(intent: Intent) {
        super.onPlay(intent)
        Log.d("OVERLAY_SERVICE", "onGotNotif: Notification received${intent.extras?.getString("package_name")}")
        try {
            windowManager.removeView(island)
        }    catch (e:Exception){
            Log.e("OVERLAY_SERVICE", "Unable to remove view: ${e.localizedMessage}", )
        }
        try {
            windowManager.addView(island, windowManagerParams)
        }catch (e:Exception){
            Log.e("OVERLAY_SERVICE", "Unable to add view: ${e.localizedMessage}", )
        }
        rotateView.setImageIcon(intent.extras?.get("icon_large") as Icon)
        animateFloatingWindow()
    }

    override fun onStop() {
        super.onStop()
    }
    override fun onServiceConnected() {
        super.onServiceConnected()
        musicPlaybackReceiver = MusicPlaybackReceiver(this)
        musicPlaybackReceiver.register(this)
        Log.d("B_CAST", "REGISTERERD");
    }

    fun dpToInt(v: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            v.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    private fun closeFloatingWindow(){
        try {

            windowManager.removeViewImmediate(island)
            windowManager.removeView(island)
        }catch (e:Exception){
            Log.e("OVERLAY_SERVICE", "Unable to remove view: ${e.localizedMessage}", )
        }
        // Stop the service
        stopSelf();
    }

    override fun onDestroy() {
        closeFloatingWindow()
        musicPlaybackReceiver.unregister(applicationContext)
        super.onDestroy()
    }
    private fun animateFloatingWindow() {
        // Calculate the target width for the floating window
        val targetWidth = dpToInt(120)

        // Create the ValueAnimator for expanding the width
        val widthAnimator = ValueAnimator.ofInt(80, targetWidth)
        widthAnimator.duration = 400
        widthAnimator.interpolator = OvershootInterpolator()
        widthAnimator.addUpdateListener {
            windowManagerParams.width = Math.abs(it.getAnimatedValue() as Int)
            windowManager.updateViewLayout(island, windowManagerParams)
        }
        // Create the AnimatorSet to play the animations sequentially
        val animatorSet = AnimatorSet()
        animatorSet.play(widthAnimator)

        // Set an AnimatorListener to handle the closing of the floating window
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
            }

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
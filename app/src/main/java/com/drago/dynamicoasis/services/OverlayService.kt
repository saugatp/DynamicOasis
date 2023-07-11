package com.drago.dynamicoasis.services

import android.accessibilityservice.AccessibilityService
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.PixelFormat
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.view.accessibility.AccessibilityEvent
import android.view.animation.OvershootInterpolator
import com.drago.dynamicoasis.R
import com.drago.dynamicoasis.views.RingView


class OverlayService: AccessibilityService() {
    lateinit var island:View
    lateinit var windowManager: WindowManager
    lateinit var windowManagerParams: LayoutParams
    lateinit var ringView:RingView

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager;
        island = LayoutInflater.from(this).inflate(R.layout.island_view, null)
        ringView = island.findViewById(R.id.ring_view)

        windowManagerParams = LayoutParams(
            dpToInt(80),
            dpToInt(40),
            LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or LayoutParams.FLAG_FULLSCREEN or LayoutParams.FLAG_LAYOUT_NO_LIMITS or LayoutParams.FLAG_LAYOUT_IN_SCREEN or LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        windowManagerParams.x=0
        windowManagerParams.y = 0
    }
    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
    }

    override fun onInterrupt() {
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        windowManager.addView(island, windowManagerParams)
        animateFloatingWindow()
    }

    fun dpToInt(v: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            v.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    private fun closeFloatingWindow(){
        if (island != null && windowManager != null) {
            windowManager.removeView(island)
        }
        // Stop the service
        stopSelf();
    }

    override fun onDestroy() {
        closeFloatingWindow()
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

                // Animate the progress from 0 to 1
                val toProgress = 0.7f
                val duration = 800L // milliseconds
                ringView.animateProgress(toProgress, duration)
            }
        })

        // Start the animation
        animatorSet.start()
    }
}
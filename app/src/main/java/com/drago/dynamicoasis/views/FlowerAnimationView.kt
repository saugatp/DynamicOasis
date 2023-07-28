package com.drago.dynamicoasis.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View


class FlowerAnimationView : View {
    private var petalPaint: Paint? = null
    private var centerPaint: Paint? = null
    private var stemPaint: Paint? = null
    private var rotationAngle = 0f
    private var startTime: Long = 0

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        petalPaint = Paint()
        petalPaint!!.color = Color.RED
        petalPaint!!.style = Paint.Style.FILL
        centerPaint = Paint()
        centerPaint!!.color = Color.YELLOW
        centerPaint!!.style = Paint.Style.FILL
        stemPaint = Paint()
        stemPaint!!.color = Color.GREEN
        stemPaint!!.style = Paint.Style.FILL
        startTime = System.currentTimeMillis()
        postInvalidateOnAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Calculate the center of the view
        val centerX = width / 2
        val centerY = height / 2

        // Draw flower stem
        canvas.drawRect(
            (centerX - FLOWER_STEM_WIDTH / 2).toFloat(),
            centerY.toFloat(),
            (centerX + FLOWER_STEM_WIDTH / 2).toFloat(),
            (centerY + FLOWER_STEM_LENGTH).toFloat(),
            stemPaint!!
        )

        // Draw flower petals
        val angleIncrement = 360f / NUM_PETALS
        for (i in 0 until NUM_PETALS) {
            val angle = i * angleIncrement + rotationAngle
            val x = (centerX + Math.cos(Math.toRadians(angle.toDouble())) * PETAL_RADIUS).toFloat()
            val y = (centerY + Math.sin(Math.toRadians(angle.toDouble())) * PETAL_RADIUS).toFloat()
            canvas.drawCircle(
                x, y, PETAL_RADIUS.toFloat(),
                petalPaint!!
            )
        }

        // Draw flower center
        canvas.drawCircle(
            centerX.toFloat(), centerY.toFloat(), FLOWER_CENTER_RADIUS.toFloat(),
            centerPaint!!
        )

        // Perform rotation animation
        val elapsedTime = System.currentTimeMillis() - startTime
        if (elapsedTime < ANIMATION_DURATION) {
            val fraction = elapsedTime.toFloat() / ANIMATION_DURATION
            rotationAngle = 360 * fraction
            postInvalidateOnAnimation()
        } else {
            // Reset rotation angle to start the animation again
            rotationAngle = 0f
            startTime = System.currentTimeMillis()
            postInvalidateOnAnimation()
        }
    }

    companion object {
        private const val NUM_PETALS = 4
        private const val PETAL_RADIUS = 100
        private const val FLOWER_CENTER_RADIUS = 20
        private const val FLOWER_STEM_LENGTH = 300
        private const val FLOWER_STEM_WIDTH = 20
        private const val FRAME_RATE = 120
        private const val ANIMATION_DURATION = 4000 // in milliseconds
    }
}

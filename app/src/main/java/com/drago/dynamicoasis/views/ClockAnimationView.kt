package com.drago.dynamicoasis.views
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class ClockAnimationView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val clockPaint: Paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 2f
        isAntiAlias = true
    }

    private val wavyCirclePaint: Paint = Paint().apply {
        color = Color.GRAY
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
    }

    private val hourHandPaint: Paint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 2f
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    private val minuteHandPaint: Paint = Paint().apply {
        color = Color.MAGENTA
        style = Paint.Style.STROKE
        strokeWidth = 8f
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    private val secondHandPaint: Paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 4f
        isAntiAlias = true
    }

    private val wavyCircleAmplitude: Float = 10f // Amplitude of the wavy outer circle

    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private var clockRadius: Float = 0f
    private var handLength: Float = 0f

    private var hourHandRotation: Float = 0f
    private var minuteHandRotation: Float = 0f
    private var secondHandRotation: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    init {
        // Start the ObjectAnimator to smoothly rotate the second hand
        val rotationAnimator = ObjectAnimator.ofFloat(this, "secondHandRotation", 0f, 360f)
        rotationAnimator.duration = 60000L // 60 seconds for one complete rotation
        rotationAnimator.repeatCount = ObjectAnimator.INFINITE
        rotationAnimator.repeatMode = ObjectAnimator.RESTART
        rotationAnimator.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        clockRadius = Math.min(w, h) / 2f - 10f
        handLength = clockRadius * 0.6f // You can adjust the length of the clock hands here
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw wavy outer circle
        val path = Path()
        val waveCount = 360 // Number of waves
        for (i in 0..waveCount) {
            val angle = i.toFloat() / waveCount * 360f
            val x = centerX + (clockRadius + wavyCircleAmplitude * sin(Math.toRadians(angle.toDouble()))).toFloat()
            val y = centerY
            if (i == 0) path.moveTo(x, y)
            else path.lineTo(x, y)
        }
        canvas.drawPath(path, wavyCirclePaint)

        // Draw clock face
        canvas.drawCircle(centerX, centerY, clockRadius, clockPaint)

        // Get the current time
        val calendar = Calendar.getInstance()
        val hours = calendar.get(Calendar.HOUR)
        val minutes = calendar.get(Calendar.MINUTE)
        val seconds = calendar.get(Calendar.SECOND)

        // Calculate angles for hour, minute, and second hands
        val hourAngle = (360f * (hours % 12) / 12f + 360f / 12f * (minutes / 60f))
        val minuteAngle = 360f * minutes / 60f
        val secondAngle = 360f * seconds / 60f

        // Convert angles to radians
        val hourRadians = Math.toRadians((hourAngle + 180).toDouble()).toFloat()
        val minuteRadians = Math.toRadians((minuteAngle + 180).toDouble()).toFloat()
        val secondRadians = Math.toRadians((secondAngle + 180).toDouble()).toFloat()

        // Calculate the endpoints of the clock hands
        val hourEndX = centerX + handLength * 0.5f * cos(hourRadians)
        val hourEndY = centerY + handLength * 0.5f * sin(hourRadians)
        val minuteEndX = centerX + handLength * 0.7f * cos(minuteRadians)
        val minuteEndY = centerY + handLength * 0.7f * sin(minuteRadians)
        val secondEndX = centerX + handLength * cos(secondRadians)
        val secondEndY = centerY + handLength * sin(secondRadians)

        // Draw the clock hands
        canvas.drawRoundRect(centerX - 10f, centerY - handLength * 0.5f, centerX + 10f, centerY, 10f, 10f, hourHandPaint)

        canvas.save() // Save the current canvas state

        // Rotate the canvas to draw the minute hand
        canvas.rotate(minuteAngle, centerX, centerY)
        canvas.drawLine(centerX, centerY, minuteEndX, minuteEndY, minuteHandPaint)

        canvas.restore() // Restore the canvas state to draw other elements

        canvas.drawLine(centerX, centerY, secondEndX, secondEndY, secondHandPaint)
    }
}

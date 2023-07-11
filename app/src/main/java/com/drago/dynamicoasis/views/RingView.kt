package com.drago.dynamicoasis.views
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Transformation

class RingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val RING_WIDTH_DP = 4
        private const val RING_COLOR = Color.GREEN
    }

    private val paint: Paint = Paint()
    private var progress: Float = 0f

    init {
        paint.color = RING_COLOR
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = dpToPx(RING_WIDTH_DP).toFloat()
        paint.strokeCap = Paint.Cap.ROUND
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val radius = (Math.min(width, height) * 0.5 - dpToPx(RING_WIDTH_DP) * 0.5).toFloat()
        val centerX = width * 0.5f
        val centerY = height * 0.5f

        val startAngle = -90f
        val sweepAngle = 360f * progress

        canvas.drawArc(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius,
            startAngle,
            sweepAngle,
            false,
            paint
        )
    }

    fun setProgress(progress: Float) {
        this.progress = progress
        invalidate()
    }

    fun animateProgress(toProgress: Float, duration: Long) {
        val animation = ProgressAnimation(toProgress)
        animation.interpolator = AccelerateDecelerateInterpolator()
        animation.duration = duration
        startAnimation(animation)
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density + 0.5f).toInt()
    }

    private inner class ProgressAnimation(private val toProgress: Float) : Animation() {
        private var fromProgress: Float = 0f

        override fun applyTransformation(interpolatedTime: Float, transformation: Transformation) {
            val newProgress = fromProgress + (toProgress - fromProgress) * interpolatedTime
            setProgress(newProgress)
        }

        override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
            super.initialize(width, height, parentWidth, parentHeight)
            fromProgress = progress
        }
    }
}

package com.drago.dynamicoasis.views
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnRepeat
import kotlin.random.Random

class WaveformView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var paint: Paint? = null
    var barHeights: FloatArray
    private var amplitude = 0f
    private var animator:ValueAnimator
    private var animValue:Float = 0F
    val rect = RectF()
    var ran = 3;


    init {
        paint = Paint()
        paint!!.color = BAR_COLOR
        paint!!.style = Paint.Style.FILL
        paint!!.isAntiAlias = true
        paint!!.strokeCap = Paint.Cap.ROUND

        barHeights = FloatArray(BAR_COUNT)
        amplitude = dpToPx(BAR_MAX_HEIGHT_DP)
        animator = ValueAnimator.ofFloat(0F, 1F)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width
        val height = height
        val barWidth = (width / BAR_COUNT)
        for (i in 0 until BAR_COUNT) {
            val centerX = (i + 0.5f) * barWidth
            val centerY = height / 2f

            val yOffset =Math.max( if (i%ran==0)animValue * amplitude else (1-animValue) * amplitude, amplitude*0.5f)

            val top = centerY - yOffset
            val bottom = centerY + yOffset
            rect.set((centerX - barWidth / 2f)+1f, top*1f, centerX + barWidth / 2f, bottom*1f)
            canvas.drawRoundRect(rect, 20f, 20f, paint!!)
        }

        invalidate()
    }

    fun startAnimation() {
        animator.apply {
            duration = 600
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = LinearInterpolator()
            addUpdateListener {
                animValue = (it.animatedValue as Float)
                if (animValue>=0.5f && animValue <=0.51f){
                    ran= Random.nextInt(2, 9)
                }
                invalidate()

            }

        }
        animator.start()
        invalidate()
    }

    fun stopAnimation() {
        // Reset the start time to stop the animation
       animator.pause();
    }

    private fun dpToPx(dp: Float): Float {
        val density = resources.displayMetrics.density
        return dp * density
    }

    companion object {
        private const val BAR_COUNT = 10
        private const val BAR_COLOR = Color.YELLOW
        private const val BAR_MAX_HEIGHT_DP = 4f
    }
}
package com.drago.dynamicoasis.views
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class RotatingAlbumView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var rotationAngle = 0f
    private var rotationSpeed = 0.1f
    private val clippingPath = Path()
    private val clippingPaint = Paint().apply {
        color = Color.TRANSPARENT
        style = Paint.Style.FILL
    }

    init {
        // Set the view to draw a circular clipping path
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    override fun onDraw(canvas: Canvas) {
        val drawable: Drawable? = drawable
        if (drawable != null) {
            // Get the center coordinates of the view
            val centerX = width / 2f
            val centerY = height / 2f

            // Reset the clipping path
            clippingPath.reset()
            clippingPath.addCircle(centerX, centerY, centerX, Path.Direction.CW)
            canvas.clipPath(clippingPath)

            // Reset the transformation matrix to identity
            canvas.matrix.reset()

            // Rotate the canvas around the center coordinates
            canvas.rotate(rotationAngle, centerX, centerY)

            // Draw the rotated drawable onto the canvas
            canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())
            drawable.draw(canvas)
        } else {
            super.onDraw(canvas)
        }
    }

    fun startRotation() {
        // Create a runnable that updates the rotation angle and invalidates the view
        val rotationRunnable = object : Runnable {
            override fun run() {
                rotationAngle += rotationSpeed
                if (rotationAngle >= 360f) {
                    rotationAngle -= 360f
                }
                invalidate()
                postDelayed(this, 16) // Delayed by 16 milliseconds for smooth animation
            }
        }

        // Start the rotation runnable
        post(rotationRunnable)
    }
}

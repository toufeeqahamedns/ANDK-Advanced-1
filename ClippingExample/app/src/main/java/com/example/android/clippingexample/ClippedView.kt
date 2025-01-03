package com.example.android.clippingexample

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View

class ClippedView(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    View(context, attrs, defStyle) {

    private val paint = Paint().apply {
        isAntiAlias = true
        strokeWidth = resources.getDimension(R.dimen.strokeWidth)
        textSize = resources.getDimension(R.dimen.textSize)
    }

    private val path = Path()

    private val clipRectRight = resources.getDimension(R.dimen.clipRectRight)
    private val clipRectBottom = resources.getDimension(R.dimen.clipRectBottom)
    private val clipRectTop = resources.getDimension(R.dimen.clipRectTop)
    private val clipRectLeft = resources.getDimension(R.dimen.clipRectLeft)

    private val rectInset = resources.getDimension(R.dimen.rectInset)
    private val smallRectOffset = resources.getDimension(R.dimen.smallRectOffset)

    private val circleRadius = resources.getDimension(R.dimen.circleRadius)

    private val textOffset = resources.getDimension(R.dimen.textOffset)
    private val textSize = resources.getDimension(R.dimen.textSize)

    private val columnOne = rectInset
    private val columnTwo = columnOne + rectInset + clipRectRight

    private val rowOne = rectInset
    private val rowTwo = rowOne + rectInset + clipRectBottom
    private val rowThree = rowTwo + rectInset + clipRectBottom
    private val rowFour = rowThree + rectInset + clipRectBottom
    private val textRow = rowFour + (1.5f * clipRectBottom)

    private var rectF = RectF(
        rectInset,
        rectInset,
        clipRectRight - rectInset,
        clipRectBottom - rectInset
    )

    private val rejectRow = rowFour + rectInset + 2*clipRectBottom

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawBackAndUnclippedRectanagle(canvas)
        drawDifferenceClippingExample(canvas)
        drawCircularClippingExample(canvas)
        drawIntersectionClippingExample(canvas)
        drawCombinedClippingExample(canvas)
        drawRoundedRectangleClippingExample(canvas)
        drawOutsideClippingExample(canvas)
        drawSkewedTextExample(canvas)
        drawTranslatedTextExample(canvas)
        drawQuickRejectExample(canvas)
    }

    private fun drawClippedRectangle(canvas: Canvas?) {

        canvas?.clipRect(clipRectLeft, clipRectTop, clipRectRight, clipRectBottom)
        canvas?.drawColor(Color.WHITE)

        paint.color = Color.RED
        canvas?.drawLine(clipRectLeft, clipRectTop, clipRectBottom, clipRectRight, paint)

        paint.color = Color.GREEN
        canvas?.drawCircle(circleRadius, clipRectBottom - circleRadius, circleRadius, paint)

        paint.color = Color.BLUE
        paint.textSize = textSize
        paint.textAlign = Paint.Align.RIGHT
        canvas?.drawText(context.getString(R.string.clipping), clipRectRight, textOffset, paint)
    }

    private fun drawRoundedRectangleClippingExample(canvas: Canvas?) {
        canvas?.apply {
            save()
            translate(columnTwo, rowThree)
            path.rewind()
            path.addRoundRect(rectF, clipRectRight / 4, clipRectRight / 4, Path.Direction.CCW)
            clipPath(path)
            drawClippedRectangle(this)
            restore()
        }
    }

    private fun drawSkewedTextExample(canvas: Canvas?) {
        canvas?.apply {
            save()
            paint.color = Color.YELLOW
            paint.textAlign = Paint.Align.RIGHT
            translate(columnTwo, textRow)
            skew(0.2f, 0.3f)
            drawText(context.getString(R.string.skewed), clipRectLeft, clipRectTop, paint)
            restore()
        }
    }

    private fun drawIntersectionClippingExample(canvas: Canvas?) {
        canvas?.apply {
            save()
            translate(columnTwo, rowTwo)
            clipRect(
                clipRectLeft,
                clipRectTop,
                clipRectRight - smallRectOffset,
                clipRectBottom - smallRectOffset
            )

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                clipRect(
                    clipRectLeft + smallRectOffset,
                    clipRectTop + smallRectOffset,
                    clipRectRight, clipRectBottom,
                    Region.Op.INTERSECT
                )
            } else {
                clipRect(
                    clipRectLeft + smallRectOffset,
                    clipRectTop + smallRectOffset,
                    clipRectRight, clipRectBottom
                )
            }

            drawClippedRectangle(this)
            restore()
        }
    }

    private fun drawCircularClippingExample(canvas: Canvas?) {
        canvas?.apply {
            save()
            translate(columnOne, rowTwo)
            path.rewind()
            path.addCircle(
                circleRadius, clipRectBottom - circleRadius,
                circleRadius, Path.Direction.CCW
            )
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                clipPath(path, Region.Op.DIFFERENCE)
            } else {
                clipOutPath(path)
            }
            drawClippedRectangle(this)
            restore()
        }
    }

    private fun drawTranslatedTextExample(canvas: Canvas?) {
        canvas?.apply {
            save()
            paint.color = Color.GREEN
            paint.textAlign = Paint.Align.LEFT
            translate(columnTwo, textRow)
            drawText(
                context.getString(R.string.translated),
                clipRectLeft,
                clipRectTop,
                paint
            )
            restore()
        }
    }

    private fun drawOutsideClippingExample(canvas: Canvas?) {
        canvas?.apply {
            save()
            translate(columnOne, rowFour)
            clipRect(
                2 * rectInset,
                2 * rectInset,
                clipRectRight - 2 * rectInset,
                clipRectBottom - 2 * rectInset
            )
            drawClippedRectangle(this)
            restore()
        }
    }

    private fun drawCombinedClippingExample(canvas: Canvas?) {
        canvas?.apply {
            save()
            translate(columnOne, rowThree)
            path.rewind()
            path.addCircle(
                clipRectLeft + rectInset + circleRadius,
                clipRectTop + circleRadius + rectInset,
                circleRadius, Path.Direction.CCW
            )
            path.addRect(
                clipRectRight / 2 - circleRadius,
                clipRectTop + circleRadius + rectInset,
                clipRectRight / 2 + circleRadius,
                clipRectBottom - rectInset, Path.Direction.CCW
            )
            clipPath(path)
            drawClippedRectangle(this)
            restore()
        }
    }

    private fun drawDifferenceClippingExample(canvas: Canvas?) {
        canvas?.apply {
            save()
            translate(columnTwo, rowOne)
            clipRect(
                2 * rectInset, 2 * rectInset,
                clipRectRight - 2 * rectInset,
                clipRectBottom - 2 * rectInset
            )

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                clipRect(
                    4 * rectInset, 4 * rectInset,
                    clipRectRight - 4 * rectInset,
                    clipRectBottom - 4 * rectInset,
                    Region.Op.DIFFERENCE
                )
            } else {
                clipOutRect(
                    4 * rectInset, 4 * rectInset,
                    clipRectRight - 4 * rectInset,
                    clipRectBottom - 4 * rectInset
                )
            }

            drawClippedRectangle(this)
            restore()
        }
    }

    private fun drawBackAndUnclippedRectanagle(canvas: Canvas?) {
        canvas?.apply {
            drawColor(Color.GRAY)
            save()
            translate(columnOne, rowOne)
            drawClippedRectangle(this)
            restore()
        }
    }

    private fun drawQuickRejectExample(canvas: Canvas?) {
        val inClipRectangle = RectF(clipRectRight / 2,
            clipRectBottom / 2,
            clipRectRight * 2,
            clipRectBottom * 2)

        val notInClipRectangle = RectF(RectF(clipRectRight+1,
            clipRectBottom+1,
            clipRectRight * 2,
            clipRectBottom * 2))

        canvas?.save()
        canvas?.translate(columnOne, rejectRow)
        canvas?.clipRect(
            clipRectLeft,clipRectTop,
            clipRectRight,clipRectBottom
        )
        if (canvas?.quickReject(
                inClipRectangle, Canvas.EdgeType.AA) == true
        ) {
            canvas.drawColor(Color.WHITE)
        }
        else {
            canvas?.drawColor(Color.BLACK)
            canvas?.drawRect(inClipRectangle, paint
            )
        }
        canvas?.restore()
    }
}
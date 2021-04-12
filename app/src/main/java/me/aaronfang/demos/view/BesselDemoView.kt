package me.aaronfang.demos.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import me.aaronfang.demos.utils.DisplayUtil

class BesselDemoView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : View(context, attrs, defStyle) {
    private val backPaint = Paint()
    private val grayPaint = Paint()
    private var centerX: Float = 0.0f
    private var centerY: Float = 0.0f
    private var moveX: Float = 160f
    private var moveY: Float = 160f
    lateinit var controlRect: Rect

    init {
        backPaint.style = Paint.Style.FILL
        backPaint.color = Color.GRAY
        backPaint.strokeWidth = 10f
        backPaint.isAntiAlias = true


        grayPaint.style= Paint.Style.STROKE
        grayPaint.color= Color.argb(180,230, 230,230)
        grayPaint.strokeWidth=2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        centerX = (measuredWidth / 2).toFloat()
        centerY = (measuredHeight / 2).toFloat()
        // 调整坐标系
        adjustCanvas(canvas = canvas)
        // 绘制网格
        drawGrids(canvas)
        // 绘制标度
        drawGridsText(canvas)
        // 绘制X Y轴
        drawXYAxis(canvas)

        //看看现在的原点
//        canvas.drawCircle(0f, 0f, 100f, backPaint)
        // 到这里原点是左下角
        // 调整到到中间，方向不用改变
        canvas.translate(centerX, centerY)
//        canvas.drawCircle(0f, 0f, 100f, backPaint)
        canvas.save()

        drawQuad(canvas)
    }

    /**
     * 二阶贝塞尔曲线
     */
    private fun drawQuad(canvas: Canvas) {
        controlRect = Rect(
            (moveX - 30f).toInt(),
            (moveY + 30f).toInt(),
            (moveX + 30).toInt(),
            (moveY - 30f).toInt()
        )
        val quePath = Path()
        // 控制点A B
        canvas.drawCircle(0f, 0f, 10f, grayPaint)
        canvas.drawCircle(320f, 0f, 10f, grayPaint)
        //第一个点和控制点的连线到最后一个点链线。为了方便观察
        val lineLeft = Path()
        lineLeft.moveTo(0f, 0f)
        lineLeft.lineTo(moveX, moveY)
        lineLeft.lineTo(320f, 0f)
        canvas.drawPath(lineLeft, backPaint)
        //第一个p0处画一个圆。第二个p1处画一个控制点圆,最后画一个。
        canvas.drawCircle(moveX, moveY, 10f, grayPaint)
        quePath.quadTo(moveX, moveY, 320f, 0f)
        canvas.drawPath(quePath, grayPaint)
    }

    private fun drawGridsText(canvas: Canvas) {
        val xCount = (measuredWidth / (4 * gridWh)).toInt()
        val yCount = (measuredHeight / (4 * gridWh)).toInt()
        val xWidth = measuredWidth / xCount
        val yWidth = measuredHeight / yCount

        val textPaint = Paint()
        textPaint.apply {
            color = Color.BLACK
            textSize = 24f
            strokeWidth = 10f
        }

        // 先绘制原点
        canvas.save()
        canvas.translate((measuredWidth / 2).toFloat(), (measuredHeight / 2).toFloat() )
        canvas.scale(1f, -1f)
        canvas.drawText("0", 0, 1, -getTextWidth(textPaint, "0") / 2, 0f, textPaint)
        canvas.restore()


        // 对称来画
        for (index in 0 until xCount / 2) {
            // 正方向
            canvas.save()
            val textPositive = ((index + 1) * 10).toString()
            val textNegative = ((index + 1) * -10).toString()
            canvas.translate(
                (centerX + xWidth * (index + 1)),
                centerY,
            )
            canvas.scale(1f, -1f)
            canvas.drawText(textPositive, 0,textPositive.length,
                -getTextWidth(textPaint, textPositive) / 2, 0f, textPaint)
            canvas.restore()
            // 负方向
            canvas.save()
            canvas.translate(
                (centerX - xWidth * (index + 1).toFloat()),
                centerY
            )
            canvas.scale(1f, -1f)
            canvas.drawText(textNegative, 0, textNegative.length,
                -getTextWidth(textPaint, textNegative) / 2, 0f, textPaint)
            canvas.restore()

        }

        // 对称来画
        for (index in 0 until yCount / 2) {
            // 正方向
            canvas.save()
            val textPositive = ((index + 1) * 10).toString()
            val textNegative = ((index + 1) * -10).toString()
            canvas.translate(
                centerX,
                (centerY + yWidth * (index + 1))
            )
            canvas.scale(1f, -1f)
            canvas.drawText(textPositive, 0,textPositive.length,
                -getTextWidth(textPaint, textPositive) / 2, 0f, textPaint)
            canvas.restore()
            // 负方向
            canvas.save()
            canvas.translate(
                centerX,
                (centerY - yWidth * (index + 1))
            )
            canvas.scale(1f, -1f)
            canvas.drawText(textNegative, 0, textNegative.length,
                -getTextWidth(textPaint, textNegative) / 2, 0f, textPaint)
            canvas.restore()

        }
    }

    private val gridWh = DisplayUtil.px2dp(context, 60f)

    private fun drawGrids(canvas: Canvas) {

        // 平行于y轴的线段
        val pathY = Path()
        pathY.moveTo(DisplayUtil.px2dp(context, 40f ), 0f)
        pathY.lineTo(DisplayUtil.px2dp(context, 40f ), measuredWidth.toFloat())
        canvas.drawPath(pathY, grayPaint)

        // 平行于x轴的线段
        val pathX = Path()
        pathX.moveTo(0f, DisplayUtil.px2dp(context = context, 40f ))
        pathX.lineTo(measuredWidth.toFloat(), DisplayUtil.px2dp(context, 40f ))
        canvas.drawPath(pathX, grayPaint)

        // x轴个数
        val xCount = measuredWidth / gridWh
        val yCount = measuredHeight / gridWh

        canvas.save()
        for (index in 0..yCount.toInt()) {
            canvas.translate(0f, gridWh)
            canvas.drawPath(pathX, grayPaint)
        }
        canvas.restore()
        for (index in 0.. xCount.toInt()) {
            canvas.translate(gridWh, 0f)
            canvas.drawPath(pathY, grayPaint)
        }
        canvas.restore()
    }

    private fun drawXYAxis(canvas: Canvas) {
        val axisPainter = Paint()
        axisPainter.style = Paint.Style.STROKE
        axisPainter.color = Color.rgb(160,160,202)
        axisPainter.strokeWidth = 5f

        val xPath = Path()
        xPath.apply {
            moveTo( (measuredWidth / 2).toFloat(), 0f)
            lineTo((measuredWidth / 2).toFloat() , measuredHeight.toFloat())
        }

        val yPath = Path()
        yPath.apply {
            moveTo(0f, (measuredHeight / 2).toFloat() )
            lineTo(measuredWidth.toFloat(), (measuredHeight / 2).toFloat())
        }

        canvas.drawPath(xPath, axisPainter)
        canvas.drawPath(yPath, axisPainter)
    }

    private fun adjustCanvas(canvas: Canvas) {
        canvas.scale(1f, -1f)
        canvas.translate(0f, -(measuredHeight.toFloat()))
        canvas.save()
    }

    private fun getTextWidth(textBackgroundPaint: Paint, textStr: String): Float {
        return textBackgroundPaint.measureText(textStr)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                //在控制点附近范围内部,进行移动
                Log.e("x=", "onTouchEvent: (x,y)"+(event.x - width / 2).toInt()+":"+(-(event.y - height / 2)).toInt())
                //将手势坐标转换为屏幕坐标
                moveX = event.x - width / 2
                moveY = -(event.y - height / 2)
                invalidate()
            }
        }

        return true
    }
}
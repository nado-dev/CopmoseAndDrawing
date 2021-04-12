package me.aaronfang.demos.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import me.aaronfang.demos.utils.DisplayUtil
import me.aaronfang.demos.view.entity.ViewPoint
import java.util.*
import kotlin.collections.ArrayList

class LineChartDemoView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0): View(context, attrs, defStyle) {
    private val backPaint = Paint()
    private val grayPaint = Paint()
    private val rectList = ArrayList<Rect>()
    private val list = ArrayList<ViewPoint>()
    // 缩放
    private lateinit var mScaleGestureDetector: ScaleGestureDetector

    init {
        backPaint.style = Paint.Style.FILL
        backPaint.color = Color.WHITE
        backPaint.strokeWidth = 10f
        backPaint.isAntiAlias = true


        grayPaint.style= Paint.Style.STROKE
        grayPaint.color=Color.WHITE
        grayPaint.strokeWidth=2f

        // 初始化数据
        initViewPoints(list)
        initScaleGestureDetector()
    }

    // 缩放因子
    private var curScale = 1.0f
    // 之前一次的缩放因子
    private var preScale = 1.0f

    private fun initScaleGestureDetector() {
        mScaleGestureDetector = ScaleGestureDetector(
            context,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener(){
                override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
                    return true
                }

                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    curScale = detector.scaleFactor * preScale
                    // 当放大倍数大于2或者缩小倍数小于0.1倍时，忽略
                    if (curScale > 2f || curScale < 0.1) {
                        preScale = curScale
                        return true
                    }
                    preScale = curScale
                    invalidate()
                    return false
                }

                override fun onScaleEnd(detector: ScaleGestureDetector?) {
                }
            }
        )
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        maxInit = measuredWidth.toFloat()
        // 调整坐标系
        adjustCanvas(canvas = canvas)

        // 绘制格子
        drawPivot(canvas)

        // 绘制折线图
        drawLines(canvas, list)

        // 根据visible是否显示弹框
        if(isWindowRectVisible) {
            drawWindowRect(canvas)
        }

        // demo
//        drawWhiteCircleSlide(canvas = canvas)
    }

    private fun drawWindowRect(canvas: Canvas) {
        if(mRect != null) {
            val rrPaint = Paint()
            rrPaint.color = Color.BLACK
            rrPaint.style = Paint.Style.FILL
            rrPaint.strokeWidth = 1f
            rrPaint.setShadowLayer(5f, -5f, -5f,
                Color.argb(50,111,111,111))

            canvas.drawRoundRect(
                mRect!!.left.toFloat(),
                mRect!!.top.toFloat(),
                mRect!!.right.toFloat(),
                mRect!!.bottom.toFloat(),
                10f, 10f,
                rrPaint
            )

            canvas.save()
            canvas.translate(mRect!!.left.toFloat(), mRect!!.top.toFloat())
            canvas.scale(1f, -1f)

            val ttPaint = Paint()
            ttPaint.apply {
                color = Color.WHITE
                style = Paint.Style.FILL
                strokeWidth = 1f
                strokeCap = Paint.Cap.ROUND
                textSize = 24f
            }

            canvas.drawText("M:${msg}", 20f, 30f, ttPaint)

        }
    }

    private fun initViewPoints(list : ArrayList<ViewPoint>) {
        // 制作折线点
        for (index in 0 until 10) {
            list.add(ViewPoint(DisplayUtil.px2dp(context, index * 160f),
                DisplayUtil.px2dp(context = context, index * 40f)))
        }

        for (index in 0 until 10) {
            list.add(ViewPoint(DisplayUtil.px2dp(context,  1600f + index * 120f),
                DisplayUtil.px2dp(context = context, 400f +index * 60f)))
        }

        for (index in 0 until 10) {
            list.add(ViewPoint(DisplayUtil.px2dp(context,  2800f + index * 120f),
                DisplayUtil.px2dp(context = context, 1000f  - index * 60f)))
        }
    }


    /**
     * 折线图
     */
    private fun drawLines(canvas: Canvas, pointList: ArrayList<ViewPoint>) {
        val linePaint = Paint()
        val circlePaint = Paint()
        val path = Path()

        linePaint.style = Paint.Style.STROKE
        linePaint.color = Color.argb(255, 34, 192, 255)
        linePaint.strokeWidth = 5f

        circlePaint.strokeWidth = 10f
        circlePaint.style = Paint.Style.FILL
        circlePaint.color = Color.WHITE

        // 连线
        for (index in 0 until pointList.size) {
            val xActual = (pointList[index].x + deltaX) * curScale
            val yActual = (pointList[index].y + deltaY) * curScale

            path.lineTo(xActual, yActual)
        }
        canvas.drawPath(path, linePaint)


        // 渐变色的填充 使区域闭合
        val lastIndex = pointList.size - 1
        path.lineTo((pointList[lastIndex].x + deltaX) * curScale, 0f)
        path.close()
        linePaint.style = Paint.Style.FILL
        linePaint.shader = getShader()
        canvas.drawPath(path, linePaint)


        // 定点画圆圈
        for (index in 0 until pointList.size) {
            val xActual = (pointList[index].x + deltaX) * curScale
            val yActual = (pointList[index].y + deltaY) * curScale
            canvas.drawCircle(xActual, yActual, 8f, circlePaint)
        }


        // 文字表示
        drawDesc(canvas, pointList)

    }

    /**
     * 标点文字描述
     */
    private fun drawDesc(canvas: Canvas, pointList: ArrayList<ViewPoint>) {
        // 画笔
        val textPaint = Paint()
        textPaint.color = Color.RED
        textPaint.textSize = 40f * curScale
        textPaint.strokeWidth = 10f * curScale

        for (index in 0 until pointList.size) {
            val xActual = (pointList[index].x + deltaX) * curScale
            val yActual = (pointList[index].y + deltaY) * curScale
            val textBackgroundPaint = 180.getTextBackgroundPaint(180, 180)
            val textHeight1 = getTextHeight(textBackgroundPaint)

            canvas.save()
            canvas.translate(xActual,  yActual + textHeight1)
            canvas.scale(1f, -1f)
            canvas.rotate((10).toFloat())

            val textWidth = getTextWidth(textPaint,
                index.toString())
            val textHeight = getTextHeight(textPaint)
            val backGroundHeight = getTextHeight(textBackgroundPaint)

            // 绘制背景
            canvas.drawRoundRect(
                0f,
                -textHeight,
                textWidth,
                backGroundHeight /2 ,
                10f, 10f, textBackgroundPaint)

            // 加入背景集合
            rectList.add(
                Rect(
                    xActual.toInt(),
                    yActual.toInt(),
                    (xActual + textWidth).toInt() ,
                    (yActual + textHeight).toInt()
                )
            )

            // 绘制文本
            canvas.drawText(
                index.toString(),
                0,
                index.toString().length,
            0f, 0f, textPaint
            )
            canvas.restore()
        }

    }

    private fun getTextWidth(textBackgroundPaint: Paint, textStr: String): Float {
        return textBackgroundPaint.measureText(textStr) * curScale
    }


    private fun getTextHeight(paint: Paint) :Float {
        val fontMetrics: Paint.FontMetrics = paint.fontMetrics
//        val h1 = fontMetrics.descent - fontMetrics.ascent + fontMetrics.leading
        val h2 = fontMetrics.bottom - fontMetrics.top + fontMetrics.leading
        return h2 * curScale
    }

    private fun Int.getTextBackgroundPaint(centerAlpha: Int, endAlpha: Int): Paint {
        val paint = Paint()
        paint.textSize = 22f
        val random = Random()
        val R = random.nextInt(225)
        val G = random.nextInt(224)
        val B = random.nextInt(223)

        val R1 = random.nextInt(222)
        val G1 = random.nextInt(221)
        val B1 = random.nextInt(220)

        val R2 = random.nextInt(219)
        val G2 = random.nextInt(218)
        val B2 = random.nextInt(217)
        val shadeColors = intArrayOf(Color.argb(this, R, G, B), Color.argb(centerAlpha, R1, G1, B1), Color.argb(endAlpha, R2, G2, B2))
        val mShader = LinearGradient(0f, 0f, 44f, 44f, shadeColors, null, Shader.TileMode.CLAMP)
        paint.shader = mShader
        return paint
    }



    /**
     * 配置Shader
     */
    private fun getShader(): Shader {
        val shaderColors = intArrayOf(
            Color.argb(255,250,49,33),
            Color.argb(165, 234, 115, 9),
            Color.argb(200, 32, 208, 88))
        return LinearGradient(
            (measuredWidth / 2).toFloat(),
            (measuredHeight / 2).toFloat(),
            (measuredWidth / 2).toFloat(),
            0f, shaderColors, null, Shader.TileMode.CLAMP)
    }

    private fun drawPivot(canvas: Canvas) {
        val gridWh =DisplayUtil.px2dp(context, 60f * curScale)
        // 平行于y轴的线段
        val pathY = Path()
        pathY.moveTo(DisplayUtil.px2dp(context, 40f * curScale), 0f)
        pathY.lineTo(DisplayUtil.px2dp(context, 40f * curScale), measuredWidth.toFloat())
        canvas.drawPath(pathY, grayPaint)

        // 平行于x轴的线段
        val pathX = Path()
        pathX.moveTo(0f, DisplayUtil.px2dp(context = context, 40f * curScale))
        pathX.lineTo(measuredWidth.toFloat(), DisplayUtil.px2dp(context, 40f * curScale))
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

    /**
     * 绘制一个白色实心圆
     */
    private var deltaX = 0f
    private var deltaY = 0f
    private var maxInit = 0f

    private fun drawWhiteCircleSlide(canvas: Canvas) {
        canvas.drawCircle( 60f * curScale, measuredHeight / 2f, 40f * curScale, backPaint)
    }

    private fun adjustCanvas(canvas: Canvas) {
        canvas.scale(1f, -1f)
        canvas.translate(0f, -(measuredHeight.toFloat() * curScale))
        canvas.save()
    }


    private var isClick: Boolean = false
    private var msg = ""
    private var mRect: Rect? = null
    private var isWindowRectVisible: Boolean = false
    private var startX = 0f
    private var startY = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
//        mScaleGestureDetector.onTouchEvent(event)
        if (event.action == MotionEvent.ACTION_DOWN)  {
            // 拦截ACTION_DOWN
            isClick = true
            startX = event.x
            return true
        }
        if (event.action == MotionEvent.ACTION_UP && isClick) {
            for (index in 0 until rectList.size) {
                val isContains = rectList[index].contains(event.x.toInt(), (measuredHeight - event.y).toInt())
                if (isContains) {
//                    Toast.makeText(context, "this is $index", Toast.LENGTH_SHORT).show()
                    msg = index.toString()
                    val x = event.x
                    val y = event.y
                    mRect = Rect(
                        (x - 70).toInt(),
                        y.toInt(),
                        (x + 70).toInt(),
                        (y - 200).toInt()
                    )
                    isWindowRectVisible = true
                    invalidate()

                    postDelayed({
                        isWindowRectVisible = false
                        invalidate()
                    }, 2000)
                    break
                }
            }
            isClick = false
            rectList.clear()
        }

        if (event.action == MotionEvent.ACTION_MOVE) {
            val disX = event.x - startX
            val disY = event.y - startY
            startX = event.x
            startY = event.y
            deltaX += disX
//            deltaY += disY
            invalidate()
            return true
        }
        return super.onTouchEvent(event)
    }
}
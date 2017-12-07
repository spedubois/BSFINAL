package com.example.spenserdubois.battleship

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

@Suppress("UNREACHABLE_CODE")
/**
 * Created by Spenser DuBois on 11/2/2017.
 */
class boardView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    private var gameWidth = 0
    private var gameHeight = 0
    private var spacing = 0
    private var path = MyPath()
    private var missPath = MyPath()
    private var hitPath = MyPath()
    private var missPaint = Paint()
    private var hitPaint = Paint()
    private var sunkPath = MyPath()
    private var sunkPaint = Paint()
    var canClick : Boolean = true

    interface OnNewShotListener{
        fun OnNewshot(boardView: boardView, x : Int, y : Int)
    }

    fun getHitPath() : MyPath
    {
        return hitPath
    }

    fun getMissPath() : MyPath
    {
        return missPath
    }

    fun setHitPath(path:MyPath)
    {
        hitPath = path
    }

    fun setMissPath(path:MyPath)
    {
        missPath = path
    }

    fun getSunkPath() : MyPath
    {
        return sunkPath
    }

    fun setSunkPath(path:MyPath)
    {
        sunkPath = path
    }



    private var onNewShotListener: OnNewShotListener? = null

    fun setOnNewShotListener(onNewShotListener: OnNewShotListener){
        this.onNewShotListener = onNewShotListener
    }

    fun setOnNewShotListener(onNewShotListener:((boardView: boardView, x : Int, y : Int)->Unit)){
        this.onNewShotListener = object : OnNewShotListener{
            override fun OnNewshot(boardView: boardView, x : Int, y : Int) {
                onNewShotListener(boardView, x, y)
            }
        }
    }

    fun removeOnNewShotListener()
    {
        onNewShotListener = null
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        missPaint.style = Paint.Style.FILL
        missPaint.color = Color.WHITE
        hitPaint.style = Paint.Style.FILL
        hitPaint.color = Color.RED
        sunkPaint.color = Color.GREEN
        sunkPaint.style = Paint.Style.FILL

        if(canvas !is Canvas)
        {
            return
        }
        var paint = Paint()
        paint.strokeWidth = 4f
        paint.style = Paint.Style.STROKE
        paint.color = Color.BLACK

        var miss = Path()
        missPath.createPath(miss)
        var hit = Path()
        hitPath.createPath(hit)
        var sunk = Path()
        sunkPath.createPath(sunk)

        canvas.drawPath(miss, missPaint)
        canvas.drawPath(hit, hitPaint)
        canvas.drawPath(sunk, sunkPaint)
        canvas.drawPath(path,paint)
    }

    fun genFrame(w : Int, h : Int)
    {
        gameHeight = h
        gameWidth = w
        spacing = w /10

        path.moveTo(0f,0f)
        path.lineTo(w + 0f, 0f)
        path.moveTo(0f,0f)
        path.lineTo(0f, h + 0f)
        path.moveTo(0f,h + 0f)
        path.lineTo(w + 0f, h + 0f)
        path.moveTo(w + 0f, 0f)
        path.lineTo(w + 0f, h + 0f)

        genGrid(spacing, w, h)
        invalidate()
    }

    fun genGrid(s : Int, w : Int, h : Int)
    {
        for(i in 1 until 10)
        {
            path.moveTo(s*i + 0f, 0f)
            path.lineTo(s*i + 0f, w + 0f)
        }
        for(i in 1 until 10)
        {
            path.moveTo(0f, s*i + 0f)
            path.lineTo(w + 0f, s*i + 0f)
        }
    }

    fun addHit(x : Float, y : Float)
    {
        hitPath.addRect(spacing*x, spacing*y, spacing*(x+1), spacing*(y+1), Path.Direction.CCW)
    }
    fun addMiss(x : Float, y : Float)
    {
        missPath.addRect(spacing*x, spacing*y, spacing*(x+1), spacing*(y+1), Path.Direction.CCW)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if(event !is MotionEvent || !canClick)
            return false

        var x = ((event.x / spacing).toInt()) + 0f
        var y = ((event.y / spacing).toInt()) + 0f

        if(x >= 10)
            x = 9f
        if(y >= 10)
            y = 9f
        onNewShotListener?.OnNewshot(this, x.toInt(), y.toInt())
        invalidate()
        return super.onTouchEvent(event)
    }

    fun addSunkPath(coords : ArrayList<Coord>)
    {
        for (c in coords)
        {
            sunkPath.addRect(spacing * (c.x+0f), spacing*(c.y+0f), spacing*(c.x + 1f), spacing*(c.y + 1f), Path.Direction.CCW )
            invalidate()
        }

    }
}
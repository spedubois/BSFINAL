package com.example.spenserdubois.battleship

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/**
 * Created by Spenser DuBois on 11/3/2017. Represents a smaller view from the game board.
 * This View shows the user where the opponent has fired and also  where the users ships are
 */
class MiniView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    private var gameWidth = 0
    private var gameHeight = 0
    var spacing = 0
    private var path = Path()
    private var missPath = MyPath()
    private var hitPath = MyPath()
    private var missPaint = Paint()
    private var hitPaint = Paint()
    var boatPath = MyPath()
    var boatPaint = Paint()
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

    fun setHitPath(path: MyPath)
    {
        hitPath = path
    }

    fun setMissPath(path: MyPath)
    {
        missPath = path
    }

    fun addHit(x : Float, y : Float)
    {
        hitPath.addRect(spacing*x, spacing*y, spacing*(x+1), spacing*(y+1), Path.Direction.CCW)
    }
    fun addMiss(x : Float, y : Float)
    {
        missPath.addRect(spacing*x, spacing*y, spacing*(x+1), spacing*(y+1), Path.Direction.CCW)
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
        boatPaint.style = Paint.Style.FILL
        boatPaint.color = Color.DKGRAY

        if(canvas !is Canvas)
        {
            return
        }
        var paint = Paint()
        var miss = Path()
        missPath.createPath(miss)
        var hit = Path()
        hitPath.createPath(hit)
        var boats = Path()
        boatPath.createPath(boats)

        paint.strokeWidth = 4f
        paint.style = Paint.Style.STROKE
        paint.color = Color.BLACK



        canvas.drawPath(boats, boatPaint)
        canvas.drawPath(miss, missPaint)
        canvas.drawPath(hit, hitPaint)

        canvas.drawPath(path,paint)
    }

    /**
     * Same as with big game view, this functions creates a border around the mini view
     */
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

    /**
     * Same as with big game view, this functions creates the grid over the game view.
     */
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

    /**
     * Draws the boats to the game View
     */
    fun drawBoats(boats : ArrayList<Boat>)
    {
        for(b in boats)
        {
            var a = b.coords
            for(c in a)
                boatPath.addRect(spacing * (c.x+0f), spacing*(c.y+0f), spacing*(c.x + 1f), spacing*(c.y + 1f), Path.Direction.CCW )
        }
        invalidate()
    }


}
package com.example.spenserdubois.battleship

import android.graphics.Path
import java.io.Serializable

/**
 * Created by Spenser DuBois on 11/4/2017.
 */
class MyPath : Path, Serializable {

    var rects : ArrayList<MyRect> = ArrayList<MyRect>()
    constructor() : super()
    constructor(src: Path?) : super(src)

    override fun addRect(left: Float, top: Float, right: Float, bottom: Float, dir: Direction?) {
        var newRect = MyRect(left, top, right, bottom)
        rects.add(newRect)
        super.addRect(left, top, right, bottom, dir)
    }

    fun createPath(path: Path)
    {
        for(r in rects)
        {
            path.addRect(r.left, r.top, r.right, r.bottom, Path.Direction.CCW)
        }
    }

}

public class MyRect : Serializable
{
    var left : Float
    var top : Float
    var right : Float
    var bottom : Float

    constructor(l : Float, t: Float, r: Float, b: Float)
    {
        left = l
        right = r
        top = t
        bottom = b
    }
}
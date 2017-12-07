package com.example.spenserdubois.battleship

import java.io.Serializable

/**
 * Created by Spenser DuBois on 11/3/2017.
 */
class Coord : Serializable{
    val x : Int
    val y : Int
    constructor(_x : Int, _y : Int)
    {
        x = _x
        y = _y
    }
}
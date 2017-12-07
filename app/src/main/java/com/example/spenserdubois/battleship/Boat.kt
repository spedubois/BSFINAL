package com.example.spenserdubois.battleship

import java.io.Serializable

/**
 * Created by Spenser DuBois on 11/3/2017.
 */
class Boat : Serializable{
    var life : Int
    val id : Int
    val coords : ArrayList<Coord>

    constructor(l : Int, type : Int)
    {
        life = l
        id = type
        coords = ArrayList<Coord>()
    }
}
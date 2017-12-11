package com.example.spenserdubois.battleship

/**
 * Created by Spenser DuBois on 12/5/2017. Used to store information tot he firebase database.
 */
class DatabaseElement {
    var turn : String = ""
    var testList : MutableList<String>
    var boatPos1: MutableList<String>
    var boatPos2: MutableList<String>
    var P1miniHits: MutableList<String>
    var P1miniMiss: MutableList<String>
    var P2miniHits: MutableList<String>
    var P2miniMiss: MutableList<String>
    var P1Hits: MutableList<String>
    var P1Miss: MutableList<String>
    var P1Sunk: MutableList<String>
    var P2Hits: MutableList<String>
    var P2Miss: MutableList<String>
    var P2Sunk: MutableList<String>
    var state = ""
    var winner = ""
    var p1Left : Long = 5
    var p2Left : Long = 5
    var p1HitsTaken: Int = 0
    var p2HitsTaken: Int = 0
    var turnNum = 0;

    constructor(){
        testList = mutableListOf()
        boatPos1 = mutableListOf()
        boatPos2 = mutableListOf()
        P1miniHits = mutableListOf()
        P2miniHits = mutableListOf()
        P1miniMiss = mutableListOf()
        P2miniMiss = mutableListOf()
        P1Hits= mutableListOf()
        P1Miss= mutableListOf()
        P1Sunk= mutableListOf()
        P2Hits= mutableListOf()
        P2Miss= mutableListOf()
        P2Sunk= mutableListOf()
    }
}
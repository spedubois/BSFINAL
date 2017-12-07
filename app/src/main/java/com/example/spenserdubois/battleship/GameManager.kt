package com.example.spenserdubois.battleship

import java.io.Serializable
import java.util.*

/**
 * Created by Spenser DuBois on 11/4/2017.
 */
class GameManager : Serializable {
    var state : String
    var name : String
    var players : List<Player>
    var winner : String
    var turn : Int
    var playerTurn : String

    constructor()
    {
        state = "JOIN"
        winner = ""
        playerTurn = "Player1"
        turn = 0
        var p1 = Player(0)
        var p2 = Player(0)
        val list = mutableListOf<Player>()
        list.add(p1)
        list.add(p2)
        players=list
        name = "Test"

    }

    constructor(player1 : Player, player2 : Player, DB : DatabaseElement)
    {
        DB.turn="Player 1"
        DB.state = "Started"
        playerTurn = "Player 1"
        state = "Started"
        val list = mutableListOf<Player>()
        list.add(player1)
        list.add(player2)
        players = list

        winner = ""
        var date = Date()
        name = date.toString()
        turn = 0
    }
    fun updateState(s : String)
    {
        state = s
    }

    fun setWin(s : String)
    {
        winner = s
    }

}
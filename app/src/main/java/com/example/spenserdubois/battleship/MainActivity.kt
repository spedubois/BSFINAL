package com.example.spenserdubois.battleship

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

/**
 * This is the main activity where a game of battle ship is played.
 */
class MainActivity : AppCompatActivity() {

    var player1 = Player(0)
    var player2 = Player(0)
    var turn = 1
    private lateinit var manager : GameManager
    private lateinit var player : String
    private lateinit var  passBtn : Button
    private lateinit var readyBtn : Button
    private lateinit var textWaitToShoot : TextView
    var gameID = ""
    var email = ""

    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var firebaseDB : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textWaitToShoot = textWaitForShot
        textWaitToShoot.visibility = View.INVISIBLE
        var intent = intent
        gameID = intent.getStringExtra("GameID").toString()
        player = intent.getStringExtra("Player")

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDB = FirebaseDatabase.getInstance().reference
         var user = firebaseAuth.currentUser

        if(user !is FirebaseUser)
            return

        val gameView = boardView
        val miniView = miniView
        val hitMiss = HitMiss
        passBtn = Pass
        readyBtn = ready
        val readyPhrase = readyPhrase


        readyBtn.setOnClickListener{
            gameView.visibility = View.VISIBLE
            miniView.visibility = View.VISIBLE
            readyBtn.visibility = View.INVISIBLE
            readyPhrase.visibility = View.INVISIBLE
            hitMiss.text = ""
            gameView.canClick = true
            passBtn.visibility = View.INVISIBLE
            turn++
            var tempPlayer : Player
            var otherPlayer : Player
            if(turn%2 == 0) {
                tempPlayer = player2
                otherPlayer = player1
            }
            else {
                tempPlayer = player1
                otherPlayer = player2
            }
            miniView.setHitPath(otherPlayer.miniHitPath)
            miniView.setMissPath(otherPlayer.miniMissPath)
            miniView.drawBoats(otherPlayer.boats)
            miniView.invalidate()
            gameView.setSunkPath(tempPlayer.sunkPath)
            gameView.setHitPath(tempPlayer.hitPath)
            gameView.setMissPath(tempPlayer.missPath)
            gameView.invalidate()
        }


        /**
         * This listener is listening for the firebase database to change. When a change is found, it checks which players turn it is.
         * If it the players turn, the game boards is enabled and the player can click.
         * If it is not the players turn, the game borad is disabled and teh player must wait for the other player to shoot.
         */
        firebaseDB.child("Games").child(gameID).child("Manager").child("turn").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(data: DataSnapshot?) {
                if(data !is DataSnapshot)
                    return

                if(data.value.toString().equals(player))
                {
                    updatePlayer()
                    textWaitToShoot.visibility = View.INVISIBLE

                    // When the player clicks the pass button, it means their turn is done. And control of the game passes to the other player.
                    var btnPass = passBtn
                    hitMiss.visibility = View.INVISIBLE
                    btnPass.visibility = View.INVISIBLE
                    var gameV = boardView
                    gameV.visibility = View.VISIBLE
                    gameV.canClick = true
                }
                else
                {
                    gameView.canClick = false
                    gameView.visibility = View.INVISIBLE
                    textWaitToShoot.visibility = View.VISIBLE
                }
            }

        })
        passBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                hitMiss.text = ""
                gameView.visibility = View.INVISIBLE
                miniView.visibility = View.INVISIBLE
                passBtn.visibility = View.INVISIBLE
                readyPhrase.visibility = View.VISIBLE
                readyBtn.visibility = View.VISIBLE
            }

        })

        var toDB = DatabaseElement()


        gameView.layoutParams.height = gameView.layoutParams.width

        gameView.genFrame(gameView.layoutParams.width, gameView.layoutParams.height)
        miniView.genFrame(miniView.layoutParams.width, gameView.layoutParams.width)
        player1 = Player(miniView.layoutParams.width/10)
        player2 = Player(miniView.layoutParams.width/10)
        player1.name = "Player 1"
        player2.name = "Player 2"

        //If this is player 2, we update the info in OUR manager to the manager in the firebase database.
        if(player.equals("Player 2"))
        {
            updateManagerForPlayer2(player1, player2)
            manager = GameManager(player1, player2, toDB)
            miniView.drawBoats(player1.boats)
        }
        else
        {
            manager = GameManager(player1, player2, toDB)
            miniView.drawBoats(player2.boats)
            miniView.invalidate()
            save(manager)
        }




        // Listens for shot to have been fired.
        gameView.setOnNewShotListener { _, x, y ->
            var tempPlayer : Player
            var otherPlayer : Player
            if(++manager.turn > 0 || manager.turn > 17)
                manager.updateState("In Progress")
            if(turn%2 == 0) {
                // update the game board and the mini view dependking on ehich players turn it is.
                manager.playerTurn = "Player 1"
                tempPlayer = player2
                otherPlayer = player1
            }
            else {
                manager.playerTurn="Player 2"
                tempPlayer = player1
                otherPlayer = player2
            }
            var shot = tempPlayer.ships[y][x]
            if( shot > 0) // If the shot sinks a ship
            {
                if(tempPlayer.hitBoat(shot) == 0)
                {
                    otherPlayer.boatsLeft--
                    if(++tempPlayer.hits == 17)
                    {
                        miniView.visibility = View.INVISIBLE
                        gameView.visibility = View.INVISIBLE
                        hitMiss.visibility = View.INVISIBLE
                        passBtn.visibility = View.INVISIBLE
                        manager.setWin(tempPlayer.name)
                        manager.updateState("Completed")
                        gotoWinScreen()
                    }
                    hitMiss.setTextColor(Color.GREEN)
                    hitMiss.text = "SUNK!"
                    miniView.boatPath = tempPlayer.boatsPath
                    miniView.setHitPath(tempPlayer.miniHitPath)
                    gameView.addHit(x + 0f, y + 0f)
                    miniView.addHit(x + 0f, y + 0f)
                    tempPlayer.miniHitPath = miniView.getHitPath()
                    tempPlayer.hitPath = gameView.getHitPath()
                    tempPlayer.boatsPath = miniView.boatPath
                    gameView.canClick = false
                    passBtn.visibility = View.VISIBLE
                    gameView.addSunkPath(tempPlayer.getBoat(shot).coords)
                    tempPlayer.sunkPath = gameView.getSunkPath()
                }
                else // If a players shot hit.
                {
                    tempPlayer.hits++
                    hitMiss.setTextColor(Color.RED)
                    hitMiss.text = "HIT!"
                    miniView.boatPath = tempPlayer.boatsPath
                    miniView.setHitPath(tempPlayer.miniHitPath)
                    gameView.addHit(x + 0f, y + 0f)
                    miniView.addHit(x + 0f, y + 0f)
                    tempPlayer.boatsPath = miniView.boatPath
                    tempPlayer.miniHitPath = miniView.getHitPath()
                    tempPlayer.hitPath = gameView.getHitPath()
                    gameView.canClick = false
                    passBtn.visibility = View.VISIBLE
                }
            }
            else // If a player shot MISSED
            {
                hitMiss.setTextColor(Color.DKGRAY)
                hitMiss.text = "MISS!"
                miniView.boatPath = tempPlayer.boatsPath
                miniView.setMissPath(tempPlayer.miniMissPath)
                gameView.addMiss(x+0f,y+0f)
                miniView.addMiss(x+0f,y+0f)
                tempPlayer.boatsPath = miniView.boatPath
                tempPlayer.miniMissPath = miniView.getMissPath()
                tempPlayer.missPath = gameView.getMissPath()
                gameView.canClick = false
                passBtn.visibility = View.VISIBLE
            }
            passBtn.visibility = View.INVISIBLE
            gameView.isEnabled = false
            textWaitToShoot.visibility = View.VISIBLE
            save(manager)
        }

    }


    /**
     * This Listener only happens once. It updates Player 2 to have the same boat layout and game state as player 1. This only happen once, at the beginning of the game.
     */
    private fun updateManagerForPlayer2(pplayer1 : Player, pplayer2: Player) {
        //Player 1 ships. Will be updated from firebase DB so both players have the same view

        firebaseDB.child("Games").child(gameID).child("Manager").child("boatPos1").addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                if (data !is DataSnapshot)
                    return

                for(i in 0 until 10)
                {
                    for(o in 0 until 10)
                        pplayer2.ships[i][o] = 0
                }
                for (b2 in pplayer2.boats) {
                    b2.coords.clear()
                }
                val iterable = data.children
                for (c in iterable) {
                    var sArr = c.value.toString().split(" ")
                    var coord = Coord(sArr[1].toInt(), sArr[2].toInt())
                    when (sArr[0].toInt()) {

                        5 -> {
                            pplayer2.boats[0].coords.add(coord)
                            pplayer2.ships[sArr[2].toInt()][sArr[1].toInt()] = 5

                        }
                        4 -> {
                            pplayer2.boats[4].coords.add(coord)
                            pplayer2.ships[sArr[2].toInt()][sArr[1].toInt()] = 4
                        }
                        3 -> {
                            pplayer2.boats[1].coords.add(coord)
                            pplayer2.ships[sArr[2].toInt()][sArr[1].toInt()] = 3
                        }
                        2 -> {
                            pplayer2.boats[2].coords.add(coord)
                            pplayer2.ships[sArr[2].toInt()][sArr[1].toInt()] = 2
                        }
                        1 -> {
                            pplayer2.boats[3].coords.add(coord)
                            pplayer2.ships[sArr[2].toInt()][sArr[1].toInt()] = 1
                        }

                    }
                }
            }

        })

        firebaseDB.child("Games").child(gameID).child("Manager").child("boatPos2").addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                if(data !is DataSnapshot)
                    return
                for(i in 0 until 10)
                {
                    for(o in 0 until 10)
                        player1.ships[i][o] = 0
                }
                for (b2 in player1.boats) {
                    b2.coords.clear()
                }
                val iterable = data.children
                for(c in iterable) {
                    var sArr = c.value.toString().split(" ")
                    var coord = Coord(sArr[1].toInt(), sArr[2].toInt())
                    when (sArr[0].toInt()) {

                        5 -> {
                            pplayer1.boats[0].coords.add(coord)
                            pplayer1.ships[sArr[2].toInt()][sArr[1].toInt()] = 5
                        }
                        4 -> {
                            pplayer1.boats[4].coords.add(coord)
                            player1.ships[sArr[2].toInt()][sArr[1].toInt()] = 4
                        }
                        3 -> {
                            pplayer1.boats[1].coords.add(coord)
                            pplayer1.ships[sArr[2].toInt()][sArr[1].toInt()] = 3
                        }
                        2 -> {
                            pplayer1.boats[2].coords.add(coord)
                            pplayer1.ships[sArr[2].toInt()][sArr[1].toInt()] = 2
                        }
                        1 -> {
                            pplayer1.boats[3].coords.add(coord)
                            pplayer1.ships[sArr[2].toInt()][sArr[1].toInt()] = 1
                        }

                    }
                }
            }

        })
        var gameView = boardView
        gameView.canClick = false
        textWaitToShoot.visibility = View.VISIBLE
    }

    fun save(manager: GameManager)
    {
        var test = DatabaseElement()
        // Update players boat positions. Only needs to be done once.
        if(manager.turn == 0) {
            for (i in 0 until player1.boats.size) {
                var boat = player1.boats.get(i)
                var boat2 = player2.boats.get(i)
                for (o in 0 until boat.coords.size) {
                    var x = boat.coords.get(o).x
                    var y = boat.coords.get(o).y
                    var x2 = boat2.coords.get(o).x
                    var y2 = boat2.coords.get(o).y
                    test.boatPos1.add("" + boat.id + " " + x + " " + y)
                    test.boatPos2.add("" + boat2.id + " " + x2 + " " + y2)
                }
            }
        }

        //Update players mini view and main view with misses, hits, sinks
        updateDatabaseElement(test)

        if(player.equals("Player 1"))
            manager.playerTurn = "Player 2"
        else
            manager.playerTurn = "Player 1"
        test.state = manager.state
        test.turn = manager.playerTurn
        test.winner = manager.winner
        test.p1HitsTaken = manager.players[0].hits
        test.p2HitsTaken = manager.players[1].hits
        firebaseDB.child("Games").child(gameID).child("Manager").setValue(test)


    }

    /**
     * Updates the Database element that is sent to the Firebase DB
     */
    fun updateDatabaseElement(test: DatabaseElement)
    {
        test.p1Left = manager.players[0].boatsLeft
        test.p2Left = manager.players[1].boatsLeft
        for(i in 0 until player1.miniHitPath.rects.size)
        {
            var rect = player1.miniHitPath.rects.get(i)
            var toAdd = ""+rect.left + " " + rect.top+ " " + rect.right + " " + rect.bottom
            test.P1miniHits.add(toAdd)
        }
        for(i in 0 until player2.miniHitPath.rects.size)
        {
            var rect = player2.miniHitPath.rects.get(i)
            var toAdd = ""+rect.left + " " + rect.top+ " " + rect.right + " " + rect.bottom
            test.P2miniHits.add(toAdd)
        }
        for(i in 0 until player1.miniMissPath.rects.size)
        {
            var rect = player1.miniMissPath.rects.get(i)
            var toAdd = ""+rect.left + " " + rect.top+ " " + rect.right + " " + rect.bottom
            test.P1miniMiss.add(toAdd)
        }
        for(i in 0 until player2.miniMissPath.rects.size)
        {
            var rect = player2.miniMissPath.rects.get(i)
            var toAdd = ""+rect.left + " " + rect.top+ " " + rect.right + " " + rect.bottom
            test.P2miniMiss.add(toAdd)
        }
        for(i in 0 until player1.hitPath.rects.size)
        {
            var rect = player1.hitPath.rects.get(i)
            var toAdd = ""+rect.left + " " + rect.top+ " " + rect.right + " " + rect.bottom
            test.P1Hits.add(toAdd)
        }
        for(i in 0 until player2.hitPath.rects.size)
        {
            var rect = player2.hitPath.rects.get(i)
            var toAdd = ""+rect.left + " " + rect.top+ " " + rect.right + " " + rect.bottom
            test.P2Hits.add(toAdd)
        }
        for(i in 0 until player1.missPath.rects.size)
        {
            var rect = player1.missPath.rects.get(i)
            var toAdd = ""+rect.left + " " + rect.top+ " " + rect.right + " " + rect.bottom
            test.P1Miss.add(toAdd)
        }
        for(i in 0 until player2.missPath.rects.size)
        {
            var rect = player2.missPath.rects.get(i)
            var toAdd = ""+rect.left + " " + rect.top+ " " + rect.right + " " + rect.bottom
            test.P2Miss.add(toAdd)
        }
        for(i in 0 until player1.sunkPath.rects.size)
        {
            var rect = player1.sunkPath.rects.get(i)
            var toAdd = ""+rect.left + " " + rect.top+ " " + rect.right + " " + rect.bottom
            test.P1Sunk.add(toAdd)
        }
        for(i in 0 until player2.sunkPath.rects.size)
        {
            var rect = player2.sunkPath.rects.get(i)
            var toAdd = ""+rect.left + " " + rect.top+ " " + rect.right + " " + rect.bottom
            test.P2Sunk.add(toAdd)
        }
    }

    /**
     * Updates the player at the beginning of their turn so they have the same game state as the other player.
     */
    fun updatePlayer()
    {
        firebaseDB.child("Games").child(gameID).child("Manager").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                if(data !is DataSnapshot)
                    return
                val children = data.children
                for(child in children)
                {
                    when(child.key)
                    {
                        "p1Hits"->
                        {
                            val childVal = child.children.last().value.toString()
                            val newRect = MyRect(childVal[0].toFloat(), childVal[1].toFloat(), childVal[2].toFloat(), childVal[3].toFloat())
                            manager.players[0].hitPath.rects.add(newRect)
                        }
                        "p2Hits"->
                        {
                            val childVal = child.children.last().value.toString()
                            val newRect = MyRect(childVal[0].toFloat(), childVal[1].toFloat(), childVal[2].toFloat(), childVal[3].toFloat())
                            manager.players[1].hitPath.rects.add(newRect)
                        }
                        "p1Miss"->
                        {
                            val childVal = child.children.last().value.toString()
                            val newRect = MyRect(childVal[0].toFloat(), childVal[1].toFloat(), childVal[2].toFloat(), childVal[3].toFloat())
                            manager.players[0].missPath.rects.add(newRect)
                        }
                        "p2Miss"->
                        {
                            val childVal = child.children.last().value.toString()
                            val newRect = MyRect(childVal[0].toFloat(), childVal[1].toFloat(), childVal[2].toFloat(), childVal[3].toFloat())
                            manager.players[1].missPath.rects.add(newRect)
                        }
                        "p1miniHits"->
                        {
                            val childVal = child.children.last().value.toString()
                            val newRect = MyRect(childVal[0].toFloat(), childVal[1].toFloat(), childVal[2].toFloat(), childVal[3].toFloat())
                            manager.players[0].miniHitPath.rects.add(newRect)
                        }
                        "p2miniHits"->
                        {
                            val childVal = child.children.last().value.toString()
                            val newRect = MyRect(childVal[0].toFloat(), childVal[1].toFloat(), childVal[2].toFloat(), childVal[3].toFloat())
                            manager.players[1].miniHitPath.rects.add(newRect)
                        }
                        "p1miniMiss"->
                        {
                            val childVal = child.children.last().value.toString()
                            val newRect = MyRect(childVal[0].toFloat(), childVal[1].toFloat(), childVal[2].toFloat(), childVal[3].toFloat())
                            manager.players[0].miniMissPath.rects.add(newRect)
                        }
                        "p2miniHits"->
                        {
                            val childVal = child.children.last().value.toString()
                            val newRect = MyRect(childVal[0].toFloat(), childVal[1].toFloat(), childVal[2].toFloat(), childVal[3].toFloat())
                            manager.players[1].miniMissPath.rects.add(newRect)
                        }
                        "p1Sunk"->
                        {
                            val childVal = child.children.last().value.toString()
                            val newRect = MyRect(childVal[0].toFloat(), childVal[1].toFloat(), childVal[2].toFloat(), childVal[3].toFloat())
                            manager.players[0].sunkPath.rects.add(newRect)
                        }
                        "p2Sunk"->
                        {
                            val childVal = child.children.last().value.toString()
                            val newRect = MyRect(childVal[0].toFloat(), childVal[1].toFloat(), childVal[2].toFloat(), childVal[3].toFloat())
                            manager.players[1].sunkPath.rects.add(newRect)
                        }
                    }
                }
            }

        })
    }

    fun gotoWinScreen()
    {
        val intent = Intent(this@MainActivity, WinActivity::class.java)
        intent.putExtra("winner", manager.winner)
        setResult(0, intent)
        startActivity(intent)
    }

}

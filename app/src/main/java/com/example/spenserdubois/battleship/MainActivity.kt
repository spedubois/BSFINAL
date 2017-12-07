package com.example.spenserdubois.battleship

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var player1 = Player(0)
    var player2 = Player(0)
    var turn = 1
    private lateinit var manager : GameManager
    private lateinit var player : String
    var gameID = ""
    var email = ""


    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var firebaseDB : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        val passBtn = Pass
        var toDB = DatabaseElement()


        gameView.layoutParams.height = gameView.layoutParams.width

        gameView.genFrame(gameView.layoutParams.width, gameView.layoutParams.height)
        miniView.genFrame(miniView.layoutParams.width, gameView.layoutParams.width)
        player1 = Player(miniView.layoutParams.width/10)
        player2 = Player(miniView.layoutParams.width/10)
        player1.name = "Player 1"
        player2.name = "Player 2"
        firebaseDB.child("Games").child(gameID).child("Player2").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                if (data !is DataSnapshot)
                    return
                email = data.value.toString()
                if(user.email.equals(email))
                {
                    updateManagerForPlayer2()
                }
            }

        })
        if(player.equals("Player2"))
        {
            manager = GameManager(player1, player2, toDB)
            gotoHoldScreen()

        }
        else
        {
            manager = GameManager(player1, player2, toDB)
            miniView.drawBoats(player2.boats)
            miniView.invalidate()
            save(manager)
        }




        gameView.setOnNewShotListener { _, x, y ->
            var tempPlayer : Player
            var otherPlayer : Player
            if(++manager.turn > 0 || manager.turn > 17)
                manager.updateState("In Progress")
            if(turn%2 == 0) {
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
            if( shot > 0)
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
                else
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
            else
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
            save(manager)
        }

        passBtn.setOnClickListener{
            gotoHoldScreen()

        }
    }

    private fun gotoHoldScreen() {
        HitMiss.text = ""
        boardView.visibility = View.INVISIBLE
        miniView.visibility = View.INVISIBLE
        Pass.visibility = View.INVISIBLE
        readyPhrase.visibility = View.VISIBLE
        readyPhrase.text = "Waitng for oppenent to shoot"
        firebaseDB.child("Games").child(gameID).child("Manager").child("turn").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                goBackToGameScreen()
            }

        })
    }

    private fun goBackToGameScreen() {
        boardView.visibility = View.VISIBLE
        miniView.visibility = View.VISIBLE
        readyPhrase.visibility = View.INVISIBLE
        HitMiss.text = ""
        boardView.canClick = true
        Pass.visibility = View.INVISIBLE
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
        boardView.setSunkPath(tempPlayer.sunkPath)
        boardView.setHitPath(tempPlayer.hitPath)
        boardView.setMissPath(tempPlayer.missPath)
        boardView.invalidate()
    }

    private fun updateManagerForPlayer2() {
        //Player 1 ships. Will be updated from firebase DB so both players have the same view
        var carrier = ArrayList<Coord>()
        var sub = ArrayList<Coord>()
        var cruiser = ArrayList<Coord>()
        var battle = ArrayList<Coord>()
        var destroy = ArrayList<Coord>()

        var carrier2 = ArrayList<Coord>()
        var sub2 = ArrayList<Coord>()
        var cruiser2 = ArrayList<Coord>()
        var battle2 = ArrayList<Coord>()
        var destroy2 = ArrayList<Coord>()


        firebaseDB.child("Games").child(gameID).child("Manager").child("boatPos1").addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                if (data !is DataSnapshot)
                    return

                for (b2 in player2.boats) {
                    b2.coords.clear()
                }
                val iterable = data.children
                for (c in iterable) {
                    var sArr = c.value.toString().split(" ")
                    var coord = Coord(sArr[1].toInt(), sArr[2].toInt())
                    when (sArr[0].toInt()) {

                        5 -> {
                            player2.boats[0].coords.add(coord)
                        }
                        4 -> {
                            player2.boats[4].coords.add(coord)
                        }
                        3 -> {
                            player2.boats[1].coords.add(coord)
                        }
                        2 -> {
                            player2.boats[2].coords.add(coord)
                        }
                        1 -> {
                            player2.boats[3].coords.add(coord)
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
                val iterable = data.children
                for(c in iterable)
                {
                    var sArr = c.value.toString().split(" ")
                    when(sArr[0].toInt()) {
                        5 -> {
                            carrier2.add(Coord(sArr[1].toInt(), sArr[2].toInt()))
                        }
                        4 -> {
                            battle2.add(Coord(sArr[1].toInt(), sArr[2].toInt()))
                        }
                        3 -> {
                            cruiser2.add(Coord(sArr[1].toInt(), sArr[2].toInt()))
                        }
                        2 -> {
                            sub2.add(Coord(sArr[1].toInt(), sArr[2].toInt()))
                        }
                        1 -> {
                            destroy2.add(Coord(sArr[1].toInt(), sArr[2].toInt()))
                        }
                    }

                }
            }

        })

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

        test.state = manager.state
        test.turn = manager.playerTurn
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

    fun gotoWinScreen()
    {
        val intent = Intent(this@MainActivity, WinActivity::class.java)
        intent.putExtra("winner", manager.winner)
        setResult(0, intent)
        startActivity(intent)
    }
    override fun onActivityResult(requestCode : Int, resultCode: Int, data: Intent?) {
        val gameView = boardView as com.example.spenserdubois.battleship.boardView
        super.onActivityReenter(resultCode, data)
        if(data !is Intent)
            return
        manager = data.getSerializableExtra("manager") as GameManager
        var tempPlayer : Player
        var otherPlayer : Player
        if(manager.playerTurn.equals("Player 2")) {
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

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        val gameView = boardView as com.example.spenserdubois.battleship.boardView
        super.onActivityReenter(resultCode, data)
        if(data !is Intent)
            return
        manager = data.getSerializableExtra("manager") as GameManager
        var tempPlayer : Player
        var otherPlayer : Player
        if(manager.playerTurn.equals("Player 2")) {
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
}

package com.example.spenserdubois.battleship

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Button
import com.example.spenserdubois.battleship.R.id.recyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_begin.*
import java.util.*


class BeginActivity : AppCompatActivity() {

    private lateinit var recyclerViewLayoutManager : LinearLayoutManager
    private lateinit var player1:String
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var myRef : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_begin)
        var firebaseAuth = FirebaseAuth.getInstance()
        myRef = FirebaseDatabase.getInstance().reference
        var user = firebaseAuth.currentUser
        if(user !is FirebaseUser)
            return

        player1 = user.email.toString()


        val start = newBtn as Button
        val btnLogout = btnLogout

        start.setOnClickListener{
            startNewGame()
        }

        btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            var intent = Intent(this@BeginActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }



        myRef.child("Games").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(data: DataSnapshot?) {
                if(data !is DataSnapshot)
                    return
                Thread.sleep(500)
                updateView(data.children)
            }

        })

    }

    private fun updateView(children: MutableIterable<DataSnapshot>) {
        recyclerViewLayoutManager  = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = recyclerViewLayoutManager

        recyclerView.adapter = GameAdapter({
            val recyclerViewDataset: MutableList<GameAdapter.CustomAdapterItem> = mutableListOf()
            for(c in children)
            {
                var gm = GameManager()
                gm.name = c.key.toString()
                gm.players[0].name = c.child("Player1").value.toString()
                gm.players[1].name = c.child("Player2").value.toString()
                gm.players[0].boatsLeft = c.child("Manager").child("p1Left").value as Long
                gm.players[1].boatsLeft = c.child("Manager").child("p2Left").value as Long
                gm.winner = c.child("Manager").child("winner").value.toString()
                gm.state = c.child("Manager").child("state").value.toString()
                gm.playerTurn = c.child("Manager").child("turn").value.toString()

                recyclerViewDataset.add(GameAdapter.BSGame(gm))
            }

            recyclerViewDataset.toTypedArray()
        }()).apply {
            setOnCustomAdapterItemSelectedListener { customAdapterItem: GameAdapter.CustomAdapterItem->

                when(customAdapterItem){
                    is GameAdapter.BSGame -> gotoChooseScreen(customAdapterItem.manager)

                    else -> Log.ERROR
                }



            }
        }
    }

    fun gotoChooseScreen(manager: GameManager)
    {
        val intent = Intent(this@BeginActivity, ChooseActivity::class.java)
        intent.putExtra("GameID", manager.name)
        intent.putExtra("manager", manager)
        setResult(0, intent)
        startActivity(intent)
    }

    fun startNewGame()
    {
        val uniqueID = UUID.randomUUID().toString()

        var init : initialDbEntry = initialDbEntry()
        myRef.child("Games").child(uniqueID).child("Manager").setValue(init)
        myRef.child("Games").child(uniqueID).child("Player1").setValue(player1)
        myRef.child("Games").child(uniqueID).child("Player2").setValue("")


        val intent = Intent(this@BeginActivity, Wait::class.java)
        intent.putExtra("GameID", uniqueID)
        intent.putExtra("Email", player1)
        startActivity(intent)
    }
}

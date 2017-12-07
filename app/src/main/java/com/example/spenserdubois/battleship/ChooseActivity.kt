package com.example.spenserdubois.battleship

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.spenserdubois.battleship.R.id.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_choose.*
import kotlinx.android.synthetic.main.activity_login.*

class ChooseActivity : AppCompatActivity() {

    private lateinit var fireAuth : FirebaseAuth
    private lateinit var fireDB : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {

        fireAuth = FirebaseAuth.getInstance()
        fireDB = FirebaseDatabase.getInstance().reference
        val user = fireAuth.currentUser
        if(user !is FirebaseUser)
            return

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)

        val backBtn = backBtn as Button
        val conBtn = contBtn as Button
        val deleteBtn = deleteBtn as Button
        val gameID = textGameId
        val p1 = textP1
        val p2 = textP2


        var intent = intent
        var manager = intent.getSerializableExtra("manager") as GameManager
        gameID.text = "GameID: "+manager.name
        p1.text="Player 1: "+manager.players[0].name
        p2.text="Player 2: "+manager.players[1].name

        if(manager.state.equals("JOIN"))
            conBtn.text="Join"

        backBtn.setOnClickListener{
            val intent = Intent(this@ChooseActivity, BeginActivity::class.java)
            startActivity(intent)
        }
        conBtn.setOnClickListener{
            fireDB.child("Games").child(manager.name).child("Player2").setValue(user.email)
            val intent = Intent(this@ChooseActivity, MainActivity::class.java)
            intent.putExtra("Player", "Player2")
            intent.putExtra("manager", manager)
            intent.putExtra("GameID", manager.name)
            Thread.sleep(1000)
            setResult(0, intent)
            startActivity(intent)
        }
        deleteBtn.setOnClickListener{

        }
    }
}

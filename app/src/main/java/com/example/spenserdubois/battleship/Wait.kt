package com.example.spenserdubois.battleship

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_wait.*

class Wait : AppCompatActivity() {

    private lateinit var fireAuth : FirebaseAuth
    private lateinit var fireDB : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireAuth = FirebaseAuth.getInstance()
        fireDB = FirebaseDatabase.getInstance().reference
        val user = fireAuth.currentUser

        val btnCancel = btnCancel

        if(user !is FirebaseUser)
            return

        setContentView(R.layout.activity_wait)

        var gameId = textGameID
        var s = intent.getStringExtra("GameID")
        gameId.text="GameID : "+s

        fireDB.child("Games").child(s).child("Player2").addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(data: DataSnapshot?) {
                if(data !is DataSnapshot)
                    return

                var p2Name = data.value.toString()

                if(!p2Name.equals("")) {
                    var intent = Intent(this@Wait, MainActivity::class.java)
                    intent.putExtra("GameID", s)
                    startActivity(intent)
                    finish()
                    return
                }
            }

        })

        btnCancel.setOnClickListener {
            fireDB.child("Games").child(s).setValue("")
            var intent = Intent(this@Wait, BeginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

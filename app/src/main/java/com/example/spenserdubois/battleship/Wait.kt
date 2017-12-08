package com.example.spenserdubois.battleship

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_wait.*

/**
 * Activity where the user waits for another player to join his game after creating a new game.
 */
class Wait : AppCompatActivity() {

    private lateinit var fireAuth : FirebaseAuth
    private lateinit var fireDB : DatabaseReference
    var canceled = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fireAuth = FirebaseAuth.getInstance()
        fireDB = FirebaseDatabase.getInstance().reference
        val user = fireAuth.currentUser
        if(user !is FirebaseUser)
            return

        setContentView(R.layout.activity_wait)

        var gameId = textGameID
        val cancel = btnCancel as Button
        var s = intent.getStringExtra("GameID")
        var e = intent.getStringArrayExtra("Email")
        gameId.text="GameID : "+s

        fireDB.child("Games").child(s).child("Player2").addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(data: DataSnapshot?) {
                if(data !is DataSnapshot)
                    return

                var p2Name = data.value.toString()

                if(!p2Name.equals("") && !canceled) {
                    var intent = Intent(this@Wait, MainActivity::class.java)
                    intent.putExtra("GameID", s)
                    intent.putExtra("Player", "Player 1")
                    startActivity(intent)
                    finish()
                    return
                }
            }

        })

        cancel.setOnClickListener {
            canceled = true
            fireDB.child("Games").child(s).setValue(null)
            var intent = Intent(this@Wait, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

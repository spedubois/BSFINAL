package com.example.spenserdubois.battleship

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_win.*

/**
 * This Activity is used when a player wins. The players see this screen and have the ability to log out, or view
 * existinf
 */
class WinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_win)

        val winText = winner
        val againBtn = againBtn

        againBtn.setOnClickListener{
            val intent = Intent(this@WinActivity, MainActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode : Int, resultCode: Int, data: Intent?) {
        val winText = winner
        super.onActivityReenter(resultCode, data)
        if(data !is Intent)
            return
        var winner = data.getStringExtra("winner")
        winText.text = "" + winner + "\nWon!!"
    }


}

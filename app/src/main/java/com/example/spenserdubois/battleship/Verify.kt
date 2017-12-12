package com.example.spenserdubois.battleship

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_verify.*

/**
 * Activity is used to verify a users emaul
 */
class Verify : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)

        var txt_title = textVerifyTitle
        var txt_ins = textInstruction
        var btn_send = sendVerif
        var btn_verify = btnVerified
        var btn_resend = btnResend

        txt_title.text = "PLEASE VERIFY YOUR EMAIL"
        var user = FirebaseAuth.getInstance().currentUser
        if(user !is FirebaseUser)
            return

//        if(user.isEmailVerified)
//        {
//            var intent = Intent(this@Verify, BeginActivity::class.java)
//            startActivity(intent)
//            finish()
//        }

        btn_send.setOnClickListener {
            btn_send.isEnabled = false


            /**
             * Sends an email request to verify the users email address
             */
            user.sendEmailVerification().addOnCompleteListener { task: Task<Void> ->
                if(task.isSuccessful) {
                    Toast.makeText(this@Verify, "Verification sent to: " + user.email, Toast.LENGTH_SHORT).show()
                    txt_ins.text = "Click \"VERIFY EMAIL\"\nafter link has been followed in email sent to you."
                    btn_verify.isEnabled = true
                    btn_resend.isEnabled = true
                }

                else
                    Toast.makeText(this@Verify, "Failed to send verification to: "+user.email, Toast.LENGTH_SHORT).show()
            }
        }

        btn_resend.setOnClickListener {
            user.sendEmailVerification().addOnCompleteListener { task: Task<Void> ->
                if(task.isSuccessful) {
                    Toast.makeText(this@Verify, "Verification sent to: " + user.email, Toast.LENGTH_SHORT).show()
                    txt_ins.text = "Click \"VERIFY EMAIL\"\nafter link has been followed in email sent to you."
                }

                else
                    Toast.makeText(this@Verify, "Failed to send verification to: "+user.email, Toast.LENGTH_SHORT).show()
            }
        }


        btn_verify.setOnClickListener {

            user.reload()
            if(user.isEmailVerified) {
                var intent = Intent(this@Verify, LoginActivity::class.java)
                startActivity(intent)
                finish()
                return@setOnClickListener
            }
            else
                Toast.makeText(this@Verify, "Email not verified", Toast.LENGTH_SHORT).show()
        }

    }
}

package com.example.spenserdubois.battleship

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.*
import kotlinx.android.synthetic.main.activity_login.*

/**
 * First Activity player encounters. Users enters a valid email and password to create an account,
 */
class LoginActivity : AppCompatActivity(){

    private lateinit var btnReg : Button
    private lateinit var textEmail : EditText
    private lateinit var textPassword : EditText
    private lateinit var textView : TextView
    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        firebaseAuth = FirebaseAuth.getInstance()

        if(firebaseAuth.currentUser != null)
        {
            val user = firebaseAuth.currentUser
            if(user !is FirebaseUser)
                return
            if(user.isEmailVerified) {
                var intent = Intent(this@LoginActivity, BeginActivity::class.java)
                startActivity(intent)
                finish()
                return
            }
        }

        btnReg = registerBtn
        textEmail = editEmail
        textPassword = editPassword
        textView = textSignIn

        btnReg.setOnClickListener{
            login()
        }
        textView.setOnClickListener {
            var intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    // Async task to run login in background of main thread.
    inner class  myAsync : AsyncTask<String, String, String>()
    {
        override fun doInBackground(vararg params: String?): String {
            val email : String = textEmail.text.toString().trim()
            val password : String = textPassword.text.toString().trim()
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this@LoginActivity, object : OnCompleteListener<AuthResult> {
                        override fun onComplete(p0: Task<AuthResult>) {

                            if(p0.isSuccessful)
                            {
                                var user = firebaseAuth.currentUser
                                if(user !is FirebaseUser)
                                    return
                                if(user.isEmailVerified) {
                                    Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()
                                    var intent = Intent(this@LoginActivity, BeginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                    return
                                }

                                else
                                {
                                    Toast.makeText(this@LoginActivity, "Need to verify your email", Toast.LENGTH_SHORT).show()
                                    var intent = Intent(this@LoginActivity, Verify::class.java)
                                    startActivity(intent)
                                    finish()
                                    return
                                }
                            }
                            else
                            {
                                Toast.makeText(this@LoginActivity, "Error logging in, Please try again", Toast.LENGTH_LONG).show()
                            }
                        }

                    })
            return "GOOD"
        }

    }

    /**
     * Once user ahs entered in all the login information,
     */
    fun login()
    {
        val email : String = textEmail.text.toString().trim()
        val password : String = textPassword.text.toString().trim()
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Must enter an email", Toast.LENGTH_SHORT).show()
            return
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Must enter a password", Toast.LENGTH_SHORT).show()
            return
        }
        myAsync().execute()
    }
}

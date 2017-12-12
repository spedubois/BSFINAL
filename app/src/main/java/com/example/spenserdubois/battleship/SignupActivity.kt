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
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_signup.*


class SignupActivity : AppCompatActivity() {

    private lateinit var btnReg : Button
    private lateinit var textEmail : EditText
    private lateinit var textPassword : EditText
    private lateinit var creatingView : TextView
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var textConfirmPassword : EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        firebaseAuth = FirebaseAuth.getInstance()

        btnReg = registerBtnCreate
        textEmail = editEmailCreate
        textPassword = editPasswordCreate
        creatingView = addingCreate
        textConfirmPassword = editNicknameCreate

        btnReg.setOnClickListener{
            register()
        }
    }

    inner class  myAsync : AsyncTask<String, String, String>()
    {
        override fun doInBackground(vararg params: String?): String {
            val email : String = textEmail.text.toString().trim()
            val password : String = textPassword.text.toString().trim()

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this@SignupActivity, object : OnCompleteListener<AuthResult> {
                        override fun onComplete(p0: Task<AuthResult>) {

                            if(p0.isSuccessful)
                            {
                                creatingView.visibility = View.INVISIBLE
                                Toast.makeText(this@SignupActivity, "Account Created!", Toast.LENGTH_SHORT).show()
                            }
                            else
                            {
                                Toast.makeText(this@SignupActivity, "Error creating account, Please try again", Toast.LENGTH_LONG).show()
                            }
                        }
                    })
            return "GOOD"
        }

    }

    fun register()
    {
        val email : String = textEmail.text.toString().trim()
        val password : String = textPassword.text.toString().trim()
        val confPassword : String = textConfirmPassword.text.toString().trim()
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
        if(TextUtils.isEmpty(confPassword))
        {
            Toast.makeText(this, "Please confirm password", Toast.LENGTH_SHORT).show()
            return
        }
        if(!checkPassword(password))
        {
            Toast.makeText(this, "Password must contain,\n" +
                    "At least 8 characters\n1 uppercase letter\n1 number\n1 special character(!,@,#,$...)", Toast.LENGTH_LONG).show()
            return
        }
        if(!checkPasswordMatch(password, confPassword))
        {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_LONG).show()
            return
        }

        creatingView.visibility = View.VISIBLE
        btnReg.isEnabled = false
        textEmail.isEnabled = false
        textPassword.isEnabled = false
        textConfirmPassword.isEnabled = false

        myAsync().execute()

        btnReg.isEnabled = true
        textEmail.isEnabled = true
        textPassword.isEnabled = true
        textConfirmPassword.isEnabled = true

        var intent = Intent(this, Verify::class.java)
        startActivity(intent)
        finish()
        return
    }

    private fun checkPasswordMatch(pass : String, conf : String): Boolean {
        return pass.equals(conf)
    }

    fun checkPassword(s : String) : Boolean
    {
        var upper : Boolean = false
        var number : Boolean = false
        var special : Boolean = false
        val sArr = s.toCharArray()

        if(s.length < 8)
            return false

        for(i in 0 until s.length)
        {
            if(s[i] >= 'A' && s[i] <= 'Z')
                upper = true
            else if(s[i] >= '0' && s[i] <= '9')
                number = true
            else if((s[i] < 'A' && s[i] > '9')||(s[i] > 'z')||(s[i] < '0'))
                special = true

            if(upper&&number&&special)
                return true
        }
        return false
    }



}

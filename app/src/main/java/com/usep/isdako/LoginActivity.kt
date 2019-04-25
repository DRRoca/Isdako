package com.usep.isdako

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.auth_main.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val textViewCreateAccount = findViewById<TextView>(R.id.createAccount)

        auth = FirebaseAuth.getInstance()

        textViewCreateAccount.setOnClickListener {
            val activityIntent = Intent (this, SignUpActivity::class.java)
            startActivity(activityIntent)
            finish()
        }

        loginButton.setOnClickListener {
            doLogin()
        }
    }

    private fun doLogin() {
        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail.text.toString()).matches()){
            userEmail.error = "Please enter valid email"
            userEmail.requestFocus()
            return
        }
        if(userPassword.text.isEmpty()){
            userPassword.error = "Please enter password"
            userPassword.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(userEmail.text.toString(), userPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (!task.isSuccessful) return@addOnCompleteListener

                val user = auth.currentUser
                updateUI(user)
            }

            .addOnFailureListener(this) { task ->
                Toast.makeText(baseContext, "Login failed: ${task.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }



    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if(currentUser != null){
            startActivity(Intent (this, StartActivity::class.java))
            finish()
        }
    }


}

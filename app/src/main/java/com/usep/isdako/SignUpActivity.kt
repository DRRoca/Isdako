package com.usep.isdako

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        loginCreate.setOnClickListener{
            signUpUser()

        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun signUpUser(){
        if(!Patterns.EMAIL_ADDRESS.matcher(userNewEmail.text.toString()).matches()){
            userNewEmail.error = "Please enter valid email"
            userNewEmail.requestFocus()
            return
        }
        else if(userNewPassword.text.isEmpty()){
            userNewPassword.error = "Please enter password"
            userNewPassword.requestFocus()
            return
        }
        else if(userVerifyPassword.text.toString() != userNewPassword.text.toString()) {
            userVerifyPassword.error = "Password did not match"
            userVerifyPassword.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(userNewEmail.text.toString(), userNewPassword.text.toString())
            .addOnCompleteListener(this) { task ->
                if (!task.isSuccessful) return@addOnCompleteListener

                val user = auth.currentUser
                updateUI(user)
            }

            .addOnFailureListener(this) { task ->
                Toast.makeText(baseContext, "Registration failed: ${task.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if(currentUser != null){
            startActivity(Intent (this, StartActivity::class.java))
            finish()
        }
    }

}

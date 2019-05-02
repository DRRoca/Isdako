package com.usep.isdako

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.usep.isdako.data.User
import kotlinx.android.synthetic.main.activity_sign_up.*
import io.proximi.proximiiolibrary.ProximiioAPI
import timber.log.Timber

class SignUpActivity : AppCompatActivity() {

    private lateinit var proximiioAPI: ProximiioAPI
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

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
        if(userBoatNumber.text.isEmpty()){
            userBoatNumber.error = "Please enter boat number"
            userBoatNumber.requestFocus()
            return
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(userNewEmail.text.toString()).matches()){
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



                addUserToDatabase(userBoatNumber.text.toString())
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

    @SuppressLint("LogNotTimber")
    private fun addUserToDatabase(boatNumber: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, boatNumber)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("SignUpActivity","User info added to Firebase Database")
            }
            .addOnFailureListener {
                Log.d("SignUpActivity", "Failed to set value to database: ${it.message}")
            }
    }

}

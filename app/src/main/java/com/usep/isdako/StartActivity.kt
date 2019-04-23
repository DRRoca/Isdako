package com.usep.isdako

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth




class StartActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        val activityIntent:Intent

        if (user != null) {
            activityIntent = Intent (this, LoginActivity::class.java)
            startActivity(activityIntent)
        } else {
            activityIntent = Intent (this, LoginActivity::class.java)
            startActivity(activityIntent)
        }



        finish()
    }


}

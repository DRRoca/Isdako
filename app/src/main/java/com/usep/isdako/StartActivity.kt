package com.usep.isdako

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
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
        val reportActivityIntent:Intent
        val mainActivityIntent:Intent

        if (user != null) {

            reportActivityIntent = Intent (this, ReportMapActivity::class.java)
            reportActivityIntent.putExtra(TUNA_TYPE,"none")
            startActivity(reportActivityIntent)


        } else {
            activityIntent = Intent (this, LoginActivity::class.java)
            startActivity(activityIntent)
        }



        finish()
    }

    companion object {
        const val TUNA_TYPE : String = "com.usep.isdako.TUNA_TYPE"
    }
}

package com.usep.isdako

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
//import android.support.v4.app.FragmentManager

class MainActivity : AppCompatActivity(), LoginFragment.OnFragmentInteractionListener {

//    lateinit var loginFragment: LoginFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

//          **Fragment Template

//        loginFragment = LoginFragment.newInstance()
//        supportFragmentManager
//            .beginTransaction()
//            .replace(R.id.container, loginFragment)
//            .addToBackStack(loginFragment.toString())
//            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//            .commit()
//
//          ** Activity Template
        val activityIntent = Intent (this, LocationComponentActivity::class.java)

        startActivity(activityIntent)


    }



    public override fun onStart() {
        super.onStart()
    }

    public override fun onResume() {
        super.onResume()
    }

    public override fun onPause() {
        super.onPause()
    }

    public override fun onStop() {
        super.onStop()
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
package com.usep.isdako

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.*
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.maps.MapboxMapOptions




//import android.support.v4.app.FragmentManager

class MainActivity : AppCompatActivity(),
    OnMapReadyCallback,
    PermissionsListener,
    NavigationView.OnNavigationItemSelectedListener,
    ReportFragment.OnFragmentInteractionListener,
    InformationFragment.OnFragmentInteractionListener  {

    private lateinit var drawer: DrawerLayout
    private lateinit var database: DatabaseReference

    private lateinit var mapFragment: SupportMapFragment

    private var permissionsManager: PermissionsManager? = null
    private var mapboxMap: MapboxMap? = null
    private var mapView: MapView? = null



    lateinit var reportFragment: ReportFragment
    lateinit var informationFragment: InformationFragment
//    lateinit var loginFragment: LoginFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = FirebaseDatabase.getInstance().reference

        Mapbox.getInstance(this,getString(R.string.accessToken))
        setContentView(R.layout.activity_main)
        mapView = findViewById(R.id.mapView)
        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync(this)

        drawer = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawer,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

//          **Fragment Template

        reportFragment = ReportFragment.newInstance()
        informationFragment = InformationFragment.newInstance()
//        supportFragmentManager
//            .beginTransaction()
//            .replace(R.id.container, loginFragment)
//            .addToBackStack(loginFragment.toString())
//            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//            .commit()
//
//          ** Activity Template
//        val activityIntent = Intent (this, LocationComponentActivity::class.java)
//
//        startActivity(activityIntent)


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navMap -> {
                supportFragmentManager
                    .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
            R.id.navReport -> {
//                supportFragmentManager
//                    .popBackStackImmediate()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, reportFragment)
                    .addToBackStack(reportFragment.toString())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            R.id.navInformation -> {
//                supportFragmentManager
//                    .popBackStackImmediate()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, informationFragment)
                    .addToBackStack(informationFragment.toString())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            R.id.navHelp -> {

            }
            R.id.navAbout -> {

            }
            R.id.navLogout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent (this, StartActivity::class.java))
                finish()
            }
        }
        drawer.closeDrawer(GravityCompat.END)
        return true
    }


    override fun onMapReady(mapboxMap: MapboxMap) {
        this@MainActivity.mapboxMap = mapboxMap

        mapboxMap.setStyle(Style.MAPBOX_STREETS) { style -> enableLocationComponent(style)
            // Map is set up and the style has loaded. Now you can add data or make other map adjustments
        }
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    private fun enableLocationComponent(loadedMapStyle: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Get an instance of the component
            val locationComponent = mapboxMap!!.locationComponent

            // Activate with options

            locationComponent.activateLocationComponent(this, loadedMapStyle)

            // Enable to make component visible
            locationComponent.isLocationComponentEnabled = true

            // Set the component's camera mode
            locationComponent.cameraMode = CameraMode.TRACKING

            // Set the component's render mode
            locationComponent.renderMode = RenderMode.COMPASS
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager!!.requestLocationPermissions(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionsManager!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            mapboxMap!!.getStyle { style -> enableLocationComponent(style) }
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show()
            finish()
        }
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
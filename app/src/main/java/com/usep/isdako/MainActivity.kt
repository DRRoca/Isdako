package com.usep.isdako

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.google.android.material.navigation.NavigationView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
//import com.mapbox.mapboxsdk.location.modes.CameraMode
//import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.*
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.maps.MapboxMapOptions
import io.proximi.proximiiolibrary.ProximiioAPI
import io.proximi.proximiiolibrary.ProximiioGeofence
import io.proximi.proximiiolibrary.ProximiioListener
import io.proximi.proximiiolibrary.ProximiioOptions
import io.proximi.proximiiomap.ProximiioMapHelper
import io.proximi.proximiiomap.ProximiioMapView


//import android.support.v4.app.FragmentManager

class MainActivity : AppCompatActivity(),
//    OnMapReadyCallback,
//    PermissionsListener,
    NavigationView.OnNavigationItemSelectedListener,
    ReportFragment.OnFragmentInteractionListener,
    InformationFragment.OnFragmentInteractionListener,
    TunaListFragment.OnTunaSelected {

    private lateinit var proximiioAPI: ProximiioAPI
    private lateinit var mapHelper: ProximiioMapHelper

    val TAG = "Isdako"

    private lateinit var drawer: androidx.drawerlayout.widget.DrawerLayout
    private lateinit var database: DatabaseReference
/**
 *Mapbox codes
    private var permissionsManager: PermissionsManager? = null
    private var mapboxMap: MapboxMap? = null
    private var mapView: MapView? = null
*/


    lateinit var reportFragment: ReportFragment
    lateinit var informationFragment: InformationFragment
//    lateinit var loginFragment: LoginFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = FirebaseDatabase.getInstance().reference

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val preferences = getSharedPreferences("Proximi.io Map Demo", MODE_PRIVATE)
        if (!preferences.contains("notificationChannel")) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (notificationManager != null) {
                val channel = NotificationChannel(
                    BackgroundListener.NOTIFICATION_CHANNEL_ID,
                    BackgroundListener.NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
                preferences.edit()
                    .putBoolean("notificationChannel", true)
                    .apply()
            }
        }
    }

    val options = ProximiioOptions()
        .setNotificationMode(ProximiioOptions.NotificationMode.ENABLED)

    // Create our Proximi.io listener
    proximiioAPI = ProximiioAPI(TAG, this, options)
    proximiioAPI.setListener(object : ProximiioListener() {
        override fun geofenceEnter(geofence: ProximiioGeofence) {
            Log.d(TAG, "Geofence enter: " + geofence.name)
        }

        override fun geofenceExit(geofence: ProximiioGeofence, @Nullable dwellTime: Long?) {
            Log.d(TAG, "Geofence exit: " + geofence.name + ", dwell time: " + dwellTime.toString())
        }

        override fun loginFailed(loginError: LoginError) {
            Log.e(TAG, "LoginError! ($loginError)")
        }
    })
    proximiioAPI.setAuth(AUTH, true)
    proximiioAPI.setActivity(this)

    // Initialize the map
    var mapView: ProximiioMapView = findViewById(R.id.map)
    mapHelper = ProximiioMapHelper.Builder(TAG, this, mapView, AUTH, savedInstanceState)
        .build()
/**
 *Mapbox codes
        Mapbox.getInstance(this,getString(R.string.accessToken))
        setContentView(R.layout.activity_main)
        mapView = findViewById(R.id.mapView)
        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync(this)
*/

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

/**
 *Mapbox codes
    override fun onMapReady(mapboxMap: MapboxMap) {
        this@MainActivity.mapboxMap = mapboxMap

        mapboxMap.setStyle(Style.MAPBOX_STREETS) { style -> enableLocationComponent(style)
            // Map is set up and the style has loaded. Now you can add data or make other map adjustments
        }
    }
 */
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }
/**
 *Mapbox codes
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
*/
    public override fun onStart() {
        super.onStart()
        proximiioAPI.onStart()
        mapHelper.onStart()
    }

    public override fun onResume() {
        super.onResume()
        mapHelper.onResume()
    }

    public override fun onPause() {
        super.onPause()
        mapHelper.onPause()
    }

    public override fun onStop() {
        super.onStop()
        proximiioAPI.onStop()
        mapHelper.onStop()
    }

    public override fun onDestroy() {
        super.onDestroy()
        mapHelper.onDestroy()
        proximiioAPI.destroy()
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapHelper.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapHelper.onLowMemory()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        proximiioAPI.onActivityResult(requestCode, resultCode, data)
        mapHelper.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        proximiioAPI.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object{

        const val AUTH = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImlzcyI6ImRiYWJiMjAxLTFmYjYtNDM0NC1iOWE4LWI1MzY1MzM5Y2M0NSIsInR5cGUiOiJhcHBsaWNhdGlvbiIsImFwcGxpY2F0aW9uX2lkIjoiMDI0ZWNhYTktMDk0Yy00Y2Q2LTliYWMtNzU2NzE2NDA3ZjNiIn0.bNB0V5DR1JHt-u_FZeXrBoGdUgCIPk_XrWi9kB0eAZU"

    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        return super.onContextItemSelected(item)
    }

    override fun onTunaSelected(tunaModel: TunaModel) {
        val detailsFragment =
            TunaDetailsFragment.newInstance(tunaModel)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, detailsFragment, "tunaDetails")
            .addToBackStack(null)
            .commit()
    }
}
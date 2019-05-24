package com.usep.isdako

//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.google.android.material.navigation.NavigationView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.util.Log
import android.view.MenuItem
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationCompat
//import androidx.media.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.usep.isdako.Isdako.Companion.CHANNEL_GEOFENCE_EVENT_ID
//import com.mapbox.mapboxsdk.location.modes.CameraMode
//import com.mapbox.mapboxsdk.location.modes.RenderMode
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

    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var proximiioAPI: ProximiioAPI
    private lateinit var mapHelper: ProximiioMapHelper

    val TAG = "Isdako"

    private lateinit var drawer: androidx.drawerlayout.widget.DrawerLayout
//    private lateinit var database: DatabaseReference
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

        notificationManager = NotificationManagerCompat.from(this)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val preferences = getSharedPreferences("Isdako", MODE_PRIVATE)
//            if (!preferences.contains("notificationChannel")) {
//                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                if (notificationManager != null) {
//                    val channel = NotificationChannel(
//                        BackgroundListener.NOTIFICATION_CHANNEL_ID  ,
//                        BackgroundListener.NOTIFICATION_CHANNEL_NAME,
//                        NotificationManager.IMPORTANCE_HIGH
//                    )
//                    notificationManager.createNotificationChannel(channel)
//                    preferences.edit()
//                        .putBoolean("notificationChannel", true)
//                        .apply()
//                }
//            }
//        }


        val geofenceRef = FirebaseDatabase.getInstance().getReference("geofence")
        geofenceRef.keepSynced(true)

        val options = ProximiioOptions()
            .setNotificationMode(ProximiioOptions.NotificationMode.ENABLED)

        // Create our Proximi.io listener
        proximiioAPI = ProximiioAPI(TAG, this, options)
        proximiioAPI.setListener(object : ProximiioListener() {
            override fun geofenceEnter(geofence: ProximiioGeofence) {
    //            Log.d(TAG, "Geofence enter: " + geofence.metadatatoString())
//                geofenceRef.child(geofence.id).child("enterNotif")
//                geofenceRef.addChildEventListener{}

                geofenceRef.child(geofence.id).addValueEventListener(getGeofenceEnterInfo)
            }

            override fun geofenceExit(geofence: ProximiioGeofence, @Nullable dwellTime: Long?) {

                geofenceRef.child(geofence.id).addValueEventListener(getGeofenceExitInfo)
//                Log.d(TAG, "Geofence exit: " + geofence.name + ", dwell time: " + dwellTime.toString())
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
            .initialPositioningZoom(true)
            .initialPositioningZoomLevel(12F)
            .showFloorPlan(false)
            .showFloorIndicator(false)
            .floorChangeButtons(false)
            .showFloorNumber(false)
            .showGeofenceMarkers(true)
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
                supportFragmentManager
                    .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, reportFragment)
                    .addToBackStack(reportFragment.toString())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }
            R.id.navInformation -> {
                supportFragmentManager
                    .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
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

    val getGeofenceEnterInfo = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
//            val notificationMessage = dataSnapshot.value
//            Toast.makeText(applicationContext,dataSnapshot.value.toString(),Toast.LENGTH_LONG).show()
//            Log.e(TAG, "NotifyUSER" + dataSnapshot.value.toString())
            geofenceEventNotifChannel(dataSnapshot.child("enterNotif").value.toString(),"You are entering " + dataSnapshot.child("geofenceName").value.toString())
            // ...
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            // ...
        }
    }

    val getGeofenceExitInfo = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            geofenceEventNotifChannel(dataSnapshot.child("exitNotif").value.toString(),"Exited " + dataSnapshot.child("geofenceName").value.toString())
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            // ...
        }
    }

    fun geofenceEventNotifChannel(message: String, title: String){
        var notification = NotificationCompat.Builder(this, CHANNEL_GEOFENCE_EVENT_ID)
            .setSmallIcon(R.drawable.notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(false)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
            .build()

        notificationManager.notify(1, notification)
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

        const val AUTH = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImlzcyI6IjVjMTI5ZmY1LWVlZmItNGMwMi1hNGQ2LTM4YmUzODE5NDBmOCIsInR5cGUiOiJhcHBsaWNhdGlvbiIsImFwcGxpY2F0aW9uX2lkIjoiMjkyMTZjY2YtMTM2My00NDZkLWJmNTEtZDE4NzhjZDZiYTRjIn0.N6fZsQ57d3iWxVRbOF8tLJFpEOt8pmRHfNZzFC1PBgs"

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
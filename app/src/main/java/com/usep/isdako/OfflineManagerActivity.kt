//package com.usep.isdako
//
//import android.content.DialogInterface
//import android.support.v7.app.AppCompatActivity
//import android.os.Bundle
//import android.support.annotation.NonNull
//import android.support.v7.app.AlertDialog
//import android.view.View
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ProgressBar
//import android.widget.Toast
//import com.mapbox.mapboxsdk.Mapbox
//import com.mapbox.mapboxsdk.camera.CameraPosition
//import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
//import com.mapbox.mapboxsdk.maps.MapView
//import com.mapbox.mapboxsdk.maps.MapboxMap
//import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
//import com.mapbox.mapboxsdk.maps.Style
//import com.mapbox.mapboxsdk.offline.*
//import org.json.JSONObject
//
//import timber.log.Timber
//import java.nio.charset.StandardCharsets.UTF_8
//import java.util.*
//import java.util.ArrayList
//
//class OfflineManagerActivity:AppCompatActivity() {
//    // UI elements
//    private lateinit var mapView: MapView
//    private lateinit var map: MapboxMap
//    private lateinit var progressBar: ProgressBar
//    private lateinit var downloadButton: Button
//    private lateinit var listButton:Button
//    private var isEndNotified:Boolean = false
//    private var regionSelected:Int = 0
//
//    // Offline objects
//    private lateinit var offlineManager: OfflineManager
//    private lateinit var offlineRegion: OfflineRegion
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        // Mapbox access token is configured here. This needs to be called either in your application
//        // object or in the same activity which contains the mapview.
//        Mapbox.getInstance(this, getString(R.string.accessToken))
//        // This contains the MapView in XML and needs to be called after the access token is configured.
//        setContentView(R.layout.activity_offline_manager)
//        // Set up the MapView
//        mapView = findViewById(R.id.mapView)
//        mapView.onCreate(savedInstanceState)
//        mapView.getMapAsync { mapboxMap ->
//            map = mapboxMap
//            mapboxMap.setStyle(Style.MAPBOX_STREETS) {
//                // Assign progressBar for later use
//                progressBar = findViewById(R.id.progress_bar)
//                // Set up the offlineManager
//                offlineManager = OfflineManager.getInstance(this@OfflineManagerActivity)
//                // Bottom navigation bar button clicks are handled here.
//                // Download offline button
//                downloadButton = findViewById(R.id.download_button)
//                downloadButton.setOnClickListener { downloadRegionDialog() }
//                // List offline regions
//                listButton = findViewById(R.id.list_button)
//                listButton.setOnClickListener { downloadedRegionList() }
//            }
//        }
//    }
//    // Override Activity lifecycle methods
//    override fun onResume() {
//        super.onResume()
//        mapView.onResume()
//    }
//    override fun onStart() {
//        super.onStart()
//        mapView.onStart()
//    }
//    override fun onStop() {
//        super.onStop()
//        mapView.onStop()
//    }
//    override fun onPause() {
//        super.onPause()
//        mapView.onPause()
//    }
//    override fun onSaveInstanceState(outState:Bundle) {
//        super.onSaveInstanceState(outState)
//        mapView.onSaveInstanceState(outState)
//    }
//    override fun onDestroy() {
//        super.onDestroy()
//        mapView.onDestroy()
//    }
//    override fun onLowMemory() {
//        super.onLowMemory()
//        mapView.onLowMemory()
//    }
//    private fun downloadRegionDialog() {
//        // Set up download interaction. Display a dialog
//        // when the user clicks downloa    d button and require
//        // a user-provided region name
//        val builder = AlertDialog.Builder(this@OfflineManagerActivity)
//        val regionNameEdit = EditText(this@OfflineManagerActivity)
//        regionNameEdit.hint = getString(R.string.set_region_name_hint)
//        // Build the dialog box
//        builder.setTitle(getString(R.string.dialog_title))
//            .setView(regionNameEdit)
//            .setMessage(getString(R.string.dialog_message))
//            .setPositiveButton(getString(R.string.dialog_positive_button)
//            ) { dialog, which ->
//                val regionName = regionNameEdit.text.toString()
//                // Require a region name to begin the download.
//                // If the user-provided string is empty, display
//                // a toast message and do not begin download.
//                if (regionName.isEmpty()) {
//                    Toast.makeText(this@OfflineManagerActivity, getString(R.string.dialog_toast), Toast.LENGTH_SHORT).show()
//                } else {
//                    // Begin download process
//                    downloadRegion(regionName)
//                }
//            }
//            .setNegativeButton(getString(R.string.dialog_negative_button)
//            ) { dialog, which -> dialog.cancel() }
//        // Display the dialog
//        builder.show()
//    }
//    private fun downloadRegion(regionName:String) {
//        // Define offline region parameters, including bounds,
//        // min/max zoom, and metadata
//        // Start the progressBar
//        startProgress()
//        // Create offline definition using the current
//        // style and boundaries of visible map area
//        val styleUrl = map.style?.url
//        val bounds = map.projection.visibleRegion.latLngBounds
//        val minZoom = map.cameraPosition.zoom
//        val maxZoom = map.maxZoomLevel
//        val pixelRatio = this.resources.displayMetrics.density
//        val definition = OfflineTilePyramidRegionDefinition(
//            styleUrl, bounds, minZoom, maxZoom, pixelRatio)
//        // Build a JSONObject using the user-defined offline region title,
//        // convert it into string, and use it to create a metadata variable.
//        // The metadata variable will later be passed to createOfflineRegion()
//        var metadata:ByteArray?
//        try
//        {
//            val jsonObject = JSONObject()
//            jsonObject.put(JSON_FIELD_REGION_NAME, regionName)
//            val json = jsonObject.toString()
//            metadata = json.toByteArray(JSON_CHARSET)
//        }
//        catch (exception:Exception) {
//            Timber.e("Failed to encode metadata: %s", exception.message)
//            metadata = null
//        }
//        // Create the offline region and launch the download
//        if (metadata != null) {
//            offlineManager.createOfflineRegion(definition, metadata, object: OfflineManager.CreateOfflineRegionCallback {
//                override fun onCreate(offlineRegion:OfflineRegion) {
//                    Timber.d("Offline region created: %s", regionName)
//                    this@OfflineManagerActivity.offlineRegion = offlineRegion
//                    launchDownload()
//                }
//                override fun onError(error:String) {
//                    Timber.e("Error: %s", error)
//                }
//            })
//        }
//    }
//    private fun launchDownload() {
//        // Set up an observer to handle download progress and
//        // notify the user when the region is finished downloading
//        offlineRegion.setObserver(object: OfflineRegion.OfflineRegionObserver {
//            override fun onStatusChanged(status: OfflineRegionStatus) {
//                // Compute a percentage
//                val percentage = if (status.requiredResourceCount >= 0)
//                    (100.0 * status.completedResourceCount / status.requiredResourceCount)
//                else
//                    0.0
//                if (status.isComplete)
//                {
//                    // Download complete
//                    endProgress(getString(R.string.end_progress_success))
//                    return
//                }
//                else if (status.isRequiredResourceCountPrecise)
//                {
//                    // Switch to determinate state
//                    setPercentage(Math.round(percentage).toInt())
//                }
//                // Log what is being currently downloaded
//                Timber.d("%s/%s resources; %s bytes downloaded.",
//                    String to status.completedResourceCount,
//                    String to status.requiredResourceCount,
//                    String to status.completedResourceSize
//                )
//            }
//            override fun onError(error: OfflineRegionError) {
//                Timber.e("onError reason: %s", error.reason)
//                Timber.e("onError message: %s", error.message)
//            }
//            override fun mapboxTileCountLimitExceeded(limit:Long) {
//                Timber.e("Mapbox tile count limit exceeded: %s", limit)
//            }
//        })
//        // Change the region state
//        offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE)
//    }
//    private fun downloadedRegionList() {
//        // Build a region list when the user clicks the list button
//        // Reset the region selected int to 0
//        regionSelected = 0
//        // Query the DB asynchronously
//        offlineManager.listOfflineRegions(object: OfflineManager.ListOfflineRegionsCallback {
//            override fun onList(offlineRegions:Array<OfflineRegion>) {
//                // Check result. If no regions have been
//                // downloaded yet, notify user and return
//                if (offlineRegions.isEmpty())
//                {
//                    Toast.makeText(applicationContext, getString(R.string.toast_no_regions_yet), Toast.LENGTH_SHORT).show()
//                    return
//                }
//                // Add all of the region names to a list
//                val offlineRegionsNames = ArrayList<String>()
//                for (offlineRegion in offlineRegions)
//                {
//                    offlineRegionsNames.add(getRegionName(offlineRegion))
//                }
//                val items = offlineRegionsNames.toArray(arrayOfNulls<CharSequence>(offlineRegionsNames.size()))
//                // Build a dialog containing the list of regions
//                val dialog = AlertDialog.Builder(this@OfflineManagerActivity)
//                    .setTitle(getString(R.string.navigate_title))
//                    .setSingleChoiceItems(items, 0) { dialog, which ->
//                        // Track which region the user selects
//                        regionSelected = which
//                    }
//                    .setPositiveButton(getString(R.string.navigate_positive_button)
//                    ) { dialog, id ->
//                        Toast.makeText(this@OfflineManagerActivity, items[regionSelected], Toast.LENGTH_LONG).show()
//                        // Get the region bounds and zoom
//                        val bounds = (offlineRegions[regionSelected].definition as OfflineTilePyramidRegionDefinition).bounds
//                        val regionZoom = (offlineRegions[regionSelected].definition as OfflineTilePyramidRegionDefinition).minZoom
//                        // Create new camera position
//                        val cameraPosition = CameraPosition.Builder()
//                            .target(bounds.center)
//                            .zoom(regionZoom)
//                            .build()
//                        // Move camera to new position
//                        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
//                    }
//                    .setNeutralButton(getString(R.string.navigate_neutral_button_title)
//                    ) { dialog, id ->
//                        // Make progressBar indeterminate and
//                        // set it to visible to signal that
//                        // the deletion process has begun
//                        progressBar.isIndeterminate = true
//                        progressBar.visibility = View.VISIBLE
//                        // Begin the deletion process
//                        offlineRegions[regionSelected].delete(object: OfflineRegion.OfflineRegionDeleteCallback {
//                            override fun onDelete() {
//                                // Once the region is deleted, remove the
//                                // progressBar and display a toast
//                                progressBar.visibility = View.INVISIBLE
//                                progressBar.isIndeterminate = false
//                                Toast.makeText(
//                                    applicationContext, getString(R.string.toast_region_deleted),
//                                    Toast.LENGTH_LONG).show()
//                            }
//
//                            override fun onError(error:String) {
//                                progressBar.visibility = View.INVISIBLE
//                                progressBar.isIndeterminate = false
//                                Timber.e("Error: %s", error)
//                            }
//                        })
//                    }
//                    .setNegativeButton(getString(R.string.navigate_negative_button_title)
//                    ) { dialog, id ->
//                        // When the user cancels, don't do anything.
//                        // The dialog will automatically close
//                    }.create()
//                dialog.show()
//            }
//            override fun onError(error:String) {
//                Timber.e("Error: %s", error)
//            }
//        })
//    }
//    private fun getRegionName(offlineRegion:OfflineRegion):String {
//        // Get the region name from the offline region metadata
//        val regionName:String
//        regionName = try {
//            val metadata = offlineRegion.metadata
//            val json = String(metadata, JSON_CHARSET)
//            val jsonObject = JSONObject(json)
//            jsonObject.getString(JSON_FIELD_REGION_NAME)
//        } catch (exception:Exception) {
//            Timber.e("Failed to decode metadata: %s", exception.message)
//            String.format(getString(R.string.region_name), offlineRegion.id)
//        }
//        return regionName
//    }
//    // Progress bar methods
//    private fun startProgress() {
//        // Disable buttons
//        downloadButton.isEnabled = false
//        listButton.isEnabled = false
//        // Start and show the progress bar
//        isEndNotified = false
//        progressBar.isIndeterminate = true
//        progressBar.visibility = View.VISIBLE
//    }
//    private fun setPercentage(percentage:Int) {
//        progressBar.isIndeterminate = false
//        progressBar.progress = percentage
//    }
//    private fun endProgress(message:String) {
//        // Don't notify more than once
//        if (isEndNotified)
//        {
//            return
//        }
//        // Enable buttons
//        downloadButton.isEnabled = true
//        listButton.isEnabled = true
//        // Stop and hide the progress bar
//        isEndNotified = true
//        progressBar.isIndeterminate = false
//        progressBar.visibility = View.GONE
//        // Show a toast
//        Toast.makeText(this@OfflineManagerActivity, message, Toast.LENGTH_LONG).show()
//    }
//    companion object {
//        private val TAG = "OffManActivity"
//        // JSON encoding/decoding
//        val JSON_CHARSET = UTF_8
//        const val JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME"
//    }
//}
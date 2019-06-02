package com.usep.isdako

import android.graphics.*
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.gson.Gson
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.BubbleLayout
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression.*
import com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_BOTTOM
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import java.lang.ref.WeakReference
import java.util.*

class ReportMapActivity:AppCompatActivity(), OnMapReadyCallback, MapboxMap.OnMapClickListener {
    private lateinit var mapView:MapView
    private lateinit var mapboxMap:MapboxMap
    private lateinit var source:GeoJsonSource
    private lateinit var featureCollection:FeatureCollection
    private lateinit var mDatabase:DatabaseReference
    private lateinit var jsonDataTask:AsyncTask<Void, Void, FeatureCollection>
    private lateinit var feature:Array<Feature>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.accessToken))
        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_report_map)
        // Initialize the map view
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }
    override fun onMapReady(@NonNull mapboxMap:MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            mDatabase = FirebaseDatabase.getInstance().getReference("reports/")
            mDatabase.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(reportsSnapshot:DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    // ...
                    val jsonCombined = StringBuilder()
                    var delim = ""
                    for (reportSnapshot in reportsSnapshot.children) {
                        if (reportSnapshot.child("properties").child("species").value.toString() == "Albacore Tuna") {
                            val json = Gson().toJson(reportSnapshot.value)
                            jsonCombined.append(delim).append(json)
                            delim = ","
                        }
                    }
                    // Object object = reportsSnapshot.getValue(Object.class);
                    //
                    val json = ("{\n" +
                            " \"type\": \"FeatureCollection\",\n" +
                            " \"features\": [" +
                            jsonCombined.toString() +
                            " ]\n" +
                            "}")
                    Log.i("FirebaseJSON", json)
                    if (jsonDataTask == null) {
                        jsonDataTask = LoadGeoJsonDataTask(this@ReportMapActivity, json).execute()
                    }
                }

                override fun onCancelled(databaseError:DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w("Main", "loadPost:onCancelled", databaseError.toException())
                    // ...
                }
            })
            mapboxMap.addOnMapClickListener(this@ReportMapActivity)
        }
    }
    override fun onMapClick(@NonNull point:LatLng):Boolean {
        return handleClickIcon(mapboxMap.projection.toScreenLocation(point))
    }
    /**
     * Sets up all of the sources and layers needed for this example
     *
     * @param collection the FeatureCollection to set equal to the globally-declared FeatureCollection
     */
    fun setUpData(collection:FeatureCollection) {
        featureCollection = collection
        if (mapboxMap != null)
        {
            mapboxMap.getStyle { style->
                setupSource(style)
                setUpImage(style)
                setUpMarkerLayer(style)
                setUpInfoWindowLayer(style) }
        }
    }
    /**
     * Adds the GeoJSON source to the map
     */
    private fun setupSource(@NonNull loadedStyle:Style) {
        source = GeoJsonSource(GEOJSON_SOURCE_ID, featureCollection)
        loadedStyle.addSource(source)
    }
    /**
     * Adds the marker image to the map for use as a SymbolLayer icon
     */
    private fun setUpImage(@NonNull loadedStyle:Style) {
        loadedStyle.addImage(MARKER_IMAGE_ID, BitmapFactory.decodeResource(
            this.resources, R.drawable.red_marker))
    }
    /**
     * Updates the display of data on the map after the FeatureCollection has been modified
     */
    private fun refreshSource() {
        if (source != null && featureCollection != null)
        {
            source.setGeoJson(featureCollection)
        }
    }
    /**
     * Setup a layer with maki icons, eg. west coast city.
     */
    private fun setUpMarkerLayer(@NonNull loadedStyle:Style) {
        loadedStyle.addLayer(SymbolLayer(MARKER_LAYER_ID, GEOJSON_SOURCE_ID)
            .withProperties(
                iconImage(MARKER_IMAGE_ID),
                iconAllowOverlap(true),
                iconOffset(arrayOf(0f, -8f))
            ))
    }
    /**
     * Setup a layer with Android SDK call-outs
     * <p>
     * name of the feature is used as key for the iconImage
     * </p>
     */
    private fun setUpInfoWindowLayer(@NonNull loadedStyle:Style) {
        loadedStyle.addLayer(SymbolLayer(CALLOUT_LAYER_ID, GEOJSON_SOURCE_ID)
            .withProperties(
                /* show image with id title based on the value of the name feature property */
                iconImage("{time}"),
                /* set anchor of icon to bottom-left */
                iconAnchor(ICON_ANCHOR_BOTTOM),
                /* all info window and marker image to appear at the same time*/
                iconAllowOverlap(true),
                /* offset the info window to be above the marker */
                iconOffset(arrayOf(-2f, -28f))
            )
            /* add a filter to show only when selected feature property is true */
            .withFilter(eq((get(PROPERTY_SELECTED)), literal(true))))
    }
    /**
     * This method handles click events for SymbolLayer symbols.
     * <p>
     * When a SymbolLayer icon is clicked, we moved that feature to the selected state.
     * </p>
     *
     * @param screenPoint the point on screen clicked
     */
    private fun handleClickIcon(screenPoint:PointF):Boolean {
        val features = mapboxMap.queryRenderedFeatures(screenPoint, MARKER_LAYER_ID)
        if (!features.isEmpty())
        {
            val time = features[0].getStringProperty(PROPERTY_TIME)
            val featureList = featureCollection.features()
            for (i in 0 until featureList!!.size)
            {
                if (featureList[i].getStringProperty(PROPERTY_TIME) == time)
                {
                    if (featureSelectStatus(i))
                    {
                        setFeatureSelectState(featureList[i], false)
                    }
                    else
                    {
                        setSelected(i)
                    }
                }
            }
            return true
        }
        else
        {
            return false
        }
    }
    /**
     * Set a feature selected state.
     *
     * @param index the index of selected feature
     */
    private fun setSelected(index:Int) {
        val feature = featureCollection.features()!![index]
        setFeatureSelectState(feature, true)
        refreshSource()
    }
    /**
     * Selects the state of a feature
     *
     * @param feature the feature to be selected.
     */
    private fun setFeatureSelectState(feature:Feature, selectedState:Boolean) {
        feature.properties()!!.addProperty(PROPERTY_SELECTED, selectedState)
        refreshSource()
    }
    /**
     * Checks whether a Feature's boolean "selected" property is true or false
     *
     * @param index the specific Feature's index position in the FeatureCollection's list of Features.
     * @return true if "selected" is true. False if the boolean property is false.
     */
    private fun featureSelectStatus(index:Int):Boolean {
        return featureCollection.features()!![index].getBooleanProperty(PROPERTY_SELECTED)
    }
    /**
     * Invoked when the bitmaps have been generated from a view.
     */
    fun setImageGenResults(imageMap:HashMap<String, Bitmap>) {
        mapboxMap.getStyle { style->
            // calling addImages is faster as separate addImage calls for each bitmap.
            style.addImages(imageMap) }
    }
    /**
     * AsyncTask to load data from the assets folder.
     */
    private class LoadGeoJsonDataTask internal constructor(activity:ReportMapActivity, json:String):AsyncTask<Void, Void, FeatureCollection>() {
        // private static DatabaseReference mDatabase;
        private val activityRef:WeakReference<ReportMapActivity> = WeakReference(activity)
        private val json:String = json
        override fun doInBackground(vararg params:Void): FeatureCollection? {
            val activity = activityRef.get() ?: return null
            // String geoJson = loadGeoJsonFromAsset(activity, "my.geojson");
            return FeatureCollection.fromJson(json)
        }
        override fun onPostExecute(featureCollection:FeatureCollection) {
            super.onPostExecute(featureCollection)
            val activity = activityRef.get()
            if (featureCollection == null || activity == null)
            {
                return
            }
            // This example runs on the premise that each GeoJSON Feature has a "selected" property,
            // with a boolean value. If your data's Features don't have this boolean property,
            // add it to the FeatureCollection 's features with the following code:
            for (singleFeature in featureCollection.features()!!)
            {
                singleFeature.addBooleanProperty(PROPERTY_SELECTED, false)
            }
            activity.setUpData(featureCollection)
            GenerateViewIconTask(activity).execute(featureCollection)
        }
        companion object {
//            internal fun loadGeoJsonFromAsset(context:Context, filename:String):String {
//                try
//                {
//                    // Load GeoJSON file from local asset folder
//                    val `is` = context.getAssets().open(filename)
//                    val size = `is`.available()
//                    val buffer = ByteArray(size)
//                    `is`.read(buffer)
//                    `is`.close()
//                    Log.i("loadGeoJSON", String(buffer, "UTF-8"))
//                    return String(buffer, )
//                }
//                catch (exception:Exception) {
//                    throw RuntimeException(exception)
//                }
//            }
        }
    }
    /**
     * AsyncTask to generate Bitmap from Views to be used as iconImage in a SymbolLayer.
     * <p>
     * Call be optionally be called to update the underlying data source after execution.
     * </p>
     * <p>
     * Generating Views on background thread since we are not going to be adding them to the view hierarchy.
     * </p>
     */
    private class GenerateViewIconTask @JvmOverloads internal constructor(activity:ReportMapActivity, refreshSource:Boolean = false):AsyncTask<FeatureCollection, Void, HashMap<String, Bitmap>>() {
        private val viewMap = HashMap<String, View>()
        private val activityRef:WeakReference<ReportMapActivity> = WeakReference(activity)
        private var refreshSource:Boolean = false
        init{
            this.refreshSource = refreshSource
        }
        override fun doInBackground(vararg params:FeatureCollection): HashMap<String, Bitmap>? {
            val activity = activityRef.get()
            if (activity != null)
            {
                val imagesMap = HashMap<String, Bitmap>()
                val inflater = LayoutInflater.from(activity)
                val featureCollection = params[0]
                for (feature in featureCollection.features()!!)
                {
                    val bubbleLayout = inflater.inflate(R.layout.symbol_layer_info_window_layout_callout, null) as BubbleLayout
                    val time = feature.getStringProperty(PROPERTY_TIME)
                    val titleTextView : TextView = bubbleLayout.findViewById(R.id.info_window_title)
                    titleTextView.text = time
                    val species = feature.getStringProperty(PROPERTY_SPECIES)
                    val speciesTextView : TextView = bubbleLayout.findViewById(R.id.info_window_species)
                    speciesTextView.text = String.format(activity.getString(R.string.species), species)
                    val length = feature.getStringProperty(PROPERTY_LENGTH)
                    val lengthTextView : TextView = bubbleLayout.findViewById(R.id.info_window_length)
                    lengthTextView.text = String.format(activity.getString(R.string.length), length)
                    val weight = feature.getStringProperty(PROPERTY_WEIGHT)
                    val weightTextView : TextView = bubbleLayout.findViewById(R.id.info_window_weight)
                    weightTextView.text = String.format(activity.getString(R.string.weight), weight)
                    val quantity = feature.getStringProperty(PROPERTY_QUANTITY)
                    val quantityTextView : TextView = bubbleLayout.findViewById(R.id.info_window_quantity)
                    quantityTextView.text = String.format(activity.getString(R.string.quantity), quantity)
                    val measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    bubbleLayout.measure(measureSpec, measureSpec)
                    val measuredWidth = bubbleLayout.measuredWidth
                    bubbleLayout.arrowPosition = (measuredWidth / 2 - 5).toFloat()
                    val bitmap = SymbolGenerator.generate(bubbleLayout)
                    imagesMap[time] = bitmap
                    viewMap[time] = bubbleLayout
                }
                return imagesMap
            }
            else
            {
                return null
            }
        }
        override fun onPostExecute(bitmapHashMap:HashMap<String, Bitmap>) {
            super.onPostExecute(bitmapHashMap)
            val activity = activityRef.get()
            if (activity != null && bitmapHashMap != null)
            {
                activity.setImageGenResults(bitmapHashMap)
                if (refreshSource)
                {
                    activity.refreshSource()
                }
            }
            Toast.makeText(activity, R.string.tap_on_marker_instruction, Toast.LENGTH_SHORT).show()
        }
    }
    /**
     * Utility class to generate Bitmaps for Symbol.
     */
    private object SymbolGenerator {
        /**
         * Generate a Bitmap from an Android SDK View.
         *
         * @param view the View to be drawn to a Bitmap
         * @return the generated bitmap
         */
        internal fun generate(@NonNull view:View):Bitmap {
            val measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            view.measure(measureSpec, measureSpec)
            val measuredWidth = view.measuredWidth
            val measuredHeight = view.measuredHeight
            view.layout(0, 0, measuredWidth, measuredHeight)
            val bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
            bitmap.eraseColor(Color.TRANSPARENT)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            return bitmap
        }
    }
    public override fun onStart() {
        super.onStart()
        mapView.onStart()
    }
    public override fun onResume() {
        super.onResume()
        mapView.onResume()
    }
    public override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
    public override fun onStop() {
        super.onStop()
        mapView.onStop()
    }
    public override fun onSaveInstanceState(outState:Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
    public override fun onDestroy() {
        super.onDestroy()
        if (mapboxMap != null)
        {
            mapboxMap.removeOnMapClickListener(this)
        }
        mapView.onDestroy()
    }
    companion object {
        private const val GEOJSON_SOURCE_ID = "GEOJSON_SOURCE_ID"
        private const val MARKER_IMAGE_ID = "MARKER_IMAGE_ID"
        private const val MARKER_LAYER_ID = "MARKER_LAYER_ID"
        private const val CALLOUT_LAYER_ID = "CALLOUT_LAYER_ID"
        private const val PROPERTY_SELECTED = "selected"
        private const val PROPERTY_TIME = "time"
        private const val PROPERTY_SPECIES = "species"
        private const val PROPERTY_LENGTH = "length"
        private const val PROPERTY_WEIGHT = "weight"
        private const val PROPERTY_QUANTITY = "quantity"
    }
}

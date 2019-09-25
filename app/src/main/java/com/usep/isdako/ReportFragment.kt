package com.usep.isdako

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.usep.isdako.data.Geometry
import com.usep.isdako.data.Properties
import kotlinx.android.synthetic.main.fragment_report.*
import com.usep.isdako.data.Report
import kotlinx.android.synthetic.main.fragment_report.view.*
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ReportFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ReportFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ReportFragment : androidx.fragment.app.Fragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private lateinit var auth: FirebaseAuth

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var lat: Double = 1.0
    private var lng: Double = 1.0
    private var species: String = ""

//    var activity: Activity? = getActivity()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        FirebaseApp.initializeApp(activity!!.applicationContext)
        auth = FirebaseAuth.getInstance()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!.applicationContext)
        getLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_report, container, false)

        val spinner = view.findViewById(R.id.reportSpecies) as Spinner
//        val adapter = ArrayAdapter.createFromResource(
//            activity!!.baseContext,
//            R.array.names,
//            android.R.layout.simple_spinner_item
//        )
////        adapter.setDropDownViewSource()
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinner.adapter = adapter

//        spinner.onItemSelectedListener(this)
        view.reportSubmit.setOnClickListener {
            validateInput()
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(parent.getChildAt(0) != null) {
                    (parent.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                }
                val text : String = parent.getItemAtPosition(position) as String
                species = text
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

//        val submitButton = view.findViewById(R.id.reportSubmit) as Button

        return view
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(){
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                lat = location?.latitude!!
                lng = location?.longitude
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reportTime.setOnClickListener {
            showDatePicker()
        }

    }

    private fun showDatePicker() {
        val date = DatePickerFragment()
        /**
         * Set Up Current Date Into dialog
         */

        val calender = Calendar.getInstance()
        calender.timeZone = TimeZone.getTimeZone("Asia/Hong_Kong")
        val args = Bundle()
        args.putInt("year", calender.get(Calendar.YEAR))
        args.putInt("month", calender.get(Calendar.MONTH))
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH))
        date.arguments = args
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate)
        date.show(fragmentManager, "Date Picker")
    }

    private fun showTimePicker() {
        val time = TimePickerFragment()
        /**
         * Set Up Current Date Into dialog
         */

        val calender = Calendar.getInstance()
        calender.timeZone = TimeZone.getTimeZone("Asia/Hong_Kong")
        val args = Bundle()
        args.putInt("hour", calender.get(Calendar.HOUR))
        args.putInt("minute", calender.get(Calendar.MINUTE))
        time.arguments = args
        /**
         * Set Call back to capture selected date
         */
        time.setCallBack(ontime)
        time.show(fragmentManager, "Time Picker")
    }

    @SuppressLint("SetTextI18n")
    var ondate: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            var month: String = (monthOfYear + 1).toString()
            var day: String = dayOfMonth.toString()

            if(monthOfYear + 1 < 10) {
                month = "0" + (monthOfYear + 1).toString()
            }

            if(dayOfMonth < 10) {
                day = "0"+ (dayOfMonth).toString()
            }
            reportTime.setText(
                "$year-$month-$day"
            )
            showTimePicker()
        }

    @SuppressLint("SetTextI18n")
    var ontime: TimePickerDialog.OnTimeSetListener=
        TimePickerDialog.OnTimeSetListener { view, hour, minute ->
            var hr: String = (hour + 1).toString()
            var min: String = minute.toString()
            if(hour + 1 < 10) {
                hr = "0" + (hour + 1).toString()
            }

            if(minute + 1 < 10) {
                min = "0" + (minute).toString()
            }

            reportTime.setText(
                reportTime.text.toString() + " " + hr + ":" + min
            )
        }

//    override fun onNothingSelected(parent: AdapterView<*>?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        var text : String = parent!!.getItemAtPosition(position) as String
//        Toast.makeText(parent!!.context, text, Toast.LENGTH_SHORT).show()
//    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ReportFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
        fun newInstance() =
            ReportFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun validateInput() {
        if (reportTime.text.isEmpty()) {
            reportTime.error = "Please enter time"
            reportTime.requestFocus()
            return
        } else if (reportLength.text.isEmpty()) {
            reportLength.error = "Please enter fish length"
            reportLength.requestFocus()
            return
        } else if (reportWeight.text.isEmpty()) {
            reportWeight.error = "Please enter fish weight"
            reportWeight.requestFocus()
            return
        } else if (reportQuantity.text.isEmpty()) {
            reportQuantity.error = "Please enter fish weight"
            reportQuantity.requestFocus()
            return
        } else if (reportSpecies.toString().isEmpty()) {
            return
        }
        addReportToDatabase(
            reportTime.text.toString(),
            reportLength.text.toString().toFloat(),
            reportWeight.text.toString().toFloat(),
            reportQuantity.text.toString().toInt())
    }

    private fun addReportToDatabase(
        time: String,
        length: Float,
        weight: Float,
        quantity: Int
    ) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().reference
        val geometry = Geometry(listOf(lng, lat),"Point")
        val properties = Properties(uid, time, length, weight, quantity, species)
        val report = Report("Feature",properties,geometry)
//        val report = Report(uid, lat, lng, time, length, weight, quantity, species)
        ref.child("reports/").push().setValue(report)
            .addOnSuccessListener {

            }
        closeFragment()
        Toast.makeText(activity, "Report Submitted Successfully", Toast.LENGTH_SHORT).show()
    }

    private fun closeFragment() {
        activity!!.supportFragmentManager.popBackStackImmediate()
    }
//    var newPostRef = postsRef.push({
//        author: "gracehop",
//        title: "Announcing COBOL, a New Programming Language"
//    });
}
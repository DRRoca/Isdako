package com.usep.isdako

import android.app.DatePickerDialog
import android.os.Bundle
import android.annotation.SuppressLint
import android.app.Dialog
import androidx.fragment.app.DialogFragment


class DatePickerFragment : DialogFragment() {
    lateinit var ondateSet: DatePickerDialog.OnDateSetListener
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0

    fun setCallBack(ondate: DatePickerDialog.OnDateSetListener) {
        ondateSet = ondate
    }

    @SuppressLint("NewApi")
    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        year = args!!.getInt("year")
        month = args.getInt("month")
        day = args.getInt("day")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return DatePickerDialog(activity, ondateSet, year, month, day)
    }
}
package com.usep.isdako

import android.app.DatePickerDialog
import android.os.Bundle
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.TimePickerDialog
import androidx.fragment.app.DialogFragment


class TimePickerFragment : DialogFragment() {
    lateinit var ontimeSet: TimePickerDialog.OnTimeSetListener
    private var hour: Int = 0
    private var minute: Int = 0

    fun setCallBack(ontime: TimePickerDialog.OnTimeSetListener) {
        ontimeSet = ontime
    }

    @SuppressLint("NewApi")
    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        hour = args!!.getInt("hour")
        minute = args.getInt("minute")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return TimePickerDialog(activity, ontimeSet, hour, minute, false)
    }
}
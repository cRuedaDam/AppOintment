package com.example.appointment.commerce.view.fragments

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar
import java.util.Locale

class TimePickerDialogFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    var buttonToUpdate: Button? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return TimePickerDialog(activity, this, hour, minute, true)
    }
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
        buttonToUpdate?.text = formattedTime
    }
}
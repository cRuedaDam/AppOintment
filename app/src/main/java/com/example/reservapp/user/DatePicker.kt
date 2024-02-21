package com.example.reservapp.user

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar


/**
 * clase que mostrará la elección de fecha que tiene un parametro que tiene tres valores día, mes y año.
 * Extiende de DialogFragment que es la vista del calendario.
 * Implementa el DatePickerDialog que es lo que va a psar cuando se seleccione la fecha.
 */
class DatePicker(val listener:(day:Int, month:Int, year:Int) -> Unit):DialogFragment(), DatePickerDialog.OnDateSetListener{
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        listener(dayOfMonth, month, year)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //objeto para elegir dia, mes o año para trabajar con fechas
        val calendar = Calendar.getInstance()

        //cada uno coge el día, mes y año actual.
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        //instancia del calendario tipo DatePickerDialog convierte la activity en contexto, el listener que se llama cuando el usuario seleccione una fecha, el listener(OnDateSetListener)
        val picker = DatePickerDialog(activity as Context, this, year, month, day)

        picker.datePicker.minDate = System.currentTimeMillis() - 1000 //el día min es el día actual y no se podrá seleccionar fechas pasadas

        //picker.datePicker.
        //devuelve la fecha
        return picker
    }
}
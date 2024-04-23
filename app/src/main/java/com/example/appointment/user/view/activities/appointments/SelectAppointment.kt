package com.example.appointment.user.view.activities.appointments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.appointment.R

class SelectAppointment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_appointment)

        val commerceName = intent.getStringExtra("COMMERCE_NAME")

        if (commerceName != null) {
            Log.d("Nombre del comercio", commerceName)
        }
    }
}
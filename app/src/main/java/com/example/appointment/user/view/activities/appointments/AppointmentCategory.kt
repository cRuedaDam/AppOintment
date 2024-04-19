package com.example.appointment.user.view.activities.appointments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.appointment.R
import com.example.appointment.databinding.ActivityAppointmentCategoryBinding
import com.example.appointment.user.view.activities.home.UserHome
import com.example.appointment.user.view.activities.menu.UserMenu

class AppointmentCategory : AppCompatActivity() {

    private lateinit var binding: ActivityAppointmentCategoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAppointmentCategoryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        goToHome()
    }

    private fun goToHome() {
        binding.backArrowIcon.setOnClickListener {
            val intent = Intent(this@AppointmentCategory, UserHome::class.java)
            startActivity(intent)
        }
    }
}
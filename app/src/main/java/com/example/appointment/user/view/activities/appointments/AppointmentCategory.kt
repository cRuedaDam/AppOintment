package com.example.appointment.user.view.activities.appointments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.appointment.databinding.ActivityAppointmentCategoryBinding
import com.example.appointment.user.view.activities.home.UserHome

class AppointmentCategory : AppCompatActivity() {

    private lateinit var binding: ActivityAppointmentCategoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAppointmentCategoryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        goToHome()
        chooseAnItem()
    }

    private fun goToHome() {
        binding.backArrowIcon.setOnClickListener {
            val intent = Intent(this@AppointmentCategory, UserHome::class.java)
            startActivity(intent)
        }
    }

    private fun chooseAnItem() {
        binding.lyEsthetics.setOnClickListener { handleCategoryClick("Estética") }
        binding.lyHair.setOnClickListener { handleCategoryClick("Peluquería") }
        binding.lyFisio.setOnClickListener { handleCategoryClick("Fisioterapia") }
        binding.lyVet.setOnClickListener { handleCategoryClick("Peluquería canina") }
        binding.lyDoc.setOnClickListener { handleCategoryClick("Salud") }
        binding.lyNutri.setOnClickListener { handleCategoryClick("Nutrición") }
    }

    private fun handleCategoryClick(category: String) {
        val intent = Intent(this, AppointmentChooseCommerce::class.java)
        intent.putExtra("CATEGORY", category)
        startActivity(intent)
    }
}
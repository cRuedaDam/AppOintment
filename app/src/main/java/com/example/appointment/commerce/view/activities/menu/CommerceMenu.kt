package com.example.appointment.commerce.view.activities.menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.appointment.commerce.view.activities.home.CommerceHome
import com.example.appointment.commerce.view.activities.menu.editProfile.CommerceEditProfile
import com.example.appointment.commerce.view.activities.menu.employees.CommerceEmployees
import com.example.appointment.commerce.view.activities.menu.specialities.CommerceSpecialities
import com.example.appointment.commerce.view.fragments.CustomAlertDialog
import com.example.appointment.common.view.Login
import com.example.appointment.databinding.ActivityCommerceMenuBinding

class CommerceMenu : AppCompatActivity() {
    private lateinit var binding: ActivityCommerceMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCommerceMenuBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        goToEditProfile()
        goToEmployees()
        goToCommerceHome()
        goToSpecialities()
        commerceLogOut()
    }
    private fun commerceLogOut() {
        binding.txtLogout.setOnClickListener{
            CustomAlertDialog.showAlertDialog(
                this@CommerceMenu,
                "¿Estás seguro de querer cerrar sesión?",
                onAccept = {
                    val intent = Intent(this@CommerceMenu, Login::class.java)
                    startActivity(intent)
                },
                onCancel = {

                }
            )
        }
    }

    private fun goToEditProfile() {
        binding.txtEditProfile.setOnClickListener {
            val intent = Intent(this@CommerceMenu, CommerceEditProfile::class.java)
            startActivity(intent)
        }
    }

    private fun goToCommerceHome() {
        binding.ivBackArrow.setOnClickListener {
            val intent = Intent(this@CommerceMenu, CommerceHome::class.java)
            startActivity(intent)
        }
    }

    private fun goToEmployees() {
        binding.txtEmployees.setOnClickListener {
            val intent = Intent(this@CommerceMenu, CommerceEmployees::class.java)
            startActivity(intent)
        }
    }

    private fun goToSpecialities() {
        binding.txtSpecialities.setOnClickListener {
            val intent = Intent(this@CommerceMenu, CommerceSpecialities::class.java)
            startActivity(intent)
        }
    }
}
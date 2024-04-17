package com.example.appointment.commerce.view.activities.menu.editProfile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.appointment.R
import com.example.appointment.commerce.view.activities.menu.CommerceMenu
import com.example.appointment.commerce.view.fragments.CustomAlertDialog
import com.example.appointment.commerce.viewModel.FireBaseManager
import com.example.appointment.common.view.Login
import com.example.appointment.databinding.ActivityCommerceEditProfileBinding
import com.google.firebase.auth.FirebaseAuth

class CommerceEditProfile : AppCompatActivity() {
    private lateinit var binding: ActivityCommerceEditProfileBinding
    private var firebaseManager = FireBaseManager()
    private val currentCommerce = FirebaseAuth.getInstance().currentUser
    private val commerceId = currentCommerce?.uid.toString()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCommerceEditProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fillListView()
        goToMenu()
    }

    private fun fillListView() {
        var listViewItems = binding.listViewEditProfile
        var itemNames = arrayOf(
            "Cambiar nombre",
            "Cambiar direción",
            "Cambiar E-Mail",
            "Cambiar número de teléfono",
            "Cambiar contraseña",
            "Eliminar cuenta"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemNames)
        listViewItems.adapter = adapter

        listViewItems.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> startActivity(Intent(this, ChangeCommerceName::class.java))
                1 -> startActivity(Intent(this, ChangeCommerceAddress::class.java))
                2 -> startActivity(Intent(this, ChangeCommerceEmail::class.java))
                3 -> startActivity(Intent(this, ChangeCommercePhone::class.java))
                4 -> startActivity(Intent(this, ChangeCommercePassword::class.java))
                5 -> CustomAlertDialog.showAlertDialog(
                    this@CommerceEditProfile,
                    "¿Estás seguro de querer eliminar la cuenta definitivamente?",
                    onAccept = {
                        firebaseManager.deleteCommerce(commerceId)
                        Toast.makeText(
                            this,
                            "La cuenta ha sido eliminada correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@CommerceEditProfile, Login::class.java)
                        startActivity(intent)
                    },
                    onCancel = {

                    }
                )
            }
        }

    }

    private fun goToMenu() {
        binding.ivBackArrow.setOnClickListener {
            val intent = Intent(this@CommerceEditProfile, CommerceMenu::class.java)
            startActivity(intent)
        }
    }
}


package com.example.appointment.commerce.view.activities.menu.editProfile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.appointment.R
import com.example.appointment.commerce.view.activities.menu.CommerceMenu
import com.example.appointment.commerce.view.fragments.CustomAlertDialog
import com.example.appointment.commerce.viewModel.FireBaseManager
import com.example.appointment.databinding.ActivityChangeCommercePhoneBinding
import com.google.firebase.auth.FirebaseAuth

class ChangeCommercePhone : AppCompatActivity() {
    private lateinit var binding: ActivityChangeCommercePhoneBinding
    private var firestore = FireBaseManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChangeCommercePhoneBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        goToEditProfile()
        changePhone()

    }

    private fun goToEditProfile() {
        binding.ivBackArrow.setOnClickListener {
            val intent = Intent(this@ChangeCommercePhone, CommerceEditProfile::class.java)
            startActivity(intent)
        }
    }

    private fun changePhone() {
        binding.btnChangePhone.setOnClickListener {
            val newCommercePhone = binding.edtNewPhone.text.toString()
            val currentCommerce = FirebaseAuth.getInstance().currentUser
            val commerceId = currentCommerce?.uid
            CustomAlertDialog.showAlertDialog(
                this@ChangeCommercePhone,
                "¿Estás seguro de que quieres cambiar el teléfono del comercio?",
                onAccept = {
                    try {
                        if (commerceId != null) {
                            firestore.changeCommercePhone(commerceId, newCommercePhone)
                        }
                        Toast.makeText(
                            this@ChangeCommercePhone,
                            "El teléfono del comercio ha sido cambiado correctamente.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent =
                            Intent(this@ChangeCommercePhone, CommerceMenu::class.java)
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@ChangeCommercePhone,
                            "Ha habido un problema al intentar cambiar el teléfono del comercio.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                onCancel = {

                }
            )
        }
    }
}
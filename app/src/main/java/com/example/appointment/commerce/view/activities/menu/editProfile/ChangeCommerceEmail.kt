package com.example.appointment.commerce.view.activities.menu.editProfile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.appointment.R
import com.example.appointment.commerce.view.activities.menu.CommerceMenu
import com.example.appointment.commerce.view.fragments.CustomAlertDialog
import com.example.appointment.commerce.viewModel.FireBaseManager
import com.example.appointment.databinding.ActivityChangeCommerceEmailBinding
import com.google.firebase.auth.FirebaseAuth

class ChangeCommerceEmail : AppCompatActivity() {
    private lateinit var binding: ActivityChangeCommerceEmailBinding
    private var firestore = FireBaseManager()
    val currentCommerce = FirebaseAuth.getInstance().currentUser
    val commerceId = currentCommerce?.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChangeCommerceEmailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        goToEditProfile()
        changeEmail()
    }

    private fun goToEditProfile() {
        binding.ivBackArrow.setOnClickListener {
            val intent = Intent(this@ChangeCommerceEmail, CommerceEditProfile::class.java)
            startActivity(intent)
        }
    }

    private fun changeEmail() {
        binding.btnChangeEmail.setOnClickListener {
            val newCommerceEmail = binding.edtNewEmail.text.toString()
            CustomAlertDialog.showAlertDialog(
                this@ChangeCommerceEmail,
                "¿Estás seguro de que quieres cambiar el email del comercio?",
                onAccept = {
                    try {
                        if (commerceId != null) {
                            firestore.changeCommerceEmail(commerceId, newCommerceEmail)
                            currentCommerce!!.verifyBeforeUpdateEmail(newCommerceEmail)
                        }
                        Toast.makeText(
                            this@ChangeCommerceEmail,
                            "El email del comercio ha sido cambiado correctamente.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent =
                            Intent(this@ChangeCommerceEmail, CommerceMenu::class.java)
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@ChangeCommerceEmail,
                            "Ha habido un problema al intentar cambiar el email del comercio.",
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
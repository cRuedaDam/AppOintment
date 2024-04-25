package com.example.appointment.user.view.activities.menu.editProfile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.appointment.R
import com.example.appointment.commerce.view.activities.menu.CommerceMenu
import com.example.appointment.commerce.view.activities.menu.editProfile.CommerceEditProfile
import com.example.appointment.commerce.view.fragments.CustomAlertDialog
import com.example.appointment.commerce.viewModel.FireBaseManager
import com.example.appointment.databinding.ActivityChangeCommercePhoneBinding
import com.example.appointment.user.view.activities.menu.UserMenu
import com.google.firebase.auth.FirebaseAuth

class ChangeUserPhone : AppCompatActivity() {
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
            val intent = Intent(this@ChangeUserPhone, UserEditProfile::class.java)
            startActivity(intent)
        }
    }

    private fun changePhone() {
        binding.btnChangePhone.setOnClickListener {
            val newUserPhone = binding.edtNewPhone.text.toString()
            val currentUser = FirebaseAuth.getInstance().currentUser
            val userId = currentUser?.uid
            CustomAlertDialog.showAlertDialog(
                this@ChangeUserPhone,
                "¿Estás seguro de que quieres cambiar el teléfono?",
                onAccept = {
                    try {
                        if (userId != null) {
                            firestore.changeUserPhone(userId, newUserPhone)
                        }
                        Toast.makeText(
                            this@ChangeUserPhone,
                            "El teléfono ha sido cambiado correctamente.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent =
                            Intent(this@ChangeUserPhone, UserMenu::class.java)
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@ChangeUserPhone,
                            "Ha habido un problema al intentar cambiar el teléfono.",
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
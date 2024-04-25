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
import com.example.appointment.databinding.ActivityChangeCommerceEmailBinding
import com.example.appointment.user.view.activities.menu.UserMenu
import com.google.firebase.auth.FirebaseAuth

class ChangeUserEmail : AppCompatActivity() {
    private lateinit var binding: ActivityChangeCommerceEmailBinding
    private var firestore = FireBaseManager()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChangeCommerceEmailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        goToEditProfile()
        changeEmail()
    }

    private fun goToEditProfile() {
        binding.ivBackArrow.setOnClickListener {
            val intent = Intent(this@ChangeUserEmail, UserEditProfile::class.java)
            startActivity(intent)
        }
    }

    private fun changeEmail() {
        binding.btnChangeEmail.setOnClickListener {
            val newUserEmail = binding.edtNewEmail.text.toString()
            CustomAlertDialog.showAlertDialog(
                this@ChangeUserEmail,
                "¿Estás seguro de que quieres cambiar el email?",
                onAccept = {
                    try {
                        if (userId != null) {
                            firestore.changeUserEmail(userId, newUserEmail)
                            currentUser!!.verifyBeforeUpdateEmail(newUserEmail)
                        }
                        Toast.makeText(
                            this@ChangeUserEmail,
                            "El email ha sido cambiado correctamente.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent =
                            Intent(this@ChangeUserEmail, UserMenu::class.java)
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@ChangeUserEmail,
                            "Ha habido un problema al intentar cambiar el email.",
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
package com.example.appointment.commerce.view.activities.menu.editProfile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.appointment.commerce.view.activities.menu.CommerceMenu
import com.example.appointment.commerce.view.fragments.CustomAlertDialog
import com.example.appointment.databinding.ActivityChangeCommercePasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ChangeCommercePassword : AppCompatActivity() {
    private lateinit var binding: ActivityChangeCommercePasswordBinding
    private var newPassword = ""
    private var confirmPassword = ""
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChangeCommercePasswordBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        goToEditProfile()
        changePassword()
    }

    private fun goToEditProfile() {
        binding.ivBackArrow.setOnClickListener {
            val intent = Intent(this@ChangeCommercePassword, CommerceEditProfile::class.java)
            startActivity(intent)
        }
    }

    private fun changePassword() {
        binding.btnChangePassword.setOnClickListener {
            CustomAlertDialog.showAlertDialog(
                this@ChangeCommercePassword,
                "¿Estás seguro de que quieres cambiar la contraseña?",
                onAccept = {
                    try {
                        newPassword = binding.edtNewPassword.text.toString()
                        confirmPassword = binding.edtConfirmPassword.text.toString()

                        if (newPassword == confirmPassword) {
                            currentUser?.updatePassword(newPassword)
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d("TAG", "Contraseña cambiada con éxito")
                                        Toast.makeText(
                                            this@ChangeCommercePassword,
                                            "La contraseña ha sido cambiada correctamente",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        // Redirigir al usuario después de cambiar la contraseña
                                        val intent = Intent(
                                            this@ChangeCommercePassword,
                                            CommerceMenu::class.java
                                        )
                                        startActivity(intent)
                                    } else {
                                        Log.e(
                                            "TAG",
                                            "Error al cambiar la contraseña",
                                            task.exception
                                        )
                                        Toast.makeText(
                                            this@ChangeCommercePassword,
                                            "Ha habido un problema al intentar cambiar la contraseña",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        } else {
                            Toast.makeText(
                                this@ChangeCommercePassword,
                                "Las contraseñas no coinciden",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Log.e("TAG", "Error al cambiar la contraseña", e)
                        Toast.makeText(
                            this@ChangeCommercePassword,
                            "Ha habido un problema al intentar cambiar la contraseña",
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
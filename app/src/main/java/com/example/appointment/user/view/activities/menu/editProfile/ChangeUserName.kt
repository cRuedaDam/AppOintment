package com.example.appointment.user.view.activities.menu.editProfile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.appointment.R
import com.example.appointment.commerce.view.activities.menu.CommerceMenu
import com.example.appointment.commerce.view.activities.menu.editProfile.CommerceEditProfile
import com.example.appointment.commerce.view.fragments.CustomAlertDialog
import com.example.appointment.commerce.viewModel.FireBaseManager
import com.example.appointment.databinding.ActivityChangeCommerceNameBinding
import com.example.appointment.databinding.ActivityChangeUserNameBinding
import com.example.appointment.user.view.activities.menu.UserMenu
import com.google.firebase.auth.FirebaseAuth

class ChangeUserName : AppCompatActivity() {
    private lateinit var binding: ActivityChangeUserNameBinding
    private var firestore = FireBaseManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChangeUserNameBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        goToEditProfile()
        changeName()
        Log.d("Current Activity: ", "ChangeUserName")
    }

    private fun goToEditProfile() {
        binding.ivBackArrow.setOnClickListener {
            val intent = Intent(this@ChangeUserName, UserEditProfile::class.java)
            startActivity(intent)
        }
    }

    private fun changeName() {
        binding.btnChangeName.setOnClickListener {
            val newUsereName = binding.edtNewName.text.toString()
            val newUserLastName = binding.edtNewLastName.text.toString()
            val currentUser = FirebaseAuth.getInstance().currentUser
            val userId = currentUser?.uid
            CustomAlertDialog.showAlertDialog(
                this@ChangeUserName,
                "¿Estás seguro de que quieres cambiar el nombre del usuario?",
                onAccept = {
                    try {
                        if (userId != null) {
                            firestore.changeUserName(userId, newUsereName)
                            firestore.changeUserLastName(userId, newUserLastName)
                        }
                        Toast.makeText(
                            this@ChangeUserName,
                            "El nombre del usuario ha sido cambiado correctamente.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent =
                            Intent(this@ChangeUserName, UserMenu::class.java)
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@ChangeUserName,
                            "Ha habido un problema al intentar cambiar el nombre del usuario.",
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
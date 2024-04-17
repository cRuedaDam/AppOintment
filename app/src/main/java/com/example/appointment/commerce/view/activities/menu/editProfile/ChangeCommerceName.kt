package com.example.appointment.commerce.view.activities.menu.editProfile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.appointment.R
import com.example.appointment.commerce.view.activities.menu.CommerceMenu
import com.example.appointment.commerce.view.fragments.CustomAlertDialog
import com.example.appointment.commerce.viewModel.FireBaseManager
import com.example.appointment.databinding.ActivityChangeCommerceNameBinding
import com.google.firebase.auth.FirebaseAuth

class ChangeCommerceName : AppCompatActivity() {
    private lateinit var binding: ActivityChangeCommerceNameBinding
    private var firestore = FireBaseManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChangeCommerceNameBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        goToEditProfile()
        changeName()
    }

    private fun goToEditProfile() {
        binding.ivBackArrow.setOnClickListener {
            val intent = Intent(this@ChangeCommerceName, CommerceEditProfile::class.java)
            startActivity(intent)
        }
    }

    private fun changeName() {
        binding.btnChangeName.setOnClickListener {
            val newCommerceName = binding.edtNewName.text.toString()
            val currentCommerce = FirebaseAuth.getInstance().currentUser
            val commerceId = currentCommerce?.uid
            CustomAlertDialog.showAlertDialog(
                this@ChangeCommerceName,
                "¿Estás seguro de que quieres cambiar el nombre del comercio?",
                onAccept = {
                    try {
                        if (commerceId != null) {
                            firestore.changeCommerceName(commerceId, newCommerceName)
                        }
                        Toast.makeText(
                            this@ChangeCommerceName,
                            "El nombre del comercio ha sido cambiado correctamente.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent =
                            Intent(this@ChangeCommerceName, CommerceMenu::class.java)
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@ChangeCommerceName,
                            "Ha habido un problema al intentar cambiar el nombre del comercio.",
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
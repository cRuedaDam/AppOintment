package com.example.appointment.commerce.view.activities.menu.editProfile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.appointment.R
import com.example.appointment.commerce.view.activities.menu.CommerceMenu
import com.example.appointment.commerce.view.fragments.CustomAlertDialog
import com.example.appointment.commerce.viewModel.FireBaseManager
import com.example.appointment.databinding.ActivityChangeCommerceAddressBinding
import com.google.firebase.auth.FirebaseAuth

class ChangeCommerceAddress : AppCompatActivity() {
    private lateinit var binding: ActivityChangeCommerceAddressBinding
    private var firebase = FireBaseManager()
    private val currentCommerce = FirebaseAuth.getInstance().currentUser
    private val commerceId = currentCommerce?.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChangeCommerceAddressBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        goToEditProfile()
        changeCommerceAddress()
    }

    private fun goToEditProfile() {
        binding.ivBackArrow.setOnClickListener {
            val intent = Intent(this@ChangeCommerceAddress, CommerceEditProfile::class.java)
            startActivity(intent)
        }
    }

    private fun changeCommerceAddress() {
        binding.btnChangeAddress.setOnClickListener {

            val newStreetType = binding.edtStreetType.text.toString()
            val newStreetName = binding.edtStreetName.text.toString()
            val newStreetNumber = binding.edtStreetNumber.text.toString()
            val newCity = binding.edtCity.text.toString()
            val newState = binding.edtState.text.toString()
            val newPostalCode = binding.edtPostalCode.text.toString()

            CustomAlertDialog.showAlertDialog(
                this@ChangeCommerceAddress,
                "¿Estás seguro de que quieres cambiar la dirección del comercio?",
                onAccept = {
                    try {
                        if (commerceId != null) {
                            firebase.changeCommerceAddress(
                                commerceId,
                                newStreetType,
                                newStreetName,
                                newStreetNumber,
                                newCity,
                                newState,
                                newPostalCode
                            )
                        }
                        Toast.makeText(
                            this@ChangeCommerceAddress,
                            "La dirección del comercio ha sido cambiado correctamente.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent =
                            Intent(this@ChangeCommerceAddress, CommerceMenu::class.java)
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@ChangeCommerceAddress,
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
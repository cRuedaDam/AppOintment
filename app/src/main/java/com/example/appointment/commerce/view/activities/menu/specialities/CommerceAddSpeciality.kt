package com.example.appointment.commerce.view.activities.menu.specialities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.appointment.R
import com.example.appointment.commerce.model.Speciality
import com.example.appointment.commerce.viewModel.FireBaseManager
import com.example.appointment.databinding.ActivityCommerceAddSpecialityBinding
import com.example.reservapp.common.viewModel.Alerts
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class CommerceAddSpeciality : AppCompatActivity() {
    private lateinit var binding: ActivityCommerceAddSpecialityBinding
    private lateinit var btnAddSpeciality: Button
    private val fireBaseManager = FireBaseManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCommerceAddSpecialityBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        goToSpecialities()
        addSpeciality()
    }

    private fun goToSpecialities() {
        binding.ivBackArrow.setOnClickListener {
            val intent = Intent(this@CommerceAddSpeciality, CommerceSpecialities::class.java)
            startActivity(intent)
        }
    }

    private fun addSpeciality() {

        btnAddSpeciality = binding.btnAddSpeciality

        btnAddSpeciality.setOnClickListener {

            val specialityName = binding.edtSpecialityName.text.toString()
            val specialityTimeRequired = binding.edtSpecialityTimeRequired.text.toString()
            val specialityPrice = binding.edtSpecialityPrice.text.toString()

            val speciality = Speciality(
                name = specialityName,
                timeRequired = specialityTimeRequired,
                price = specialityPrice
            )

            val currentCommerce = FirebaseAuth.getInstance().currentUser
            val commerceId = currentCommerce?.uid
            val alert = Alerts()

            if (commerceId != null) {
                alert.showLoading(this@CommerceAddSpeciality, "Espere por favor...")
                try {
                    MainScope().launch(Dispatchers.IO) {
                        fireBaseManager.addSpeciality(speciality, commerceId)
                        launch(Dispatchers.Main) {
                            Toast.makeText(
                                this@CommerceAddSpeciality,
                                "La especialidad \"$specialityName\" ha sido agregada correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@CommerceAddSpeciality, CommerceSpecialities::class.java)
                            startActivity(intent)
                            alert.hideLoading()
                        }
                    }

                } catch (e: Exception) {
                    alert.hideLoading()
                    Toast.makeText(
                        this@CommerceAddSpeciality,
                        "No ha sio posible añadir la especialidad a la Base de datos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this@CommerceAddSpeciality,
                    "No ha sio posible añadir la especialidad a la Base de datos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
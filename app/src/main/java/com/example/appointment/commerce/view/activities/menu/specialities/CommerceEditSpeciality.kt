package com.example.appointment.commerce.view.activities.menu.specialities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.Toast
import com.example.appointment.R
import com.example.appointment.commerce.view.activities.menu.CommerceMenu
import com.example.appointment.commerce.view.fragments.CustomAlertDialog
import com.example.appointment.commerce.viewModel.FireBaseManager
import com.example.appointment.databinding.ActivityCommerceEditSpecialityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CommerceEditSpeciality : AppCompatActivity() {
    private lateinit var binding: ActivityCommerceEditSpecialityBinding
    private val fireBaseManager = FireBaseManager()
    private val db = FirebaseFirestore.getInstance()
    private val currentCommerce = FirebaseAuth.getInstance().currentUser
    private val commerceId = currentCommerce?.uid
    private lateinit var btnEditSpeciality: Button
    private lateinit var newData: Map<String, String>
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCommerceEditSpecialityBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        deleteSpeciality()
        goToMenu()
        fillSpecialityData()
        editSpeciality()
    }

    private fun editSpeciality() {
        btnEditSpeciality = binding.btnChangeSpeciality

        btnEditSpeciality.setOnClickListener {
            CustomAlertDialog.showAlertDialog(
                this@CommerceEditSpeciality,
                "¿Estás seguro de que quieres guardar los nuevos datos del empleado?",
                onAccept = {
                    newData = hashMapOf(
                        "name" to binding.edtSpecialityName.text.toString(),
                        "price" to binding.edtSpecialityPrice.text.toString(),
                        "timeRequired" to binding.edtSpecialityTimeRequired.text.toString()
                    )


                    val specialityId = intent.getSerializableExtra("specialityId")


                    try {
                        if (specialityId != null && commerceId != null) {
                            fireBaseManager.editSpeciality(
                                specialityId.toString(),
                                commerceId,
                                newData
                            )
                            Toast.makeText(
                                this@CommerceEditSpeciality,
                                "La especialidad ha sido agregada correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent =
                                Intent(
                                    this@CommerceEditSpeciality,
                                    CommerceSpecialities::class.java
                                )
                            startActivity(intent)
                        }
                    } catch (e: Exception) {
                        e
                    }
                },
                onCancel = {

                }
            )
        }
    }

    private fun deleteSpeciality() {
        binding.txtDeleteSpeciality.setOnClickListener {
            CustomAlertDialog.showAlertDialog(
                this@CommerceEditSpeciality,
                "¿Estás seguro de querer eliminar la especialidad definitivamente?",
                onAccept = {
                    try {
                        val specialityId = intent.getSerializableExtra("specialityId")
                        fireBaseManager.deleteSpeciality(specialityId.toString(), commerceId!!)
                        Toast.makeText(
                            this@CommerceEditSpeciality,
                            "La especialidad ha sido eliminada de la Base de datos.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent =
                            Intent(this@CommerceEditSpeciality, CommerceSpecialities::class.java)
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@CommerceEditSpeciality,
                            "Ha habido un problema al intentar eliminar la especialidad de la Base de datos.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                onCancel = {

                }
            )
        }
    }

    private fun goToMenu() {
        binding.ivBackArrow.setOnClickListener {
            val intent = Intent(this@CommerceEditSpeciality, CommerceMenu::class.java)
            startActivity(intent)
        }
    }

    private fun fillSpecialityData() {

        val specialityName = intent.getStringExtra("specialityName")
        val specialityTimeRequired = intent.getStringExtra("specialityTimeRequired")
        val specialityPrice = intent.getStringExtra("specialityPrice")

        binding.edtSpecialityName.text = Editable.Factory.getInstance().newEditable(specialityName)
        binding.edtSpecialityTimeRequired.text =
            Editable.Factory.getInstance().newEditable(specialityTimeRequired)
        binding.edtSpecialityPrice.text =
            Editable.Factory.getInstance().newEditable(specialityPrice)

    }
}


package com.example.appointment.user.view.activities.appointments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.appointment.R
import com.example.appointment.commerce.view.activities.home.CommerceHome
import com.example.appointment.commerce.view.fragments.CustomAlertDialog
import com.example.appointment.commerce.viewModel.FireBaseManager
import com.example.appointment.databinding.ActivityAppointmentCommerceViewBinding
import com.example.appointment.databinding.ActivityAppointmentCustomerViewBinding
import com.example.appointment.user.view.activities.home.UserHome
import com.google.firebase.firestore.FirebaseFirestore

class AppointmentCustomerView : AppCompatActivity() {

    private lateinit var binding: ActivityAppointmentCustomerViewBinding
    private lateinit var deleteAppointment: Button
    private val fireBaseManager = FireBaseManager()
    private val firestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAppointmentCustomerViewBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        goBackHome()
        fillAppointmentData()
        deleteAppointment()
    }

    /**
     * Esta función devuelve al usuario a la pantalla de inicio al hacer click sobre el "BackArrow"
     */
    private fun goBackHome() {
        binding.backArrowIcon.setOnClickListener {
            var intent: Intent = Intent(this@AppointmentCustomerView, UserHome::class.java)
            startActivity(intent)
        }
    }

    private fun deleteAppointment() {
        deleteAppointment = binding.btnDeleteAppointment

        deleteAppointment.setOnClickListener {

            CustomAlertDialog.showAlertDialog(
                this@AppointmentCustomerView,
                "¿Estás seguro de que quieres eliminar esta cita?",
                onAccept = {
                    try {
                        val appointmentId = intent.getStringExtra("appointmentId").toString()
                        fireBaseManager.deleteAppointment(appointmentId)
                        Toast.makeText(
                            this@AppointmentCustomerView,
                            "La cita ha sido eliminada de calendario.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent =
                            Intent(this@AppointmentCustomerView, CommerceHome::class.java)
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@AppointmentCustomerView,
                            "Ha habido un problema al intentar eliminar al empleado de la Base de datos.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                onCancel = {

                }
            )
        }
    }

    private fun fillAppointmentData() {
        val commerceName = intent.getStringExtra("commerceName")
        val commerceId = intent.getStringExtra("commerceId")
        val date = intent.getStringExtra("appointmentDate")
        val time = intent.getStringExtra("appointmentTime")
        val service = intent.getStringExtra("serviceId")
        val address = intent.getStringExtra("commerceAddress")

        //binding.txtCustomerName.text = userName.toString()
        binding.txtAppointmentDate.text = date.toString()
        binding.txtAppointmentTime.text = time.toString()
        binding.txtServiceType.text = service.toString()
        binding.txtCommerceAddress.text = address.toString()
        binding.txtCommerceName.text = commerceName.toString()

        if (commerceId != null) {
            firestore.collection("commerces")
                .document(commerceId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val commerceEmail = documentSnapshot.getString("commerce_email")
                        val commercePhone = documentSnapshot.getString("commerce_phone_number")

                        // Acceder al subdocumento "address"
                        val address = documentSnapshot.get("address") as? Map<String, Any>
                        val commerceStreetName = address?.get("commerce_street_name") as? String
                        val commerceTypeStreet = address?.get("commerce_street_type") as? String
                        val commerceVillage = address?.get("commerce_city") as? String
                        val commerceCity = address?.get("commerce_state") as? String
                        val commerceStreetNumber = address?.get("commerce_street_number") as? String
                        val commercePostalCode = address?.get("commerce_postal_code") as? String


                        binding.txtCustomerEmailInfo.text = commerceEmail.toString()
                        binding.txtCustomerPhoneNumber.text = commercePhone.toString()
                        val addressText =
                            "${commerceTypeStreet ?: ""} ${commerceStreetName ?: ""}, ${commerceStreetNumber ?: ""}, ${commercePostalCode ?: ""} ${commerceVillage ?: ""} (${commerceCity ?: ""})"
                        binding.txtCommerceAddress.text = addressText.trim()
                    } else {
                        // El documento no existe
                        Log.d("TAG", "No existe el documento con ID: $commerceId")
                    }
                }
                .addOnFailureListener { e ->
                    // Manejar errores
                    Log.e("TAG", "Error al obtener el documento con ID: $commerceId", e)
                }
        }
    }
}
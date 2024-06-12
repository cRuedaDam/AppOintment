package com.example.appointment.commerce.view.activities.appointments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.appointment.R
import com.example.appointment.commerce.view.activities.home.CommerceHome
import com.example.appointment.commerce.view.fragments.CustomAlertDialog
import com.example.appointment.commerce.viewModel.FireBaseManager
import com.example.appointment.databinding.ActivityAppointmentCommerceViewBinding
import com.google.firebase.firestore.FirebaseFirestore

class AppointmentCommerceView : AppCompatActivity() {
    private lateinit var binding: ActivityAppointmentCommerceViewBinding
    private lateinit var deleteAppointment: Button
    private val fireBaseManager = FireBaseManager()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAppointmentCommerceViewBinding.inflate(layoutInflater)
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
            var intent: Intent = Intent(this@AppointmentCommerceView, CommerceHome::class.java)
            startActivity(intent)
        }
    }

    private fun deleteAppointment() {
        deleteAppointment = binding.btnDeleteAppointment

        deleteAppointment.setOnClickListener {

            CustomAlertDialog.showAlertDialog(
                this@AppointmentCommerceView,
                "¿Estás seguro de que quieres eliminar esta cita?",
                onAccept = {
                    try {
                        val appointmentId = intent.getStringExtra("appointmentId").toString()
                        fireBaseManager.deleteAppointment(appointmentId)
                        Toast.makeText(
                            this@AppointmentCommerceView,
                            "La cita ha sido eliminada de calendario.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent =
                            Intent(this@AppointmentCommerceView, CommerceHome::class.java)
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@AppointmentCommerceView,
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
        val userName = intent.getStringExtra("userName")
        val userId = intent.getStringExtra("userId")
        val date = intent.getStringExtra("appointmentDate")
        val time = intent.getStringExtra("appointmentTime")
        val service = intent.getStringExtra("serviceId")
        val appointmentId = intent.getStringExtra("appointmentId")

        //binding.txtCustomerName.text = userName.toString()
        binding.txtAppointmentDate.text = date.toString()
        binding.txtAppointmentTime.text = time.toString()
        binding.txtServiceType.text = service.toString()
        binding.txtCustomerFullName.text = userName.toString()

        if (userId != null) {
            firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userEmail = documentSnapshot.getString("user_email")
                        val userPhone = documentSnapshot.getString("user_phone_number")
                        val userLastName = documentSnapshot.getString("user_last_name")
                        binding.txtCustomerEmailInfo.text = userEmail.toString()
                        binding.txtCustomerPhoneNumber.text = userPhone.toString()
                        binding.txtCustomerLastName.text = userLastName.toString()
                    } else {
                        // El documento no existe
                        Log.d("TAG", "No existe el documento con ID: $userId")
                    }
                }
                .addOnFailureListener { e ->
                    // Manejar errores
                    Log.e("TAG", "Error al obtener el documento con ID: $userId", e)
                }
        }

        if (appointmentId != null) {
            firestore.collection("appointments")
                .document(appointmentId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val optionalRequest = documentSnapshot.getString("optional_request")
                        binding.txtOptionalRequest.text = optionalRequest.toString()
                        if(optionalRequest.toString() == "") {
                            binding.lyOptionalRequest.visibility = View.GONE
                        }else{
                            binding.lyOptionalRequest.visibility = View.VISIBLE
                        }
                    } else {
                        // El documento no existe
                        Log.d("TAG", "No existe el documento con ID: $appointmentId")
                    }
                }
                .addOnFailureListener { e ->
                    // Manejar errores
                    Log.e("TAG", "Error al obtener el documento con ID: $appointmentId", e)
                }
        }
    }
}
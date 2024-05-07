package com.example.appointment.user.view.activities.menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appointment.R
import com.example.appointment.commerce.viewModel.FireBaseManager
import com.example.appointment.common.model.Appointment
import com.example.appointment.databinding.ActivityUserHistoryBinding
import com.example.appointment.user.view.activities.menu.editProfile.UserEditProfile
import com.example.appointment.user.view.adapters.AppointmentHistoryAdapter
import com.example.appointment.user.view.adapters.AppointmentUserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserHistory : AppCompatActivity() {

    private lateinit var binding: ActivityUserHistoryBinding
    private lateinit var appointmentUserAdapter: AppointmentHistoryAdapter
    private var appointmentList = ArrayList<Appointment>()
    private lateinit var rvAppointments: RecyclerView
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val userId = currentUser?.uid
    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseManager = FireBaseManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityUserHistoryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        goToUserMenu()
        fillRecyclerView()
        Log.d("Current Activity", "UserHistory")
    }

    private fun goToUserMenu() {
        binding.ivBackArrow.setOnClickListener {
            val intent = Intent(this@UserHistory, UserMenu::class.java)
            startActivity(intent)
        }
    }

    private fun fillRecyclerView() {
        appointmentList.clear()
        rvAppointments = binding.rvAppointments
        rvAppointments.layoutManager = LinearLayoutManager(this)

        if (userId != null) {
            firestore.collection("appointments")
                .whereEqualTo("user_id", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->

                    for (doc in querySnapshot) {
                        val appointment = Appointment(
                            appointmentId = doc.data["appointment_id"].toString(),
                            serviceId = doc.data["service_id"].toString(),
                            commerceId = doc.data["commerce_id"].toString(),
                            userId = doc.data["user_id"].toString(),
                            employeeId = doc.data["employee_id"].toString(),
                            commerceName = doc.data["commerce_name"].toString(),
                            appointmentDate = doc.data["appointment_date"].toString(),
                            appointmentTime = doc.data["appointment_time"].toString(),
                            optionalRequest = doc.data["optional_request"].toString()
                        )
                        appointmentList.add(appointment)
                    }

                    // Una vez que se ha llenado la lista de citas, ahora podemos obtener los nombres de los servicios
                    appointmentList.forEach { appointment ->
                        firebaseManager.getServiceNameByIds(
                            appointment.serviceId,
                            appointment.commerceId
                        ) { serviceName ->
                            appointment.serviceId =
                                if (serviceName.isNullOrEmpty()) "Servicio no disponible" else serviceName //Si el servicio ha sido eliminado se trata la excepcion
                            // Notificar al adaptador que los datos han cambiado
                            appointmentUserAdapter.notifyDataSetChanged()
                        }
                    }

                    // Después de obtener los nombres de los servicios, configuramos el adaptador
                    appointmentUserAdapter = AppointmentHistoryAdapter(appointmentList)
                    rvAppointments.adapter = appointmentUserAdapter
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreError", "Error getting documents: ", exception)
                }
        }
    }
}
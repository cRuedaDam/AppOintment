package com.example.appointment.user.view.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.appointment.R
import com.example.appointment.common.model.Appointment
import com.example.appointment.user.view.activities.appointments.AppointmentCustomerView
import com.google.firebase.firestore.FirebaseFirestore

class AppointmentUserAdapter(private val appointments: List<Appointment>) :
    RecyclerView.Adapter<AppointmentUserAdapter.AppointmentUserViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    class AppointmentUserViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val txtCommerceName: TextView = itemView.findViewById(R.id.txt_business_name)
        val txtCustomerService: TextView = itemView.findViewById(R.id.txt_customer_service)
        val txtDate: TextView = itemView.findViewById(R.id.txt_date)
        val txtTime: TextView = itemView.findViewById(R.id.txt_time)
        val lyAppointments: ConstraintLayout = itemView.findViewById(R.id.ly_appointments)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentUserViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_customer_appointment, parent, false)
        return AppointmentUserViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return if (appointments.isEmpty()) 0 else appointments.size
    }

    override fun onBindViewHolder(holder: AppointmentUserViewHolder, position: Int) {

        val currentAppointment = appointments[position]
        val context = holder.itemView.context

        Log.d("AdapterData", "Commerce Name: ${currentAppointment.commerceName}")
        Log.d("AdapterData", "Service ID: ${currentAppointment.serviceId}")
        Log.d("AdapterData", "User ID: ${currentAppointment.userId}")

        holder.txtCommerceName.text = currentAppointment.commerceName
        holder.txtCustomerService.text = currentAppointment.serviceId
        holder.txtDate.text = currentAppointment.appointmentDate
        holder.txtTime.text = currentAppointment.appointmentTime

        // Esta acción provoca un Intent hacia una nueva activity con los datos de Item
        holder.itemView.setOnClickListener {

            var appointmentId: String = ""

            db.collection("appointments")
                .whereEqualTo("commerce_id", currentAppointment.commerceId)
                .whereEqualTo("user_id",currentAppointment.userId)
                .whereEqualTo("appointment_date",currentAppointment.appointmentDate)
                .whereEqualTo("appointment_time",currentAppointment.appointmentTime)
                .get()
                .addOnSuccessListener {
                    try {
                        for (doc in it) {
                            appointmentId = doc.id
                            Log.d("AppointmentId:", appointmentId)

                            db.collection("appointments")
                                .document(appointmentId)
                                .get()
                                .addOnSuccessListener { documentSnapshot ->

                                    if (documentSnapshot.exists()) {

                                        val intent =
                                            Intent(context, AppointmentCustomerView::class.java)
                                        intent.putExtra("commerceId", currentAppointment.commerceId)
                                        intent.putExtra("appointmentId", appointmentId)
                                        Log.d("AppointmentIdIntent:", appointmentId)
                                        intent.putExtra(
                                            "commerceName",
                                            currentAppointment.commerceName
                                        )
                                        intent.putExtra(
                                            "appointmentDate",
                                            currentAppointment.appointmentDate
                                        )
                                        intent.putExtra(
                                            "appointmentTime",
                                            currentAppointment.appointmentTime
                                        )
                                        intent.putExtra("serviceId", currentAppointment.serviceId)
                                        context.startActivity(intent)

                                    } else {
                                        Log.d("TAG", "No existe la cita con ID: $appointmentId")
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e(
                                        "TAG",
                                        "Error obteniendo la cita con ID: $appointmentId",
                                        e
                                    )
                                }
                        }
                    } catch (e: Exception) {
                        e
                    }
                }
        }
    }
}

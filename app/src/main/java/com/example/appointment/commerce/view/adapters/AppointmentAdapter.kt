package com.example.appointment.commerce.view.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.appointment.R
import com.example.appointment.commerce.view.activities.appointments.AppointmentCommerceView
import com.example.appointment.common.model.Appointment
import com.google.firebase.firestore.FirebaseFirestore

class AppointmentAdapter(private val appointments: List<Appointment>) :
    RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    class AppointmentViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val txtCustomerName: TextView = itemView.findViewById(R.id.txt_customer_name)
        val txtCustomerService: TextView = itemView.findViewById(R.id.txt_customer_service)
        val txtDate: TextView = itemView.findViewById(R.id.txt_date)
        val txtTime: TextView = itemView.findViewById(R.id.txt_time)
        val txtNoAppointments: TextView = itemView.findViewById(R.id.txt_no_appointments_today)
        val lyAppointments: ConstraintLayout = itemView.findViewById(R.id.ly_appointments)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_appointment, parent, false)
        return AppointmentViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return if (appointments.isEmpty()) 1 else appointments.size
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {

        if (appointments.isEmpty()) {
            holder.txtNoAppointments.visibility = View.VISIBLE
            holder.lyAppointments.visibility = View.GONE

        } else {
            holder.txtNoAppointments.visibility = View.GONE
            holder.lyAppointments.visibility = View.VISIBLE

            val currentAppointment = appointments[position]

            Log.d("AdapterData", "Customer Name: ${currentAppointment.userId}")
            Log.d("AdapterData", "Service ID: ${currentAppointment.serviceId}")
            Log.d("AdapterData", "User ID: ${currentAppointment.userId}")

            holder.txtCustomerName.text = currentAppointment.userId
            holder.txtCustomerService.text = currentAppointment.serviceId
            holder.txtDate.text = currentAppointment.appointmentDate
            holder.txtTime.text = currentAppointment.appointmentTime

            // Esta acción provoca un Intent hacia una nueva activity con los datos de Item
            holder.itemView.setOnClickListener {

                var appointmentId: String = ""

                db.collection("appointments")
                    .whereEqualTo("commerce_id", currentAppointment.commerceId)
                    .get()
                    .addOnSuccessListener {
                        try {
                            for (doc in it) {
                                appointmentId = doc.id.toString()
                                val context = holder.itemView.context

                                db.collection("appointments")
                                    .document(appointmentId)
                                    .get()
                                    .addOnSuccessListener { documentSnapshot ->
                                        var userId = ""

                                        if (documentSnapshot.exists()) {
                                            userId =
                                                documentSnapshot.getString("user_id").toString()
                                            Log.d(
                                                "TAG",
                                                "User ID for Appointment $appointmentId: $userId"
                                            )

                                            val intent =
                                                Intent(context, AppointmentCommerceView::class.java)
                                            intent.putExtra("userId", userId)
                                            intent.putExtra("appointmentId", appointmentId)
                                            intent.putExtra("userName", currentAppointment.userId)
                                            intent.putExtra(
                                                "appointmentDate",
                                                currentAppointment.appointmentDate
                                            )
                                            intent.putExtra(
                                                "appointmentTime",
                                                currentAppointment.appointmentTime
                                            )
                                            intent.putExtra(
                                                "serviceId",
                                                currentAppointment.serviceId
                                            )
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
}

package com.example.appointment.user.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appointment.R
import com.example.appointment.common.model.Appointment
import com.google.firebase.firestore.FirebaseFirestore

class AppointmentHistoryAdapter(private val appointments: List<Appointment>) :
    RecyclerView.Adapter<AppointmentHistoryAdapter.AppointmentViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    class AppointmentViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val txtCommerceName: TextView = itemView.findViewById(R.id.txt_customer_name)
        val txtCustomerService: TextView = itemView.findViewById(R.id.txt_customer_service)
        val txtDate: TextView = itemView.findViewById(R.id.txt_date)
        val txtTime: TextView = itemView.findViewById(R.id.txt_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_history_appointment, parent, false)
        return AppointmentViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return if (appointments.isEmpty()) 0 else appointments.size
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {

        val currentAppointment = appointments[position]

        Log.d("AdapterData", "Customer Name: ${currentAppointment.userId}")
        Log.d("AdapterData", "Service ID: ${currentAppointment.serviceId}")
        Log.d("AdapterData", "User ID: ${currentAppointment.userId}")

        holder.txtCommerceName.text = currentAppointment.commerceName
        holder.txtCustomerService.text = currentAppointment.serviceId
        holder.txtDate.text = currentAppointment.appointmentDate
        holder.txtTime.text = currentAppointment.appointmentTime

        holder.itemView.setOnClickListener {
        }
    }
}

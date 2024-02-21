package com.example.reservapp.user.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservapp.R
import com.example.reservapp.user.model.AppointmentItem


class CustomAppointmentsAdapter(private var listAppointment: ArrayList<AppointmentItem>, private val onClickItem:(AppointmentItem) -> Unit): RecyclerView.Adapter<CustomAppointmentsAdapter.AppointmentListViewHolder>(){
    inner class AppointmentListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val itemCommerceName: TextView = itemView.findViewById(R.id.commerceNameListAppoint)
        private val itemDate: TextView = itemView.findViewById(R.id.commerceDateListAppoint)
        private val itemHour: TextView = itemView.findViewById(R.id.commerceHourListAppoint)
        
        fun bind(item: AppointmentItem, onClickItem: (AppointmentItem) -> Unit) {
            itemCommerceName.text = item.commerceName
            itemDate.text = item.appointmentDate
            itemHour.text = item.appointmentTime
            itemView.setOnClickListener { onClickItem(item) }

            
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppointmentListViewHolder {
        return AppointmentListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.usr_appointment_row, parent, false))
    }
    override fun onBindViewHolder(holder: AppointmentListViewHolder, position: Int){
        val currentItem = listAppointment[position]
        holder.bind(currentItem, onClickItem)
    }

    /**
     * fun que devuelve un int que sera el tamaño de la lista a mostrar
     * en el recyclerView
     */
    override fun getItemCount(): Int {
        return listAppointment.size
    }

}
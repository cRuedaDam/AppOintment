package com.example.reservapp.user.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservapp.R
import com.example.reservapp.user.model.AppointmentItem

class CustomAppointmentsHistorialAdapter(private var listAppointment: ArrayList<AppointmentItem>, private val onClickItem:(AppointmentItem) -> Unit): RecyclerView.Adapter<CustomAppointmentsHistorialAdapter.AppointmentListViewHolder>(){

    inner class AppointmentListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val imageButtonDetails: ImageButton = itemView.findViewById(R.id.imgBtnDetailOldAppointment)
        private val commerceNameHist: TextView = itemView.findViewById(R.id.commerceNameHist)
        private val commerceServiceHist: TextView = itemView.findViewById(R.id.commerceServiceHist)
        
        fun bind(item: AppointmentItem, onClickItem: (AppointmentItem) -> Unit) {
            commerceNameHist.text = item.commerceName
            commerceServiceHist.text = item.appointmentDate
            imageButtonDetails.setOnClickListener { onClickItem(item) }

        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppointmentListViewHolder {
        return AppointmentListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.usr_row_hist, parent, false))
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

    // Actualizamos la lista de citas guardada en el Adapter con los nuevos datos recibidos del Fragment (filtro de citas pasadas)
    fun setFilteredAppointments(filteredList: ArrayList<AppointmentItem>) {
        this.listAppointment = filteredList
        notifyDataSetChanged()
    }

}
package com.example.appointment.user.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.appointment.R

class TimeAdapter(private val times: List<String>) : RecyclerView.Adapter<TimeAdapter.TimeViewHolder>() {

    private var selectedIndex = RecyclerView.NO_POSITION

    inner class TimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnTime: Button = itemView.findViewById(R.id.btn_time)

        init {
            btnTime.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Actualizar el índice seleccionado
                    selectedIndex = position
                    // Notificar al adaptador sobre el cambio en el estado del botón
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_time, parent, false)
        return TimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        val time = times[position]
        holder.btnTime.text = time
        // Actualizar el estado del botón según si es el elemento seleccionado
        holder.btnTime.isSelected = position == selectedIndex
    }

    override fun getItemCount(): Int {
        return times.size
    }
}

package com.example.appointment.user.view.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appointment.R
import com.example.appointment.user.model.Commerce
import com.example.appointment.user.view.activities.appointments.SelectAppointmentSpeciality

class CommerceAdapter(private val commerceList: MutableList<Commerce> = mutableListOf()) : RecyclerView.Adapter<CommerceAdapter.CommerceViewHolder>() {

    private var onCommerceFilterListener: OnCommerceFilterListener? = null
    private var filteredList: List<Commerce> = commerceList

    inner class CommerceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commerceName: TextView = itemView.findViewById(R.id.txt_commerce_name)
        val commerceAddress: TextView = itemView.findViewById(R.id.txt_commerce_address)
        val commercePhone: TextView = itemView.findViewById(R.id.txt_commerce_phone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommerceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_commerce, parent, false)
        return CommerceViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommerceViewHolder, position: Int) {
        if (position < filteredList.size){

            val commerce = filteredList[position]

            val addressMap = commerce.address
            val streetType = addressMap["commerce_street_type"]
            val streetName = addressMap["commerce_street_name"]
            val streetNumber = addressMap["commerce_street_number"]
            val city = addressMap["commerce_city"]
            val state = addressMap["commerce_state"]

            val fullAddress = "$streetType $streetName $streetNumber, $city ($state)"


            holder.commerceName.text = commerce.commerce_name
            holder.commerceAddress.text = fullAddress
            holder.commercePhone.text = commerce.commerce_phone_number
            holder.itemView.visibility = View.VISIBLE

            holder.itemView.setOnClickListener {
                val intent = Intent(holder.itemView.context, SelectAppointmentSpeciality::class.java)
                intent.putExtra("COMMERCE_NAME", commerce.commerce_name)
                holder.itemView.context.startActivity(intent) // Iniciar la actividad con el Intent
            }

        }else{
            holder.itemView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return commerceList.size
    }

    fun setOnCommerceFilterListener(listener: OnCommerceFilterListener) {
        onCommerceFilterListener = listener
    }

    fun filter(text: String) {
        filteredList = if (text.isNotBlank()) { // Si el buscador no está en blanco
            commerceList.filter {
                it.commerce_name.contains(
                    text,
                    ignoreCase = true
                )
            } // si la iteración de caracteres contiene la secuancia de los elementos, se filtrarán.
        } else {
            commerceList.toList() // En caso contrario, se muestra la lista original
        }
        //Notificamos al adaptador de que los datos han cambiado
        notifyDataSetChanged()
        //Notificamos al listener sobre el cambio en el texto del filtro
        onCommerceFilterListener?.onFilterTextChanged(text)
    }

    interface OnCommerceFilterListener {
        fun onFilterTextChanged(text: String)
    }
}
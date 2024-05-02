package com.example.appointment.user.view.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appointment.R
import com.example.appointment.commerce.model.Speciality
import com.example.appointment.user.view.activities.appointments.SelectAppointmentEmployee
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SpecialitiesAdapter(
    private val specialitiesList: MutableList<Speciality> = mutableListOf(),
    private val commerceName: String? = null,
    private val commerceType: String? = null,
    private val commerceId: String?= null
) : RecyclerView.Adapter<SpecialitiesAdapter.ViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private var onSpecialitiesFilterListener: OnSpecialitiesFilterListener? = null
    private var filteredList: List<Speciality> = specialitiesList

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val specialityName: TextView = itemView.findViewById(R.id.txt_speciality_name)
        val specialityTimeRequired: TextView =
            itemView.findViewById(R.id.txt_speciality_time_required)
        val specialityPrice: TextView = itemView.findViewById(R.id.txt_speciality_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_speciality, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SpecialitiesAdapter.ViewHolder, position: Int) {
        //Si la posición del item es menor al tamaño de la lista
        if (position < filteredList.size) {

            val speciality = filteredList[position]

            holder.specialityName.text = speciality.name
            holder.specialityTimeRequired.text = "${speciality.timeRequired} min."
            holder.specialityPrice.text = "${speciality.price} €"
            holder.itemView.visibility = View.VISIBLE

            holder.itemView.setOnClickListener {

                db.collection("commerces")
                    .document(commerceId!!)
                    .collection("specialities")
                    .whereEqualTo("name", speciality.name)
                    .get()
                    .addOnSuccessListener { querySnapshot ->

                        val context = holder.itemView.context
                        val intent = Intent(
                            context,
                            SelectAppointmentEmployee::class.java
                        ) // Viajamos a donde nos diga la clase desde la que se llama

                        intent.putExtra("SPECIALITY_NAME", speciality.name)
                        intent.putExtra("SPECIALITY_TIME_REQUIRED", speciality.timeRequired)
                        intent.putExtra("SPECIALITY_PRICE", speciality.price)
                        intent.putExtra("COMMERCE_NAME", commerceName)
                        intent.putExtra("COMMERCE_TYPE", commerceType)
                        intent.putExtra("COMMERCE_ID", commerceId)

                        context.startActivity(intent)

                    }.addOnFailureListener { exception ->
                        Log.e("FirestoreError", "Error al obtener datos", exception)
                    }
            }
        } else {
            holder.itemView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return specialitiesList.size
    }

    fun setOnSpecialitiesFilterListener(listener: OnSpecialitiesFilterListener) {
        onSpecialitiesFilterListener = listener
    }

    fun filter(text: String) {
        filteredList = if (text.isNotBlank()) {
            specialitiesList.filter {
                it.name!!.contains(
                    text,
                    ignoreCase = true
                )
            }
        } else {
            specialitiesList.toList()
        }
        notifyDataSetChanged()
        onSpecialitiesFilterListener?.onFilterTextChanged(text)
    }

    interface OnSpecialitiesFilterListener {
        fun onFilterTextChanged(text: String)
    }

}
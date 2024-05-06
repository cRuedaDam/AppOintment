package com.example.appointment.user.view.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appointment.R
import com.example.appointment.commerce.model.Employee
import com.example.appointment.user.view.activities.appointments.SelectAppointmentTime
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EmployeesAdapter(
    private val employeesList: MutableList<Employee> = mutableListOf(),
    private val commerceName: String? = null,
    private val commerceType: String? = null,
    private val commerceId: String? = null,
    private val commerceFullAddress: String? = null,
    private val specialityName: String? = null,
    private val specialityId: String? = null,
    private val employeeId: String? = null
) : RecyclerView.Adapter<EmployeesAdapter.ViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private var onEmployeesFilterListener: EmployeesAdapter.OnEmployeesFilterListener? = null
    private var filteredList: List<Employee> = employeesList

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val employeeName: TextView = itemView.findViewById(R.id.txt_employee_name)
        val employeeLastName: TextView = itemView.findViewById(R.id.txt_employee_last_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_employee, parent, false)
        return EmployeesAdapter.ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return employeesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (position < filteredList.size) {

            val employee = filteredList[position]

            holder.employeeName.text = employee.name
            holder.employeeLastName.text = employee.lastName

            holder.itemView.setOnClickListener {
                val context = holder.itemView.context
                val intent = Intent(context, SelectAppointmentTime::class.java)

                intent.putExtra("SPECIALITY_NAME", specialityName)
                intent.putExtra("SPECIALITY_ID", specialityId)
                intent.putExtra("COMMERCE_NAME", commerceName)
                intent.putExtra("COMMERCE_TYPE", commerceType)
                intent.putExtra("COMMERCE_ID", commerceId)
                intent.putExtra("COMMERCE_FULL_ADDRESS", commerceFullAddress)
                intent.putExtra("EMPLOYEE_ID", employeeId)


                context.startActivity(intent)

            }
        } else {
            holder.itemView.visibility = View.GONE
        }
    }

    fun setOnEmployeesFilterListener(listener: OnEmployeesFilterListener) {
        onEmployeesFilterListener = listener
    }

    fun filter(text: String) {
        filteredList = if (text.isNotBlank()) {
            employeesList.filter {
                it.name!!.contains(
                    text,
                    ignoreCase = true
                )
            }
        } else {
            employeesList.toList()
        }
        notifyDataSetChanged()
        onEmployeesFilterListener?.onFilterTextChanged(text)
    }

    interface OnEmployeesFilterListener {
        fun onFilterTextChanged(text: String)
    }
}
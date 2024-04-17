package com.example.appointment.commerce.view.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appointment.R
import com.example.appointment.commerce.model.Employee
import com.example.appointment.commerce.view.activities.menu.employees.CommerceEditEmlpoyee
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EmployeeAdapter(private val employeeList: List<Employee>) :
    RecyclerView.Adapter<EmployeeAdapter.ViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val currentCommerce = FirebaseAuth.getInstance().currentUser
    private val commerceId = currentCommerce?.uid
    private var onEmployeeFilterListener: OnEmployeeFilterListener? = null
    private var filteredList: List<Employee> = employeeList

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val employeeName: TextView = itemView.findViewById(R.id.txt_employee_name)
        val employeeLastName: TextView = itemView.findViewById(R.id.txt_employee_last_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_employee, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Si la posición del item es menor al tamaño de la lista
        if (position < filteredList.size) {
            val employee = filteredList[position] // La lista original se iguala a la posición de la lista filtrada
            holder.employeeName.text = employee.name
            holder.employeeLastName.text = employee.lastName
            holder.itemView.visibility = View.VISIBLE // Establecemos el Item como Visible

            holder.itemView.setOnClickListener { //Cuando clicamos sobre el item

                var idEmployee: String = ""

                db.collection("commerces")
                    .document(commerceId!!)
                    .collection("employees")
                    .whereEqualTo("name", employee.name)
                    .get()
                    .addOnSuccessListener {
                        try {
                            for (document in it) {
                                idEmployee = document.id.toString()
                                val context = holder.itemView.context
                                val intent = Intent(context, CommerceEditEmlpoyee::class.java) // Viajamos a Editar empleado

                                intent.putExtra("employeeId", idEmployee) //Recogemos el Id del documento (Empleado)
                                intent.putExtra("employeeName", employee.name)  //Gardamos el nombre y apellidos
                                intent.putExtra("employeeLastName", employee.lastName)
                                intent.putExtra("checkIn", employee.checkInTime)
                                intent.putExtra("checkOut", employee.checkOutTime)
                                context.startActivity(intent)
                            }
                        } catch (e: Exception) {
                            e
                        }
                    }
            }
        } else { // En caso contrario los items desaparecen
            holder.itemView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return employeeList.size
    }

    fun setOnEmployeeFilterListener(listener: OnEmployeeFilterListener) {
        onEmployeeFilterListener = listener
    }

    /**
     * Esta función se encarga de realizar el filtro de los elementos en función de si contiene una secuencia
     * de caracteres
     */
    fun filter(text: String) {
        filteredList = if (text.isNotBlank()) { // Si el buscador no está en blanco
            employeeList.filter {
                it.name.contains(
                    text,
                    ignoreCase = true
                )
            } // si la iteración de caracteres contiene la secuancia de los elementos, se filtrarán.
        } else {
            employeeList.toList() // En caso contrario, se muestra la lista original
        }
        //Notificamos al adaptador de que los datos han cambiado
        notifyDataSetChanged()
        //Notificamos al listener sobre el cambio en el texto del filtro
        onEmployeeFilterListener?.onFilterTextChanged(text)
    }

    interface OnEmployeeFilterListener {
        fun onFilterTextChanged(text: String)
    }
}
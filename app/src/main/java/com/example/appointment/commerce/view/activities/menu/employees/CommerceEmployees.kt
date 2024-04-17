package com.example.appointment.commerce.view.activities.menu.employees

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appointment.R
import com.example.appointment.commerce.model.Employee
import com.example.appointment.commerce.view.activities.menu.CommerceMenu
import com.example.appointment.commerce.view.adapters.EmployeeAdapter
import com.example.appointment.databinding.ActivityCommerceEmployeesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CommerceEmployees : AppCompatActivity(), EmployeeAdapter.OnEmployeeFilterListener {
    private lateinit var binding: ActivityCommerceEmployeesBinding
    private lateinit var rvEmployees: RecyclerView
    private lateinit var employeeAdapter: EmployeeAdapter
    private lateinit var edtSearch: EditText
    private val employeeList = ArrayList<Employee>()
    private val firestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCommerceEmployeesBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        goToAddEmployee()
        goToMenu()
        fillEmployeesRecyclerView()
        filteredSearch()
    }

    /**
     * Esta función se encarga de filtrar las búsquedas a tavés del adaptador
     */
    private fun filteredSearch() {

        employeeAdapter =
            EmployeeAdapter(employeeList) // Iguala el adaptador a la lista recibida desde el Employee Adapter
        employeeAdapter.setOnEmployeeFilterListener(this) // Se le agrega un listener al adaptador
        rvEmployees.adapter = employeeAdapter // El Recycler View se iguala al adaptador

        edtSearch = binding.etSearchEmployee

        edtSearch.addTextChangedListener(object :
            TextWatcher { // Se le añade un TextListener al Buscador para realizar la acción de filtro cuando el texto cambie

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                employeeAdapter.filter(s.toString()) // LLamamos a la fucnión filter de EmployeeAdapter y le pasamos como parámetro el charSquence para que pueda realizar la búsqueda
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    /**
     *  Esta función dirige la Activity al View CommerceAddEmployee cuando haces click sobre AddEmployee
     */
    private fun goToAddEmployee() {
        binding.btnAddEmployee.setOnClickListener {
            val intent = Intent(this@CommerceEmployees, CommerceAddEmployee::class.java)
            startActivity(intent)
        }
    }

    /**
     *  Esta función dirige la Activity al View CommerceMenu cuando haces click sobre el back_arrow
     */
    private fun goToMenu() {
        binding.ivBackArrow.setOnClickListener {
            val intent = Intent(this@CommerceEmployees, CommerceMenu::class.java)
            startActivity(intent)
        }
    }

    /**
     *  Esta función se encarga de rellenar el recyclerView de empleados con los datos recogidos de BBDD
     */
    private fun fillEmployeesRecyclerView() {
        rvEmployees = binding.rvEmployees
        rvEmployees.layoutManager = LinearLayoutManager(this)

        val currentCommerce = FirebaseAuth.getInstance().currentUser
        val commerceId = currentCommerce?.uid

        if (commerceId != null) {
            val employeesCollection = firestore.collection("commerces/$commerceId/employees")
            employeesCollection.addSnapshotListener { querySnapshot, error ->

                if (error != null) {
                    Log.e("FirestoreError", "Error al obtener datos", error)
                    return@addSnapshotListener
                }

                for (document in querySnapshot!!) {
                    val employee = document.toObject(Employee::class.java)
                    employeeList.add(employee)
                }

                employeeAdapter = EmployeeAdapter(employeeList)
                rvEmployees.adapter = employeeAdapter

                if (employeeList.isEmpty()) {
                    binding.txtNoEmployees.visibility = View.VISIBLE
                } else {
                    binding.txtNoEmployees.visibility = View.GONE
                }
            }
        } else {

        }
    }

    /**
     * Esta función hace referencia a la interfaz onFilterTextChanged del EmployeeAdapter
     */
    override fun onFilterTextChanged(text: String) {
        employeeAdapter.filter(text)
    }
}
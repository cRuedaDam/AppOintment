package com.example.appointment.user.view.activities.appointments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appointment.commerce.model.Employee
import com.example.appointment.commerce.model.Speciality
import com.example.appointment.commerce.viewModel.FireBaseManager
import com.example.appointment.databinding.ActivitySelectAppointmentEmployeeBinding
import com.example.appointment.user.view.adapters.EmployeesAdapter
import com.example.appointment.user.view.adapters.SpecialitiesAdapter
import com.google.firebase.firestore.FirebaseFirestore

class SelectAppointmentEmployee : AppCompatActivity(),
    EmployeesAdapter.OnEmployeesFilterListener {

    private lateinit var binding: ActivitySelectAppointmentEmployeeBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var employeesAdapter: EmployeesAdapter
    private val employeesList = ArrayList<Employee>()
    private lateinit var rvEmployees: RecyclerView
    private lateinit var edtSearch: EditText
    private var firebaseManager = FireBaseManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySelectAppointmentEmployeeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        goToSelectSpeciality()
        fillEmployeesRecyclerView()
        //filteredSearch()
        Log.d("Current Activity:", "SelectAppointmentEmployee")
    }


    private fun goToSelectSpeciality() {
        val commerceName = intent.getStringExtra("COMMERCE_NAME")
        val commerceType = intent.getStringExtra("COMMERCE_TYPE")

        binding.backArrowIcon.setOnClickListener {
            val intent =
                Intent(this@SelectAppointmentEmployee, SelectAppointmentSpeciality::class.java)
            intent.putExtra("COMMERCE_NAME", commerceName)
            intent.putExtra("COMMERCE_TYPE", commerceType)
            startActivity(intent)
        }
    }

    private fun filteredSearch() {

        /*employeesAdapter =
            EmployeesAdapter(employeesList) // Iguala el adaptador a la lista recibida desde el Employee Adapter
        employeesAdapter.setOnEmployeesFilterListener(this) // Se le agrega un listener al adaptador
        rvEmployees.adapter = employeesAdapter // El Recycler View se iguala al adaptador*/

        edtSearch = binding.etSearchEmployee

        edtSearch.addTextChangedListener(object :
            TextWatcher { // Se le añade un TextListener al Buscador para realizar la acción de filtro cuando el texto cambie

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                employeesAdapter.filter(s.toString()) // LLamamos a la fucnión filter de EmployeeAdapter y le pasamos como parámetro el charSquence para que pueda realizar la búsqueda
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun fillEmployeesRecyclerView() {
        rvEmployees = binding.rvEmployees
        rvEmployees.layoutManager = LinearLayoutManager(this)

        val specialityName = intent.getStringExtra("SPECIALITY_NAME")
        val commerceId = intent.getStringExtra("COMMERCE_ID")

        if (!specialityName.isNullOrEmpty() && !commerceId.isNullOrEmpty()){
            firebaseManager.getServiceIdByName(specialityName,commerceId) { specialityId ->
                if(specialityId != null) {
                    Log.d("SPECIALITY_ID", specialityId ?: "null")
                    Log.d("SPECIALITY_NAME", specialityName ?: "null")
                    Log.d("COMMERCE_ID", commerceId ?: "null")
                    db.collection("commerces/$commerceId/employees")
                        .whereEqualTo("specialities", specialityId)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            val employeesList = mutableListOf<Employee>()
                            for (document in querySnapshot.documents) {
                                val employee = document.toObject(Employee::class.java)
                                employee?.let { employeesList.add(it) }
                            }
                            employeesAdapter = EmployeesAdapter(employeesList)
                            rvEmployees.adapter = employeesAdapter
                        }
                        .addOnFailureListener { exception ->
                            Log.e("FirestoreError", "Error al obtener datos", exception)
                        }
                }
            }
        }
    }

    override fun onFilterTextChanged(text: String) {
        employeesAdapter.filter(text)
    }
}
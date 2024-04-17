package com.example.appointment.commerce.view.activities.menu.employees

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import com.example.appointment.R
import com.example.appointment.commerce.model.Employee
import com.example.appointment.commerce.view.fragments.TimePickerDialogFragment
import com.example.appointment.commerce.viewModel.FireBaseManager
import com.example.appointment.databinding.ActivityCommerceAddEmployeeBinding
import com.example.reservapp.common.viewModel.Alerts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class CommerceAddEmployee : AppCompatActivity() {
    private lateinit var binding: ActivityCommerceAddEmployeeBinding
    private lateinit var checkInTimeButton: Button
    private lateinit var checkOutTimeButton: Button
    private lateinit var btnAddEmployee: Button
    private lateinit var spinnerSpecialitiesItems: Spinner
    private lateinit var specialitiesList: List<String>
    private val currentCommerce = FirebaseAuth.getInstance().currentUser
    private val commerceId = currentCommerce?.uid
    private val alert = Alerts()
    private val fireBaseManager = FireBaseManager()
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCommerceAddEmployeeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        goToEmployees() // Lleva al Activity CommerceEmployees al pulsar sobre el back_arrow
        chooseCheckInTime() // Despliega un TimePicker cuando se pulsa sobre hora de entrada
        chooseCheckOutTime()    // Despliega un TimePicker cuando se pulsa sobre hora de salida
        addEmployee()   // Recoge los text de los EditText y los ingresa a la BBDD
        fillSpinner() // Rellena el Spinner Specialities con datos recogidos de BBDD
    }

    private fun fillSpinner() {
        spinnerSpecialitiesItems = binding.spinnerSpecialities

        val specialityCollection =
            db.collection("commerces").document(commerceId!!).collection("specialities")
        specialityCollection.get()
            .addOnSuccessListener { result ->
                specialitiesList = result.documents.map { it.getString("name")!! }
                val spinnerAdapter =
                    ArrayAdapter(this, android.R.layout.simple_spinner_item, specialitiesList)
                spinnerSpecialitiesItems.adapter = spinnerAdapter
            }
            .addOnFailureListener { exception ->
                val specialitiesList: List<String> = mutableListOf("No existe ningúna especialidad")
                val spinnerAdapter =
                    ArrayAdapter(this, android.R.layout.simple_spinner_item, specialitiesList)
                spinnerSpecialitiesItems.adapter = spinnerAdapter

            }

    }

    private fun showTimePicker(button: Button) {
        val timePickerDialog = TimePickerDialogFragment()
        timePickerDialog.buttonToUpdate = button
        timePickerDialog.show(supportFragmentManager, "timePicker")
    }

    private fun chooseCheckInTime() {
        checkInTimeButton = binding.btnCheckInTime
        checkInTimeButton.setOnClickListener {
            showTimePicker(checkInTimeButton)
        }
    }

    private fun chooseCheckOutTime() {
        checkOutTimeButton = binding.btnCheckOutTime
        checkOutTimeButton.setOnClickListener {
            showTimePicker(checkOutTimeButton)
        }
    }

    private fun goToEmployees() {
        binding.ivBackArrow.setOnClickListener {
            val intent = Intent(this@CommerceAddEmployee, CommerceEmployees::class.java)
            startActivity(intent)
        }
    }

    private fun addEmployee() {

        btnAddEmployee = binding.btnAddEmployee

        btnAddEmployee.setOnClickListener {

            val employeeName = binding.edtEmployeeName.text.toString()
            val employeeLastName = binding.edtEmployeeLastName.text.toString()
            val employeeCheckInTime = binding.btnCheckInTime.text.toString()
            val employeeCheckOutTime = binding.btnCheckOutTime.text.toString()
            var itemSpinnerSelected = spinnerSpecialitiesItems.selectedItem.toString()

            var idSpeciality: String = ""

            db.collection("commerces")
                .document(commerceId!!)
                .collection("specialities")
                .whereEqualTo("name", itemSpinnerSelected)
                .get()
                .addOnSuccessListener {
                    try {
                        for (document in it) {
                            idSpeciality = document.id.toString()

                        }
                        val employee = Employee(
                            name = employeeName,
                            lastName = employeeLastName,
                            specialities = idSpeciality,
                            checkInTime = employeeCheckInTime,
                            checkOutTime = employeeCheckOutTime
                        )

                        if (commerceId != null) {
                            alert.showLoading(this@CommerceAddEmployee, "Espere por favor...")
                            try {
                                MainScope().launch(Dispatchers.IO) {
                                    fireBaseManager.addEmployee(employee, commerceId)
                                    launch(Dispatchers.Main) {
                                        Toast.makeText(
                                            this@CommerceAddEmployee,
                                            "El empleado \"$employeeName\" ha sido agregado correctamente",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val intent =
                                            Intent(
                                                this@CommerceAddEmployee,
                                                CommerceEmployees::class.java
                                            )
                                        startActivity(intent)
                                        alert.hideLoading()
                                    }
                                }
                            } catch (e: Exception) {
                                alert.hideLoading()
                                Toast.makeText(
                                    this@CommerceAddEmployee,
                                    "No ha sio posible añadir al empleado a la Base de datos",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@CommerceAddEmployee,
                                "No ha sio posible añadir al empleado a la Base de datos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        e
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this@CommerceAddEmployee,
                        "No ha sido posible",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}
package com.example.appointment.commerce.view.activities.menu.employees

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.appointment.R
import com.example.appointment.commerce.view.fragments.CustomAlertDialog
import com.example.appointment.commerce.view.fragments.TimePickerDialogFragment
import com.example.appointment.commerce.viewModel.FireBaseManager
import com.example.appointment.databinding.ActivityCommerceEditEmlpoyeeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CommerceEditEmlpoyee : AppCompatActivity() {
    private lateinit var binding: ActivityCommerceEditEmlpoyeeBinding
    private lateinit var checkInTime: Button
    private lateinit var checkOutTime: Button
    private lateinit var txtDeleteEmployee: TextView
    private lateinit var txtEditEmployee: TextView
    private lateinit var spinnerSpecialitiesItems: Spinner
    private val fireBaseManager = FireBaseManager()
    private val db = FirebaseFirestore.getInstance()
    private val currentCommerce = FirebaseAuth.getInstance().currentUser
    private val commerceId = currentCommerce?.uid
    private lateinit var specialitiesList: List<String>
    private lateinit var newData: Map<String, String>
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCommerceEditEmlpoyeeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        goToEmployees()
        fillEmployeeData()
        fillSpinner()
        setTimePicker()
        deleteEmployee()
        editEmployee()
    }

    private fun editEmployee() {
        val employeeId = intent.getSerializableExtra("employeeId")
            ?.toString()
            ?: throw IllegalStateException("Employee ID is null")

        txtEditEmployee = binding.btnChangeEmployee

        txtEditEmployee.setOnClickListener {

            var itemSpinnerSelected = spinnerSpecialitiesItems.selectedItem
                ?.toString()
                ?: throw IllegalStateException("Selected item is null")

            CustomAlertDialog.showAlertDialog(
                this@CommerceEditEmlpoyee,
                "¿Estás seguro de que quieres guardar los nuevos datos del empleado?",
                onAccept = {
                    var specialityId: String
                    val query = db.collection("commerces/$commerceId/specialities")
                        .whereEqualTo("name", itemSpinnerSelected)
                    query.get()
                        .addOnSuccessListener { documents ->
                            for (speciality in documents) {
                                specialityId = speciality.id
                                newData = hashMapOf(
                                    "name" to binding.edtEmployeeName.text.toString(),
                                    "lastName" to binding.edtEmployeeLastName.text.toString(),
                                    "specialities" to specialityId,
                                    "checkInTime" to binding.btnCheckInTime.text.toString(),
                                    "checkOutTime" to binding.btnCheckOutTime.text.toString()
                                )
                                try {
                                    if (employeeId != null && commerceId != null) {
                                        fireBaseManager.editEmployee(
                                            employeeId.toString(),
                                            commerceId,
                                            newData
                                        )
                                        Toast.makeText(
                                            this@CommerceEditEmlpoyee,
                                            "El empleado ha sido agregado correctamente",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val intent =
                                            Intent(
                                                this@CommerceEditEmlpoyee,
                                                CommerceEmployees::class.java
                                            )
                                        startActivity(intent)
                                    }
                                } catch (e: Exception) {
                                    e
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            println("Error al obtener el id del servicio: $exception")
                        }
                },
                onCancel = {

                }
            )
        }
    }

    private fun deleteEmployee() {
        txtDeleteEmployee = binding.txtDeleteEmployee

        txtDeleteEmployee.setOnClickListener {

            CustomAlertDialog.showAlertDialog(
                this@CommerceEditEmlpoyee,
                "¿Estás seguro de que quieres eliminar este empleado definitivamente?",
                onAccept = {
                    try {
                        val employeeId = intent.getStringExtra("employeeId").toString()
                        fireBaseManager.deleteEmployee(employeeId, commerceId!!)
                        Toast.makeText(
                            this@CommerceEditEmlpoyee,
                            "El empleado ha sido eliminado de la Base de datos.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent =
                            Intent(this@CommerceEditEmlpoyee, CommerceEmployees::class.java)
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@CommerceEditEmlpoyee,
                            "Ha habido un problema al intentar eliminar al empleado de la Base de datos.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                onCancel = {

                }
            )
        }
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
                val specialitiesList: List<String> =
                    mutableListOf("No existe ningúna especialidad")
                val spinnerAdapter =
                    ArrayAdapter(this, android.R.layout.simple_spinner_item, specialitiesList)
                spinnerSpecialitiesItems.adapter = spinnerAdapter
            }
    }

    private fun setTimePicker() {
        checkInTime = binding.btnCheckInTime
        checkOutTime = binding.btnCheckOutTime

        checkInTime.setOnClickListener {
            showTimePicker(checkInTime)
        }

        checkOutTime.setOnClickListener {
            showTimePicker(checkOutTime)
        }
    }

    private fun showTimePicker(button: Button) {
        val timePickerDialog = TimePickerDialogFragment()
        timePickerDialog.buttonToUpdate = button
        timePickerDialog.show(supportFragmentManager, "timePicker")
    }

    private fun goToEmployees() {
        binding.ivBackArrow.setOnClickListener {
            val intent = Intent(this@CommerceEditEmlpoyee, CommerceEmployees::class.java)
            startActivity(intent)
        }
    }

    private fun fillEmployeeData() {

        val employeeName = intent.getStringExtra("employeeName")
        val employeeLastName = intent.getStringExtra("employeeLastName")
        val employeeCheckIn = intent.getStringExtra("checkIn")
        val employeeCheckOut = intent.getStringExtra("checkOut")

        binding.edtEmployeeName.text = Editable.Factory.getInstance().newEditable(employeeName)
        binding.edtEmployeeLastName.text =
            Editable.Factory.getInstance().newEditable(employeeLastName)
        binding.btnCheckInTime.text =
            Editable.Factory.getInstance().newEditable(employeeCheckIn)
        binding.btnCheckOutTime.text =
            Editable.Factory.getInstance().newEditable(employeeCheckOut)
    }
}
package com.example.appointment.user.view.activities.appointments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.appointment.R
import com.example.appointment.commerce.view.activities.menu.employees.CommerceEmployees
import com.example.appointment.commerce.view.fragments.CustomAlertDialog
import com.example.appointment.commerce.viewModel.FireBaseManager
import com.example.appointment.databinding.ActivityConfirmAppointmentBinding
import com.example.appointment.user.view.activities.home.UserHome

class ConfirmAppointment : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmAppointmentBinding
    private lateinit var commerceName: String
    private lateinit var commerceType: String
    private lateinit var commerceId: String
    private lateinit var commerceFullAddress: String
    private lateinit var specialityName: String
    private lateinit var specialityId: String
    private lateinit var userId: String
    private lateinit var employeeId: String
    private var dateSelected: String = ""
    private var optionalRequest: String = ""
    private var timeSelected: String = ""
    private var fireBaseManager = FireBaseManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityConfirmAppointmentBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        commerceName = intent.getStringExtra("COMMERCE_NAME").toString()
        commerceType = intent.getStringExtra("COMMERCE_TYPE").toString()
        commerceId = intent.getStringExtra("COMMERCE_ID").toString()
        commerceFullAddress = intent.getStringExtra("COMMERCE_FULL_ADDRESS").toString()
        specialityName = intent.getStringExtra("SPECIALITY_NAME").toString()
        specialityId = intent.getStringExtra("SPECIALITY_ID").toString()
        employeeId = intent.getStringExtra("EMPLOYEE_ID").toString()
        dateSelected = intent.getStringExtra("APPOINTMENT_DATE").toString()
        timeSelected = intent.getStringExtra("APPOINTMENT_TIME").toString()
        userId = intent.getStringExtra("USER_ID").toString()

        fillAppointmentData()
        addAppointment()

    }

    private fun fillAppointmentData() {
        binding.txtCommerceName.text = commerceName
        binding.txtCommerceAddress.text = commerceFullAddress
        binding.txtAppointmentDate.text = dateSelected
        binding.txtAppointmentTime.text = timeSelected
    }

    private fun addAppointment() {

        binding.btnConfirmAppointment.setOnClickListener {

            if (binding.edtOptionalRequest.text.isNotEmpty()) {
                optionalRequest = binding.edtOptionalRequest.text.toString()
            }else{
                optionalRequest = ""
            }

            CustomAlertDialog.showAlertDialog(
                this@ConfirmAppointment,
                "¿Deseas confirmar la cita?",
                onAccept = {
                    try {
                        fireBaseManager.addAppointment(
                            dateSelected,
                            timeSelected,
                            commerceId,
                            commerceName,
                            employeeId,
                            optionalRequest,
                            specialityId,
                            userId
                        )
                        Toast.makeText(
                            this@ConfirmAppointment,
                            "La cita ha sido seleccionada correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent =
                            Intent(
                                this@ConfirmAppointment,
                                UserHome::class.java
                            )
                        startActivity(intent)
                    } catch (e: Exception) {
                        e
                    }
                }, onCancel = {

                }
            )
        }
    }
}
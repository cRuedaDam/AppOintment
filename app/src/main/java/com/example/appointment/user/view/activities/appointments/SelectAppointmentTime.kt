package com.example.appointment.user.view.activities.appointments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import com.example.appointment.databinding.ActivitySelectAppointmentTimeBinding

class SelectAppointmentTime : AppCompatActivity() {

    private lateinit var binding: ActivitySelectAppointmentTimeBinding
    private lateinit var commerceName: String
    private lateinit var commerceType: String
    private lateinit var commerceId: String
    private lateinit var specialityName: String
    private lateinit var specialityId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySelectAppointmentTimeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        commerceName = intent.getStringExtra("COMMERCE_NAME").toString()
        commerceType = intent.getStringExtra("COMMERCE_TYPE").toString()
        commerceId = intent.getStringExtra("COMMERCE_ID").toString()
        specialityName = intent.getStringExtra("SPECIALITY_NAME").toString()
        specialityId = intent.getStringExtra("SPECIALITY_ID").toString()

        goToSelectEmployee()
        selectDate()
    }

    private fun goToSelectEmployee() {
        binding.backArrowIcon.setOnClickListener {
            val intent =
                Intent(this@SelectAppointmentTime, SelectAppointmentEmployee::class.java)
            intent.putExtra("COMMERCE_NAME", commerceName)
            intent.putExtra("COMMERCE_TYPE", commerceType)
            intent.putExtra("COMMERCE_ID", commerceId)
            intent.putExtra("SPECIALITY_NAME", specialityName)
            intent.putExtra("SPECIALITY_ID", specialityId)
            startActivity(intent)
        }
    }

    private fun selectDate(){
        binding.btnSelectDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker(){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
                val formattedDate = "%d-%d-%d".format(selectedDayOfMonth, selectedMonth + 1, selectedYear)
                binding.btnSelectDate.text = formattedDate
            },
            year,
            month,
            currentDay
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }
}
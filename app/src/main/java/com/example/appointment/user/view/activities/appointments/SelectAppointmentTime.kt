package com.example.appointment.user.view.activities.appointments

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appointment.commerce.viewModel.FireBaseManager
import com.example.appointment.databinding.ActivitySelectAppointmentTimeBinding
import com.example.appointment.user.view.adapters.TimeAdapter
import com.example.appointment.user.view.adapters.TimeAdapter.OnItemSelectedListener
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale

class SelectAppointmentTime : AppCompatActivity(), OnItemSelectedListener {

    private lateinit var binding: ActivitySelectAppointmentTimeBinding
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
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val currentUser = firebaseAuth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySelectAppointmentTimeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        commerceName = intent.getStringExtra("COMMERCE_NAME").toString()
        commerceType = intent.getStringExtra("COMMERCE_TYPE").toString()
        commerceId = intent.getStringExtra("COMMERCE_ID").toString()
        commerceFullAddress = intent.getStringExtra("COMMERCE_FULL_ADDRESS").toString()
        specialityName = intent.getStringExtra("SPECIALITY_NAME").toString()
        specialityId = intent.getStringExtra("SPECIALITY_ID").toString()
        employeeId = intent.getStringExtra("EMPLOYEE_ID").toString()

        goToSelectEmployee()
        getTimeDataAndfillScheduleRV()
        selectDate()
        goToConfirmAppointment()
        Log.d("Current Activity:", "SelectAppointmentTime")
        Log.d("EmployeeId:", "$employeeId")
    }

    private fun goToConfirmAppointment() {

        if (currentUser != null){
            userId = currentUser.uid
        }

        binding.btnSelectAppointment.setOnClickListener {

            val intent = Intent(this@SelectAppointmentTime, ConfirmAppointment::class.java)

            intent.putExtra("COMMERCE_NAME", commerceName)
            intent.putExtra("COMMERCE_ID", commerceId)
            intent.putExtra("SPECIALITY_NAME", specialityName)
            intent.putExtra("SPECIALITY_ID", specialityId)
            intent.putExtra("EMPLOYEE_ID", employeeId)
            intent.putExtra("USER_ID", userId)
            intent.putExtra("APPOINTMENT_DATE", dateSelected)
            intent.putExtra("APPOINTMENT_TIME", timeSelected)
            intent.putExtra("COMMERCE_FULL_ADDRESS", commerceFullAddress)

            startActivity(intent)

        }
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
            intent.putExtra("EMPLOYEE_ID", employeeId)
            startActivity(intent)
        }
    }

    private fun fillScheduleRV(timeList: List<String>) {

        // Configura el RecyclerView con la lista de horas
        val rvTimes: RecyclerView = binding.rvSchedule
        val layoutManager = GridLayoutManager(this, 3)
        val adapter = TimeAdapter(timeList, this)

        rvTimes.layoutManager = layoutManager
        rvTimes.adapter = adapter

    }

    private fun calculateAvailableTimes(
        startTime: String,
        endTime: String,
        timeRequired: String
    ): List<String> {

        val timeList = mutableListOf<String>()
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()

        val startDateTime = formatter.parse(startTime)
        val endDateTime = formatter.parse(endTime)

        calendar.time = startDateTime
        timeList.add(formatter.format(calendar.time))

        while (calendar.time.before(endDateTime)) {
            // Agregar el tiempo requerido a la hora actual
            calendar.add(Calendar.MINUTE, timeRequired.toInt())
            // Asegurarse de que la hora calculada no sea después de la hora de fin
            if (calendar.time.before(endDateTime)) {
                timeList.add(formatter.format(calendar.time))
            }
        }

        return timeList
    }

    private fun getTimeDataAndfillScheduleRV() {

        fireBaseManager.getEmployeeWorkSchedule(employeeId, commerceId) { entryTime, exitTime ->
            fireBaseManager.getSpecialityTimeRequired(specialityId, commerceId) { timeRequired ->
                val timeList = calculateAvailableTimes(entryTime, exitTime, timeRequired.toString())
                fillScheduleRV(timeList)
            }
        }
    }

    private fun selectDate() {
        binding.btnSelectDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
                val formattedDate =
                    "%d-%d-%d".format(selectedDayOfMonth, selectedMonth + 1, selectedYear)
                binding.btnSelectDate.text = formattedDate
                binding.lyAvailableAppointments.visibility = View.VISIBLE
            },
            year,
            month,
            currentDay
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    override fun onItemSelected(item: String) {
        timeSelected = item
        Log.d("Selected Time:", "$timeSelected")
        dateSelected = binding.btnSelectDate.text.toString()
        Log.d("Selected Time:", "$dateSelected")
    }
}
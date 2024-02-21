package com.example.reservapp.commerce.view.activities.menu.editProfile

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.example.reservapp.R
import com.example.reservapp.commerce.view.fragments.TimePickerDialogFragment
import com.example.reservapp.databinding.ActivityChangeCommerceScheduleBinding
import java.util.Calendar
import java.util.Locale

class ChangeCommerceSchedule : AppCompatActivity() {

    private lateinit var binding: ActivityChangeCommerceScheduleBinding
    private lateinit var openTimeButton: Button
    private lateinit var closeTimeButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChangeCommerceScheduleBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        goToEditProfile()

        openTimeButton = binding.edtOpenTime
        closeTimeButton = binding.edtCloseTime

        openTimeButton.setOnClickListener {
            showTimePicker(openTimeButton)
        }

        closeTimeButton.setOnClickListener {
            showTimePicker(closeTimeButton)
        }
    }
    private fun showTimePicker(button: Button) {
        val timePickerDialog = TimePickerDialogFragment()
        timePickerDialog.buttonToUpdate = button
        timePickerDialog.show(supportFragmentManager, "timePicker")
    }

    private fun goToEditProfile(){
        binding.ivBackArrow.setOnClickListener {
            val intent = Intent(this@ChangeCommerceSchedule, CommerceEditProfile::class.java)
            startActivity(intent)
        }
    }
}
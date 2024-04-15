package com.example.appointment.common.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.appointment.R
import com.example.appointment.commerce.view.CommerceSignUp
import com.example.appointment.databinding.ActivityCustommerOrCommerceBinding
import com.example.appointment.user.view.CustomerSignUp

class CustommerOrCommerce : AppCompatActivity() {

    private lateinit var binding: ActivityCustommerOrCommerceBinding
    private lateinit var customerBtn: Button
    private lateinit var commerceButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCustommerOrCommerceBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        customerBtn = binding.btnCustomer
        commerceButton = binding.btnCommerce

        customerBtn.setOnClickListener() {
            val intent = Intent(this@CustommerOrCommerce, CustomerSignUp::class.java)
            startActivity(intent)
        }

        commerceButton.setOnClickListener() {
            val intent = Intent(this@CustommerOrCommerce, CommerceSignUp::class.java)
            startActivity(intent)
        }
    }
}
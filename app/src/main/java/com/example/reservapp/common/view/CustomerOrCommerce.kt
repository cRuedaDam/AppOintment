package com.example.reservapp.common.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.reservapp.commerce.view.activities.CommerceSignUp
import com.example.reservapp.user.CustomerSignUp
import com.example.reservapp.databinding.ActivityCustomerOrCommerceBinding

class CustomerOrCommerce : AppCompatActivity() {
    private lateinit var binding: ActivityCustomerOrCommerceBinding
    private lateinit var customerBtn: Button
    private lateinit var commerceButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCustomerOrCommerceBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        customerBtn = binding.btnCustomer
        commerceButton = binding.btnCommerce

        customerBtn.setOnClickListener(){
            val intent = Intent(this@CustomerOrCommerce, CustomerSignUp::class.java)
            startActivity(intent)

        }

        commerceButton.setOnClickListener(){
            val intent = Intent(this@CustomerOrCommerce, CommerceSignUp::class.java)
            startActivity(intent)
        }



    }
}
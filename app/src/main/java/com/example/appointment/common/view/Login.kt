package com.example.appointment.common.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.appointment.R
import com.example.appointment.databinding.ActivityLoginBinding
import com.example.reservapp.commerce.model.AuthCommerce
import com.example.reservapp.common.viewModel.Alerts
import com.example.reservapp.user.model.AuthUser
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private var alerts: Alerts = Alerts()
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var customer: AuthUser
    private lateinit var commerce: AuthCommerce
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var linkToSignUp: TextView = findViewById(R.id.txt_link_to_sign_up)

        //Recuperamos el Email del SignUp para mostrarlo en el edtEmail
        val emailFromSignUp = intent.getStringExtra("email")
        if (!emailFromSignUp.isNullOrBlank()) {
            binding.edtEmail.setText(emailFromSignUp)
        }

        firebaseAuth = FirebaseAuth.getInstance() // Inicializamos la instancia del autentificador de firebase

        //Ir a Registro de Usuario
        linkToSignUp.setOnClickListener() {
            var intent = Intent(this@Login, CustommerOrCommerce::class.java)
            startActivity(intent)
        }
    }
}
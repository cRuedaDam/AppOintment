package com.example.appointment.user.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.appointment.R
import com.example.appointment.common.view.Login
import com.example.appointment.databinding.ActivityCustomerSignUpBinding
import com.example.reservapp.common.viewModel.Alerts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class CustomerSignUp : AppCompatActivity() {
    private lateinit var binding: ActivityCustomerSignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var alert: Alerts = Alerts()
    private lateinit var userName: String
    private lateinit var userLastName: String
    private lateinit var userPhoneNumber: String
    private lateinit var userEmail: String
    private lateinit var userPassword: String
    private lateinit var userConfirmPassword: String
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCustomerSignUpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance() // Paquete de autentificación de Firebase

        //Creación de usuario en BBDD
        binding.btnSignUp.setOnClickListener() {

            userName = binding.edtName.text.toString()
            userLastName = binding.edtLastName.text.toString()
            userPhoneNumber = binding.edtPhoneNumber.text.toString()
            userEmail = binding.edtEmail.text.toString()
            userPassword = binding.edtPassword.text.toString()
            userConfirmPassword = binding.edtConfirmPassword.text.toString()

            if (userName.trim().isNotEmpty() && userLastName.trim().isNotEmpty()
                && userPhoneNumber.trim().isNotEmpty() && userEmail.trim().isNotEmpty()
                && userPassword.trim().isNotEmpty() && userConfirmPassword.trim().isNotEmpty()
            ) {
                if (userPassword.length >= 6) {
                    if (userPassword == userConfirmPassword) {
                        userSignUp()
                    } else {
                        alert.showAlert(
                            this@CustomerSignUp,
                            getString(R.string.error_msg),
                            getString(R.string.not_same_password)
                        )
                    }
                } else {
                    alert.showAlert(
                        this@CustomerSignUp,
                        getString(R.string.error_msg),
                        getString(R.string.short_password)
                    )
                }
            } else {
                alert.showAlert(
                    this@CustomerSignUp,
                    getString(R.string.error_msg),
                    getString(R.string.empty_fields)
                )
            }
        }

        //Ir a la pantalla Login
        binding.txtLinkToLogIn.setOnClickListener() {
            var intent = Intent(this@CustomerSignUp, Login::class.java)
            startActivity(intent)
        }
    }

    /**
     * Esta función hace un intent a la activity LogIn, recogiendo el email
     * del usuario.
     */
    private fun goToLogIn(email: String) {
        var intent = Intent(this@CustomerSignUp, Login::class.java).apply {
            putExtra("email", email)
        }
        startActivity(intent)
    }

    /**
     * Esta función se encarga de registrar a un usuario
     */
    private fun userSignUp() {
        alert.showLoading(this@CustomerSignUp, getString(R.string.wait_msg))
        firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    val userRef = db.collection("users").document(user!!.uid)

                    val userData = hashMapOf(
                        "user_name" to userName,
                        "user_last_name" to userLastName,
                        "user_email" to userEmail,
                        "user_phone_number" to userPhoneNumber
                    )

                    userRef.set(userData)
                        .addOnCompleteListener { userTask ->
                            if (userTask.isSuccessful) {
                                goToLogIn(userEmail)
                            } else {
                                alert.hideLoading()
                                alert.showAlert(
                                    this@CustomerSignUp,
                                    getString(R.string.error_msg),
                                    getString(R.string.unsuccessful_user_saved_data)
                                )
                            }
                        }
                } else {
                    alert.hideLoading()
                    alert.showAlert(
                        this@CustomerSignUp,
                        getString(R.string.error_msg),
                        getString(R.string.unsuccessful_user_creation)
                    )
                }
            }
    }
}
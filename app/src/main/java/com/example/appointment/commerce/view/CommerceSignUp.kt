package com.example.appointment.commerce.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.appointment.R
import com.example.appointment.common.view.Login
import com.example.appointment.databinding.ActivityCommerceSignUpBinding
import com.example.reservapp.common.viewModel.Alerts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class CommerceSignUp : AppCompatActivity() {

    private lateinit var binding: ActivityCommerceSignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var alert: Alerts = Alerts()
    private lateinit var commerceName: String
    private lateinit var commercePhoneNumber: String
    private lateinit var spinnerCategory: String
    private lateinit var streetType: String
    private lateinit var streetName: String
    private lateinit var streetNumber: String
    private lateinit var city: String
    private lateinit var state: String
    private lateinit var postalCode: String
    private lateinit var commerceEmail: String
    private lateinit var commercePassword: String
    private lateinit var commerceConfirmPassword: String
    private var db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCommerceSignUpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnSignUp.setOnClickListener() {
            commerceName = binding.edtName.text.toString()
            commercePhoneNumber = binding.edtPhoneNumber.text.toString()
            streetType = binding.edtStreetType.text.toString()
            streetName = binding.edtStreetName.text.toString()
            spinnerCategory = binding.spinnerCategories.selectedItem.toString()
            streetNumber = binding.edtStreetNumber.text.toString()
            city = binding.edtCity.text.toString()
            state = binding.edtState.text.toString()
            postalCode = binding.edtPostalCode.text.toString()
            commerceEmail = binding.edtEmail.text.toString()
            commercePassword = binding.edtPassword.text.toString()
            commerceConfirmPassword = binding.edtConfirmPassword.text.toString()

            if (commerceName.trim().isNotEmpty() && commercePhoneNumber.trim().isNotEmpty()
                && streetType.trim().isNotEmpty() && streetName.trim().isNotEmpty()
                && streetNumber.trim().isNotEmpty() && spinnerCategory != "- Tipo de actividad -"
                && city.trim().isNotEmpty() && state.trim().isNotEmpty()
                && postalCode.trim().isNotEmpty() && commerceEmail.trim().isNotEmpty()
                && commercePassword.trim().isNotEmpty() && commerceConfirmPassword.trim()
                    .isNotEmpty()
            ) {
                if (commercePassword.length >= 6) {
                    if (commercePassword == commerceConfirmPassword) {
                        commerceSignUp()
                    } else {
                        alert.showAlert(
                            this@CommerceSignUp,
                            getString(R.string.error_msg),
                            getString(R.string.not_same_password)
                        )
                    }
                } else {
                    alert.showAlert(
                        this@CommerceSignUp,
                        getString(R.string.error_msg),
                        getString(R.string.short_password)
                    )
                }
            } else {
                alert.showAlert(
                    this@CommerceSignUp,
                    getString(R.string.error_msg),
                    getString(R.string.empty_fields)
                )
            }
        }

        //Ir a la pantalla Login
        binding.txtLinkToLogIn.setOnClickListener() {
            var intent = Intent(this@CommerceSignUp, Login::class.java)
            startActivity(intent)
        }
    }

    /**
     * Esta función hace un intent a la activity LogIn, recogiendo el email
     * del usuario.
     */
    private fun goToLogIn(email: String) {
        var intent = Intent(this@CommerceSignUp, Login::class.java).apply {
            putExtra("email", email)
        }
        startActivity(intent)
    }

    /**
     * Esta función se encarga de registrar a un comercio
     */
    private fun commerceSignUp() {
        alert.showLoading(this@CommerceSignUp, getString(R.string.wait_msg))
        try {
            MainScope().launch(Dispatchers.IO) {
                firebaseAuth.createUserWithEmailAndPassword(commerceEmail, commercePassword)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val commerce =
                                FirebaseAuth.getInstance().currentUser // Apunta al usuario
                            var commerceRef = db.collection("commerces")
                                .document(commerce!!.uid) //Referencia a al BBDD en la coleccion users

                            val commerceData = hashMapOf(
                                "commerce_name" to commerceName,
                                "commerce_phone_number" to commercePhoneNumber,
                                "commerce_type" to spinnerCategory,
                                "commerce_email" to commerceEmail,
                                "address" to hashMapOf(
                                    "commerce_street_type" to streetType,
                                    "commerce_street_name" to streetName,
                                    "commerce_street_number" to streetNumber,
                                    "commerce_city" to city,
                                    "commerce_state" to state,
                                    "commerce_postal_code" to postalCode
                                )
                            )

                            commerceRef.set(commerceData)
                                .addOnSuccessListener {
                                    goToLogIn(commerceEmail)
                                }
                                .addOnFailureListener() {
                                    alert.hideLoading()
                                    alert.showAlert(
                                        this@CommerceSignUp,
                                        getString(R.string.error_msg),
                                        getString(R.string.unsuccessful_user_saved_data)
                                    )
                                }
                        } else {
                            alert.hideLoading()
                            alert.showAlert(
                                this@CommerceSignUp,
                                getString(R.string.error_msg),
                                getString(R.string.unsuccessful_user_creation)
                            )
                        }
                    }
                alert.hideLoading()
            }

        } catch (e: Exception) {
            alert.hideLoading()
            alert.showAlert(
                this@CommerceSignUp,
                getString(R.string.error_msg),
                getString(R.string.unsuccessful_user_creation)
            )
        }
    }
}
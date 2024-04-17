package com.example.appointment.common.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.appointment.R
import com.example.appointment.commerce.view.activities.home.CommerceHome
import com.example.appointment.databinding.ActivityLoginBinding
import com.example.reservapp.commerce.model.AuthCommerce
import com.example.reservapp.common.viewModel.Alerts
import com.example.reservapp.user.model.AuthUser
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

        //Autentificar Usuario por mail y contraseña
        binding.btnAccept.setOnClickListener() {
            email = binding.edtEmail.text.toString()
            password = binding.edtPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginWithEmailPassword(email, password)
            } else {
                alerts.hideLoading()
                alerts.showAlert(
                    this@Login,
                    getString(R.string.error_msg),
                    getString(R.string.empty_fields)
                )
            }
        }
    }

    /**
     * Esta función hace un intent a la pantalla principal de comercio
     * Recogiendo el email del usuario y el tipo de proveedor
     */
    private fun goCommerceHome(commerce: AuthCommerce) {
        val intent = Intent(this@Login, CommerceHome::class.java).apply {
            putExtra("commerce", commerce)
        }
        startActivity(intent)
    }

    /**
     * Esta función hace un intent a la pantalla principal de cliente
     * Recogiendo el email del usuario y el tipo de proveedor
     */
    //private fun goCustomerHome(customer: AuthUser) {
    /*private fun goCustomerHome(customer: AuthUser) {
        val intent = Intent(this@Login, CustomerHome::class.java).apply {
            putExtra("customer", customer)
        }
        startActivity(intent)
    }*/

    /**
     * Esta función realiza el inicio de sesión mediante email y password
     */
    private fun loginWithEmailPassword(email: String, password: String) {
        alerts.showLoading(this@Login, "Espere por favor...")

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this@Login) { task ->
                MainScope().launch(Dispatchers.Main) {
                    try {
                        task.getResultOrThrow() // Esto lanzará la excepción si la tarea no es exitosa
                        checkUserType() // Inicio de sesión exitoso, verificar el tipo de usuario
                    } catch (e: Exception) {
                        alerts.hideLoading()
                        alerts.showAlert(
                            this@Login,
                            getString(R.string.error_msg),
                            getString(R.string.login_fail)
                        )
                    }
                }
            }
    }

    // Extensión de la clase Task para obtener el resultado o lanzar la excepción si la tarea no tiene éxito
    private fun <T> Task<T>.getResultOrThrow(): T {
        if (isSuccessful) {
            return result!!
        } else {
            throw exception ?: RuntimeException("Task failed without exception")
        }
    }

    /**
     * Comprobamos si el Uid del usuario se encuentra en la BBDD
     */
    private fun checkUserType() {
        alerts.showLoading(this@Login, "Espere por favor...")
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            // Utilizar MainScope.launch para ejecutar la tarea en segundo plano
            MainScope().launch(Dispatchers.IO) {
                try {
                    val documentSnapshot =
                        FirebaseFirestore.getInstance().collection("users").document(userId).get()
                            .await()

                    if (documentSnapshot.exists()) {
                        // Esta variable recoge los datos de la clase AuthUser
                        customer = AuthUser(
                            userId = userId,
                            userEmail = documentSnapshot["user_email"].toString(),
                            userName = documentSnapshot["user_name"].toString(),
                            userLastName = documentSnapshot["user_last_name"].toString(),
                            userPhoneNumber = documentSnapshot["user_phone_number"].toString()
                        )
                        // Usuario regular, redirigir a la pantalla principal de usuarios
                        launch(Dispatchers.Main) {
                            //goCustomerHome(customer)
                            alerts.hideLoading()
                        }
                    } else {
                        // No es un usuario regular, verificar en la colección de comercios
                        checkCommerceType()
                    }
                } catch (e: Exception) {
                    launch(Dispatchers.Main) {
                        alerts.hideLoading()
                        alerts.showAlert(
                            this@Login,
                            getString(R.string.error_msg),
                            getString(R.string.user_not_found)
                        )
                    }
                }
            }
        }
    }


    /**
     * Comprobamos si el Uid del comercio se encuentra en la BBDD
     */
    private fun checkCommerceType() {

        val commerceId = FirebaseAuth.getInstance().currentUser?.uid
        if (commerceId != null) {
            // Utilizar MainScope.launch para ejecutar la tarea en segundo plano
            MainScope().launch(Dispatchers.IO) {
                try {
                    val documentSnapshot =
                        FirebaseFirestore.getInstance().collection("commerces").document(commerceId)
                            .get().await()

                    if (documentSnapshot.exists()) {
                        // Esta variable recoge los datos de la clase AuthCommerce
                        commerce = AuthCommerce(
                            commerceId = commerceId,
                            commerceName = documentSnapshot["commerce_name"].toString(),
                            commerceEmail = documentSnapshot["commerce_email"].toString(),
                            commercePhoneNumber = documentSnapshot["commerce_phone_number"].toString(),
                            commerceType = documentSnapshot["commerce_type"].toString(),
                            commerceStreetType = documentSnapshot["commerce_street_type"].toString(),
                            commerceStreetName = documentSnapshot["commerce_street_name"].toString(),
                            commerceStreetNumber = documentSnapshot["commerce_street_number"].toString(),
                            commerceCity = documentSnapshot["commerce_city"].toString(),
                            commerceState = documentSnapshot["commerce_state"].toString(),
                            commercePostalCode = documentSnapshot["commerce_postal_code"].toString()
                        )
                        // El usuario es un comercio, redirigir a la pantalla principal de comercio
                        launch(Dispatchers.Main) {
                            goCommerceHome(commerce)
                            alerts.hideLoading()
                        }
                    }else{
                        launch(Dispatchers.Main) {
                            alerts.hideLoading()
                            alerts.showAlert(
                                this@Login,
                                getString(R.string.error_msg),
                                getString(R.string.user_not_found)
                            )
                        }
                    }
                } catch (e: Exception) {
                    launch(Dispatchers.Main) {
                        alerts.hideLoading()
                        alerts.showAlert(
                            this@Login,
                            getString(R.string.error_msg),
                            getString(R.string.user_not_found)
                        )
                    }
                }
            }
        }
    }
}
package com.example.appointment.commerce.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appointment.R
import com.example.appointment.commerce.view.activities.menu.CommerceMenu
import com.example.appointment.commerce.view.adapters.AppointmentAdapter
import com.example.appointment.commerce.viewModel.FireBaseManager
import com.example.appointment.common.model.Appointment
import com.example.appointment.databinding.ActivityCommerceHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar

class CommerceHome : AppCompatActivity() {
    private lateinit var binding: ActivityCommerceHomeBinding
    private lateinit var rvAppointments: RecyclerView
    private lateinit var appointmentAdapter: AppointmentAdapter
    private lateinit var profileImage: ImageView
    private lateinit var profileImageUri: Uri
    private var appointmentList = ArrayList<Appointment>()
    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseManager: FireBaseManager = FireBaseManager()
    val currentCommerce = FirebaseAuth.getInstance().currentUser
    val commerceId = currentCommerce?.uid
    private var currentDate = getCurrentDate()
    private var todaysDate = getToday()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCommerceHomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fillRecyclerView(currentDate)
        appointmentsPreviousDay()
        appointmentsNextDay()
        goToMenu()
        chooseProfileImage()

        if (commerceId != null) {
            getCurrentCommerceData(commerceId)
        }

    }

    private fun chooseProfileImage() {
        profileImage = binding.ivCommerceImage
        profileImage.setOnClickListener {
            openGalery()
        }
    }

    /**
     * Esta función nos desplaza al menú cuando clicamos sobre el item Menu
     */
    private fun goToMenu() {
        binding.menuIcon.setOnClickListener {
            val intent = Intent(this@CommerceHome, CommerceMenu::class.java)
            startActivity(intent)
        }
    }

    /**
     * Esta función rellena el recyclerView con los datos recogidos desde la BBDD
     */
    private fun fillRecyclerView(selectedDate: String) {
        rvAppointments = binding.rvAppointments
        rvAppointments.layoutManager = LinearLayoutManager(this)

        if (commerceId != null) {
            firestore.collection("appointments")
                .whereEqualTo("commerce_id", commerceId)
                .get()
                .addOnSuccessListener { querySnapshot ->

                    for (doc in querySnapshot) {
                        val appointmentDate = doc.data["appointment_date"].toString()

                        if (isAppointmentForCurrentDate(appointmentDate, selectedDate)) {
                            val appointment = Appointment(
                                serviceId = doc.data["service_id"].toString(),
                                commerceId = doc.data["commerce_id"].toString(),
                                userId = doc.data["user_id"].toString(),
                                employeeId = doc.data["employee_id"].toString(),
                                appointmentDate = appointmentDate,
                                appointmentTime = doc.data["appointment_time"].toString(),
                                optionalRequest = doc.data["optional_request"].toString()
                            )

                            firebaseManager.getServiceNameById(appointment.serviceId) { serviceName ->
                                appointment.serviceId = serviceName
                                firebaseManager.getUserNameByUid(appointment.userId) { userName ->
                                    appointment.userId = userName
                                    appointmentList.add(appointment)
                                    appointmentList.sortBy { it.appointmentTime } // Ordenamos la lista por hora
                                    updateRecyclerView()
                                    Log.d(
                                        "CommerceHome1",
                                        "Appointment List Size: ${appointmentList.size}"
                                    )
                                    if (appointmentList.size > 0) {
                                        binding.txtNoAppointments.visibility = View.GONE
                                    } else {
                                        binding.txtNoAppointments.visibility = View.VISIBLE
                                    }
                                }
                            }
                        }
                    }

                    Log.d("CommerceHome3", "Appointment List Size: ${appointmentList.size}")

                    if (currentDate == todaysDate) {
                        binding.txtAppointments.text = "Hoy"
                    } else {
                        binding.txtAppointments.text = selectedDate
                    }
                }
        } else {
            // Manejar el caso en el que el usuario actual no sea un comercio
            // Puedes mostrar un mensaje o redirigir a la pantalla de inicio de sesión, por ejemplo.
        }
    }

    private fun updateRecyclerView() {
        if (!::appointmentAdapter.isInitialized) {
            appointmentAdapter = AppointmentAdapter(appointmentList)
            rvAppointments.adapter = appointmentAdapter
        } else {
            appointmentAdapter.notifyDataSetChanged()
            Log.d("CommerceHome", "Adapter notifyDataSetChanged called")
        }
    }

    private fun getCurrentDate(): String {
        val today = Calendar.getInstance()
        val day = today.get(Calendar.DAY_OF_MONTH)
        val month = today.get(Calendar.MONTH) + 1 // El mes comienza desde 0
        val year = today.get(Calendar.YEAR)
        return "$day-$month-$year"
    }

    private fun getToday(): String {
        val today = Calendar.getInstance()
        val day = today.get(Calendar.DAY_OF_MONTH)
        val month = today.get(Calendar.MONTH) + 1 // El mes comienza desde 0
        val year = today.get(Calendar.YEAR)
        return "$day-$month-$year"
    }

    private fun handleDateChange(daysToAdd: Int) {
        Log.d("CommerceHome", "Current Date: $currentDate")
        val calendar = Calendar.getInstance()
        val dateParts = currentDate.split("-")
        calendar.set(dateParts[2].toInt(), dateParts[1].toInt() - 1, dateParts[0].toInt())
        calendar.add(Calendar.DAY_OF_MONTH, daysToAdd)

        val newDate = "${calendar.get(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH) + 1}-${
            calendar.get(Calendar.YEAR)
        }"
        currentDate = newDate
        Log.d("CommerceHome", "New Date: $newDate")
        fillRecyclerView(newDate)
    }

    private fun appointmentsNextDay() {
        binding.ivNextDay.setOnClickListener {
            handleDateChange(1)
            appointmentList.clear()
        }
    }

    private fun appointmentsPreviousDay() {
        binding.ivPreviousDay.setOnClickListener {
            handleDateChange(-1)
            appointmentList.clear()
        }
    }


    private fun isAppointmentForCurrentDate(appointmentDate: String, currentDate: String): Boolean {
        return appointmentDate == currentDate
    }

    private fun getCurrentCommerceData(commerceId: String) {
        val commerceRef = firestore.collection("commerces").document(commerceId)
        commerceRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val commerceName = documentSnapshot.getString("commerce_name")
                    val commerceEmail = documentSnapshot.getString("commerce_email")
                    val commerceImageUrl = documentSnapshot.getString("commerce_picture")

                    binding.txtCommerceName.text = commerceName.toString()
                    binding.txtCommerceEmail.text = commerceEmail.toString()

                    if (!commerceImageUrl.isNullOrEmpty()) {
                        loadCommerceImage(commerceImageUrl)
                    }

                } else {
                    Log.d("FirestoreError", "El documento no existe")
                }
            }
            .addOnFailureListener { e ->

            }
    }

    private fun loadCommerceImage(imageUrl: String) {
        // Cargar la imagen usando Glide
        Glide.with(this)
            .load(imageUrl)
            .into(profileImage)
    }

    private fun openGalery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            profileImageUri = data.data!!

            // Establecer la imagen seleccionada en el ImageView
            profileImage.setImageURI(profileImageUri)

            // Subir la imagen a Firebase Storage y guardar la URL en Firestore
            uploadProfileImage(profileImageUri)
        }
    }

    private fun uploadProfileImage(imageUri: Uri) {
        val commerceId = currentCommerce?.uid
        if (commerceId != null) {
            val storageReference =
                FirebaseStorage.getInstance().reference.child("commerces/$commerceId/commerce_picture/profileImage.jpg")

            storageReference.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    // Obtener la URL de descarga de la imagen
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        // Guardar la URL de la imagen en Firestore
                        saveImageUriToFirestore(uri.toString(), commerceId)
                    }
                }
                .addOnFailureListener { exception ->
                    // Manejar errores en la subida de la imagen
                    Log.d("URLImage", "La imagen No se ha actualizado correctamente")
                }
        }
    }

    private fun saveImageUriToFirestore(imageUri: String, commerceId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val commerceRef = firestore.collection("commerces").document(commerceId)

        // Actualizar el campo 'picture' en el documento de comercio con la URL de la imagen
        commerceRef.update("commerce_picture", imageUri)
            .addOnSuccessListener {
                Log.d("URLImage", "La imagen se ha actualizado correctamente")
            }
            .addOnFailureListener { exception ->
                // Manejar la actualización no exitosa
                Log.d("URLImage", "La imagen No se ha actualizado correctamente")
            }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
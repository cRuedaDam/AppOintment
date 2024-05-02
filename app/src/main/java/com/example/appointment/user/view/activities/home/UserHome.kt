package com.example.appointment.user.view.activities.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appointment.commerce.view.adapters.AppointmentAdapter
import com.example.appointment.commerce.viewModel.FireBaseManager
import com.example.appointment.common.model.Appointment
import com.example.appointment.databinding.ActivityUserHomeBinding
import com.example.appointment.user.view.activities.appointments.AppointmentCategory
import com.example.appointment.user.view.activities.menu.UserMenu
import com.example.appointment.user.view.adapters.AppointmentUserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar

class UserHome : AppCompatActivity() {
    private lateinit var binding: ActivityUserHomeBinding
    private lateinit var rvAppointments: RecyclerView
    private lateinit var appointmentUserAdapter: AppointmentUserAdapter
    private lateinit var profileImage: ImageView
    private lateinit var profileImageUri: Uri
    private var appointmentList = ArrayList<Appointment>()
    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseManager: FireBaseManager = FireBaseManager()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val userId = currentUser?.uid
    private var currentDate = getCurrentDate()
    private var todayDate = getToday()
    private lateinit var txtNoAppointments: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityUserHomeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fillRecyclerView(currentDate)
        appointmentsPreviousDay()
        appointmentsNextDay()
        goToMenu()
        chooseProfileImage()
        makeAnAppointment()
        Log.d("Current Activity: ", "UserHome")

        if (userId != null) {
            getCurrentUserData(userId)
        }

    }

    private fun chooseProfileImage() {
        profileImage = binding.ivUserImage
        profileImage.setOnClickListener {
            openGalery()
        }
    }

    /**
     * Esta función nos desplaza al menú cuando clicamos sobre el item Menu
     */
    private fun goToMenu() {
        binding.menuIcon.setOnClickListener {
            val intent = Intent(this@UserHome, UserMenu::class.java)
            startActivity(intent)
        }
    }

    /**
     * Esta función rellena el recyclerView con los datos recogidos desde la BBDD
     */
    private fun fillRecyclerView(selectedDate: String) {
        appointmentList.clear()
        rvAppointments = binding.rvAppointments
        rvAppointments.layoutManager = LinearLayoutManager(this)

        if (userId != null) {
            firestore.collection("appointments")
                .whereEqualTo("user_id", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->

                    for (doc in querySnapshot) {
                        val appointmentDate = doc.data["appointment_date"].toString()

                        if (isAppointmentForCurrentDate(appointmentDate, selectedDate)) {
                            val appointment = Appointment(
                                appointmentId = doc.data["appointment_id"].toString(),
                                serviceId = doc.data["service_id"].toString(),
                                commerceId = doc.data["commerce_id"].toString(),
                                userId = doc.data["user_id"].toString(),
                                employeeId = doc.data["employee_id"].toString(),
                                commerceName = doc.data["commerce_name"].toString(),
                                appointmentDate = appointmentDate,
                                appointmentTime = doc.data["appointment_time"].toString(),
                                optionalRequest = doc.data["optional_request"].toString()
                            )


                            firebaseManager.getCommerceNameByUid(appointment.commerceId) { commerceName ->
                                appointment.commerceName = commerceName
                                firebaseManager.getServiceNameByIds(
                                    appointment.serviceId,
                                    appointment.commerceId
                                ) { serviceName ->
                                    appointment.serviceId = if (serviceName.isNullOrEmpty()) "Servicio no disponible" else serviceName //Si el servicio ha sido eliminado se trata la excepcion
                                    appointmentList.add(appointment)
                                    appointmentList.sortBy { it.appointmentTime }
                                    updateRecyclerView()
                                    Log.d(
                                        "UserHome1",
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

                    Log.d("UserHome3", "Appointment List Size: ${appointmentList.size}")

                    if (currentDate == todayDate) {
                        binding.txtAppointments.text = "Hoy"
                    } else {
                        binding.txtAppointments.text = selectedDate
                    }
                }
        } else {

        }
        handleNoAppointmentsVisibility()
    }

    private fun updateRecyclerView() {
        if (!::appointmentUserAdapter.isInitialized) {
            appointmentUserAdapter = AppointmentUserAdapter(appointmentList)
            rvAppointments.adapter = appointmentUserAdapter
        } else {
            appointmentUserAdapter.notifyDataSetChanged()
            Log.d("UserHome", "Adapter notifyDataSetChanged called")
        }
    }

    private fun handleNoAppointmentsVisibility() {
        txtNoAppointments = binding.txtNoAppointments

        if (appointmentList.isEmpty()) {
            txtNoAppointments.visibility = View.VISIBLE
        } else {
            txtNoAppointments.visibility = View.GONE
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
        Log.d("UserHome", "Current Date: $currentDate")
        val calendar = Calendar.getInstance()
        val dateParts = currentDate.split("-")
        calendar.set(dateParts[2].toInt(), dateParts[1].toInt() - 1, dateParts[0].toInt())
        calendar.add(Calendar.DAY_OF_MONTH, daysToAdd)

        val newDate = "${calendar.get(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH) + 1}-${
            calendar.get(Calendar.YEAR)
        }"
        currentDate = newDate
        Log.d("UserHome", "New Date: $newDate")
        fillRecyclerView(newDate)
    }

    private fun appointmentsNextDay() {
        binding.ivNextDay.setOnClickListener {
            handleDateChange(1)
        }
    }

    private fun appointmentsPreviousDay() {
        binding.ivPreviousDay.setOnClickListener {
            handleDateChange(-1)
        }
    }


    private fun isAppointmentForCurrentDate(appointmentDate: String, currentDate: String): Boolean {
        return appointmentDate == currentDate
    }

    private fun getCurrentUserData(userId: String) {
        val userRef = firestore.collection("users").document(userId)
        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val userName = documentSnapshot.getString("user_name")
                    val userEmail = documentSnapshot.getString("user_email")
                    val userImageUrl = documentSnapshot.getString("user_picture")

                    binding.txtUserName.text = userName.toString()
                    binding.txtUserEmail.text = userEmail.toString()

                    if (!userImageUrl.isNullOrEmpty()) {
                        loadUserImage(userImageUrl)
                    }

                } else {
                    Log.d("FirestoreError", "El documento no existe")
                }
            }
            .addOnFailureListener { e ->

            }
    }

    // Cargar la imagen usando Glide
    private fun loadUserImage(imageUrl: String) {
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
        val userId = currentUser?.uid
        if (userId != null) {
            val storageReference =
                FirebaseStorage.getInstance().reference.child("user/$userId/user_picture/profileImage.jpg")

            storageReference.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    // Obtener la URL de descarga de la imagen
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        // Guardar la URL de la imagen en Firestore
                        saveImageUriToFirestore(uri.toString(), userId)
                    }
                }
                .addOnFailureListener { exception ->
                    // Manejar errores en la subida de la imagen
                    Log.d("URLImage", "La imagen No se ha actualizado correctamente")
                }
        }
    }

    private fun saveImageUriToFirestore(imageUri: String, userId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val userRef = firestore.collection("users").document(userId)

        // Actualizar el campo 'picture' en el documento de comercio con la URL de la imagen
        userRef.update("user_picture", imageUri)
            .addOnSuccessListener {
                Log.d("URLImage", "La imagen se ha actualizado correctamente")
            }
            .addOnFailureListener { exception ->
                // Manejar la actualización no exitosa
                Log.d("URLImage", "La imagen No se ha actualizado correctamente")
            }
    }

    private fun makeAnAppointment(){
        binding.btnMakeAppointment.setOnClickListener {
            val intent = Intent(this@UserHome, AppointmentCategory::class.java)
            startActivity(intent)
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
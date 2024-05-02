package com.example.appointment.user.view.activities.appointments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appointment.commerce.model.Speciality
import com.example.appointment.commerce.viewModel.FireBaseManager
import com.example.appointment.databinding.ActivitySelectAppointmentSpecialityBinding
import com.example.appointment.user.view.adapters.SpecialitiesAdapter
import com.google.firebase.firestore.FirebaseFirestore

class SelectAppointmentSpeciality : AppCompatActivity(),
    SpecialitiesAdapter.OnSpecialitiesFilterListener {

    private lateinit var binding: ActivitySelectAppointmentSpecialityBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var specialitiesAdapter: SpecialitiesAdapter
    private val specialitiesList = ArrayList<Speciality>()
    private lateinit var rvSpecialities: RecyclerView
    private lateinit var edtSearch: EditText
    private var firebaseManager = FireBaseManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySelectAppointmentSpecialityBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        goToSelectCommerce()
        fillSpecialitiesRecyclerView()
        filteredSearch()
        Log.d("Current Activity: ", "SelectAppointmentSpeciality")
    }

    private fun fillSpecialitiesRecyclerView() {

        rvSpecialities = binding.rvSpecialities
        rvSpecialities.layoutManager = LinearLayoutManager(this)

        val commerceName = intent.getStringExtra("COMMERCE_NAME")
        val commerceType = intent.getStringExtra("COMMERCE_TYPE")

        if (!commerceName.isNullOrEmpty()) {
            // Obtener el ID del comercio por su nombre
            firebaseManager.getCommerceIdByName(commerceName) { commerceId ->
                if (commerceId != null) {
                    // Realizar la consulta a Firestore para obtener las especialidades del comercio por su ID
                    val specialitiesCollection = db.collection("commerces/$commerceId/specialities")
                    specialitiesCollection.addSnapshotListener { querySnapshot, error ->
                        if (error != null) {
                            Log.e("FirestoreError", "Error al obtener datos", error)
                            return@addSnapshotListener
                        }

                        if (querySnapshot != null) {
                            for (document in querySnapshot) {
                                val specialities = document.toObject(Speciality::class.java)
                                specialitiesList.add(specialities)
                            }
                        }

                        specialitiesAdapter = SpecialitiesAdapter(specialitiesList, commerceName, commerceType)
                        rvSpecialities.adapter = specialitiesAdapter
                    }
                } else {
                    // Manejar el caso en que no se encuentre ningún comercio con el nombre dado
                    Log.e(
                        "CommerceError",
                        "No se encontró ningún comercio con el nombre proporcionado"
                    )
                }
            }
        } else {
            // Manejar el caso en que no se proporcione ningún nombre de comercio
            Log.e("CommerceError", "No se proporcionó ningún nombre de comercio")
        }
    }

    private fun goToSelectCommerce() {
        val category = intent.getStringExtra("COMMERCE_TYPE")
        binding.backArrowIcon.setOnClickListener {
            val intent = Intent(this@SelectAppointmentSpeciality, AppointmentChooseCommerce::class.java)
            intent.putExtra("CATEGORY", category)
            startActivity(intent)
        }
    }

    private fun filteredSearch() {

        specialitiesAdapter =
            SpecialitiesAdapter(specialitiesList) // Iguala el adaptador a la lista recibida desde el Employee Adapter
        specialitiesAdapter.setOnSpecialitiesFilterListener(this) // Se le agrega un listener al adaptador
        rvSpecialities.adapter = specialitiesAdapter // El Recycler View se iguala al adaptador

        edtSearch = binding.etSearchSpeciality

        edtSearch.addTextChangedListener(object :
            TextWatcher { // Se le añade un TextListener al Buscador para realizar la acción de filtro cuando el texto cambie

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                specialitiesAdapter.filter(s.toString()) // LLamamos a la fucnión filter de EmployeeAdapter y le pasamos como parámetro el charSquence para que pueda realizar la búsqueda
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    override fun onFilterTextChanged(text: String) {
        specialitiesAdapter.filter(text)
    }
}
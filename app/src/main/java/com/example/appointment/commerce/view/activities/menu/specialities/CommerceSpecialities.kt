package com.example.appointment.commerce.view.activities.menu.specialities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appointment.commerce.model.Speciality
import com.example.appointment.commerce.view.activities.menu.CommerceMenu
import com.example.appointment.commerce.view.adapters.SpecialityAdapter
import com.example.appointment.databinding.ActivityCommerceSpecialitiesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CommerceSpecialities : AppCompatActivity(), SpecialityAdapter.OnSpecialityFilterListener {
    private lateinit var binding: ActivityCommerceSpecialitiesBinding
    private lateinit var rvSpecialities: RecyclerView
    private lateinit var specialityAdapter: SpecialityAdapter
    private lateinit var edtSearch: EditText
    private val specialitiesList = ArrayList<Speciality>()
    private val firestore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCommerceSpecialitiesBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fillSpecialitiesRecyclerView()
        goToAddSpeciality()
        goToMenu()
        filteredSearch()
    }

    private fun filteredSearch() {

        specialityAdapter =
            SpecialityAdapter(specialitiesList) // Iguala el adaptador a la lista recibida desde el Employee Adapter
        specialityAdapter.setOnSpecialityFilterListener(this) // Se le agrega un listener al adaptador
        rvSpecialities.adapter = specialityAdapter // El Recycler View se iguala al adaptador

        edtSearch = binding.etSearchSpeciality

        edtSearch.addTextChangedListener(object :
            TextWatcher { // Se le añade un TextListener al Buscador para realizar la acción de filtro cuando el texto cambie

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                specialityAdapter.filter(s.toString()) // LLamamos a la fucnión filter de EmployeeAdapter y le pasamos como parámetro el charSquence para que pueda realizar la búsqueda
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun goToAddSpeciality() {
        binding.btnAddSpeciality.setOnClickListener {
            val intent = Intent(this@CommerceSpecialities, CommerceAddSpeciality::class.java)
            startActivity(intent)
        }
    }

    private fun goToMenu() {
        binding.ivBackArrow.setOnClickListener {
            val intent = Intent(this@CommerceSpecialities, CommerceMenu::class.java)
            startActivity(intent)
        }
    }

    private fun fillSpecialitiesRecyclerView() {

        rvSpecialities = binding.rvSpecialities
        rvSpecialities.layoutManager = LinearLayoutManager(this)

        val currentCommerce = FirebaseAuth.getInstance().currentUser
        val commerceId = currentCommerce?.uid

        if (commerceId != null) {
            val specialitiesCollection = firestore.collection("commerces/$commerceId/specialities")
            specialitiesCollection.addSnapshotListener { querySnapshot, error ->

                if (error != null) {
                    Log.e("FirestoreError", "Error al obtener datos", error)
                    return@addSnapshotListener
                }

                for (document in querySnapshot!!) {
                    val speciality = document.toObject(Speciality::class.java)
                    specialitiesList.add(speciality)
                }

                specialityAdapter = SpecialityAdapter(specialitiesList)
                rvSpecialities.adapter = specialityAdapter

                if (specialitiesList.isEmpty()) {
                    binding.txtNoSpecialities.visibility = View.VISIBLE
                } else {
                    binding.txtNoSpecialities.visibility = View.GONE
                }
            }
        } else {

        }
    }

    override fun onFilterTextChanged(text: String) {
        specialityAdapter.filter(text)
    }
}
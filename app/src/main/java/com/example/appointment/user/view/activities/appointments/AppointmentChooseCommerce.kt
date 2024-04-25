package com.example.appointment.user.view.activities.appointments

import android.content.ContentValues.TAG
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
import com.example.appointment.databinding.ActivityChooseCommerceBinding
import com.example.appointment.user.model.Commerce
import com.example.appointment.user.view.adapters.CommerceAdapter
import com.google.firebase.firestore.FirebaseFirestore

class AppointmentChooseCommerce : AppCompatActivity(), CommerceAdapter.OnCommerceFilterListener {

    private lateinit var binding: ActivityChooseCommerceBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var commerceAdapter: CommerceAdapter
    private val commerceList = ArrayList<Commerce>()
    private lateinit var rvCommerces: RecyclerView
    private lateinit var edtSearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChooseCommerceBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fillCommercesRecyclerView()
        goToTypes()
        filteredSearch()
    }

    private fun filteredSearch() {

        commerceAdapter =
            CommerceAdapter(commerceList) // Iguala el adaptador a la lista recibida desde el Employee Adapter
        commerceAdapter.setOnCommerceFilterListener(this) // Se le agrega un listener al adaptador
        rvCommerces.adapter = commerceAdapter // El Recycler View se iguala al adaptador

        edtSearch = binding.etSearchEmployee

        edtSearch.addTextChangedListener(object :
            TextWatcher { // Se le añade un TextListener al Buscador para realizar la acción de filtro cuando el texto cambie

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                commerceAdapter.filter(s.toString()) // LLamamos a la fucnión filter de EmployeeAdapter y le pasamos como parámetro el charSquence para que pueda realizar la búsqueda
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    override fun onFilterTextChanged(text: String) {
        commerceAdapter.filter(text)
    }

    private fun fillCommercesRecyclerView() {

        rvCommerces = binding.rvCommerces
        rvCommerces.layoutManager = LinearLayoutManager(this)

        val category = intent.getStringExtra("CATEGORY")
        val commerceRef = db.collection("commerces")


        commerceRef.whereEqualTo("commerce_type", category).get()
            .addOnSuccessListener { documents ->
                val commerceList = mutableListOf<Commerce>()

                for (document in documents) {
                    val commerce = document.toObject(Commerce::class.java)
                    commerceList.add(commerce)
                }

                if (commerceList.isEmpty()) {
                    binding.txtNoCommerces.visibility = View.VISIBLE
                } else {
                    binding.txtNoCommerces.visibility = View.GONE
                }

                commerceAdapter = CommerceAdapter(commerceList)
                binding.rvCommerces.adapter = commerceAdapter
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting documents: ", exception)
            }
    }

    private fun goToTypes() {
        binding.backArrowIcon.setOnClickListener {
            val intent = Intent(this@AppointmentChooseCommerce, AppointmentCategory::class.java)
            startActivity(intent)
        }
    }
}

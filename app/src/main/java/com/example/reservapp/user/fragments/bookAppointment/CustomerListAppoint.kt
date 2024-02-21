package com.example.reservapp.user.fragments.bookAppointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservapp.R
import com.example.reservapp.user.adapter.ListCommerceAdapter
import com.example.reservapp.user.model.CommerceItem
import com.google.firebase.firestore.FirebaseFirestore
import java.util.HashMap
import java.util.Locale

/**
 * Fragment que muestra la lista de los comercios.
 * Contiene una barra de busqueda para buscar por region, nombre de comercio o los dos a la vez.
 * Contiene un argumento category de tipo String.
 */
class CustomerListAppoint : Fragment() {
    private lateinit var commerceName: SearchView
    private lateinit var regionNameCommerce: SearchView
    private lateinit var adapter: ListCommerceAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemsArrayListCommerce: ArrayList<CommerceItem>

    private val args:CustomerListAppointArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_list_appoint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(view)
        //categoria que viene pasada del directions del fragment anterior.
        val category: String = args.category
        //icono para cada una de las lineas del recyclerView
        val icon : Int = R.drawable.icon_store

        //la lista para la lista que va a mostrar el recyclerView
        itemsArrayListCommerce = ArrayList()
        //union de todos los elementos del recyclerView
        val layoutManager = LinearLayoutManager(context)
        recyclerView.isClickable = true
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = ListCommerceAdapter(itemsArrayListCommerce) { onItemSelected(it) }

        recyclerView.adapter = adapter

        //Se hace la llamada a la BBDD con el query buscando los comercios que sean de tipo del que se ha pasado del
        //anterior fragment y se llena la lista con la lista del comercio para que se muestre en el recyclerView y se notifica el
        //adapter de los cambios.
        FirebaseFirestore.getInstance().collection("commerces")
            .whereEqualTo("commerce_type", category).get().addOnSuccessListener { docs ->
                for (doc in docs) {
                    val address = doc.data["address"] as HashMap<*, *>
                    itemsArrayListCommerce.add(
                        CommerceItem(
                            icon,
                            doc.data["commerce_name"].toString(),
                            address["commerce_city"].toString()
                        )
                    )
                }
                adapter.notifyDataSetChanged()
            }
        //logica de la barra de búsqueda por nombre de comercio
        commerceName.setOnQueryTextListener(
            object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }
                override fun onQueryTextChange(text: String?): Boolean {
                    filterList(text, "commerce")
                    return true
                }
            }
        )
        //logica de la barra de búsqueda por nombre de region
        regionNameCommerce.setOnQueryTextListener(
            object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }
                override fun onQueryTextChange(text: String?): Boolean {
                    filterList(text, "region")
                    return true
                }
            }
        )
    }

    /**
     * Inicializa las views
     */
    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.storeCatListView)
        commerceName = view.findViewById(R.id.usrSearchCommerce)
        regionNameCommerce = view.findViewById(R.id.usrSearchRegion)

    }

    /**
     * Función para el recyclerView de cuando se clique una comercio de la lista del recyclerView
     * este pasara al siguiente fragment(orden declarado en el navigation_fragments.xml)
     * Pasa de este fragment a CustomerChoiceAppoint y este fragment tiene un argumento comm_name de tipo String.
     */
    private fun onItemSelected(itemList: CommerceItem) {
        val action = CustomerListAppointDirections.actionCustomerListAppointToCustomerChoiceAppoint(itemList.stringSelected)
        findNavController().navigate(action)
    }

    /**
     * esta función tiene como parametro el string que teclea el usuario y lo busca en la
     * lista de comercio o categoría teniendo el cuenta si el anterior esta lleno.
     */
      private fun filterList(query: String?, searchType: String?) {
            val otherFilter = if (searchType == "region") commerceName.query else regionNameCommerce.query

            if(query != null){
                val filteredList = ArrayList<CommerceItem>()
                for(i in itemsArrayListCommerce){
                    val type = if (searchType == "region") i.commerceRegion else i.stringSelected
                    val otherType = if (searchType == "region") i.stringSelected else i.commerceRegion

                    if (otherFilter.isNotEmpty()) {
                        if (type.lowercase(Locale.ROOT).contains(query.lowercase()) and otherType.lowercase(Locale.ROOT).contains(otherFilter.toString().lowercase()) ){
                            filteredList.add(i)
                        }
                    } else {
                        if (type.lowercase(Locale.ROOT).contains(query.lowercase()) ){
                            filteredList.add(i)
                        }
                    }
                }
               adapter.setFilteredList(filteredList)
            }
        }
}
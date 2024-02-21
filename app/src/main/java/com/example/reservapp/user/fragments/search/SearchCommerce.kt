package com.example.reservapp.user.fragments.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservapp.R
import com.example.reservapp.user.adapter.ListCommerceAdapter
import com.example.reservapp.user.model.CommerceItem
import com.google.firebase.firestore.FirebaseFirestore


/**
 * Fragment con la logica de la busqueda de comercio por nombre
 */
class SearchCommerce : Fragment() {
    private lateinit var searchBar: SearchView
    private lateinit var recyclerViewCommerce: RecyclerView
    private lateinit var adapter: ListCommerceAdapter
    private lateinit var itemArrayListCommerce: ArrayList<CommerceItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_commerce, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(view)


        itemArrayListCommerce = ArrayList()

        val layoutManager = LinearLayoutManager(context)
        recyclerViewCommerce.isClickable = true
        recyclerViewCommerce.layoutManager = layoutManager
        recyclerViewCommerce.setHasFixedSize(true)
        adapter = ListCommerceAdapter(itemArrayListCommerce) { onItemSelected(it) }

        recyclerViewCommerce.adapter = adapter
        //Logica de la Barra de busqueda
        searchBar.setOnQueryTextListener(
            object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(textSubmit: String?): Boolean {
                    //se pone la lista vacia para no tener duplicados
                    itemArrayListCommerce.clear()
                    //el texto que ha tecleado el usuario se pasa a minúsculas para no tener problemas de case-sensitive
                    val newTextSubmit = textSubmit.toString().lowercase()
                    //icono para cada item del recyclerView
                    val icon : Int = R.drawable.icon_store
                    //Llamada a la BBDD buscando los comercios
                    FirebaseFirestore.getInstance().collection("commerces").get().addOnSuccessListener { docs ->
                        for (doc in docs) {
                            //el nombre se pasa a minúsculas para np  tener problemas de case-sensitive
                            val commerceName = doc.data["commerce_name"].toString().lowercase()
                            //si el texto escrito por el usuario coincide con alguna de las letras del nombre del comercio se añade a la lista de comercios
                            //como objeto CommerceItem
                            //el address se obtiene como un hashMap porque en la BBDD esta como sus campos separados calle, num calle etc.
                            if (newTextSubmit in commerceName) {
                                val address = doc.data["address"] as HashMap<*, *>
                                itemArrayListCommerce.add(
                                    CommerceItem(
                                        icon,
                                        doc.data["commerce_name"].toString(),
                                        address["commerce_city"].toString()
                                    )
                                )
                            }
                        }
                        //se avisa al adapter de posibles cambios
                        adapter.notifyDataSetChanged()
                    }
                    //al finalizar la búsqueda se pone la barra de busqueda vacía y se quita el foco para evitar que se ponga un fondo gris que tiene
                    //la barra de busqeuda al cambiar a false.
                    searchBar.setQuery("", false)
                    searchBar.clearFocus()
                    return true
                }
                override fun onQueryTextChange(text: String?): Boolean {

                    return false
                }
            }
        )


    }

    /**
     * Inicializa las views
     */
    private fun initViews(view: View){
        recyclerViewCommerce = view.findViewById(R.id.nameCommerceRecyclerView)
        searchBar = view.findViewById(R.id.searchCommerce)

    }
    /**
     * Función de cambia del actual fragmnet a el fragment de selecionar la fecha servcio y empleado para agilizar la cita al usuario
     */

    private fun onItemSelected(itemList: CommerceItem) {
        val action = SearchCommerceDirections.actionSearchCommerceOrServiceToCustomerChoiceAppoint(itemList.stringSelected)
        findNavController().navigate(action)
    }

}
package com.example.reservapp.user.fragments.bookAppointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservapp.R
import com.example.reservapp.user.model.CustomerObjectItemList
import com.example.reservapp.user.adapter.ListCategoriesAdapter

/**
 * Fragment que muestra el listado de categorias que hay en la aplicacion
 * dentro esta el recyclerView y el adapter para poner la lista de categorias
 * orden de los fragments: res/navigation/nav_appointment_user.xml
 */

class CustomerCategoriesAppoint : Fragment(){
    private lateinit var adapter: ListCategoriesAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemsArrayList: ArrayList<CustomerObjectItemList>

    /**
     * Crea la vista y la muestra en el fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_categories_appoint, container, false)

    }

    /**
     * una vez creada la vista pasa a el siguiente metodo que una vez creada la vista
     * se inicializa la lista de categorias, se busca el id de el xml recyclerview(list_view) del
     * fragment_user_categories_appoint.xml
     * se le asigna el layoutManager que es un objeto que controla cómo los components se sitúan en el recyclerView.
     * decirle que tiene un tamaño fijo para optimizar el recyclerView.
     *Adapter que es del tipo ListCategoriesAdapter que contiene la logica del adapter que tiene como param
     * un ArrayList y un eventListerner.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.listView)
        recyclerView.isClickable = true
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter =
            ListCategoriesAdapter(itemsArrayList
            ) { itemListString ->
                onItemSelected(
                    itemListString
                )
            }
        recyclerView.adapter = adapter

    }

    /**
     * Inicializa la lista de categorias a mostrar. coge el iconos del drawable y el string almacenados en res/values/strings.xml
     */
    private fun initData(){
        itemsArrayList = ArrayList()
        itemsArrayList.add(CustomerObjectItemList(R.drawable.icon_beauty,  getString(R.string.aes)))
        itemsArrayList.add(CustomerObjectItemList(R.drawable.icon_hair_dresser, getString(R.string.hair_dresser)))
        itemsArrayList.add(CustomerObjectItemList(R.drawable.icon_pet, getString(R.string.pet)))
        itemsArrayList.add(CustomerObjectItemList( R.drawable.icon_brush, getString(R.string.nail_salon)))
        itemsArrayList.add(CustomerObjectItemList(R.drawable.icon_fisio, getString(R.string.fisio)))

    }

    /**
     * funcion que se le pasa el Objeto clicado de la lista del recyclerView
     * la action es la direcion en la que va a cambiar de fragment y el siguiente fragment tiene un argumento
     * tipo String llamado category que se le pasa y este sera el string del item clickado que se le pasa a el
     * siguiente fragment para que muestre solo los comercios con esta categoria
     * el navcontroler cambia de fragment y le pasa la action que es el string recogido.
     */
    private fun onItemSelected(itemList: CustomerObjectItemList){
        val action = CustomerCategoriesAppointDirections.actionCustomerCategoriesAppointToCustomerListAppoint(category = itemList.stringSelected)
        findNavController().navigate(action)
    }

}






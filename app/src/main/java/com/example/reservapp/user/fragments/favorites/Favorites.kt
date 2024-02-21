package com.example.reservapp.user.fragments.favorites

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservapp.R
import com.example.reservapp.user.adapter.FavoritesAdapter
import com.example.reservapp.user.model.CommerceItem
import com.example.reservapp.user.viewmodel.AppointmentViewModel


/**
 *Fragmento con toda la logica de los favoritos de los usuarios
 */
class Favorites : Fragment() {
    private lateinit var emptyState: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoritesAdapter
    private lateinit var favoritesList: ArrayList<CommerceItem>
    private val appointmentViewModel: AppointmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        favoritesList = ArrayList()
        //textView de texto que se muestra cuando no hay favoritos
        emptyState = view.findViewById(R.id.textViewEmptyFav)
        appointmentViewModel.getFavoritedCommerces()

        recyclerView = view.findViewById(R.id.rowRecyclerFav)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.isClickable = true
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = FavoritesAdapter(favoritesList) { onItemSelected(it) }
        recyclerView.adapter = adapter

        //Como en el viewModel la lista de appointments es de tipo MutableLiveData se puede utilizar el observe para mirar si la lista tiene un cambio y la actualiza.
        //se asigna la lista del viewModel a la lista favoritesList para que el adapter la muestre en la vista.
        appointmentViewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            favoritesList = favorites
            adapter.setFavoritesList(favoritesList)
            //si la lista esta vacía muestra que no hay comercios favoritos de lo contrario no muestra nada
            if(favorites.size == 0) {
                emptyState.text = getString(R.string.fav_empty_state)
            } else {
                emptyState.text = ""
            }
        }
    }

    /**
     * Función que va de este fragment a la pantalla de selecionar fecha, servicio y empleado para agilizar citas.
     */
    private fun onItemSelected(commerceName: String){
        val action = FavoritesDirections.actionFavoritesToCustomerChoiceAppoint(commName = commerceName)
        findNavController().navigate(action)
    }
}
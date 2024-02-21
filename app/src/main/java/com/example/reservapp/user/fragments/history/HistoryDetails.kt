package com.example.reservapp.user.fragments.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.reservapp.R
import com.example.reservapp.user.model.AppointmentItem
import com.example.reservapp.user.model.CommerceItem
import com.example.reservapp.user.viewmodel.AppointmentViewModel


/**
 *Fragment que muestra el detalle de citas pasadas
 */
class HistoryDetails : Fragment() {
    private lateinit var btnCategory: ImageButton
    private lateinit var btnSaveFav: Button
    private lateinit var btnRemoveFav: Button
    private lateinit var commerceName: TextView
    private lateinit var service: TextView
    private lateinit var date: TextView
    private lateinit var hour: TextView
    private lateinit var employee: TextView
    private val appointmentViewModel: AppointmentViewModel by activityViewModels()
    private lateinit var favoritesList: ArrayList<CommerceItem>
    private lateinit var appointmentSelected: AppointmentItem
    private val appointmentsViewModel: AppointmentViewModel by activityViewModels()
    private val args: HistoryDetailsArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(view)
        //de inicio si es favorito sera falso
        var isFavorited = false
        //lista de fovoritos del usuario
        favoritesList = ArrayList()
        //Objeto de tipo AppointmentItem que se le asigna lo que devuelve la función del viewModel de las citas y se le pasa como argumento el argumento pasado por el anterior fragment.
        appointmentSelected = appointmentsViewModel.getSingleAppointment(args.commerId)

        appointmentsViewModel.getCommerceType(appointmentSelected.commerceId)
        //Según el appointmentViewModel cogiendo el tipo de categoría esta el comercio se pone su icono correpondiente
        appointmentsViewModel.commerceTypeString.observe(viewLifecycleOwner) { category ->

            when(category){
                getString(R.string.aes) -> btnCategory.setImageResource(R.drawable.icon_beauty)
                getString(R.string.hair_dresser) -> btnCategory.setImageResource(R.drawable.icon_hair_dresser)
                getString(R.string.pet) -> btnCategory.setImageResource(R.drawable.icon_pet)
                getString(R.string.nail_salon) -> btnCategory.setImageResource(R.drawable.icon_brush)
                getString(R.string.fisio) -> btnCategory.setImageResource(R.drawable.icon_fisio)
            }
        }
        //Mediante el appointmentsViewModel internamente hace una llamada a la BBDD con el id de la especialidad para coger el nombre para poder mostrarlo en la vista
        appointmentsViewModel.serviceName.observe(viewLifecycleOwner) { name ->
            service.text = name
        }
        //Mediante el appointmentsViewModel internamente hace una llamada a la BBDD con el id del empleado para coger el nombre y poder mostrarlo en la vista
        appointmentsViewModel.employeeName.observe(viewLifecycleOwner) { name ->
            employee.text = name
        }

        // Llamada la BBDD para recuperar los favoritos del usuario
        appointmentViewModel.getFavoritedCommerces()

        // Observer que escucha el listado de favoritos y lo actualiza en el Fragment
        appointmentViewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            favoritesList = favorites
            for (favorite in favoritesList) {
                if (appointmentSelected.commerceName == favorite.stringSelected) {
                    isFavorited = true
                }
            }
            if(isFavorited) {
                btnRemoveFav.visibility = View.VISIBLE
            } else {
                btnSaveFav.visibility = View.VISIBLE
            }
        }
        //Añadir el  texto a las vistas con la información mediante los atributos del modelo appointmentItem
        commerceName.text = appointmentSelected.commerceName
        date.text = appointmentSelected.appointmentDate
        hour.text = appointmentSelected.appointmentTime

        //Si el comercio aún no esta guardado como en favoritos se muestra el botón de guardar en favoritos y se avisa al viewModel para que lo guarde en local y en la BBDD
        //y se muestra un toast de la acción realizada
        btnSaveFav.setOnClickListener {
            appointmentViewModel.addToFavorites(appointmentSelected.commerceName)
            btnSaveFav.visibility = View.INVISIBLE
            btnRemoveFav.visibility = View.VISIBLE
            Toast.makeText(context, getString(R.string.fav_commerce), Toast.LENGTH_SHORT).show()
        }
        //Si el comercio esta guardado en favoritos se muestra el botón de quitar de favoritos y se avisa al viewModel para que lo quite en local y en la BBDD
        //y se muestra un toast de la acción realizada
        btnRemoveFav.setOnClickListener {
            appointmentViewModel.deleteFromFavorites(appointmentSelected.commerceName)
            btnRemoveFav.visibility = View.INVISIBLE
            btnSaveFav.visibility = View.VISIBLE
            Toast.makeText(context, getString(R.string.eliminate_fav_commerce), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Inicialización de views
     */
    private fun initViews(view: View){
        btnCategory =  view.findViewById(R.id.imgButtonServHistory)
        btnSaveFav = view.findViewById(R.id.btnSaveFavAppoint)
        btnRemoveFav = view.findViewById(R.id.btnRemoveFavAppoint)
        commerceName= view.findViewById(R.id.textViewSubtitleHistory)
        service = view.findViewById(R.id.txtViewServHistory)
        date=view.findViewById(R.id.txtViewDateHistory)
        hour=view.findViewById(R.id.txtViewHourHistory)
        employee = view.findViewById(R.id.textViewNameEmployee)
    }
}
package com.example.reservapp.user.fragments.bookAppointment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservapp.R
import com.example.reservapp.user.DateConverter
import com.example.reservapp.user.model.AppointmentItem
import com.example.reservapp.user.adapter.CustomAppointmentsAdapter
import com.example.reservapp.user.viewmodel.AppointmentViewModel
import com.example.reservapp.user.viewmodel.CustomerViewModel
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * en viewUserHome es el inflator del fragment_user:home y de esta instancia
 * se busca el botón de pedir cita para ponerle un clicklistener.
 * al clicar el botón pedir cita se navega al fragment user_categories_appoint.xml
 * que contiene el título de categorias y el listado de estas.
 */
class CustomerHomeFragment: Fragment() {
    /**
     * dentro de esta funcion se prepara el layout para mostrarlo
     * y dentro cuando ya este creada se pone la logica de la busqueda de la
     * barra de busqueda.
     * Se pone un eventListener a el buton de pedir cita y con el findnavControler se pasa al siguiente fragment
     * orden de los fragments: res/navigation/nav_appointment_user.xml
     */
    private lateinit var customerName: TextView
    private lateinit var customerEmail: TextView
    private lateinit var emptyState: TextView
    private lateinit var imageBtn: ShapeableImageView
    private lateinit var imageBtnEdit: ImageView
    private lateinit var catAest: ImageView
    private lateinit var catHair: ImageView
    private lateinit var catPet: ImageView
    private lateinit var catNails: ImageView
    private lateinit var catFisio: ImageView
    private lateinit var adapter: CustomAppointmentsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemsAppointment: ArrayList<AppointmentItem>
    private val customerViewModel: CustomerViewModel by activityViewModels()
    private val appointmentsViewModel: AppointmentViewModel by activityViewModels()
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewUserHome = inflater.inflate(R.layout.fragment_customer_home, container, false)
        //boton con eventListener para comenzar la cita pasa al siguente fragment
        viewUserHome.findViewById<Button>(R.id.usrBtnAppointment).setOnClickListener {
            findNavController().navigate(R.id.customerCategoriesAppoint)
        }
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Se parsea la imagen, se guarda la imagen en Firebase storage y se pone la imagen en el perfil del usuario
            if (uri != null) {
                val imageUri = Uri.parse(uri.toString())
                val storageRef = FirebaseStorage.getInstance().getReference("Users/${customerViewModel.currentCustomer.value!!.userId}")
                storageRef.putFile(imageUri)
                imageBtn.setImageURI(uri)
            }
        }
        return viewUserHome
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(view)
        initData()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.appointmentsUserHome)
        recyclerView.isClickable = true
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = CustomAppointmentsAdapter(itemsAppointment) { appointmentItem ->
            onItemSelected(
                appointmentItem
            )
        }
        recyclerView.adapter = adapter

        catAest.setOnClickListener{navigateFromCatHome(getString(R.string.aes))}
        catHair.setOnClickListener { navigateFromCatHome(getString(R.string.hair_dresser)) }
        catPet.setOnClickListener { navigateFromCatHome(getString(R.string.pet)) }
        catNails.setOnClickListener { navigateFromCatHome(getString(R.string.nail_salon)) }
        catFisio.setOnClickListener { navigateFromCatHome(getString(R.string.fisio)) }

        imageBtnEdit.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.SingleMimeType("image/jpeg")))
        }

    }

    /**
     * Inicializa las  vistas
     */
    private fun initViews(view: View) {
        customerEmail = view.findViewById(R.id.homeUsrEmailEditTxt)
        customerName = view.findViewById(R.id.homeUsrNameEditTxt)
        emptyState = view.findViewById(R.id.textViewEmptyAppoint)
        imageBtn = view.findViewById(R.id.homeUsrPhoto)
        imageBtnEdit = view.findViewById(R.id.imageBtnEditPhoto)
        catAest = view.findViewById(R.id.usrIconBeauty)
        catHair = view.findViewById(R.id.usrIconHairDresser)
        catPet = view.findViewById(R.id.usrPet)
        catNails = view.findViewById(R.id.usrIconNails)
        catFisio = view.findViewById(R.id.usrIconFisio)
    }

    /**
     * del viewModel se recupera el usuario que es de tipo mutableLiveData, en el observer esta pendiente de los cambios del usuario.
     * recuperado el getCustomer se tiene acceso a el email y el nombre para mostrarlo en la view, crea una lista que utilizara el recycler view
     * se obtendra del appointmentModel las citas del usuario y se añadira a la lista del adapter para mostrarlo en el recyclerView y se le pone al adapoter que este
     * pendiente de cambios por si una cita ha sido añadida.
     */
    private fun initData(){
        imageBtn.setImageResource(R.drawable.user_mat)
        customerViewModel.getCustomer()
        //Empty state para verificar si esta vacía la lista de appointments
        var isEmpty = true
        customerViewModel.currentCustomer.observe(viewLifecycleOwner) { user ->
            customerEmail.text = user.userEmail
            customerName.text = getString(R.string.customerName, user.userName, user.userLastName)
        }

        customerViewModel.userImage.observe(viewLifecycleOwner) { image ->
                imageBtn.setImageBitmap(image)
        }
        val dateConv = DateConverter()
        itemsAppointment = ArrayList()

        appointmentsViewModel.getUserAppointments()

        appointmentsViewModel.appointments.observe(viewLifecycleOwner) { appointments ->
            for (appointment in appointments) {
                //parsea la fecha
                val parsedAppointmentDate = convertStringToDate(appointment.appointmentDate)
                //Verifica si la fecha es hoy
                val comparedDates = parsedAppointmentDate.compareTo(LocalDate.now())
                //Verifica si la hora es mayor a la actual para no mostrar las citas de horas pasadas del mismo día
                val boolTime = LocalTime.parse(appointment.appointmentTime) > LocalTime.now()
                val conditionForToday = comparedDates == 0 && boolTime
                //añade a la lista de appointments pendientes si la fecha es igual a hoy o posterior y si la fecha es de hoy y la hora no ha pasado.
                if (comparedDates > 0 || (conditionForToday) ) {
                    itemsAppointment.add(appointment)
                    isEmpty = false
                }

            }
            //si la lista esta vacía muestra que no hay citas de lo contrario no muestra nada
            if(isEmpty) {
                emptyState.text = getString(R.string.home_empty_state)
            } else {
                emptyState.text = ""
            }
            //ordena lista por fechas
            itemsAppointment.sortBy{
                LocalDate.parse(it.appointmentDate,DateTimeFormatter.ofPattern(getString(R.string.date_pattern)) )
            }
            adapter.notifyDataSetChanged()
        }
    }

    /**
     * esta funcion hace que se cambie de fragment que pase al siguente (del navigation_fragments) que sera el que muestre el detalle de la cita y poder anular cita
     * que al pasar a la siguente la siguiente tiene que recibir un argumento de tipo string que es el id del appointment para poder buscar la informacion en la base de datos
     *
     */
    private fun onItemSelected(appointmentItem: AppointmentItem){
        val action = CustomerHomeFragmentDirections.actionCustomerHomeToDetailsAppointment(appointmentId = appointmentItem.appointmentId)
        findNavController().navigate(action)
    }
    /**
     * Convertimos el string de fecha a tipo LocalDate para comparar y mostrar solo fechas futuras
     */
    private fun convertStringToDate (date: String): LocalDate {
        val format = DateTimeFormatter.ofPattern(getString(R.string.date_pattern))
        return LocalDate.parse(date, format)
    }

    /**
     * pasa de este fragment al siguiente en el orden del navigation_fragments.xml, el siguiente recibe un argumento category de tipo String
     */
    private fun navigateFromCatHome(category: String){
        val action = CustomerHomeFragmentDirections.actionCustomerHomeToCustomerListAppoint(category)
        findNavController().navigate(action)
    }
}
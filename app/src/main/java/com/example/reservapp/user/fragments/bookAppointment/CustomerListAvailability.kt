package com.example.reservapp.user.fragments.bookAppointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reservapp.R
import com.example.reservapp.user.adapter.AvailabilityAppointAdapter
import com.example.reservapp.user.viewmodel.AppointmentViewModel

/**
 * en esta clase se muestra el recyclerview con la citas disponibles con los datos pasados del anterior fragment fecha, servicio y empleado
 */
class CustomerListAvailability : Fragment() {
    private lateinit var commerceTitle: TextView
    private lateinit var appointmentTitle: TextView
    private lateinit var appointSubTitle: TextView
    private lateinit var appointmentDate: TextView
    private lateinit var adapter: AvailabilityAppointAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var availabilityAppointments: ArrayList<String>
    private val appointmentViewModel: AppointmentViewModel by activityViewModels()
    private val args:CustomerListAvailabilityArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_list_availability, container, false)
    }

    /**
     * con el appointmentViewModel se recupera la fecha y del argumento que recibe el fragment se recupera con el args
     * para mostrarla en la view
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        commerceTitle = view.findViewById(R.id.usrAppointCommTitle)
        appointmentTitle = view.findViewById(R.id.textViewTitle)
        appointSubTitle = view.findViewById(R.id.usrAppointCommSubtitle)
        appointmentDate = view.findViewById(R.id.textViewSubtitle)
        appointmentDate.text = appointmentViewModel.appointment.value!!.appointmentDate
        commerceTitle.text = appointmentViewModel.appointment.value!!.commerceName
        appointSubTitle.text = args.commerceAddress
        availabilityAppointments = ArrayList()

        val layoutInflater = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recyclerViewAvailability)
        recyclerView.isClickable = true
        recyclerView.layoutManager = layoutInflater
        recyclerView.setHasFixedSize(true)
        adapter = AvailabilityAppointAdapter(availabilityAppointments) { onItemSelected(it) }
        recyclerView.adapter = adapter

        appointmentViewModel.getAvailabilityHours()

        appointmentViewModel.availabilityHours.observe(viewLifecycleOwner) { availabilityHours ->
            adapter.setAvailabilityHours(availabilityHours)
        }
    }

    /**
     * realiza la navigation al siguente fragment el cual tiene que recibir un parametro que es la hora para añadirlo en el modelo
     * para poder recuperarla y toner  el texto en la vista de summary
     */
    private fun onItemSelected(itemHour: String){
        appointmentViewModel.appointment.value!!.appointmentTime = itemHour

        val action = CustomerListAvailabilityDirections.actionCustomerListAvailabilityToCustomerSummaryAppoint(hour = itemHour)
        findNavController().navigate(action)
    }
}
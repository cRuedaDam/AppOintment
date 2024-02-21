package com.example.reservapp.user.fragments.history

import android.os.Bundle
import android.util.Log
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
import com.example.reservapp.user.DateConverter
import com.example.reservapp.user.adapter.CustomAppointmentsHistorialAdapter
import com.example.reservapp.user.model.AppointmentItem
import com.example.reservapp.user.viewmodel.AppointmentViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


/**
 *Fragment que muestra el historial (listado) del usuario
 */
class Historial : Fragment() {
    private lateinit var emptyState: TextView
    private lateinit var adapter: CustomAppointmentsHistorialAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var appointments: ArrayList<AppointmentItem>
    private val appointmentViewModel: AppointmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_historial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        appointments = ArrayList()
        //textView de texto que se muestra cuando no hay citas pasadas
        emptyState = view.findViewById(R.id.textViewEmptyHist)

        recyclerView = view.findViewById(R.id.rowHistorial)

        val layoutManager = LinearLayoutManager(context)
        recyclerView.isClickable = true
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = CustomAppointmentsHistorialAdapter(appointments) { onItemSelected(it)}

        recyclerView.adapter = adapter


        filterAppointmentsByDate(appointmentViewModel.appointments.value!!)

        adapter.notifyDataSetChanged()

    }
    /**
     * Filtramos el listado de Citas del cliente y devolvemos solo las fechas pasadas
     */
    private fun filterAppointmentsByDate (appointments: ArrayList<AppointmentItem>) {
        val dateConverter = DateConverter()
        var filteredAppointments: ArrayList<AppointmentItem> = ArrayList()
        for (appointment in appointments) {
            //pasea la fecha y hora
            val parsedAppointmentDate = dateConverter.convertStringToDate(appointment.appointmentDate)
            //Verifica si la fecha es hoy
            val comparedDates = parsedAppointmentDate.compareTo(LocalDate.now())
            //Verifica si la hora es mayor a la actual para no mostrar las citas de horas pasadas del mismo día
            val boolTime = LocalTime.parse(appointment.appointmentTime) < LocalTime.now()
            //Verifica si es hoy y si la hora ya es pasada
            val conditionForToday = comparedDates == 0 && boolTime

            if (comparedDates < 0 || (conditionForToday)) {
                filteredAppointments.add(appointment)
            }
        }
        if(filteredAppointments.isEmpty()) {
            emptyState.text = getString(R.string.hist_empty_state)
        } else {
            emptyState.text = ""
        }
        //ordena lista por fechas
        filteredAppointments.sortBy{
            LocalDate.parse(it.appointmentDate, DateTimeFormatter.ofPattern(getString(R.string.date_pattern)) )
        }
        adapter.setFilteredAppointments(filteredAppointments)
    }

    /**
     *Función que al clickar en un item del recyclerView del historial va a el detalle de esa cita pasada.
     * Params itemList de tipo AppointmentItem.
     * El siguiente fragment pide un argumento de appointmentId de tipo String.
     */
    private fun onItemSelected(itemList: AppointmentItem) {
        val action = HistorialDirections.actionHistorialToHistoryDetails(commerId = itemList.appointmentId)
        findNavController().navigate(action)
    }

}
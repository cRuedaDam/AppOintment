package com.example.reservapp.user.fragments.bookAppointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.reservapp.R
import com.example.reservapp.user.DateConverter
import com.example.reservapp.user.viewmodel.AppointmentViewModel
import com.example.reservapp.user.viewmodel.CustomerViewModel
import com.google.android.material.textfield.TextInputEditText

/**
 * Fragment que muestra el resumen de la cita con todos los datos recuperados de los argumentos pasados de los argumentos, customerViewModel y el appointmentViewModel
 */
class CustomerSummaryAppoint : Fragment() {
    private lateinit var commerceName: TextView
    private lateinit var infoTitle: TextView
    private lateinit var serviceAppointment: TextView
    private lateinit var dateAppointment: TextView
    private lateinit var hourAppointment: TextView
    private lateinit var nameClient: TextView
    private lateinit var emailClient: TextView
    private lateinit var phoneNumClient: TextView
    private lateinit var btnBookAppointment: Button
    private lateinit var imgService: ImageButton
    private lateinit var optionalRequest: TextInputEditText

    private val customerViewModel: CustomerViewModel by activityViewModels()
    private val appointmentViewModel: AppointmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_summary_appoint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(view)

        val customerInfo = customerViewModel.currentCustomer.value!!
        val appointmentInfo = appointmentViewModel.appointment.value!!
        val serviceName = appointmentViewModel.serviceName.value

        val dateString = DateConverter()
        //se le añade el texto a los textViews para ver el resumen de la cita
        commerceName.text = appointmentInfo.commerceName
        serviceAppointment.text = serviceName
        infoTitle.text = getString(R.string.resev_usr_details)
        dateAppointment.text = dateString.convertStringToDateString(appointmentInfo.appointmentDate)
        hourAppointment.text = appointmentInfo.appointmentTime
        nameClient.text = customerInfo.userName
        emailClient.text = customerInfo.userEmail
        phoneNumClient.text = customerInfo.userPhoneNumber

        when(appointmentViewModel.commerceTypeString.value.toString()){
            getString(R.string.aes) -> imgService.setImageResource(R.drawable.icon_beauty)
            getString(R.string.hair_dresser) -> imgService.setImageResource(R.drawable.icon_hair_dresser)
            getString(R.string.pet) -> imgService.setImageResource(R.drawable.icon_pet)
            getString(R.string.nail_salon) -> imgService.setImageResource(R.drawable.icon_brush)
            getString(R.string.fisio) -> imgService.setImageResource(R.drawable.icon_fisio)
        }

        btnBookAppointment.setOnClickListener {
            appointmentViewModel.appointment.value!!.userId = customerInfo.userId
            appointmentViewModel.appointment.value!!.optionalRequest = optionalRequest.text.toString()
            // Save in data base la cita
            appointmentViewModel.addAppointment(appointmentInfo)
            //se elimina el stack
            parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            //evita que al acabar la cita se pueda volver para atras.
            activity?.onBackPressedDispatcher
            //vuelve al home
            findNavController().navigate(R.id.customerHome)
        }

    }

    /**
     * Inicializa las views
     */
    private fun initViews(view: View){
        commerceName = view.findViewById(R.id.usrAppointCommSubtitle)
        serviceAppointment = view.findViewById(R.id.textViewService)
        dateAppointment = view.findViewById(R.id.textViewDate)
        hourAppointment = view.findViewById(R.id.textViewHour)
        infoTitle = view.findViewById(R.id.textViewTitleSummary)
        nameClient = view.findViewById(R.id.textViewNameUser)
        emailClient = view.findViewById(R.id.textViewEmailUser)
        phoneNumClient = view.findViewById(R.id.textViewPhoneUser)
        optionalRequest = view.findViewById(R.id.textInputOptional)
        imgService = view.findViewById(R.id.imageButtonService)
        btnBookAppointment = view.findViewById(R.id.btnFinishAppointment)
    }

}
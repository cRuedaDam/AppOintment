package com.example.reservapp.user.fragments.bookAppointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.reservapp.R
import com.example.reservapp.user.DatePicker
import com.example.reservapp.user.model.AppointmentItem
import com.example.reservapp.user.viewmodel.AppointmentViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.HashMap

class CustomerChoiceAppoint : Fragment() {
    private lateinit var datePicker: EditText
    private lateinit var spinnerService: Spinner
    private lateinit var spinnerEmployee: Spinner
    private lateinit var titleCommerce: TextView
    private lateinit var addressCommerce: TextView
    private lateinit var btnAccept: Button
    private lateinit var listSpecialtyCommerce: ArrayList<String>
    private lateinit var listEmployeeCommerce: ArrayList<String>
    private lateinit var mapSpecialtyId: HashMap<String, String>
    private lateinit var mapEmployeesId: HashMap<String, String>
    private lateinit var selectedService: String
    private lateinit var selectedEmployee: String
    private  var selectedDate: String = ""
    private val appointmentViewModel: AppointmentViewModel by activityViewModels()
    private val args:CustomerChoiceAppointArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewUserChoice = inflater.inflate(R.layout.fragment_customer_choice_appoint, container, false)
        //eventListener en el boton de continuar a la otra pantalla verifica si se ha seleccionado la fecha, servicio y empleado
        //se tiene que quitar
        viewUserChoice.findViewById<Button>(R.id.btnToAvailability).setOnClickListener {
            if (selectedDate != "" && selectedService != "" && selectedEmployee != "") {
                appointmentViewModel.appointment.value!!.appointmentDate = selectedDate
                val action = CustomerChoiceAppointDirections.actionCustomerChoiceAppointToCustomerListAvailability(commerceAddress = addressCommerce.text.toString())
                findNavController().navigate(action)
            }
        }

        return viewUserChoice
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(view)
        //parametro que tiene el fragment al utilizar el navigation y el argumento que recibe del fragmento anterior
        val commName: String = args.commName
        enabledDisabledButton()
        datePicker.setOnClickListener{
            openDialogDataPicker()
        }
        FirebaseFirestore.getInstance().collection("commerces")
            .whereEqualTo("commerce_name", commName).get().addOnSuccessListener { docs ->
                for (doc in docs) {
                    val commId = doc.id
                    appointmentViewModel.appointment.value = AppointmentItem(
                        "",
                        "",
                        "",
                        "",
                        "",
                        commId,
                        "",
                        commName,
                        ""
                    )
                    appointmentViewModel.commerceTypeString.value = doc.data["commerce_type"].toString()
                    populateServiceSpinner(commId)
                    titleCommerce.text = doc.data["commerce_name"].toString()
                    val address = doc.data["address"] as HashMap<*, *>
                    addressCommerce.text =
                        getString(
                            R.string.addressCommerce,
                            address["commerce_street_type"],
                            address["commerce_street_name"],
                            address["commerce_street_number"],
                            address["commerce_postal_code"],
                            address["commerce_city"]
                        )
                }
            }
    }

    /**
     * Función que deshabilita el botón en el caso que no se haya selecionado la fecha porque los spinner ya tiene selcionado con lo que viene de la BBDD.
     */
    private fun enabledDisabledButton() {
        if (selectedDate == "") {
            btnAccept.isClickable = false
            context?.let { ContextCompat.getColor(it, R.color.md_theme_light_outlineVariant) }
                ?.let { btnAccept.setBackgroundColor(it) }
        }else{
            context?.let { ContextCompat.getColor(it, R.color.seed) }
                ?.let { btnAccept.setBackgroundColor(it) }
        }
    }

    private fun initViews(view: View){
        datePicker = view.findViewById(R.id.usrAppointDateChooser)
        spinnerService = view.findViewById(R.id.spinnerServices)
        spinnerEmployee = view.findViewById(R.id.spinnerEmployees)
        btnAccept = view.findViewById(R.id.btnToAvailability)
        titleCommerce = view.findViewById(R.id.usrAppointTitle)
        addressCommerce = view.findViewById(R.id.usrAppointSubtitle)
        datePicker = view.findViewById(R.id.usrAppointDateChooser)
    }

    /**
     * Función que hace una llamada a la BBDD para buscar los servicios que tiene el commercio (commID) selecionado por el usuario
     * y se añade a una lista el nombre y en un map el nombre y el id de la especialidad para guaradarla en la cita al finalizar la elección de esta.
     * Se le avisa al adapter de los cambios para que actualice la lista del spinner.
     * y se asigna al spinner el adapter para mostrar las especialidades.
     * Cuando se seleciona un item del spinner de especialidades tiene un event listener que va a llenar el siguiente
     * spinner de empleado según la especialidad elegida.
     */
    private fun populateServiceSpinner(commId: String){

        listSpecialtyCommerce = ArrayList()
        mapSpecialtyId = HashMap()
        val adapter = context?.let { ArrayAdapter(it, android.R.layout.simple_list_item_1, listSpecialtyCommerce) }
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        FirebaseFirestore.getInstance().collection("commerces").document(commId).collection("specialities").get().addOnSuccessListener{ docs ->
            for (doc in docs) {
                listSpecialtyCommerce.add(doc.data["name"].toString())
                mapSpecialtyId[doc.data["name"].toString()] = doc.id
            }
            adapter?.notifyDataSetChanged()
        }
        spinnerService.adapter = adapter
        spinnerService.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adaptView: AdapterView<*>?, view: View?, position: Int, idPosition: Long) {
                if (adaptView != null) {
                    val service = adaptView.getItemAtPosition(position).toString()
                    populateEmployeeSpinner(commId, service)
                    selectedService = service
                    appointmentViewModel.appointment.value!!.serviceId= mapSpecialtyId[service].toString()
                    appointmentViewModel.serviceName.value = service
                }
            }
            override fun onNothingSelected(view: AdapterView<*>?){}
        }
    }

    /**
     * Se rellena el spinner con la lista de empleados.
     * Recibe como argumentos el id del comercio y el string del servicio, para hacer una query  a la BBDD que coincida con el id del comercio
     * y el id de la especialidad sea igual a la id de la especialidad selecionada y según esta guardar los empleados para mostrarlos en el spinner.
     */
    private fun populateEmployeeSpinner(commId: String, service: String){
        listEmployeeCommerce = ArrayList()
        val specialtyId = mapSpecialtyId[service]
        mapEmployeesId = HashMap()

        specialtyId.toString()
        val adapter = context?.let { ArrayAdapter(it, android.R.layout.simple_list_item_1, listEmployeeCommerce) }
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        FirebaseFirestore.getInstance().collection("commerces").document(commId).collection("employees")
            .whereEqualTo("specialities", specialtyId).get().addOnSuccessListener{ docs ->
                for (doc in docs) {
                    listEmployeeCommerce.add(doc.data["name"].toString())
                    mapEmployeesId[doc.data["name"].toString()] = doc.id
                }
                adapter?.notifyDataSetChanged()
            }

        spinnerEmployee.adapter = adapter
        spinnerEmployee.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adaptView: AdapterView<*>?, view: View?, position: Int, idPosition: Long) {
                if (adaptView != null) {
                    val employee = adaptView.getItemAtPosition(position).toString()
                    selectedEmployee = employee
                    appointmentViewModel.appointment.value!!.employeeId = mapEmployeesId[employee].toString()
                    appointmentViewModel.employeeName.value = employee
                }
            }
            override fun onNothingSelected(view: AdapterView<*>?) {}
        }
    }

    /**
     * función que llama la clase DatePicker que contiene la logica de la fecha que como parametro es una
     * funcion lambda.
     */
    private fun openDialogDataPicker(){
        val datePicker = DatePicker { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(parentFragmentManager, "datePicker")

    }

    /**
     * Despúes de selecionar la fecha se muestra la fecha en la view
     * y se guarda en el modelo la fecha para recuperarla más adelante al final de la cita para el resumen de la cita
     * y se chequea si esta llena la fecha para activar el boton y continuar al siguente fragment
     */
    private fun onDateSelected(day:Int, month:Int, year:Int){
        //se muestra la fecha seleccionada en el editText
        var newDay = if(day < 10) "0$day" else day
        var newMonth = if(month < 10) "0${month + 1}" else month+1
        datePicker.setText("$newDay-$newMonth-$year")
        //mes mas uno porque empieza en zero
        selectedDate = "$day-${month + 1}-$year"
        btnAccept.isClickable = true
        enabledDisabledButton()
    }
}
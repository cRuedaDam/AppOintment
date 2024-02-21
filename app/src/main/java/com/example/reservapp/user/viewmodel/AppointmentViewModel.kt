package com.example.reservapp.user.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.reservapp.user.DateConverter
import com.example.reservapp.user.model.AppointmentItem
import com.example.reservapp.user.model.CommerceItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalTime

class AppointmentViewModel: ViewModel() {
    private var db = FirebaseFirestore.getInstance()
    private var userId = FirebaseAuth.getInstance().currentUser?.uid

    var serviceName: MutableLiveData<String> = MutableLiveData()
    var employeeName: MutableLiveData<String> = MutableLiveData()
    var commerceEmail: MutableLiveData<String> = MutableLiveData()
    var commercePhone: MutableLiveData<String> = MutableLiveData()
    var commerceTypeString: MutableLiveData<String> = MutableLiveData()
    var appointment: MutableLiveData<AppointmentItem> = MutableLiveData()
    var appointments: MutableLiveData<ArrayList<AppointmentItem>> = MutableLiveData()
    var favorites: MutableLiveData<ArrayList<CommerceItem>> = MutableLiveData()
    var availabilityHours: MutableLiveData<ArrayList<String>> = MutableLiveData()


    /**
     * Llama a la BBDD y recupera el listado de Citas del usuario
     * Se guardan los datos recibidos a un ArrayList de tipo AppointmentItem
     * Esa lista es un objeto MutableLiveData que se actualiza con la llamada a la BBDD y
     * lo comunica a sus Observers
     */
    fun getUserAppointments() {
        appointments.value?.clear()

        val newAppointments: ArrayList<AppointmentItem> = ArrayList()

        db.collection("appointments").whereEqualTo("user_id", userId).get().addOnSuccessListener {
            for (doc in it) {
                newAppointments.add(
                    AppointmentItem(
                        appointmentId = doc.id,
                        serviceId = doc.data["service_id"].toString(),
                        commerceId = doc.data["commerce_id"].toString(),
                        userId = doc.data["user_id"].toString(),
                        appointmentDate = doc.data["appointment_date"].toString(),
                        appointmentTime = doc.data["appointment_time"].toString(),
                        employeeId = doc.data["employee_id"].toString(),
                        commerceName = doc.data["commerce_name"].toString(),
                        optionalRequest = doc.data["optional_request"].toString()
                    )
                )
            }
            appointments.value = newAppointments
        }
    }
    /**
     * Guardamos a la BBDD una nueva Cita
     */
    fun addAppointment (appointment: AppointmentItem) {
        val newAppointment = hashMapOf(
            "service_id" to appointment.serviceId,
            "appointment_date" to appointment.appointmentDate,
            "appointment_time" to appointment.appointmentTime,
            "employee_id" to appointment.employeeId,
            "commerce_id" to appointment.commerceId,
            "user_id" to appointment.userId,
            "optional_request" to appointment.optionalRequest,
            "commerce_name" to appointment.commerceName
        )

        db.collection("appointments").add(newAppointment)
    }

    /**
     * Filtramos el listado de Citas y recuperamos una Cita por su ID
     * Devuelve la cita encontrada de tipo AppointmentItem
     */
    fun getSingleAppointment (id: String): AppointmentItem {
        lateinit var returnedAppointment: AppointmentItem
        for (appointment in appointments.value!!) {
            if (appointment.appointmentId == id) {
                returnedAppointment = appointment
                val commerceId = appointment.commerceId
                val serviceId = appointment.serviceId
                val employeeId = appointment.employeeId
                getServiceName(commerceId, serviceId)
                getEmployeeName(commerceId, employeeId)
                getCommerceInfo(commerceId)
            }
        }
        return returnedAppointment
    }

    /**
     * Recuperamos de la BBDD la categoría asignada a un comercio
     */
    fun getCommerceType(id:String) {
        var newCommerceType = ""
        db.collection("commerces").document(id).get().addOnSuccessListener { doc ->
            if(!doc.data?.isEmpty()!!) {
                newCommerceType = doc.data?.get("commerce_type").toString()
            }
            commerceTypeString.value = newCommerceType
        }
    }

    /**
     * Recuperamos de la BBDD el nombre del servicio asignada a una cita
     */
    private fun getServiceName(commerceId: String, serviceId:String) {
        var newServiceName = ""
        db.collection("commerces").document(commerceId).collection("specialities").get().addOnSuccessListener { docs ->
            for (doc in docs) {
                if (doc.id == serviceId) {
                    newServiceName = doc.data["name"].toString()
                    serviceName.value = newServiceName
                }
            }
        }
    }
    /**
     * Recuperamos de la BBDD el email y número del comercio para mostrarlo en el detalle de la cita
     */
    private fun getCommerceInfo(commerceId: String) {
        var newCommerceEmail = ""
        var newCommercePhoneNumber = ""
        db.collection("commerces").document(commerceId).get().addOnSuccessListener { docs ->
            newCommerceEmail = (docs.data?.get("commerce_email") ?: "").toString()
            newCommercePhoneNumber = (docs.data?.get("commerce_phone_number") ?: "").toString()
            commerceEmail.value = newCommerceEmail
            commercePhone.value = newCommercePhoneNumber
        }
    }

    /**
     * Recuperamos de la BBDD el nombre del empleado asignado a una cita
     */
    private fun getEmployeeName(commerceId: String, employeeId: String) {
        var newEmployeeName = ""
        db.collection("commerces").document(commerceId).collection("employees").document(employeeId).get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                newEmployeeName = doc.data?.get("name").toString()
                employeeName.value = newEmployeeName
            }

        }
    }

    /**
     * Borra una Cita de la BBDD
     */
    fun deleteAppointment(id: String){
        db.collection("appointments").document(id).delete()
    }

    /**
     * Recupera el listado de comercios Favoritos de la BBDD
     * Asigna los datos a la lista favorites de tipo FavoriteItem
     */
    fun getFavoritedCommerces() {
        val newFavorites: ArrayList<CommerceItem> = ArrayList()
        db.collection("users/$userId/favorites").get().addOnSuccessListener { docs ->
            for (doc in docs) {
                newFavorites.add(
                    CommerceItem(
                    0,
                    doc.data["commerce_name"].toString(),
                    ""
                    )
                )
            }
            favorites.value = newFavorites
        }
    }

    /**
     * Guarda un comercio favorito a la BBDD
     * collection("users/$userId/favorites")
     */
    fun addToFavorites(commerceName:String) {
        val data = hashMapOf(
            "commerce_name" to commerceName
        )
        db.collection("users/$userId/favorites").add(data)
    }

    /**
     * Borra un comercio favorito de la BBDD
     * collection("users/$userId/favorites")
     */
    fun deleteFromFavorites(commerceName: String) {
        db.collection("users/$userId/favorites").whereEqualTo("commerce_name", commerceName).get().addOnSuccessListener { docs ->
            for (doc in docs) {
                doc.reference.delete()
            }
        }
    }

    /**
     * Calcula las horas disponibles de una Cita.
     * Se hace una query compleja teniendo en cuenta el commerceId, los empleados, sus especialidades y las citas ya selecionadas por otros
     * usuarios poniendola estas en una lista llamada unavailableHours de la collection appointments.
     *
     */
    fun getAvailabilityHours() {
        db.collection("commerces").document(appointment.value!!.commerceId).collection("employees").get().addOnSuccessListener { employees ->
            for (employee in employees) {
                if (employee.id == appointment.value!!.employeeId) {
                    db.collection("commerces").document(appointment.value!!.commerceId).collection("specialities").get().addOnSuccessListener { specialities ->
                        for (speciality in specialities) {
                            if (speciality.id == appointment.value!!.serviceId) {
                                db.collection("appointments")
                                    .whereEqualTo("commerce_name", appointment.value!!.commerceName)
                                    .whereEqualTo("appointment_date", appointment.value!!.appointmentDate)
                                    .whereEqualTo("employee_id", appointment.value!!.employeeId).get().addOnSuccessListener { appointments ->
                                        val checkInTime = employee.data["checkInTime"].toString()
                                        val checkOutTime = employee.data["checkOutTime"].toString()
                                        val timeRequired = speciality.data["timeRequired"].toString()
                                        var unavailableHours: ArrayList<String> = ArrayList()
                                        for (appointment in appointments) {
                                            unavailableHours.add(appointment.data["appointment_time"].toString())
                                        }
                                        calculateAvailabilityHours(checkInTime, checkOutTime, timeRequired, unavailableHours)
                                    }

                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Función que calcula las horas disponibles se le pasa el string de hora de entrada de empleado, String hora de salida,
     * String con el tiempo que se requiere para el servicio y una lista con las citas que ya estan cogidas para ese día en concreto
     * y devuelve una lista solo con las horas que se podrán seleccionar para pedir cita.
     */
    private fun calculateAvailabilityHours(checkInTime: String, checkOutTime: String, timeRequired: String, unavailableHours: ArrayList<String>) {
        val newCheckInTime = LocalTime.parse(checkInTime)
        val newCheckOutTime = LocalTime.parse(checkOutTime)
        val newTimeRequired = timeRequired.toLong()
        //Añadido para hora
        var dateConv = DateConverter()

        var dateFormat = dateConv.convertStringToDate(appointment.value?.appointmentDate.toString())

        var currentHour = newCheckInTime
        var result = ArrayList<String>()

        while(currentHour < newCheckOutTime) {
            if (currentHour.toString() !in unavailableHours && currentHour.toString() != "14:00") {
                //Condición de si es hoy y la hora actual es mayor a la de cada hora posible a mostrar
                val boolTime = LocalTime.now() < currentHour
                val boolDate = LocalDate.now() == dateFormat
                if(boolDate){
                    if(boolTime){
                        result.add(currentHour.toString())
                    }
                } else{
                    result.add(currentHour.toString())
                }
            }
            currentHour = currentHour.plusMinutes(newTimeRequired)
        }

        availabilityHours.value = result
    }

}
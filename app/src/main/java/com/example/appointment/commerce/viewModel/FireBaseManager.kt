package com.example.appointment.commerce.viewModel

import android.util.Log
import com.example.appointment.commerce.model.Employee
import com.example.appointment.commerce.model.Speciality
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FireBaseManager {

    private val firestore = FirebaseFirestore.getInstance()

    //Employees
    fun addEmployee(employee: Employee, commerceId: String) {
        val employeesCollection = firestore.collection("commerces/$commerceId/employees")
        employeesCollection.add(employee).addOnSuccessListener {
            Log.d("FirebaseMsg", "El empleado se ha añadido a la Base de datos.")
        }.addOnFailureListener {
            Log.d("FirebaseMsg", "Error al añadir al empleado.")
        }
    }

    fun deleteEmployee(employeeId: String, commerceId: String) {
        val employeeRoute = "commerces/$commerceId/employees/$employeeId"
        firestore.document(employeeRoute)
            .delete()
            .addOnSuccessListener {
                Log.d("FirebaseMsg", "El empleado ha sido eliminado de la Base de datos.")
            }
            .addOnFailureListener {
                Log.d("FirebaseMsg", "Error al eliminar al empleado.")
            }
    }

    fun editEmployee(employeeId: String, commerceId: String, newData: Map<String, String>) {
        val employeeRoute = "commerces/$commerceId/employees/$employeeId"
        firestore.document(employeeRoute)
            .update(newData)
            .addOnSuccessListener {
                Log.d("FirebaseMsg", "El empleado ha sido actualizado en la Base de datos.")
            }
            .addOnFailureListener {
                Log.d("FirebaseMsg", "Error al actualizar al empleado.")
            }
    }


    //Specialities
    fun addSpeciality(speciality: Speciality, commerceId: String) {
        val specialitiesCollection = firestore.collection("commerces/$commerceId/specialities")
        specialitiesCollection.add(speciality).addOnSuccessListener {
            Log.d("FirebaseMsg", "La especialidad se ha añadido a la Base de datos.")
        }.addOnFailureListener {
            Log.d("FirebaseMsg", "Error al añadir la especialidad.")
        }
    }

    fun deleteSpeciality(specialityId: String, commerceId: String) {
        val specialityRoute = "commerces/$commerceId/specialities/$specialityId"
        firestore.document(specialityRoute)
            .delete()
            .addOnSuccessListener {
                Log.d("FirebaseMsg", "La especialidad ha sido eliminado de la Base de datos.")
            }
            .addOnFailureListener {
                Log.d("FirebaseMsg", "Error al eliminar la especialidad.")
            }
    }

    fun editSpeciality(specialityId: String, commerceId: String, newData: Map<String, String>) {
        val specialityRoute = "commerces/$commerceId/specialities/$specialityId"
        firestore.document(specialityRoute)
            .update(newData)
            .addOnSuccessListener {
                Log.d("FirebaseMsg", "La especialidad ha sido actualizada en la Base de datos.")
            }
            .addOnFailureListener {
                Log.d("FirebaseMsg", "Error al actualizar La especialidad.")
            }
    }

    fun getServiceNameByIds(serviceId: String, commerceId: String, onComplete: (String?) -> Unit) {
        val serviceRef = firestore.collection("commerces").document(commerceId)
            .collection("specialities").document(serviceId)

        serviceRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val serviceName = document.getString("name")
                    onComplete(serviceName)
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener { exception ->
                onComplete(null)
                println("Error al obtener el nombre del servicio: $exception")
            }
    }

    fun getServiceNameById(serviceId: String, onComplete: (String) -> Unit) {
        val currentCommerce = FirebaseAuth.getInstance().currentUser
        val commerceId = currentCommerce?.uid

        if (commerceId != null) {
            firestore.collection("commerces")
                .document(commerceId)
                .collection("specialities")
                .document(serviceId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val serviceName = documentSnapshot.getString("name")
                        onComplete.invoke(serviceName ?: "")
                    } else {
                        onComplete.invoke("")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreError", "Error getting service document: ${e.message}")
                    onComplete.invoke("")
                }
        } else {
            onComplete.invoke("")
        }
    }

    fun getServiceIdByName(specialityName: String, commerceId: String, onComplete: (String?) -> Unit) {
        firestore.collection("commerces")
            .document(commerceId)
            .collection("specialities")
            .whereEqualTo("name", specialityName)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val serviceId = document.id
                    onComplete(serviceId)
                    return@addOnSuccessListener
                }
                Log.d("ERROR:", "No existe el servicio: $specialityName")
                onComplete("No existe el servicio: $specialityName")
            }
            .addOnFailureListener { exception ->
                onComplete("No existe el servicio: $specialityName")
            }
    }


    //Appointments
    fun deleteAppointment(appointmentId: String) {
        firestore.document("appointments/$appointmentId")
            .delete()
            .addOnSuccessListener {
                Log.d("FirebaseMsg", "La especialidad ha sido eliminado de la Base de datos.")
            }
            .addOnFailureListener {
                Log.d("FirebaseMsg", "Error al eliminar la especialidad.")
            }
    }


    //Users
    fun getUserNameByUid(userId: String, onComplete: (String) -> Unit) {
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val userName = documentSnapshot.getString("user_name")
                    onComplete.invoke(
                        userName ?: ""
                    ) // Invoca la lambda con el nombre o una cadena vacía si es nulo
                } else {
                    onComplete.invoke("") // Invoca la lambda con una cadena vacía si el documento no existe
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error getting user document: ${e.message}")
                onComplete.invoke("") // Invoca la lambda con una cadena vacía en caso de error
            }
    }

    fun changeUserEmail(userId: String, newUserEmail: String){
        val userRef = firestore.collection("users").document(userId)
        userRef.update("user_email", newUserEmail)
            .addOnSuccessListener {
                Log.d("FirebaseMsg", "El email se ha cambiado en la Base de datos.")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error cambiando el email.")
            }
    }

    fun changeUserName(userId: String, newUserName: String){
        val userRef = firestore.collection("users").document(userId)
        userRef.update("user_name", newUserName)
            .addOnSuccessListener {
                Log.d("FirebaseMsg", "El nombre del usuario se ha cambiado en la Base de datos.")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error cambiando el nombre del usuario")
            }
    }

    fun changeUserLastName(userId: String, newUserLastName: String){
        val userRef = firestore.collection("users").document(userId)
        userRef.update("user_last_name", newUserLastName)
            .addOnSuccessListener {
                Log.d("FirebaseMsg", "El apellido del usuario se ha cambiado en la Base de datos.")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error cambiando el apellido del usuario")
            }
    }

    fun changeUserPhone(userId: String, newUserPhone: String) {
        val userRef = firestore.collection("users").document(userId)
        userRef.update("user_phone_number", newUserPhone)
            .addOnSuccessListener {
                Log.d("FirebaseMsg", "El teléfono se ha cambiado en la Base de datos.")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error cambiando el teléfono")
            }
    }



    fun deleteUser(userId: String){
        val userRef = firestore.collection("users").document(userId)
        userRef.delete()
            .addOnSuccessListener {
                Log.d("FirebaseMsg", "El usuario ha sido eliminado de la Base de datos.")
            }
            .addOnFailureListener {
                Log.d("FirebaseMsg", "Error al eliminar el comercio.")
            }
    }

    //Commerces
    fun getCommerceNameByUid(commerceId: String, onComplete: (String) -> Unit) {
        firestore.collection("commerces")
            .document(commerceId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val commerceName = documentSnapshot.getString("commerce_name")
                    onComplete.invoke(
                        commerceName ?: ""
                    ) // Invoca la lambda con el nombre o una cadena vacía si es nulo
                } else {
                    onComplete.invoke("") // Invoca la lambda con una cadena vacía si el documento no existe
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error getting commerce document: ${e.message}")
                onComplete.invoke("") // Invoca la lambda con una cadena vacía en caso de error
            }
    }

    fun getCommerceIdByName(commerceName: String, onComplete: (String) -> Unit) {
        firestore.collection("commerces")
            .whereEqualTo("commerce_name", commerceName)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val commerceId = document.id
                    onComplete(commerceId)
                    return@addOnSuccessListener
                }
                Log.d("ERROR:", "No existe el comercio: $commerceName")
                onComplete("No existe el comercio: $commerceName")
            }
            .addOnFailureListener { exception ->
                onComplete("No existe el comercio: $commerceName")
            }
    }

    fun changeCommerceName(commerceId: String, newCommerceName: String){
        val commerceRef = firestore.collection("commerces").document(commerceId)
        commerceRef.update("commerce_name", newCommerceName)
            .addOnSuccessListener {
                Log.d("FirebaseMsg", "El nombre del comercio se ha cambiado en la Base de datos.")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error cambiando el nombre del comercio")
            }
    }

    fun deleteCommerce(commerceId: String){
        val commerceRef = firestore.collection("commerces").document(commerceId)
        commerceRef.delete()
            .addOnSuccessListener {
                Log.d("FirebaseMsg", "El comercio ha sido eliminado de la Base de datos.")
            }
            .addOnFailureListener {
                Log.d("FirebaseMsg", "Error al eliminar el comercio.")
            }
    }

    fun changeCommerceEmail(commerceId: String, newCommerceEmail: String){
        val commerceRef = firestore.collection("commerces").document(commerceId)
        commerceRef.update("commerce_email", newCommerceEmail)
            .addOnSuccessListener {
                Log.d("FirebaseMsg", "El email del comercio se ha cambiado en la Base de datos.")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error cambiando el email del comercio")
            }
    }

    fun changeCommercePhone(commerceId: String, newCommercePhone: String) {
        val commerceRef = firestore.collection("commerces").document(commerceId)
        commerceRef.update("commerce_phone_number", newCommercePhone)
            .addOnSuccessListener {
                Log.d("FirebaseMsg", "El teléfono del comercio se ha cambiado en la Base de datos.")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error cambiando el teléfono del comercio")
            }
    }

    fun changeCommerceAddress(
        commerceId: String,
        newStreetType: String,
        newStreetName: String,
        newStreetNumber: String,
        newCity: String,
        newState: String,
        newPostalCode: String
    ) {
        val commerceRef = firestore.collection("commerces").document(commerceId)
        val addressData = hashMapOf(
            "commerce_street_type" to newStreetType,
            "commerce_street_name" to newStreetName,
            "commerce_street_number" to newStreetNumber,
            "commerce_city" to newCity,
            "commerce_state" to newState,
            "commerce_postal_code" to newPostalCode
        )

        commerceRef.update("address", addressData)
            .addOnSuccessListener {
                Log.d("FirebaseMsg", "La dirección del comercio se ha cambiado en la Base de datos.")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error cambiando la dirección del comercio: ${e.message}")
            }
    }
}
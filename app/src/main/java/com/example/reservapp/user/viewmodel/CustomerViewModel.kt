package com.example.reservapp.user.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.reservapp.user.model.AuthUser
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class CustomerViewModel: ViewModel() {
    private var db = FirebaseFirestore.getInstance()
    private var auth = FirebaseAuth.getInstance()
    private var currentUser = auth.currentUser
    private var userId = currentUser?.uid

    var currentCustomer: MutableLiveData<AuthUser> = MutableLiveData()

    var userImage: MutableLiveData<Bitmap> = MutableLiveData()

    fun getCustomer() {
        db.collection("users").document(userId!!).addSnapshotListener(EventListener { value, e ->
            if (e != null) {
                return@EventListener
            }
            if (value != null) {
                currentCustomer.value = AuthUser(
                    userId = value.id,
                    userEmail = value.data?.get("user_email").toString(),
                    userName = value.data?.get("user_name").toString(),
                    userLastName = value.data?.get("user_last_name").toString(),
                    userPhoneNumber = value.data?.get("user_phone_number").toString()
                )
                val userImageRef = FirebaseStorage.getInstance().getReference("Users/" + value.id)
                val localFile = File.createTempFile("tempImage", "jpg")
                userImageRef.getFile(localFile).addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                    userImage.value = bitmap
                }
            }
        })

    }

    fun editUserProfile(newProfile: AuthUser) {
        val newUserProfile: Map<String, String> = mapOf(
            "user_name" to newProfile.userName,
            "user_last_name" to newProfile.userLastName,
            "user_email" to newProfile.userEmail,
            "user_phone_number" to newProfile.userPhoneNumber
        )
        db.collection("users").document(currentCustomer.value!!.userId).set(newUserProfile)
        currentCustomer.value = newProfile
    }

    fun editUserEmailAuth(newEmail: String) {
        // TO DO: Implementar reautentificación del usuario si mantenemos la sesión
        // Para cambiar el email debemos usar el método verifyBeforeUpdateEmail que manda un email al usuario para confirmar el cambio (al nuevo email)
        currentUser!!.verifyBeforeUpdateEmail(newEmail)
    }

    fun deleteUserAuth() {
        //Borramos los datos del usuario en Firebase Auth
        currentUser!!.delete()
        //se busca la imagen del usuario y la borramos
        val storageRef = FirebaseStorage.getInstance().getReference("Users/${currentCustomer.value!!.userId}" )
        storageRef.delete()

        //Borramos también el document en la colección users
        db.collection("users").document(currentCustomer.value!!.userId).delete()
    }

    fun editUserPasswordAuth(oldPassword: String, newPassword: String): LiveData<Boolean> {
        var result = MutableLiveData<Boolean>()
        val credential = EmailAuthProvider.getCredential(currentUser!!.email.toString(), oldPassword)
        currentUser!!.reauthenticate(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                currentUser!!.updatePassword(newPassword).addOnCompleteListener { task ->
                    result.value = task.isSuccessful
                }
            } else {
                result.value = false
            }
        }

        return result

    }

}
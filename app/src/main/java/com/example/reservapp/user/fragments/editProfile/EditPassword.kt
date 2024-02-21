package com.example.reservapp.user.fragments.editProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.reservapp.R
import com.example.reservapp.user.viewmodel.CustomerViewModel
import com.google.android.material.snackbar.Snackbar

/**
 * Fragmento donde se muestra la vista para cambiar la contraseña.
 */
class EditPassword : Fragment() {
    private lateinit var currentPassword: EditText
    private lateinit var newPassword: EditText
    private lateinit var newConfirmPassword: EditText
    private lateinit var btnSave: Button
    private val customerViewModel: CustomerViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(view)
        //coge el String tecleado por el usuario
        //verifica si el nuevo password y la confirmación son la misma y se cambia en el modelo y en la BBDD
        //O si la contraseña antigua no es la correcta.
        btnSave.setOnClickListener {
            val oldPassword = currentPassword.text.toString()
            val newPassword = newPassword.text.toString()
            val newConfirmPassword = newConfirmPassword.text.toString()
            if ( newPassword == newConfirmPassword) {
                customerViewModel.editUserPasswordAuth(oldPassword, newPassword).observe(viewLifecycleOwner) { result ->
                    if (result) {
                        Snackbar.make(view,getString(R.string.msg_toast_password), Toast.LENGTH_SHORT).show()
                    } else {
                        Snackbar.make(view,getString(R.string.msg_toast_password_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Snackbar.make(view,getString(R.string.msg_toast_password_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Inicializa las views
     */
    private fun initViews(view: View){
        currentPassword = view.findViewById(R.id.currentPassword)
        newPassword = view.findViewById(R.id.newPassword)
        newConfirmPassword = view.findViewById(R.id.newConfirmPassword)
        btnSave = view.findViewById(R.id.btnSavePassword)
    }

}
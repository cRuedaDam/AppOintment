package com.example.reservapp.user.fragments.editProfile

import android.content.DialogInterface
import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.reservapp.user.model.AuthUser
import com.example.reservapp.R
import com.example.reservapp.common.view.Login
import com.example.reservapp.user.viewmodel.CustomerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

/**
 *Fragment donde se cambia toda la información del usuario
 */
class EditProfile : Fragment() {
    private lateinit var editName :EditText
    private lateinit var editLastName : EditText
    private lateinit var editPhone : EditText
    private lateinit var editEmail : EditText
    private lateinit var editPassword: EditText
    private lateinit var btnSaveProfile: Button
    private lateinit var btnDeleteAccount: Button
    private val customerViewModel: CustomerViewModel by activityViewModels()
    private lateinit var currentCustomer: AuthUser
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)

    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews(view)
        currentCustomer = customerViewModel.currentCustomer.value!!
        //poner toda la info del usuario que viene de la BBDD para que el usuario sepa que datos tiene en su perfil y los pueda cambiar.
        editName.setText(currentCustomer.userName)
        editLastName.setText(currentCustomer.userLastName)
        editPhone.setText(currentCustomer.userPhoneNumber)
        editEmail.setText(currentCustomer.userEmail)
        editPassword.setText(getString(R.string.password))
        //para que no se pueda editar pero se pueda clicar para navegar a otro fragment
        editPassword.showSoftInputOnFocus = false
        //event listener a cambiar password para navegar al frgament de editar el password
        editPassword.setOnFocusChangeListener { _, editPassword ->
            if(editPassword){
                findNavController().navigate(R.id.editPassword)
            }
        }
        //Event listener en guardar perfil. Verifica si el email ha cambiado y lo modifica en el Authentification de firebase.
        //Authentification tiene a su disposición enviar a el nuevo email un link para que el usuario confirme su nuevo email y lo cambia en la parte de authentification.
        //Se asigna de nuevo todos los textView a el usuario(tipo AuthUser) por si se ha modificado y se pasa este objeto al modelo para que realice los cambios en la BBDD.
        btnSaveProfile.setOnClickListener {

            if(editEmail.text.toString() != currentCustomer.userEmail) {
                customerViewModel.editUserEmailAuth(editEmail.text.toString())
                Snackbar.make(view,getString(R.string.msg_toast_email), Toast.LENGTH_SHORT).show()
            }else{
                Snackbar.make(view,getString(R.string.msg_toast), Toast.LENGTH_SHORT).show()
            }

            currentCustomer.userName = editName.text.toString()
            currentCustomer.userLastName = editLastName.text.toString()
            currentCustomer.userPhoneNumber = editPhone.text.toString()
            currentCustomer.userEmail = editEmail.text.toString()

            customerViewModel.editUserProfile(currentCustomer)


        }
        //event listener en el botón de eliminar cuenta. Muestra un dialogo de si estas seguro o no de borrarla.
        // se llama a la función para difuminar el fondo.
        //En el caso de aceptar se le dice al viewModel que elimine la cuenta del cliente de la BBDD.
        //Se vacia el stack y se redirige al login.
        btnDeleteAccount.setOnClickListener {
            val builder = context?.let { dialog ->
                MaterialAlertDialogBuilder(dialog)
                    .setMessage(getString(R.string.del_usr))
                    .setPositiveButton(getString(R.string.accept)){_, _ ->
                        blurFragment(view, false)
                        customerViewModel.deleteUserAuth()

                        val intent = Intent(context, Login::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)

                    }.setNeutralButton(getString(R.string.refuse)){ _, _ -> blurFragment(view, false)}
            }
            //se muestra el diálogo
            val dialog = builder?.create()
            dialog?.show()
            if (dialog != null) {
                //se activa el difuminado
                blurFragment(view, true)
                //solo se puede clickar en el diálogo
                dialog.setCanceledOnTouchOutside(false)
            }
            //se pone el texto/botón del dialogo aceptar en rojo
            val btn = dialog?.getButton(DialogInterface.BUTTON_POSITIVE)
            with(btn){
                this?.setTextColor(ContextCompat.getColor(context, R.color.md_theme_dark_onError))
            }
        }
    }

    /**
     * Se inicializa las views
     */
    private fun initViews(view: View){
        editName = view.findViewById(R.id.editProfileName)
        editLastName = view.findViewById(R.id.editProfileLastName)
        editPhone = view.findViewById(R.id.editProfilePhone)
        editEmail = view.findViewById(R.id.editProfileEmail)
        editPassword = view.findViewById(R.id.editProfilePassword)
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile)
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount)
    }

    /**
     * Función que fumina el fondo.
     */
    @RequiresApi(Build.VERSION_CODES.S)
    private fun blurFragment(view: View, setBlur: Boolean){
        val blur = RenderEffect.createBlurEffect(8F, 8F, Shader.TileMode.MIRROR)
        if(setBlur){
            view.setRenderEffect(blur)
        }else{
            view.setRenderEffect(null)
        }
    }

}
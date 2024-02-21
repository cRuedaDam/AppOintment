package com.example.reservapp.user.fragments.cancelappointment

import android.content.DialogInterface
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.reservapp.user.model.AuthUser
import com.example.reservapp.R
import com.example.reservapp.user.DateConverter
import com.example.reservapp.user.model.AppointmentItem
import com.example.reservapp.user.viewmodel.AppointmentViewModel
import com.example.reservapp.user.viewmodel.CustomerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class DetailsAppointment : Fragment() {
    private lateinit var btnCategory: ImageButton
    private lateinit var commerceName: TextView
    private lateinit var infoCommerce: TextView
    private lateinit var service: TextView
    private lateinit var date: TextView
    private lateinit var hour: TextView
    private lateinit var commerceEmail: TextView
    private lateinit var commercePhone: TextView
    private lateinit var appointmentSelected: AppointmentItem
    private lateinit var currentCustomer: AuthUser
    private val appointmentsViewModel: AppointmentViewModel by activityViewModels()
    private val customerViewModel: CustomerViewModel by activityViewModels()

    private val args: DetailsAppointmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details_appointment, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)

        appointmentSelected = appointmentsViewModel.getSingleAppointment(args.appointmentId)
        currentCustomer = customerViewModel.currentCustomer.value!!
        val dateString = DateConverter()

        appointmentsViewModel.getCommerceType(appointmentSelected.commerceId)

        appointmentsViewModel.commerceTypeString.observe(viewLifecycleOwner) { category ->

            when(category){
                getString(R.string.aes) -> btnCategory.setImageResource(R.drawable.icon_beauty)
                getString(R.string.hair_dresser) -> btnCategory.setImageResource(R.drawable.icon_hair_dresser)
                getString(R.string.pet) -> btnCategory.setImageResource(R.drawable.icon_pet)
                getString(R.string.nail_salon) -> btnCategory.setImageResource(R.drawable.icon_brush)
                getString(R.string.fisio) -> btnCategory.setImageResource(R.drawable.icon_fisio)
            }
        }

        appointmentsViewModel.serviceName.observe(viewLifecycleOwner) { name ->
            service.text = name
        }
        appointmentsViewModel.commerceEmail.observe(viewLifecycleOwner) { name ->
            commerceEmail.text  = name
        }
        appointmentsViewModel.commercePhone.observe(viewLifecycleOwner) { name ->
            commercePhone.text = name
        }

        commerceName.text = appointmentSelected.commerceName
        infoCommerce.text = getString(R.string.resev_usr_commerce)
        date.text = dateString.convertStringToDateString(appointmentSelected.appointmentDate)
        hour.text = appointmentSelected.appointmentTime

        //event listener en el botón de cancelar. Muestra un dialogo de si estas seguro o no de borrar la cita
        // se llama a la función para difuminar el fondo.
        //En el caso de aceptar se le dice al viewModel que elimine la cita con el id pasado.
        //Se vacia el stack para que ya no se pueda volver a una vista de una cita que ya no existe.
        //Y se navega a la Home
        view.findViewById<Button>(R.id.btnCancelAppointment).setOnClickListener{
            val builder = context?.let { dialog ->
                MaterialAlertDialogBuilder(dialog)
                    .setMessage(getString(R.string.del_appoint))
                    .setPositiveButton(getString(R.string.accept)){_, _ ->
                        blurFragment(view, false)
                        appointmentsViewModel.deleteAppointment(appointmentSelected.appointmentId)
                        parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        findNavController().navigate(R.id.customerHome)
                        activity?.onBackPressedDispatcher
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
     * Inicializa las views
     */
    private fun initViews(view: View){
        btnCategory = view.findViewById(R.id.imgButtonServAppoint)
        commerceName = view.findViewById(R.id.textViewSubtitleAppoint)
        service = view.findViewById(R.id.txtViewServAppoint)
        date = view.findViewById(R.id.txtViewDate)
        hour = view.findViewById(R.id.txtViewHour)
        infoCommerce = view.findViewById(R.id.txtViewTitleAppoint)
        commerceEmail = view.findViewById(R.id.txtViewEmailCommerce)
        commercePhone = view.findViewById(R.id.txtViewPhoneCommerce)
    }

    /**
     * Función que difuma una vista.
     * params view y un boolean.
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
package com.example.reservapp.common.viewModel

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.view.WindowManager
import com.example.appointment.R

class Alerts {

    /**
     * Este metodo se encarga de mostrar un mensaje de Alerta cuando se establerzca desde el codigo,
     * al cual se le pasan como parametros un Titulo y un mensaje.
     */
    fun showAlert(context: Context, title: String, message: String){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    protected var dialog: ProgressDialog? = null


    /**
     * Esta función se encarga de mostrar un Alertdialog que muestra un mensaje de espera mientras
     * las operaciones en segundo plano se están ejecutando.
     */
    fun showLoading(context: Context, msg: String? = "Espere por favor...") {
        try {
            hideLoading()
            dialog = ProgressDialog(context, R.style.MyAlertDialogStyle)
            dialog!!.show()
            dialog!!.setCancelable(false)
            dialog!!.setMessage(msg)
        } catch (e: WindowManager.BadTokenException) {
        }
    }

    /**
     * Esta funcion se encarga de ocultar el Loading
     */
    fun hideLoading() {
        if (dialog != null) dialog!!.dismiss() // .hide();
    }
}
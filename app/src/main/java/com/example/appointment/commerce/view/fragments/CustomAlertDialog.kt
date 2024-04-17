package com.example.appointment.commerce.view.fragments

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.appointment.R

class CustomAlertDialog {
    companion object {
        fun showAlertDialog(
            context: Context, //Pasamos el contexto desde la clase
            alertTitle: String, // El Title será el mensaje que queramos que aparezca
            onAccept: () -> Unit, // Desde aquí controlamos las acciones del botón Aceptar
            onCancel: () -> Unit // Desde aquí controlamos las acciones del botón Cancelar
        ) {
            val inflater = LayoutInflater.from(context)
            val viewDialog = inflater.inflate(R.layout.custom_alert_dialog, null)

            val btnCancel = viewDialog.findViewById<View>(R.id.txt_no)
            val btnAccept = viewDialog.findViewById<View>(R.id.txt_yes)

            btnCancel.setOnClickListener {
                dismissDialog(viewDialog)
                onCancel.invoke()
            }

            btnAccept.setOnClickListener {
                dismissDialog(viewDialog)
                onAccept.invoke()
            }

            val dialog = Dialog(context, R.style.DialogTheme)
            dialog.setContentView(viewDialog)

            val title: TextView = viewDialog.findViewById(R.id.txt_title)
            title.text = alertTitle

            viewDialog.tag = dialog

            dialog.setCancelable(false)
            dialog.show()
        }

        fun dismissDialog(view: View) {
            val dialog = view.tag as Dialog?
            dialog?.let {
                if (it.isShowing) {
                    it.dismiss()
                }
            }
        }
    }
}
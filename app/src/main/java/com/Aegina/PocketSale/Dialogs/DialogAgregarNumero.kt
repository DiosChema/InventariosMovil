package com.Aegina.PocketSale.Dialogs

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import com.Aegina.PocketSale.R

class DialogAgregarNumero : AppCompatDialogFragment() {

    lateinit var agregarNumero: DialogAgregarNumero

    fun crearDialogNumero(context : Context, posicion : Int){
        val dialogAgregarNumero = Dialog(context)

        dialogAgregarNumero.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAgregarNumero.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        dialogAgregarNumero.setCancelable(false)
        dialogAgregarNumero.setContentView(R.layout.dialog_numero)

        agregarNumero = context as DialogAgregarNumero

        val dialogNumeroTitulo = dialogAgregarNumero.findViewById<View>(R.id.dialogNumeroTitulo) as TextView
        val dialogNumeroText = dialogAgregarNumero.findViewById<View>(R.id.dialogNumeroText) as EditText
        val dialogNumeroAceptar = dialogAgregarNumero.findViewById<View>(R.id.dialogNumeroAceptar) as Button
        val dialogNumeroCancelar = dialogAgregarNumero.findViewById<View>(R.id.dialogNumeroCancelar) as Button

        dialogNumeroTitulo.text = context.getString(R.string.dialog_numero_text_cantidad)

        dialogNumeroAceptar.setOnClickListener {
            if(dialogNumeroText.length() > 0) {
                agregarNumero.obtenerNumero(Integer.parseInt(dialogNumeroText.text.toString()),posicion)
                val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm.isAcceptingText)
                {
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY)
                }
                dialogAgregarNumero.dismiss()
            }
        }

        dialogNumeroCancelar.setOnClickListener {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isAcceptingText)
            {
                imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY)
            }
            dialogAgregarNumero.dismiss()
        }

        dialogAgregarNumero.show()
    }

    interface DialogAgregarNumero {
        fun obtenerNumero(numero : Int, posicion : Int)
    }
}
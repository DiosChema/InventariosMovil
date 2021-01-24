package com.Aegina.PocketSale.Dialogs

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat.getSystemService
import com.Aegina.PocketSale.R


class DialogAgregarNumero : AppCompatDialogFragment() {

    lateinit var agregarNumero: DialogAgregarNumero

    fun crearDialogNumero(context : Context, posicion : Int, cantidad: Int){
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
        dialogNumeroText.setText(cantidad.toString())

        dialogNumeroAceptar.setOnClickListener {
            if(dialogNumeroText.length() > 0) {
                agregarNumero.obtenerNumero(Integer.parseInt(dialogNumeroText.text.toString()),posicion)
                view?.let { it1 -> hideSoftkeybard(it1) }
                dialogAgregarNumero.dismiss()
            }
        }

        dialogNumeroCancelar.setOnClickListener {
            view?.let { it1 -> hideSoftkeybard(it1) }
            dialogAgregarNumero.dismiss()
        }

        dialogAgregarNumero.show()
    }

    private fun hideSoftkeybard(v: View) {
        val inputMethodManager = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
    }

    interface DialogAgregarNumero {
        fun obtenerNumero(numero : Int, posicion : Int)
    }
}
package com.Aegina.PocketSale.Dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import com.Aegina.PocketSale.R

class DialogAgregarNumero : AppCompatDialogFragment() {

    lateinit var agregarNumero: DialogAgregarNumero

    fun crearDialog(context : Context, posicion : Int){
        val dialog = Dialog(context)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_numero)

        agregarNumero = context as DialogAgregarNumero

        val dialogNumeroTitulo = dialog.findViewById<View>(R.id.dialogNumeroTitulo) as TextView
        val dialogNumeroText = dialog.findViewById<View>(R.id.dialogNumeroText) as EditText
        val dialogNumeroAceptar = dialog.findViewById<View>(R.id.dialogNumeroAceptar) as Button
        val dialogNumeroCancelar = dialog.findViewById<View>(R.id.dialogNumeroCancelar) as Button

        dialogNumeroTitulo.text = context.getString(R.string.dialog_numero_text_cantidad)

        dialogNumeroAceptar.setOnClickListener {
            if(dialogNumeroText.length() > 0) {/*
                listaArticulos[posicion].cantidadArticulo =
                    Integer.parseInt(dialogNumeroText.text.toString())
                mViewEstadisticaArticulo.notifyDataSetChanged()*/
                agregarNumero.obtenerNumero(Integer.parseInt(dialogNumeroText.text.toString()),posicion)
                val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)

                dialog.dismiss()
            }
        }

        dialogNumeroCancelar.setOnClickListener {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            dialog.dismiss()
        }

        dialog.show()
    }

    interface DialogAgregarNumero {
        fun obtenerNumero(numero : Int, posicion : Int)
    }



}
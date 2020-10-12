package com.Aegina.PocketSale.Dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import com.Aegina.PocketSale.R

class DialogAgregarNumero : AppCompatDialogFragment() {

    lateinit var listener: ExampleDialogListener

    fun crearDialog(context : Context, posicion : Int){
        val dialog = Dialog(context)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_numero)

        listener = context as ExampleDialogListener

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
                listener.obtenerNumero(Integer.parseInt(dialogNumeroText.text.toString()),posicion)
                dialog.dismiss()
            }
        }

        dialogNumeroCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    interface ExampleDialogListener {
        fun obtenerNumero(numero : Int, posicion : Int)
    }



}
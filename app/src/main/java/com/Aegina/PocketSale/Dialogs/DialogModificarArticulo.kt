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
import com.Aegina.PocketSale.R
import java.lang.Double.parseDouble

class DialogModificarArticulo {

    lateinit var modificarArticulo: ModificarArticulo

    fun crearDialogModificarArticulo(context : Context, posicion : Int, precio : Double, costo : Double, tipoArticulo: Int){
        val dialogModificarArticulo = Dialog(context)

        dialogModificarArticulo.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogModificarArticulo.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        dialogModificarArticulo.setCancelable(false)
        dialogModificarArticulo.setContentView(R.layout.dialog_modificar_articulo)

        modificarArticulo = context as ModificarArticulo

        val dialogModificarArticuloPrecioText = dialogModificarArticulo.findViewById<View>(R.id.dialogModificarArticuloPrecioText) as TextView
        val dialogModificarArticuloCostoText = dialogModificarArticulo.findViewById<View>(R.id.dialogModificarArticuloCostoText) as TextView
        val dialogModificarArticuloPrecio = dialogModificarArticulo.findViewById<View>(R.id.dialogModificarArticuloPrecio) as EditText
        val dialogModificarArticuloCosto = dialogModificarArticulo.findViewById<View>(R.id.dialogModificarArticuloCosto) as EditText
        val dialogModificarArticuloCancelar = dialogModificarArticulo.findViewById<View>(R.id.dialogModificarArticuloCancelar) as Button
        val dialogModificarArticuloAceptar = dialogModificarArticulo.findViewById<View>(R.id.dialogModificarArticuloAceptar) as Button

        dialogModificarArticuloPrecioText.text = context.getString(R.string.editar_articulo_precio)
        dialogModificarArticuloCostoText.text = context.getString(R.string.mensaje_costo_articulo)
        dialogModificarArticuloPrecio.setText(precio.toString())
        dialogModificarArticuloCosto.setText(costo.toString())

        dialogModificarArticuloAceptar.setOnClickListener()
        {
            if(dialogModificarArticuloPrecio.length() > 0 && dialogModificarArticuloCosto.length() > 0) {

                modificarArticulo.cambiarPrecioCosto(parseDouble(
                    dialogModificarArticuloPrecio.text.toString()),
                    parseDouble(dialogModificarArticuloCosto.text.toString()),
                    posicion)

                val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm.isAcceptingText)
                {
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY)
                }
                dialogModificarArticulo.dismiss()
            }
        }

        dialogModificarArticuloCancelar.setOnClickListener()
        {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isAcceptingText)
            {
                imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY)
            }
            dialogModificarArticulo.dismiss()
        }

        dialogModificarArticulo.show()
        if(tipoArticulo == 1)
        {
            dialogModificarArticuloPrecio.visibility = View.GONE
            dialogModificarArticuloPrecioText.visibility = View.GONE
            dialogModificarArticuloCosto.requestFocus()
        }

    }

    interface ModificarArticulo {
        fun cambiarPrecioCosto(precio : Double, costo : Double, posicion : Int)
    }
}
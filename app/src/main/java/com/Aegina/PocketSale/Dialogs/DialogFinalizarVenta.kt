package com.Aegina.PocketSale.Dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import com.Aegina.PocketSale.R
import java.lang.Float.parseFloat

class DialogFinalizarVenta : AppCompatDialogFragment() {

    lateinit var finalizarVenta: DialogFinalizarVenta

    fun crearDialog(context : Context, totalVenta : Float){
        val dialog = Dialog(context)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_terminar_venta)

        finalizarVenta = context as DialogFinalizarVenta

        val dialogTotalVenta = dialog.findViewById<View>(R.id.dialogTotalVenta) as TextView
        val dialogTotalPago = dialog.findViewById<View>(R.id.dialogTotalPago) as EditText
        val dialogTotalCambio = dialog.findViewById<View>(R.id.dialogTotalCambio) as TextView
        val dialogTerminarVentaCancelar = dialog.findViewById<View>(R.id.dialogTerminarVentaCancelar) as Button
        val dialogTerminarVentaAceptar = dialog.findViewById<View>(R.id.dialogTerminarVentaAceptar) as Button

        dialogTotalVenta.text = totalVenta.toString()

        dialogTotalPago.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int,count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()/* && parseFloat(s.toString()) >= totalVenta*/) {
                    dialogTotalCambio.text = (parseFloat(s.toString()) - totalVenta).toString()
                }
                else
                {
                    dialogTotalCambio.text = ""
                }
            }
        })

        dialogTerminarVentaCancelar.setOnClickListener {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            dialog.dismiss()
        }

        dialogTerminarVentaAceptar.setOnClickListener {
            if(dialogTotalPago.text.isNotEmpty()){
                if(parseFloat(dialogTotalPago.text.toString()) >= totalVenta){
                    dialog.dismiss()
                    finalizarVenta.finalizarVenta(parseFloat(dialogTotalPago.text.toString()) - totalVenta)
                }
            }
        }

        dialog.show()
    }

    interface DialogFinalizarVenta {
        fun finalizarVenta(cambio : Float)
    }
}
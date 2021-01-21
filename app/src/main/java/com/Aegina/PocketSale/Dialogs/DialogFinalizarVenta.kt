package com.Aegina.PocketSale.Dialogs

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import com.Aegina.PocketSale.R
import java.lang.Double.parseDouble
import java.lang.Float.parseFloat

class DialogFinalizarVenta : AppCompatDialogFragment() {

    lateinit var finalizarVenta: DialogFinalizarVenta

    fun crearDialog(context : Context, totalVenta : Double){
        val dialogFinalizarVenta = Dialog(context)

        dialogFinalizarVenta.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogFinalizarVenta.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        dialogFinalizarVenta.setCancelable(false)
        dialogFinalizarVenta.setContentView(R.layout.dialog_terminar_venta)

        finalizarVenta = context as DialogFinalizarVenta

        val dialogTotalVenta = dialogFinalizarVenta.findViewById<View>(R.id.dialogTotalVenta) as TextView
        val dialogTotalPago = dialogFinalizarVenta.findViewById<View>(R.id.dialogTotalPago) as EditText
        val dialogTotalCambio = dialogFinalizarVenta.findViewById<View>(R.id.dialogTotalCambio) as TextView
        val dialogTerminarVentaCancelar = dialogFinalizarVenta.findViewById<View>(R.id.dialogTerminarVentaCancelar) as Button
        val dialogTerminarVentaAceptar = dialogFinalizarVenta.findViewById<View>(R.id.dialogTerminarVentaAceptar) as Button

        dialogTotalVenta.text = totalVenta.toString()

        dialogTotalPago.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int,count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()/* && parseFloat(s.toString()) >= totalVenta*/) {
                    dialogTotalCambio.text = (parseDouble(s.toString()) - totalVenta).round(2).toString()
                }
                else
                {
                    dialogTotalCambio.text = ""
                }
            }
        })

        dialogTerminarVentaCancelar.setOnClickListener {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isAcceptingText)
            {
                imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY)
            }
            dialogFinalizarVenta.dismiss()
        }

        dialogTerminarVentaAceptar.setOnClickListener {
            if(dialogTotalPago.text.isNotEmpty()){
                if(parseDouble(dialogTotalPago.text.toString()) >= totalVenta){
                    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    if (imm.isAcceptingText)
                    {
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY)
                    }
                    finalizarVenta.finalizarVenta((parseDouble(dialogTotalPago.text.toString()) - totalVenta).round(2))
                    dialogFinalizarVenta.dismiss()
                }
            }
        }

        dialogFinalizarVenta.show()
    }

    interface DialogFinalizarVenta {
        fun finalizarVenta(cambio : Double)
    }

    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }
}
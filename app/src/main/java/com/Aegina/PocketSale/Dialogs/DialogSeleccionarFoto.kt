package com.Aegina.PocketSale.Dialogs

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDialogFragment
import com.Aegina.PocketSale.R

class DialogSeleccionarFoto : AppCompatDialogFragment()
{
    lateinit var dialogFoto: Dialog
    lateinit var dialogFotoGaleria: ImageView
    lateinit var dialogFotoCamara: ImageView
    lateinit var dialogFotoCancelar: Button
    lateinit var interfazFoto : DialogSeleccionarFoto

    fun crearDialog(context : Context)
    {
        dialogFoto = Dialog(context)

        dialogFoto.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogFoto.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        dialogFoto.setCancelable(false)
        dialogFoto.setContentView(R.layout.dialog_foto)

        dialogFotoGaleria = dialogFoto.findViewById(R.id.dialogFotoGaleria) as ImageButton
        dialogFotoCamara = dialogFoto.findViewById(R.id.dialogFotoCamara) as ImageButton
        dialogFotoCancelar = dialogFoto.findViewById(R.id.dialogFotoCancelar) as Button


        interfazFoto = context as DialogSeleccionarFoto

        dialogFotoGaleria.setOnClickListener()
        {
            interfazFoto.abrirGaleria()
            dialogFoto.dismiss()
        }

        dialogFotoCamara.setOnClickListener()
        {
            interfazFoto.abrirCamara()
            dialogFoto.dismiss()
        }

        dialogFotoCancelar.setOnClickListener()
        {
            dialogFoto.dismiss()
        }
    }

    fun mostrarDialogoFoto()
    {
        dialogFoto.show()
    }

    interface DialogSeleccionarFoto {
        fun abrirGaleria()
        fun abrirCamara()
    }
}
package com.Aegina.PocketSale.Metodos

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import com.Aegina.PocketSale.Activities.MainActivity
import com.Aegina.PocketSale.Objets.Respuesta
import com.Aegina.PocketSale.R
import com.google.gson.GsonBuilder


class Errores()
{
    fun procesarError(context: Context, mensaje: String, activity: Activity)
    {
        activity.runOnUiThread()
        {
            try
            {
                val gson = GsonBuilder().create()
                val respuesta = gson.fromJson(mensaje, Respuesta::class.java)

                if(respuesta.status == 2)
                {
                    crearDialolog(context,mensaje,activity)
                }
                else
                {
                    Toast.makeText(context, context.getString(R.string.mensaje_error), Toast.LENGTH_LONG).show()
                }
            }
            catch(e:Exception)
            {
                Toast.makeText(context, context.getString(R.string.mensaje_error), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun procesarErrorCerrarVentana(context: Context, mensaje: String, activity: Activity)
    {
        activity.runOnUiThread()
        {
            try
            {
                val gson = GsonBuilder().create()
                val respuesta = gson.fromJson(mensaje, Respuesta::class.java)

                if(respuesta.status == 2)
                {
                    crearDialolog(context,mensaje,activity)
                }
                else
                {
                    Toast.makeText(context, context.getString(R.string.mensaje_error), Toast.LENGTH_LONG).show()
                    activity.finish()
                }
            }
            catch(e:Exception)
            {
                Toast.makeText(context, context.getString(R.string.mensaje_error), Toast.LENGTH_LONG).show()
                activity.finish()
            }
        }
    }

    fun crearDialolog(context: Context, respuesta: String, activity: Activity)
    {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage(R.string.mensaje_sesion_expirada)

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            finishAffinity(activity)
        }

        builder.setCancelable(false)

        builder.show()
    }
}
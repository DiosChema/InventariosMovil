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
                    crearDialologTokenInvalido(context,activity)
                }
                else
                {
                    Toast.makeText(context, obtenerMensaje(respuesta.status,context), Toast.LENGTH_LONG).show()
                }
            }
            catch(e:Exception)
            {
                Toast.makeText(context, context.getString(R.string.mensaje_error_intentear_mas_tarde), Toast.LENGTH_LONG).show()
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
                    crearDialologTokenInvalido(context,activity)
                }
                else
                {
                    Toast.makeText(context, obtenerMensaje(1,context), Toast.LENGTH_LONG).show()
                    activity.finish()
                }
            }
            catch(e:Exception)
            {
                Toast.makeText(context, obtenerMensaje(1,context), Toast.LENGTH_LONG).show()
                activity.finish()
            }
        }
    }

    fun crearDialologTokenInvalido(context: Context, activity: Activity)
    {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage(obtenerMensaje(-2,context))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            finishAffinity(activity)
        }

        builder.setCancelable(false)

        builder.show()
    }

    fun crearDialologIniciarSesion(context: Context, activity: Activity)
    {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("")
        builder.setMessage(obtenerMensaje(55,context))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            finishAffinity(activity)
        }

        builder.setCancelable(false)

        builder.show()
    }

    fun obtenerMensaje(codigoMensaje: Int, context: Context) : String
    {
        var mensaje = ""

        when(codigoMensaje)
        {
            -2 -> mensaje = context.getString(R.string.mensaje_error_sesion_expirada)
            5 -> mensaje = context.getString(R.string.mensaje_cuenta_ya_registrado)
            6 -> mensaje = context.getString(R.string.mensaje_cuenta_registro_pendiente)
            7 -> mensaje = context.getString(R.string.mensaje_usuario_invalido)
            30 -> mensaje = context.getString(R.string.mensaje_error_contrasena)
            32 -> mensaje = context.getString(R.string.mensaje_error_familia_existe)
            33 -> mensaje = context.getString(R.string.mensaje_error_subfamilia_existe)
            55 -> mensaje = context.getString(R.string.mensaje_iniciar_sesion)
            else -> mensaje = context.getString(R.string.mensaje_error_intentear_mas_tarde)
        }

        return mensaje
    }
}
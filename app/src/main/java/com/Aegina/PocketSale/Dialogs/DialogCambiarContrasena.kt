package com.Aegina.PocketSale.Dialogs

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.Aegina.PocketSale.Metodos.Errores
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.Respuesta
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class DialogCambiarContrasena
{

    private val urls: Urls = Urls()

    lateinit var dialogCambiarContrasena: Dialog
    lateinit var dialogContrasena: EditText
    lateinit var dialogContrasenaNueva: EditText

    lateinit var globalVariable: GlobalClass
    lateinit var activityTmp: Activity

    fun crearDialogContrasena(context : Context, globalVariableTmp: GlobalClass, activity : Activity){

        globalVariable = globalVariableTmp
        activityTmp = activity

        dialogCambiarContrasena = Dialog(context)

        dialogCambiarContrasena.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogCambiarContrasena.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        dialogCambiarContrasena.setCancelable(false)
        dialogCambiarContrasena.setContentView(R.layout.dialog_cambiar_contrasena)

        dialogContrasena = dialogCambiarContrasena.findViewById<View>(R.id.dialogContrasena) as EditText
        dialogContrasenaNueva = dialogCambiarContrasena.findViewById<View>(R.id.dialogContrasenaNueva) as EditText
        val dialogNumeroAceptar = dialogCambiarContrasena.findViewById<View>(R.id.dialogContrasenaAceptar) as Button
        val dialogNumeroCancelar = dialogCambiarContrasena.findViewById<View>(R.id.dialogContrasenaCancelar) as Button

        dialogNumeroAceptar.setOnClickListener {
            if(dialogContrasena.length() > 7 && dialogContrasenaNueva.length() > 7) {
                val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm.isAcceptingText)
                {
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                }

                cambiarContrasena(dialogContrasena.text.toString(), dialogContrasenaNueva.text.toString(), context)

                dialogCambiarContrasena.dismiss()
            }
            else
            {
                activityTmp.runOnUiThread {
                    Toast.makeText(context, context.getString(R.string.mensaje_contraseña_corta), Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialogNumeroCancelar.setOnClickListener {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isAcceptingText)
            {
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            }
            dialogCambiarContrasena.dismiss()
        }

    }

    private fun cambiarContrasena(contrasena: String, contrasenaNueva: String, context: Context) {
        val url = urls.url+urls.endPointUsers.endPointCambiarContrasena

        val jsonObject = JSONObject()
        try {
            jsonObject.put("user", globalVariable.usuario!!.user)
            jsonObject.put("password", contrasena)
            jsonObject.put("newPassword", contrasenaNueva)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonObject.toString())

        val request = Request.Builder()
            .url(url)
            .put(body)
            .build()

        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage(context.getString(R.string.mensaje_espera))
        progressDialog.setCancelable(false)
        progressDialog.show()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response)
            {
                val body = response.body()!!.string()

                if(body != null && body.isNotEmpty()) {
                    val gson = GsonBuilder().create()

                    val respuesta = gson.fromJson(body, Respuesta::class.java)

                    if(respuesta.status == 0)
                    {
                        activityTmp.runOnUiThread {
                            Toast.makeText(context, context.getString(R.string.mensaje_contrasena_cambiar), Toast.LENGTH_LONG).show()
                        }
                    }
                    else
                    {
                        val errores = Errores()
                        errores.procesarError(context,body,activityTmp)
                    }
                }

                progressDialog.dismiss()
            }
        })
    }

    fun mostrarVentana()
    {
        dialogContrasena.setText("")
        dialogContrasenaNueva.setText("")
        dialogCambiarContrasena.show()
    }
}
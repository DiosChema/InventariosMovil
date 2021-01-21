package com.Aegina.PocketSale.Dialogs

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
import java.lang.Integer.parseInt

class DialogRecuperarContrasena {
    private val urls: Urls = Urls()

    lateinit var dialogRecuperarContrasena: Dialog
    lateinit var globalVariable: GlobalClass
    lateinit var activityTmp: Activity

    lateinit var dialogTextTitulo: TextView
    lateinit var dialogTextEntrada: EditText
    lateinit var dialogTextCancelar: Button
    lateinit var dialogTextAceptar: Button

    fun crearDialogRecuperarContrasena(context : Context, activity : Activity)
    {
        activityTmp = activity

        dialogRecuperarContrasena = Dialog(context)

        dialogRecuperarContrasena.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogRecuperarContrasena.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        dialogRecuperarContrasena.setCancelable(false)
        dialogRecuperarContrasena.setContentView(R.layout.dialog_text)

        dialogTextTitulo = dialogRecuperarContrasena.findViewById(R.id.dialogTextTitulo)
        dialogTextEntrada = dialogRecuperarContrasena.findViewById(R.id.dialogTextEntrada)
        dialogTextCancelar = dialogRecuperarContrasena.findViewById(R.id.dialogTextCancelar) as Button
        dialogTextAceptar = dialogRecuperarContrasena.findViewById(R.id.dialogTextAceptar) as Button

        dialogTextTitulo.text = context.getString(R.string.mensaje_crear_correo)

        dialogTextAceptar.setOnClickListener()
        {
            if(dialogTextEntrada.text.toString().isEmailValid()) {
                val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm.isAcceptingText)
                {
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY)
                }

                recuperarContrasena(context, dialogTextEntrada.text.toString().toLowerCase())
            }
            else
            {
                activityTmp.runOnUiThread {
                    Toast.makeText(context, context.getString(R.string.mensaje_contraseña_corta), Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialogTextCancelar.setOnClickListener()
        {
            dialogRecuperarContrasena.dismiss()
        }

    }

    private fun recuperarContrasena(context: Context, usuario: String) {
        val url = urls.url+urls.endPointUsers.endPointRecuperarContrasena

        val jsonObject = JSONObject()
        try {
            jsonObject.put("user", usuario)
            jsonObject.put("idioma", parseInt(context.getString(R.string.numero_idioma)))
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
                activityTmp.runOnUiThread()
                {
                    Toast.makeText(context, context.getString(R.string.mensaje_error_intentear_mas_tarde), Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response)
            {
                val body = response.body()!!.string()

                if(body != null && body.isNotEmpty()) {
                    val gson = GsonBuilder().create()

                    val respuesta = gson.fromJson(body, Respuesta::class.java)

                    if(respuesta.status == 0)
                    {
                        dialogRecuperarContrasena.dismiss()
                        activityTmp.runOnUiThread {
                            Toast.makeText(context, context.getString(R.string.mensaje_dialog_recuperar_contrasena), Toast.LENGTH_LONG).show()
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
        dialogTextEntrada.setText("")
        dialogRecuperarContrasena.show()
    }

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}
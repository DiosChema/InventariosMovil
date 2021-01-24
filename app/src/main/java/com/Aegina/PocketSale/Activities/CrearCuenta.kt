@file:Suppress("DEPRECATION")

package com.Aegina.PocketSale.Activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.Aegina.PocketSale.Metodos.Errores
import com.Aegina.PocketSale.Objets.Respuesta
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.lang.Integer.parseInt
import java.util.*

class CrearCuenta : AppCompatActivity() {

    private val urls: Urls = Urls()
    lateinit var context: Context
    lateinit var crearCuentaEmail : EditText
    lateinit var crearCuentaPassword : EditText
    lateinit var crearCuentaConfirmarPassword : EditText
    lateinit var crearCuentaBotonCrearCuenta : Button
    lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cuenta)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        asignarCampos()
        asignarBotones()

        activity = this
    }

    fun asignarBotones(){
        crearCuentaBotonCrearCuenta.setOnClickListener{
            crearCuenta()
        }
    }

    fun asignarCampos(){
        context = this

        crearCuentaEmail = findViewById(R.id.crearCuentaEmail)
        crearCuentaPassword = findViewById(R.id.crearCuentaPassword)
        crearCuentaConfirmarPassword = findViewById(R.id.crearCuentaConfirmarPassword)
        crearCuentaBotonCrearCuenta = findViewById(R.id.crearCuentaBotonCrearCuenta)
    }

    fun crearCuenta(){

        var email = crearCuentaEmail.text.toString()
        email = email.toLowerCase(Locale.ROOT)
        val password = crearCuentaPassword.text.toString()
        val passwordConfirmar = crearCuentaConfirmarPassword.text.toString()

        if(!email.isEmailValid()) {
            Toast.makeText(this, getString(R.string.mensaje_email_invalido), Toast.LENGTH_SHORT).show()
            return
        }

        if(password.length < 8){
            Toast.makeText(this, getString(R.string.mensaje_contraseña_corta), Toast.LENGTH_SHORT).show()
            return
        }

        if(password != passwordConfirmar){
            Toast.makeText(this, getString(R.string.mensaje_contraseña_diferente), Toast.LENGTH_SHORT).show()
            return
        }

        val url = urls.url+urls.endPointUsers.endPointRegistrarNuevaTienda

        val jsonObject = JSONObject()
        try {
            jsonObject.put("email", email)
            jsonObject.put("password", password)
            jsonObject.put("idioma", parseInt(getString(R.string.numero_idioma)))
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonObject.toString())

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.mensaje_espera))
        progressDialog.setCancelable(false)
        progressDialog.show()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread()
                {
                    progressDialog.dismiss()
                }
            }
            override fun onResponse(call: Call, response: Response)
            {
                val body = response.body()!!.string()
                val gson = GsonBuilder().create()
                var mensajeRespuesta : String

                try
                {
                    val respuesta = gson.fromJson(body, Respuesta::class.java)

                    runOnUiThread {

                        progressDialog.dismiss()

                        if(respuesta.status == 0)
                        {
                            Toast.makeText(context, getString(R.string.mensaje_cuenta_creada), Toast.LENGTH_LONG).show()
                            finish()
                        }
                        else
                        {
                            val errores = Errores()
                            errores.procesarError(context,body,activity)
                        }
                    }
                }
                catch(e: Exception)
                {
                    val errores = Errores()
                    errores.procesarErrorCerrarVentana(context,body,activity)
                }
            }
        })
    }

    fun String.isEmailValid(): Boolean
    {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}
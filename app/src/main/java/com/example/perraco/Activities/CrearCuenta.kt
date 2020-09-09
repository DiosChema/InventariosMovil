package com.example.perraco.Activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.perraco.Objets.GlobalClass
import com.example.perraco.Objets.Respuesta
import com.example.perraco.Objets.Urls
import com.example.perraco.R
import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class CrearCuenta : AppCompatActivity() {

    private val urls: Urls = Urls()
    lateinit var context: Context
    lateinit var crearCuentaEmail : EditText
    lateinit var crearCuentaPassword : EditText
    lateinit var crearCuentaConfirmarPassword : EditText
    lateinit var crearCuentaBotonCrearCuenta : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cuenta)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        context = this

        crearCuentaEmail = findViewById(R.id.crearCuentaEmail)
        crearCuentaPassword = findViewById(R.id.crearCuentaPassword)
        crearCuentaConfirmarPassword = findViewById(R.id.crearCuentaConfirmarPassword)
        crearCuentaBotonCrearCuenta = findViewById(R.id.crearCuentaBotonCrearCuenta)

        crearCuentaBotonCrearCuenta.setOnClickListener{
            crearCuenta()
        }
    }

    fun crearCuenta(){

        var email = crearCuentaEmail.text.toString()
        email = email.toLowerCase()
        var password = crearCuentaPassword.text.toString()
        var passwordConfirmar = crearCuentaConfirmarPassword.text.toString()

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

        val url = urls.url+urls.endPointRegistrarNuevaTienda

        val jsonObject = JSONObject()
        try {
            jsonObject.put("email", email)
            jsonObject.put("password", password)
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
        progressDialog.show()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response)
            {
                progressDialog.dismiss()

                val body = response.body()!!.string()
                val gson = GsonBuilder().create()

                val respuesta = gson.fromJson(body, Respuesta::class.java)

                if(respuesta.status != 0){
                    runOnUiThread {
                        Toast.makeText(context, respuesta.mensaje, Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    runOnUiThread{
                        Toast.makeText(context, getString(R.string.mensaje_cuenta_creada), Toast.LENGTH_SHORT).show()
                    }
                    finish()
                }
            }
        })



    }

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}
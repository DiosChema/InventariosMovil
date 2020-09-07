package com.example.perraco.Activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.perraco.Objets.GlobalClass
import com.example.perraco.Objets.Respuesta
import com.example.perraco.Objets.Urls
import com.example.perraco.R
import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private val urls: Urls = Urls()
    lateinit var context: Context
    lateinit var loginEmail : EditText
    lateinit var loginPassword : EditText
    lateinit var loginBotonIniciarSesion : Button
    lateinit var loginTextPasswordOlvidada : TextView
    lateinit var loginTextNuevaCuenta : TextView
    lateinit var loginPasswordButton : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = this

        loginEmail = findViewById(R.id.loginEmail)
        loginPassword = findViewById(R.id.loginPassword)
        loginBotonIniciarSesion = findViewById(R.id.loginBotonIniciarSesion)
        loginTextPasswordOlvidada = findViewById(R.id.loginTextPasswordOlvidada)
        loginTextNuevaCuenta = findViewById(R.id.loginTextNuevaCuenta)
        loginPasswordButton = findViewById(R.id.loginPasswordButton)

        loginEmail.setText("taco666@hotmail.com")
        loginPassword.setText("perraco12")


        loginBotonIniciarSesion.setOnClickListener{
            iniciarSesion()
        }

        loginTextNuevaCuenta.setOnClickListener{
            pantallaCrearCuenta()
        }

        loginPasswordButton.setOnClickListener {
            if(loginPassword.transformationMethod == PasswordTransformationMethod.getInstance()){
                loginPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else{
                loginPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }


    fun iniciarSesion(){

        var email = loginEmail.text.toString()
        email = email.toLowerCase()
        var password = loginPassword.text.toString()

        if(!email.isEmailValid()) {
            Toast.makeText(this, getString(R.string.mensaje_email_invalido), Toast.LENGTH_SHORT).show()
            return
        }

        if(password.length < 8){
            Toast.makeText(this, getString(R.string.mensaje_contraseña_corta), Toast.LENGTH_SHORT).show()
            return
        }

        val url = urls.url+urls.endPointLoginUsuario

        val jsonObject = JSONObject()
        try {
            jsonObject.put("user", email)
            jsonObject.put("password", password)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonObject.toString())

        val request = Request.Builder()
            .url(url)
            .put(body)
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
                    val globalVariable = applicationContext as GlobalClass
                    globalVariable.token = respuesta.respuesta
                    val intent = Intent(context, Menu::class.java)
                    startActivity(intent)
                }
            }
        })

    }

    fun pantallaCrearCuenta(){
        val intent = Intent(context, CrearCuenta::class.java)
        startActivity(intent)
    }

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }



}
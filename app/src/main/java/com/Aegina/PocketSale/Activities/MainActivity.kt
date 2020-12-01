@file:Suppress("DEPRECATION")

package com.Aegina.PocketSale.Activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.Respuesta
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {

    private val urls: Urls = Urls()
    lateinit var context: Context
    lateinit var loginEmail : EditText
    lateinit var loginPassword : EditText
    lateinit var loginBotonIniciarSesion : Button
    lateinit var loginTextPasswordOlvidada : TextView
    lateinit var loginTextNuevaCuenta : TextView
    lateinit var loginPasswordButton : ImageView
    lateinit var loginLayout : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        asignarCampos()
        asignarBotones()
    }

    fun asignarCampos(){
        context = this
        loginEmail = findViewById(R.id.loginEmail)
        loginPassword = findViewById(R.id.loginPassword)
        loginBotonIniciarSesion = findViewById(R.id.loginBotonIniciarSesion)
        loginTextPasswordOlvidada = findViewById(R.id.loginTextPasswordOlvidada)
        loginTextNuevaCuenta = findViewById(R.id.loginTextNuevaCuenta)
        loginPasswordButton = findViewById(R.id.loginPasswordButton)
        loginLayout = findViewById(R.id.loginLayout)

        loginEmail.setText("taco666@hotmail.com")
        loginPassword.setText("perraco12")

        //Handler().postDelayed(Runnable { loginLayout.visibility = View.VISIBLE }, 500)

    }

    fun asignarBotones(){
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
        email = email.toLowerCase(Locale.ROOT)
        val password = loginPassword.text.toString()

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
                runOnUiThread {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response)
            {


                val json = response.body()!!.string()
                val gson = GsonBuilder().create()

                val respuesta = gson.fromJson(json, Respuesta::class.java)

                if(respuesta.status != 0){
                    runOnUiThread {
                        when (respuesta.status) {
                            -1 -> Toast.makeText(context, getString(R.string.login_usuario_credenciales_incorrectas), Toast.LENGTH_SHORT).show()
                            -2 -> Toast.makeText(context, getString(R.string.login_usuario_cuenta_expirada), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    val globalVariable = applicationContext as GlobalClass
                    globalVariable.token = respuesta.respuesta
                    val intent = Intent(context, Menu::class.java)
                    startActivity(intent)
                }

                progressDialog.dismiss()
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
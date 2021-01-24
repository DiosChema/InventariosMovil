@file:Suppress("DEPRECATION")

package com.Aegina.PocketSale.Activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.Aegina.PocketSale.Dialogs.DialogRecuperarContrasena
import com.Aegina.PocketSale.Metodos.Errores
import com.Aegina.PocketSale.Objets.ActualizarVentana
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.RespuestaLogin
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
    lateinit var activity: Activity
    lateinit var loginHelp: ImageButton

    var dialogRecuperarContrasena = DialogRecuperarContrasena()
    private val sharedPrefFile = "kotlinsharedpreference"
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        asignarCampos()
        asignarBotones()

        activity = this
        dialogRecuperarContrasena.crearDialogRecuperarContrasena(context,activity)

        val sharedPreferences = getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
        val emailTmp = sharedPreferences.getString("email","defaultname")
        if(emailTmp != "defaultname")
        {
            loginEmail.setText(emailTmp)
            loginPassword.requestFocus()
            // open the soft keyboard
            //val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            //activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        }
    }

    fun asignarCampos()
    {
        context = this
        loginEmail = findViewById(R.id.loginEmail)
        loginPassword = findViewById(R.id.loginPassword)
        loginBotonIniciarSesion = findViewById(R.id.loginBotonIniciarSesion)
        loginTextPasswordOlvidada = findViewById(R.id.loginTextPasswordOlvidada)
        loginTextNuevaCuenta = findViewById(R.id.loginTextNuevaCuenta)
        loginPasswordButton = findViewById(R.id.loginPasswordButton)
        loginLayout = findViewById(R.id.loginLayout)
        loginHelp = findViewById(R.id.loginHelp)

        //loginEmail.setText("taco666@hotmail.com")
        //loginPassword.setText("perraco12")

        //Handler().postDelayed(Runnable { loginLayout.visibility = View.VISIBLE }, 500)

    }

    fun asignarBotones()
    {
        loginBotonIniciarSesion.setOnClickListener()
        {
            iniciarSesion()
        }

        loginTextNuevaCuenta.setOnClickListener()
        {
            pantallaCrearCuenta()
        }

        loginTextPasswordOlvidada.setOnClickListener()
        {
            dialogRecuperarContrasena.mostrarVentana()
        }

        loginPasswordButton.setOnClickListener()
        {

            loginPassword.transformationMethod =
                if (loginPassword.transformationMethod == PasswordTransformationMethod.getInstance())
                {
                    HideReturnsTransformationMethod.getInstance()
                }
                else
                {
                    PasswordTransformationMethod.getInstance()
                }
        }

        loginHelp.setOnClickListener()
        {
            val idioma = when(parseInt(context.getString(R.string.numero_idioma))) {
                2 -> "es"
                else -> "en"
            }
            openNewTabWindow("https://pocketsale.herokuapp.com/pocketsale/$idioma", context)
        }
    }


    fun iniciarSesion()
    {

        var email = loginEmail.text.toString()
        email = email.toLowerCase(Locale.ROOT)
        val password = loginPassword.text.toString()

        if(!email.isEmailValid())
        {
            Toast.makeText(this, getString(R.string.mensaje_email_invalido), Toast.LENGTH_SHORT).show()
            return
        }

        if(password.length < 8)
        {
            Toast.makeText(this, getString(R.string.mensaje_contraseña_corta), Toast.LENGTH_SHORT).show()
            return
        }

        habilitarBotones(false)

        val url = urls.url+urls.endPointUsers.endPointLoginUsuario

        val jsonObject = JSONObject()

        try
        {
            jsonObject.put("user", email)
            jsonObject.put("password", password)
            loginPassword.setText("")
        }
        catch (e: JSONException)
        {
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
        progressDialog.setCancelable(false)
        progressDialog.show()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback
        {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()

                runOnUiThread()
                {
                    habilitarBotones(true)
                    Toast.makeText(context, getString(R.string.mensaje_error_intentear_mas_tarde), Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response)
            {
                val json = response.body()!!.string()
                val gson = GsonBuilder().create()

                try
                {
                    val respuesta = gson.fromJson(json, RespuestaLogin::class.java)

                    if(respuesta.status != 0)
                    {
                        runOnUiThread()
                        {
                            when (respuesta.status)
                            {
                                -1 -> Toast.makeText(context, getString(R.string.login_usuario_credenciales_incorrectas), Toast.LENGTH_SHORT).show()
                                -2 ->
                                {
                                    val globalVariable = applicationContext as GlobalClass
                                    globalVariable.tokenEspecial = respuesta.tokenEspecial

                                    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                                    if (imm.isAcceptingText)
                                    {
                                        imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY)
                                    }

                                    val intent = Intent(context, Suscripcion::class.java)
                                    startActivity(intent)
                                    Toast.makeText(context, getString(R.string.login_usuario_cuenta_expirada), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    else
                    {
                        val globalVariable = applicationContext as GlobalClass
                        globalVariable.usuario = respuesta.usuario
                        globalVariable.actualizarVentana = ActualizarVentana()
                        globalVariable.tokenEspecial = respuesta.tokenEspecial

                        val sharedPreferences = getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
                        val editor =  sharedPreferences.edit()
                        editor.putString("email",email)
                        editor.apply()
                        editor.commit()

                        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                        if (imm.isAcceptingText)
                        {
                            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY)
                        }

                        val intent = Intent(context, Menu::class.java)
                        startActivity(intent)
                    }
                }
                catch (e:Exception)
                {
                    val errores = Errores()
                    errores.procesarError(context,json,activity)
                }

                runOnUiThread()
                {
                    progressDialog.dismiss()
                    habilitarBotones(true)
                }

            }
        })

    }

    fun openNewTabWindow(urls: String, context: Context) {
        val uris = Uri.parse(urls)
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val b = Bundle()
        b.putBoolean("new_window", true)
        intents.putExtras(b)
        context.startActivity(intents)
    }

    fun habilitarBotones(habilitar: Boolean)
    {
        loginBotonIniciarSesion.isEnabled = habilitar
        loginTextPasswordOlvidada.isEnabled = habilitar
        loginTextNuevaCuenta.isEnabled = habilitar
    }

    fun pantallaCrearCuenta(){
        habilitarBotones(false)
        val intent = Intent(context, CrearCuenta::class.java)
        startActivity(intent)
        habilitarBotones(true)
    }

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }



}
package com.Aegina.PocketSale.Activities

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.squareup.picasso.Picasso
import java.lang.Exception

class MenuPerfil : AppCompatActivity() {

    lateinit var globalVariable: GlobalClass
    private val urls: Urls = Urls()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_perfil)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        globalVariable = this.applicationContext as GlobalClass

        asignarBotones()
    }

    fun asignarBotones(){

        val logoTienda = findViewById<ImageView>(R.id.menuPerfilTienda)

        logoTienda.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+globalVariable.usuario!!.user+".jpeg"+"&token="+globalVariable.usuario!!.token+"&tipoImagen=1")

        val botonPerfilUsuario = findViewById<ImageView>(R.id.menuPerfilBotonPerfil)
        botonPerfilUsuario.setOnClickListener()
        {
            val intent = Intent(this, PerfilDetalle::class.java).apply {
                putExtra("correo", globalVariable.usuario?.user)
            }
            startActivity(intent)
        }

        val buttonTienda = findViewById<ImageView>(R.id.menuPerfilBotonTienda)
        buttonTienda.setOnClickListener()
        {
            val intent = Intent(this, TiendaDetalle::class.java)
            startActivity(intent)
        }

        val buttonSuscripcion = findViewById<ImageView>(R.id.menuPerfilBotonSuscripcion)
        buttonSuscripcion.setOnClickListener()
        {
            val intent = Intent(this, Suscripcion::class.java)
            startActivity(intent)
        }

        val buttonEmpleados = findViewById<ImageView>(R.id.buttonEmpleados)
        buttonEmpleados.setOnClickListener()
        {
            //val intent = Intent(this, Suscripcion::class.java)
            if(globalVariable.usuario!!.permisosAdministrador)
            {
                val intent = Intent(this, Empleados::class.java)
                startActivity(intent)
            }
            else
            {
                Toast.makeText(this,getString(R.string.permisos_denegado), Toast.LENGTH_LONG).show()
            }
        }

    }

    fun ImageView.loadUrl(url: String) {
        try {
            Picasso.with(context).load(url).into(this)}
        catch(e:Exception){}
    }
}
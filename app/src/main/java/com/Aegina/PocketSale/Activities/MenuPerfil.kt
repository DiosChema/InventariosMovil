package com.Aegina.PocketSale.Activities

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.R
import kotlinx.android.synthetic.*

class MenuPerfil : AppCompatActivity() {

    lateinit var globalVariable: GlobalClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_perfil)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        globalVariable = this.applicationContext as GlobalClass

        asignarBotones()
    }

    fun asignarBotones(){
        val botonPerfilUsuario = findViewById<ImageView>(R.id.menuPerfilBotonPerfil)
        botonPerfilUsuario.setOnClickListener()
        {
            val intent = Intent(this, PerfilDetalle::class.java).apply {
                putExtra("correo", globalVariable.usuario?.user)
            }
            startActivity(intent)
            /*val intent = Intent(this, PerfilUsuario::class.java)
            startActivity(intent)*/
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
            val intent = Intent(this, Perfil::class.java)
            startActivity(intent)
        }

    }
}
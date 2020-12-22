package com.Aegina.PocketSale.Activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.R


class Menu : AppCompatActivity() {

    lateinit var globalVariable: GlobalClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        globalVariable = this.applicationContext as GlobalClass

        asignarBotones()
    }

    fun asignarBotones(){
        val buttonVenta = findViewById<ImageView>(R.id.Venta)
        buttonVenta.setOnClickListener()
        {
            if(globalVariable.usuario!!.permisosVenta)
            {
                val intent = Intent(this, Venta::class.java)
                startActivity(intent)
            }
            else
            {
                Toast.makeText(this,getString(R.string.permisos_denegado),Toast.LENGTH_LONG).show()
            }
        }

        val buttonInventario = findViewById<ImageView>(R.id.Inventario)
        buttonInventario.setOnClickListener()
        {
            val intent = Intent(this, InventarioPager::class.java)
            startActivity(intent)
        }

        val buttonEstadistica = findViewById<ImageView>(R.id.estadistica)
        buttonEstadistica.setOnClickListener()
        {
            if(globalVariable.usuario!!.permisosEstadisticas)
            {
                val intent = Intent(this, EstadisticaArticulo::class.java)
                startActivity(intent)
            }
            else
            {
                Toast.makeText(this,getString(R.string.permisos_denegado),Toast.LENGTH_LONG).show()
            }
        }

        val estadistica2 = findViewById<ImageView>(R.id.estadistica2)
        estadistica2.setOnClickListener()
        {
            if(globalVariable.usuario!!.permisosEstadisticas)
            {
                val intent = Intent(this, EstadisticasPager::class.java)
                startActivity(intent)
            }
            else
            {
                Toast.makeText(this,getString(R.string.permisos_denegado),Toast.LENGTH_LONG).show()
            }
        }

        val estadistica3 = findViewById<ImageView>(R.id.estadistica3)
        estadistica3.setOnClickListener()
        {
            //val intent = Intent(this, Suscripcion::class.java)
            if(globalVariable.usuario!!.permisosAdministrador)
            {
                val intent = Intent(this, Perfil::class.java)
                startActivity(intent)
            }
            else
            {
                Toast.makeText(this,getString(R.string.permisos_denegado),Toast.LENGTH_LONG).show()
            }
        }

        val estadistica4 = findViewById<ImageView>(R.id.estadistica4)
        estadistica4.setOnClickListener()
        {
            if(globalVariable.usuario!!.permisosProovedor)
            {
                val intent = Intent(this, SurtidoPager::class.java)
                startActivity(intent)
            }
            else
            {
                Toast.makeText(this,getString(R.string.permisos_denegado),Toast.LENGTH_LONG).show()
            }
        }
    }
}
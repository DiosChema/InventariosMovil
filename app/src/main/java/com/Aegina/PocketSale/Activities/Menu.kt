package com.Aegina.PocketSale.Activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.Aegina.PocketSale.R


class Menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        asignarBotones()
    }

    fun asignarBotones(){
        val buttonVenta = findViewById<ImageView>(R.id.Venta)
        buttonVenta.setOnClickListener()
        {
            val intent = Intent(this, Venta::class.java)
            startActivity(intent)
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
            val intent = Intent(this, EstadisticaArticulo::class.java)
            startActivity(intent)
        }

        val estadistica2 = findViewById<ImageView>(R.id.estadistica2)
        estadistica2.setOnClickListener()
        {
            val intent = Intent(this, EstadisticasPager::class.java)
            startActivity(intent)
        }
        val estadistica3 = findViewById<ImageView>(R.id.estadistica3)
        estadistica3.setOnClickListener()
        {
            val intent = Intent(this, Suscripcion::class.java)
            startActivity(intent)
        }

        val estadistica4 = findViewById<ImageView>(R.id.estadistica4)
        estadistica4.setOnClickListener()
        {
            val intent = Intent(this, Surtido::class.java)
            startActivity(intent)
        }
    }
}
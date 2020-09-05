package com.example.perraco.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.perraco.Objets.GlobalClass
import com.example.perraco.R


class Menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val buttonVenta = findViewById<Button>(R.id.Venta)
        buttonVenta?.setOnClickListener()
        {
            val intent = Intent(this, Venta::class.java)
            startActivity(intent)
        }

        val buttonInventario = findViewById<Button>(R.id.Inventario)
        buttonInventario?.setOnClickListener()
        {
            val intent = Intent(this, Inventario::class.java)
            startActivity(intent)
        }
    }
}
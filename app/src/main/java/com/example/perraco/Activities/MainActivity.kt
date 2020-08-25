package com.example.perraco.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.widget.Button
import com.example.perraco.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val buttonVenta = findViewById<Button>(R.id.Venta)
        buttonVenta?.setOnClickListener()
        {
            val intent = Intent(this, Venta::class.java).apply {
                putExtra(EXTRA_MESSAGE, 15)//tienda
            }
            startActivity(intent)
        }

        val buttonInventario = findViewById<Button>(R.id.Inventario)
        buttonInventario?.setOnClickListener()
        {
            val intent = Intent(this, Inventario::class.java).apply {
                putExtra(EXTRA_MESSAGE, 15)//tienda
            }
            startActivity(intent)
        }


    }






}
package com.example.perraco

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val button = findViewById<Button>(R.id.Inventario)
        button?.setOnClickListener()
        {
            val intent = Intent(this, Inventario::class.java).apply {
                putExtra(EXTRA_MESSAGE, 15)//tienda
            }
            startActivity(intent)
        }


    }






}
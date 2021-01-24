package com.Aegina.PocketSale.Activities

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.squareup.picasso.Picasso
import java.lang.Exception

class MenuStatistics : AppCompatActivity() {

    lateinit var globalVariable: GlobalClass
    private val urls: Urls = Urls()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_statistics)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        globalVariable = this.applicationContext as GlobalClass

        asignarBotones()
    }

    fun asignarBotones(){

        val logoTienda = findViewById<ImageView>(R.id.menuStatisticsStore)

        logoTienda.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+"t"+globalVariable.usuario!!.tienda+".jpeg"+"&token="+globalVariable.usuario!!.token+"&tipoImagen=2")

        val buttonMenuStatisticsItems = findViewById<ImageView>(R.id.menuStatisticsItems)
        buttonMenuStatisticsItems.setOnClickListener()
        {
            val intent = Intent(this, EstadisticaArticulo::class.java)
            startActivity(intent)
        }

        val buttonMenuStatisticsInventory = findViewById<ImageView>(R.id.menuStatisticsInventory)
        buttonMenuStatisticsInventory.setOnClickListener()
        {
            val intent = Intent(this, EstadisticasPager::class.java)
            startActivity(intent)
        }

        val buttonMenuStatisticsNoSells = findViewById<ImageView>(R.id.buttonMenuStatisticsNoSells)
        buttonMenuStatisticsNoSells.setOnClickListener()
        {
            val intent = Intent(this, EstadisticaArticulosNoVendidos::class.java)
            startActivity(intent)
        }

    }

    fun ImageView.loadUrl(url: String) {
        try {
            Picasso.with(context).load(url).into(this)}
        catch(e:Exception){}
    }
}
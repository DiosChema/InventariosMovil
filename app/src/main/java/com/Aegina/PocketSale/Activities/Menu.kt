package com.Aegina.PocketSale.Activities

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.squareup.picasso.Picasso
import java.lang.Exception

class Menu : AppCompatActivity() {

    lateinit var globalVariable: GlobalClass
    private val urls: Urls = Urls()
    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        globalVariable = this.applicationContext as GlobalClass

        asignarBotones()
    }

    fun asignarBotones(){

        val logoTienda = findViewById<ImageView>(R.id.Tienda)

        logoTienda.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+"t"+globalVariable.usuario!!.tienda+".jpeg"+"&token="+globalVariable.usuario!!.token+"&tipoImagen=2")

        val buttonVenta = findViewById<ImageView>(R.id.menuSales)
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

        val buttonMenuSales = findViewById<ImageView>(R.id.menuInventory)
        buttonMenuSales.setOnClickListener()
        {
            val intent = Intent(this, InventarioPager::class.java)
            startActivity(intent)
        }

        val buttonMenuAssortment = findViewById<ImageView>(R.id.menuAssortment)
        buttonMenuAssortment.setOnClickListener()
        {

            val intent = Intent(this, SurtidoPager::class.java)
            startActivity(intent)
        }

        val buttonMenuStatisticsInventory = findViewById<ImageView>(R.id.menuStatisticsInventory)
        buttonMenuStatisticsInventory.setOnClickListener()
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

        val buttonMenuStatisticsItems = findViewById<ImageView>(R.id.statisticsItems)
        buttonMenuStatisticsItems.setOnClickListener()
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

        val buttonMenuLoss = findViewById<ImageView>(R.id.menuLoss)
        buttonMenuLoss.setOnClickListener()
        {
            val intent = Intent(this, LossPager::class.java)
            startActivity(intent)
        }

        val buttonMenuProfile = findViewById<ImageView>(R.id.menuPerfilBotonPerfil)
        buttonMenuProfile.setOnClickListener()
        {
            val intent = Intent(this, PerfilDetalle::class.java).apply {
                putExtra("correo", globalVariable.usuario?.user)
            }
            startActivity(intent)
        }

        val buttonMenuEmployees = findViewById<ImageView>(R.id.buttonEmployees)
        buttonMenuEmployees.setOnClickListener()
        {
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

        val buttonMenuShop = findViewById<ImageView>(R.id.buttonMenuShop)
        buttonMenuShop.setOnClickListener()
        {
            val intent = Intent(this, TiendaDetalle::class.java)
            startActivity(intent)
        }

        val ButtonMenuSubscription = findViewById<ImageView>(R.id.menuSubscription)
        ButtonMenuSubscription.setOnClickListener()
        {
            val intent = Intent(this, Suscripcion::class.java)
            startActivity(intent)
        }

        val menuTutorial = findViewById<ImageView>(R.id.menuTutorial)
        menuTutorial.setOnClickListener()
        {
            val idioma = when(Integer.parseInt(context.getString(R.string.numero_idioma))) {
                2 -> "es"
                else -> "en"
            }
            openNewTabWindow(urls.url + "pocketsale/$idioma", context)
        }

    }

    fun openNewTabWindow(urls: String, context: Context) {
        val uris = Uri.parse(urls)
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val b = Bundle()
        b.putBoolean("new_window", true)
        intents.putExtras(b)
        context.startActivity(intents)
    }

    fun ImageView.loadUrl(url: String) {
        try {Picasso.with(context).load(url).into(this)}
        catch(e:Exception){}
    }
}
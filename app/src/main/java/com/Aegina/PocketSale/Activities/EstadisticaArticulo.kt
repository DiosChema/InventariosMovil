package com.Aegina.PocketSale.Activities

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Dialogs.DialogAgregarArticulos
import com.Aegina.PocketSale.Dialogs.DialogFecha
import com.Aegina.PocketSale.Metodos.Errores
import com.Aegina.PocketSale.Metodos.Meses
import com.Aegina.PocketSale.Metodos.Paginado
import com.Aegina.PocketSale.Objets.*
import com.Aegina.PocketSale.Objets.Inventory.ListEstadisticaArticuloObject
import com.Aegina.PocketSale.Objets.Inventory.ListInventoryNoSells
import com.Aegina.PocketSale.Objets.Inventory.ListInventoryObject
import com.Aegina.PocketSale.R
import com.Aegina.PocketSale.RecyclerView.RecyclerViewEstadisticaArticulo
import java.lang.Integer.parseInt
import java.util.*
import kotlin.collections.ArrayList

class EstadisticaArticulo : AppCompatActivity(),
    DialogFecha.DialogFecha, DialogAgregarArticulos.DialogAgregarArticulo {

    var listaTmp:MutableList<EstadisticaArticuloObject> = ArrayList()
    lateinit var context : Context
    lateinit var mViewEstadisticaArticulo : RecyclerViewEstadisticaArticulo
    lateinit var mRecyclerView : RecyclerView
    lateinit var globalVariable: GlobalClass

    lateinit var fechaInicial : Date
    lateinit var fechaFinal : Date
    lateinit var estadisticaArticuloFechaInicial : TextView
    lateinit var estadisticaArticuloFechaFinal : TextView
    lateinit var estadisticaArticuloFiltro : ImageView
    lateinit var estadisticaArticuloLeft : ImageButton
    lateinit var estadisticaArticuloRight : ImageButton
    lateinit var estadisticaArticuloBack : ImageButton

    val nombreMes = Meses()

    var dialogoAgregarArticulos = DialogAgregarArticulos()
    val urls = Urls()
    var dialogFecha = DialogFecha()
    val paginado = Paginado()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadistica_articulo)
        context = this
        dialogFecha.crearVentana(this)
        globalVariable = context.applicationContext as GlobalClass
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        asignarBotones()
        crearRecyclerView()

        dialogoAgregarArticulos.crearDialogInicial(this,globalVariable, Activity())
        dialogoAgregarArticulos.crearDialogArticulos(0)
        dialogoAgregarArticulos.crearDialogFiltrosArticulosMasVendidos(1)

        asignarFechaSemana()
    }

    fun crearRecyclerView(){
        mViewEstadisticaArticulo = RecyclerViewEstadisticaArticulo()
        mRecyclerView = findViewById(R.id.estadisticaArticuloRecyclerView)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mViewEstadisticaArticulo.RecyclerAdapter(listaTmp, context)
        mRecyclerView.adapter = mViewEstadisticaArticulo
    }

    fun asignarBotones(){
        estadisticaArticuloFechaInicial = findViewById(R.id.estadisticaArticuloFechaInicial)
        estadisticaArticuloFechaInicial.setOnClickListener()
        {
            dialogFecha.abrirDialogFechaInicial(fechaInicial, fechaFinal)
        }

        estadisticaArticuloFechaFinal = findViewById(R.id.estadisticaArticuloFechaFinal)
        estadisticaArticuloFechaFinal.setOnClickListener()
        {
            dialogFecha.abrirDialogFechaFinal(fechaInicial, fechaFinal)
        }

        estadisticaArticuloFiltro = findViewById(R.id.estadisticaArticuloFiltro)
        estadisticaArticuloFiltro.setOnClickListener()
        {
            dialogoAgregarArticulos.mostrarDialogFiltros()
        }

        estadisticaArticuloLeft = findViewById(R.id.estadisticaArticuloLeft)
        estadisticaArticuloLeft.setOnClickListener()
        {
            if(dialogoAgregarArticulos.totalArticulos > dialogoAgregarArticulos.limiteArticulos && dialogoAgregarArticulos.pagina > 0)
            {
                dialogoAgregarArticulos.pagina--
                dialogoAgregarArticulos.buscarArticulosMasVendidos()
            }
        }
        estadisticaArticuloRight = findViewById(R.id.estadisticaArticuloRight)
        estadisticaArticuloRight.setOnClickListener()
        {
            val maximoPaginas = paginado.obtenerPaginadoMaximo(dialogoAgregarArticulos.totalArticulos, dialogoAgregarArticulos.limiteArticulos)

            if(dialogoAgregarArticulos.totalArticulos > dialogoAgregarArticulos.limiteArticulos && dialogoAgregarArticulos.pagina < maximoPaginas)
            {
                dialogoAgregarArticulos.pagina++
                dialogoAgregarArticulos.buscarArticulosMasVendidos()
            }
        }

        estadisticaArticuloBack = findViewById(R.id.estadisticaArticuloBack)
        estadisticaArticuloBack.setOnClickListener()
        {
            finish()
        }
    }

    fun asignarFechaInicial(){
        val calendar = Calendar.getInstance()
        calendar.time = fechaInicial

        val text = if(parseInt(getString(R.string.numero_idioma))  > 1)
        {
            "" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + nombreMes.obtenerMesCorto((calendar.get(Calendar.MONTH) + 1),context) + "-" + calendar.get(Calendar.YEAR)
        }
        else
        {
            "" + nombreMes.obtenerMesCorto((calendar.get(Calendar.MONTH) + 1),context) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.YEAR)
        }

        estadisticaArticuloFechaInicial.text = text
    }

    fun asignarFechaFinal(){
        val calendar = Calendar.getInstance()
        calendar.time = fechaFinal

        val text = if(parseInt(getString(R.string.numero_idioma))  > 1)
        {
            "" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + nombreMes.obtenerMesCorto((calendar.get(Calendar.MONTH) + 1),context) + "-" + calendar.get(Calendar.YEAR)
        }
        else
        {
            "" + nombreMes.obtenerMesCorto((calendar.get(Calendar.MONTH) + 1),context) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.YEAR)
        }

        estadisticaArticuloFechaFinal.text = text
    }

    fun asignarHoraCalendar(calendar : Calendar, hora : Int, minuto : Int, segundo : Int) : Calendar{
        calendar.set(Calendar.HOUR_OF_DAY, hora)
        calendar.set(Calendar.MINUTE, minuto)
        calendar.set(Calendar.SECOND, segundo)

        return calendar
    }

    fun asignarFechaHoy(){
        var calendar = Calendar.getInstance()
        calendar = asignarHoraCalendar(calendar, 23, 59, 59)
        fechaFinal = calendar.time

        calendar = asignarHoraCalendar(calendar, 0, 0, 0)
        fechaInicial = calendar.time

        buscarArticulo()
    }

    fun asignarFechaSemana(){
        var calendar = Calendar.getInstance()
        calendar = asignarHoraCalendar(calendar, 23, 59, 59)
        fechaFinal = calendar.time

        calendar.add(Calendar.DAY_OF_MONTH, -6)
        calendar = asignarHoraCalendar(calendar, 0, 0, 0)
        fechaInicial = calendar.time

        buscarArticulo()
    }

    fun asignarFechaMes(){
        var calendar = Calendar.getInstance()
        calendar = asignarHoraCalendar(calendar, 23, 59, 59)
        fechaFinal = calendar.time

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar = asignarHoraCalendar(calendar, 0, 0, 0)
        fechaInicial = calendar.time

        buscarArticulo()
    }

    fun buscarArticulo(){
        asignarFechaInicial()
        asignarFechaFinal()
        dialogoAgregarArticulos.pagina = 0
        dialogoAgregarArticulos.asignarFechas(fechaInicial, fechaFinal)
        dialogoAgregarArticulos.buscarArticulosMasVendidos()
    }

    override fun obtenerFechaInicial() {
        fechaInicial = dialogFecha.fechaInicial
        buscarArticulo()
    }

    override fun obtenerFechaFinal() {
        fechaFinal = dialogFecha.fechaFinal
        buscarArticulo()
    }

    override fun obtenerFecha() {
        fechaInicial = dialogFecha.fechaInicial
        fechaFinal = dialogFecha.fechaFinal
        buscarArticulo()
    }

    override fun numeroArticulo(articulo: InventarioObjeto) {}

    override fun agregarArticulos(articulosCarrito: MutableList<InventarioObjeto>) {}

    override fun abrirCamara() {}

    override fun listaArticulos(listInventoryObject: ListInventoryObject) {}
    override fun listaArticulosNoVendidos(listInventoryObject: ListInventoryNoSells) {}

    override fun listaArticulosMasVendidos(listaArticulos: ListEstadisticaArticuloObject)
    {
        runOnUiThread {
            if(listaArticulos.count > dialogoAgregarArticulos.limiteArticulos)
            {
                estadisticaArticuloLeft.visibility = View.VISIBLE
                estadisticaArticuloRight.visibility = View.VISIBLE
            }
            else
            {
                estadisticaArticuloLeft.visibility = View.GONE
                estadisticaArticuloRight.visibility = View.GONE
            }
            mViewEstadisticaArticulo.RecyclerAdapter(listaArticulos.inventory.toMutableList(), context)
            mViewEstadisticaArticulo.notifyDataSetChanged()
        }
    }

    override fun lanzarMensaje(mensaje: String) {
        runOnUiThread()
        {
            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
        }
    }

    override fun procesarError(json: String) {
        val errores = Errores()
        errores.procesarError(context,json,this)
    }
}
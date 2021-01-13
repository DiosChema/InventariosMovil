@file:Suppress("DEPRECATION")

package com.Aegina.PocketSale.Activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.Aegina.PocketSale.Objets.*
import com.Aegina.PocketSale.R
import com.Aegina.PocketSale.RecyclerView.RecyclerViewEstadisticaArticulo
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.lang.Integer.parseInt
import java.text.SimpleDateFormat
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
    lateinit var estadisticaArticuloFiltro : ImageButton

    val nombreMes = Meses()

    var dialogoAgregarArticulos = DialogAgregarArticulos()
    val urls = Urls()
    var dialogFecha = DialogFecha()

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
        dialogoAgregarArticulos.crearDialogFiltrosArticulosMasVendidos()

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

    override fun numeroArticulo(articulo: InventarioObjeto) {    }

    override fun agregarArticulos(articulosCarrito: MutableList<InventarioObjeto>) {    }

    override fun abrirCamara() {    }

    override fun listaArticulos(listaArticulos: MutableList<ArticuloInventarioObjeto>) {    }

    override fun listaArticulosMasVendidos(listaArticulos: MutableList<EstadisticaArticuloObject>)
    {
        runOnUiThread {
            mViewEstadisticaArticulo.RecyclerAdapter(listaArticulos, context)
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
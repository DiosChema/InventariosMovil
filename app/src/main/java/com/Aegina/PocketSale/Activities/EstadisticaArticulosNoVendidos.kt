package com.Aegina.PocketSale.Activities

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Dialogs.DialogAgregarArticulos
import com.Aegina.PocketSale.Metodos.Errores
import com.Aegina.PocketSale.Metodos.Meses
import com.Aegina.PocketSale.Metodos.Paginado
import com.Aegina.PocketSale.Objets.*
import com.Aegina.PocketSale.Objets.Inventory.InventoryDateObject
import com.Aegina.PocketSale.Objets.Inventory.ListEstadisticaArticuloObject
import com.Aegina.PocketSale.Objets.Inventory.ListInventoryNoSells
import com.Aegina.PocketSale.Objets.Inventory.ListInventoryObject
import com.Aegina.PocketSale.R
import com.Aegina.PocketSale.RecyclerView.RecyclerViewArticulosNoVendidos
import java.util.*
import kotlin.collections.ArrayList

class EstadisticaArticulosNoVendidos : AppCompatActivity(),
    DialogAgregarArticulos.DialogAgregarArticulo {

    var listaTmp:MutableList<InventoryDateObject> = ArrayList()
    lateinit var context : Context
    lateinit var mViewEstadisticaArticulo : RecyclerViewArticulosNoVendidos
    lateinit var mRecyclerView : RecyclerView
    lateinit var globalVariable: GlobalClass

    lateinit var fechaInicial : Date
    lateinit var fechaFinal : Date
    lateinit var estadisticaArticuloFiltro : ImageButton
    lateinit var estadisticaArticuloLeft : ImageButton
    lateinit var estadisticaArticuloRight : ImageButton

    val nombreMes = Meses()
    val paginado = Paginado()

    var dialogoAgregarArticulos = DialogAgregarArticulos()
    val urls = Urls()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadistica_articulo)
        context = this
        globalVariable = context.applicationContext as GlobalClass
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        asignarBotones()
        crearRecyclerView()

        dialogoAgregarArticulos.crearDialogInicial(this,globalVariable, Activity())
        dialogoAgregarArticulos.crearDialogArticulos(0)
        dialogoAgregarArticulos.crearDialogFiltrosArticulosMasVendidos(2)

        buscarArticulo()
    }

    fun crearRecyclerView(){
        mViewEstadisticaArticulo = RecyclerViewArticulosNoVendidos()
        mRecyclerView = findViewById(R.id.estadisticaArticuloRecyclerView)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mViewEstadisticaArticulo.RecyclerAdapter(listaTmp, context)
        mRecyclerView.adapter = mViewEstadisticaArticulo
    }

    fun asignarBotones(){
        val estadisticaArticuloFechaInicial = findViewById<TextView>(R.id.estadisticaArticuloFechaInicial)
        estadisticaArticuloFechaInicial.visibility = View.INVISIBLE

        val estadisticaArticuloFechaFinal = findViewById<TextView>(R.id.estadisticaArticuloFechaFinal)
        estadisticaArticuloFechaFinal.visibility = View.INVISIBLE

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
                dialogoAgregarArticulos.buscarArticulosNoVendidos()
            }
        }
        estadisticaArticuloRight = findViewById(R.id.estadisticaArticuloRight)
        estadisticaArticuloRight.setOnClickListener()
        {
            val maximoPaginas = paginado.obtenerPaginadoMaximo(dialogoAgregarArticulos.totalArticulos, dialogoAgregarArticulos.limiteArticulos)

            if(dialogoAgregarArticulos.totalArticulos > dialogoAgregarArticulos.limiteArticulos && dialogoAgregarArticulos.pagina < maximoPaginas)
            {
                dialogoAgregarArticulos.pagina++
                dialogoAgregarArticulos.buscarArticulosNoVendidos()
            }
        }
    }

    fun buscarArticulo(){
        dialogoAgregarArticulos.pagina = 0
        dialogoAgregarArticulos.buscarArticulosNoVendidos()
    }

    override fun numeroArticulo(articulo: InventarioObjeto) {    }

    override fun agregarArticulos(articulosCarrito: MutableList<InventarioObjeto>) {    }

    override fun abrirCamara() {    }

    override fun listaArticulos(listInventoryObject: ListInventoryObject) {    }

    override fun listaArticulosNoVendidos(listInventoryObject: ListInventoryNoSells) {
        runOnUiThread {
            if(listInventoryObject.count > dialogoAgregarArticulos.limiteArticulos)
            {
                estadisticaArticuloLeft.visibility = View.VISIBLE
                estadisticaArticuloRight.visibility = View.VISIBLE
            }
            else
            {
                estadisticaArticuloLeft.visibility = View.GONE
                estadisticaArticuloRight.visibility = View.GONE
            }

            mViewEstadisticaArticulo.RecyclerAdapter(listInventoryObject.Inventory.toMutableList(), context)
            mViewEstadisticaArticulo.notifyDataSetChanged()
        }
    }

    override fun listaArticulosMasVendidos(listaArticulos: ListEstadisticaArticuloObject) {}

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
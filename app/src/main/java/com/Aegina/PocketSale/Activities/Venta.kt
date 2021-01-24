@file:Suppress("DEPRECATION")

package com.Aegina.PocketSale.Activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Dialogs.DialogAgregarArticulos
import com.Aegina.PocketSale.Dialogs.DialogAgregarNumero
import com.Aegina.PocketSale.Dialogs.DialogFinalizarVenta
import com.Aegina.PocketSale.Dialogs.DialogModificarArticulo
import com.Aegina.PocketSale.Metodos.Errores
import com.Aegina.PocketSale.Objets.*
import com.Aegina.PocketSale.Objets.Inventory.ListEstadisticaArticuloObject
import com.Aegina.PocketSale.Objets.Inventory.ListInventoryNoSells
import com.Aegina.PocketSale.Objets.Inventory.ListInventoryObject
import com.Aegina.PocketSale.R
import com.Aegina.PocketSale.RecyclerView.*
import com.google.gson.GsonBuilder
import com.google.zxing.integration.android.IntentIntegrator
import okhttp3.*
import java.io.IOException
import java.lang.Double.parseDouble
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Venta : AppCompatActivity(), DialogAgregarArticulos.DialogAgregarArticulo,
    DialogAgregarNumero.DialogAgregarNumero, DialogFinalizarVenta.DialogFinalizarVenta,
    DialogModificarArticulo.ModificarArticulo{

    lateinit var context : Context
    lateinit var activity: Activity
    val urls: Urls = Urls()

    var listaArticulosVenta: MutableList<InventarioObjeto> = ArrayList()
    var dialogoAgregarArticulos = DialogAgregarArticulos()
    var dialogoFinalizarVenta = DialogFinalizarVenta()

    lateinit var mViewVenta : RecyclerViewVenta
    lateinit var mRecyclerView : RecyclerView

    lateinit var globalVariable: GlobalClass
    lateinit var ventaTotalArticulos: TextView
    lateinit var ventaTotalVenta: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venta)

        context = this
        activity = this

        globalVariable = applicationContext as GlobalClass
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        dialogoAgregarArticulos.crearDialogInicial(context,globalVariable, Activity())
        dialogoAgregarArticulos.crearDialogArticulos(0)
        dialogoAgregarArticulos.crearDialogFiltros()
        crearRecyclerView()

        asignarRecursos()
    }

    fun asignarRecursos(){

        ventaTotalArticulos = findViewById(R.id.ventaTotalArticulos)
        ventaTotalVenta = findViewById(R.id.ventaTotalVenta)

        val ButtonNuevoArticulo = findViewById<ImageButton>(R.id.ventaNuevoArticulo)
        ButtonNuevoArticulo.setOnClickListener {
            dialogoAgregarArticulos.mostrarDialogArticulos()
        }

        val ButtonTerminarVenta = findViewById<ImageButton>(R.id.ventaTerminarVenta)
        ButtonTerminarVenta.setOnClickListener {
            if(listaArticulosVenta.size > 0)
                dialogoFinalizarVenta.crearDialog(context,parseDouble(ventaTotalVenta.text.toString().substring(1,ventaTotalVenta.text.length)))
        }

        actualizarCantidadPrecio()
    }

    fun actualizarCantidadPrecio(){

        var totalCantidad = 0
        var totalPrecio = 0.0

        for(articulos in listaArticulosVenta){
            totalCantidad += articulos.cantidad
            totalPrecio += (articulos.precio * articulos.cantidad).round(2)
        }

        ventaTotalArticulos.text = totalCantidad.toString()
        val textTmp = "$" + totalPrecio.round(2)
        ventaTotalVenta.text = textTmp
    }

    fun crearRecyclerView(){
        mViewVenta = RecyclerViewVenta()
        mRecyclerView = findViewById(R.id.rvVenta)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mViewVenta.RecyclerAdapter(listaArticulosVenta, context)
        mRecyclerView.adapter = mViewVenta

        val dialogAgregarNumero = DialogAgregarNumero()
        val dialogModificarArticulo = DialogModificarArticulo()

        mRecyclerView.addOnItemTouchListener(RecyclerItemClickListener(context, mRecyclerView, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int)
            {
                dialogAgregarNumero.crearDialogNumero(context, position, listaArticulosVenta[position].cantidad)
            }

            override fun onLongItemClick(view: View?, position: Int)
            {
                dialogModificarArticulo.crearDialogModificarArticulo(context, position, listaArticulosVenta[position].precio, listaArticulosVenta[position].costo, 0)
            }
        }))
    }

    fun subirVenta() {
        val url = urls.url+urls.endPointVentas.endPointVenta

        val articulos: MutableList<ArticuloObjeto> = ArrayList()

        for (articuloTmp in listaArticulosVenta) {

            val articulo = ArticuloObjeto(
                articuloTmp.idArticulo,
                articuloTmp.cantidad,
                articuloTmp.precio,
                articuloTmp.nombre,
                articuloTmp.costo,
                articuloTmp.modificaInventario)

            if(articulo.cantidad > 0)
                articulos.add(articulo)

        }

        if(articulos.size == 0) {
            Toast.makeText(this, getString(R.string.mensaje_venta_sin_articulos), Toast.LENGTH_SHORT).show()
            return
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val currentDate = sdf.format(Date())

        val venta = VentaObjeto(globalVariable.usuario!!.token, currentDate.toString(), articulos)

        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val jsonVenta: String = gsonPretty.toJson(venta)

        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonVenta)

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val client = OkHttpClient()
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.mensaje_espera))
        progressDialog.setCancelable(false)
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                runOnUiThread()
                {
                    Toast.makeText(context, context.getString(R.string.mensaje_error_intentear_mas_tarde), Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {

                val body = response.body()!!.string()

                if(body != null && body.isNotEmpty())
                {
                    try
                    {
                        val gson = GsonBuilder().create()
                        val respuesta = gson.fromJson(body, Respuesta::class.java)

                        if(respuesta.status == 0)
                        {
                            runOnUiThread {
                                listaArticulosVenta.clear()
                                mViewVenta.notifyDataSetChanged()
                                actualizarCantidadPrecio()
                                Toast.makeText(context, getString(R.string.mensaje_venta_exitosa), Toast.LENGTH_SHORT).show()
                            }
                        }
                        else
                        {
                            val errores = Errores()
                            errores.procesarError(context,body,activity)
                        }

                    }
                    catch(e:Exception){}
                }
                progressDialog.dismiss()
            }
        })

    }

    override fun numeroArticulo(articuloCarrito : InventarioObjeto) {
        runOnUiThread{
            if(!comprobarArticuloEnVenta(articuloCarrito))
            {
                listaArticulosVenta.add(articuloCarrito)
            }

            mViewVenta.notifyDataSetChanged()
            actualizarCantidadPrecio()
        }
    }

    fun comprobarArticuloEnVenta(articuloTmp: InventarioObjeto): Boolean
    {
        for (i in 0 until listaArticulosVenta.size)
        {
            if(articuloTmp.idArticulo == listaArticulosVenta[i].idArticulo)
            {
                listaArticulosVenta[i].cantidad += articuloTmp.cantidad
                return true
            }
        }

        return false
    }

    override fun agregarArticulos(articulosCarrito: MutableList<InventarioObjeto>) {
        runOnUiThread{
            for(i in 0 until articulosCarrito.size)
            {
                if(!comprobarArticuloEnVenta(articulosCarrito[i]))
                {
                    listaArticulosVenta.add(articulosCarrito[i])
                }
            }

            //listaArticulosVenta.addAll(articulosCarrito)

            mViewVenta.notifyDataSetChanged()
            actualizarCantidadPrecio()
        }
    }

    override fun obtenerNumero(numero : Int, posicion : Int) {
        runOnUiThread{
            listaArticulosVenta[posicion].cantidad = numero
            mViewVenta.notifyDataSetChanged()
            actualizarCantidadPrecio()
        }
    }

    override fun finalizarVenta(cambio : Double) {
        runOnUiThread{
            subirVenta()
        }
    }

    override fun abrirCamara() {
        val intentIntegrator = IntentIntegrator(activity)
        intentIntegrator.setBeepEnabled(false)
        intentIntegrator.setCameraId(0)
        intentIntegrator.setPrompt("SCAN")
        intentIntegrator.setBarcodeImageEnabled(false)
        intentIntegrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int,resultCode: Int,data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                runOnUiThread {
                    dialogoAgregarArticulos.buscarArticulo(java.lang.Long.parseLong(result.contents))
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun listaArticulos(listaArticulos: ListInventoryObject) {
        dialogoAgregarArticulos.llenarListaArticulos(listaArticulos.Inventory.toMutableList())
    }

    override fun listaArticulosNoVendidos(listInventoryObject: ListInventoryNoSells) {}

    override fun listaArticulosMasVendidos(listaArticulos: ListEstadisticaArticuloObject) {    }

    override fun lanzarMensaje(mensaje: String) {
        runOnUiThread()
        {
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
        }
    }

    override fun procesarError(json: String) {
        val errores = Errores()
        errores.procesarError(this,json,this)
    }

    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }

    override fun cambiarPrecioCosto(precio: Double, costo: Double, posicion: Int) {
        runOnUiThread{
            listaArticulosVenta[posicion].precio = precio
            listaArticulosVenta[posicion].costo = costo
            mViewVenta.notifyDataSetChanged()
            actualizarCantidadPrecio()
        }
    }

}
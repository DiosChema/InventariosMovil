@file:Suppress("DEPRECATION")

package com.Aegina.PocketSale.Activities

import android.app.Activity
import android.app.ProgressDialog
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
import com.Aegina.PocketSale.Objets.*
import com.Aegina.PocketSale.R
import com.Aegina.PocketSale.RecyclerView.*
import com.google.gson.GsonBuilder
import com.google.zxing.integration.android.IntentIntegrator
import okhttp3.*
import java.io.IOException
import java.lang.Float.parseFloat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Venta : AppCompatActivity(), DialogAgregarArticulos.DialogAgregarArticulo,
    DialogAgregarNumero.DialogAgregarNumero, DialogFinalizarVenta.DialogFinalizarVenta {

    var context = this
    val urls: Urls = Urls()

    var listaArticulosVenta: MutableList<InventarioObjeto> = ArrayList()
    var adapter: AdapterListVenta? = null
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

        globalVariable = applicationContext as GlobalClass
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        dialogoAgregarArticulos.crearDialogInicial(context,globalVariable, Activity())
        dialogoAgregarArticulos.crearDialogArticulos()
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
                dialogoFinalizarVenta.crearDialog(context,parseFloat(ventaTotalVenta.text.toString().substring(1,ventaTotalVenta.text.length)))
        }

        actualizarCantidadPrecio()
    }

    fun actualizarCantidadPrecio(){

        var totalCantidad = 0
        var totalPrecio = 0.0

        for(articulos in listaArticulosVenta){
            totalCantidad += articulos.cantidadArticulo
            totalPrecio += (articulos.precioArticulo * articulos.cantidadArticulo)
        }

        ventaTotalArticulos.text = totalCantidad.toString()
        val textTmp = "$$totalPrecio"
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

        mRecyclerView.addOnItemTouchListener(RecyclerItemClickListener(context, mRecyclerView, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                dialogAgregarNumero.crearDialog(context, position)
            }

            override fun onLongItemClick(view: View?, position: Int) {}
        }))
    }

    fun subirVenta() {
        val url = urls.url+urls.endPointVentas.endPointVenta

        val articulos: MutableList<ArticuloObjeto> = ArrayList()

        for (articuloTmp in listaArticulosVenta) {

            val articulo = ArticuloObjeto(
                articuloTmp.idArticulo,
                articuloTmp.cantidadArticulo,
                articuloTmp.precioArticulo,
                articuloTmp.nombreArticulo,
                articuloTmp.costoArticulo)

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
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()

            }
            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    progressDialog.dismiss()
                    listaArticulosVenta.clear()
                    mViewVenta.notifyDataSetChanged()
                    actualizarCantidadPrecio()
                    Toast.makeText(context, getString(R.string.mensaje_venta_exitosa), Toast.LENGTH_SHORT).show()
                }

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
                listaArticulosVenta[i].cantidadArticulo += articuloTmp.cantidadArticulo
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
            listaArticulosVenta[posicion].cantidadArticulo = numero
            mViewVenta.notifyDataSetChanged()
            actualizarCantidadPrecio()
        }
    }

    override fun finalizarVenta(cambio : Float) {
        runOnUiThread{
            subirVenta()
        }
    }

    override fun abrirCamara() {
        val intentIntegrator = IntentIntegrator(context)
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

    override fun listaArticulos(listaArticulos: MutableList<ArticuloInventarioObjeto>) {
        dialogoAgregarArticulos.llenarListaArticulos(listaArticulos)
    }

}
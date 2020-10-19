@file:Suppress("DEPRECATION")

package com.Aegina.PocketSale.Activities

import android.app.Activity
import android.app.Dialog
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
import com.Aegina.PocketSale.Dialogs.DialogFecha
import com.Aegina.PocketSale.Objets.*
import com.Aegina.PocketSale.R
import com.Aegina.PocketSale.RecyclerView.*
import com.google.gson.GsonBuilder
import com.google.zxing.integration.android.IntentIntegrator
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Venta : AppCompatActivity(), DialogAgregarArticulos.ExampleDialogListener, DialogAgregarNumero.ExampleDialogListener{

    var context = this
    val urls: Urls = Urls()

    var listaArticulos: MutableList<InventarioObjeto> = ArrayList()

    var adapter: AdapterListVenta? = null

    var dialogoAgregarArticulos = DialogAgregarArticulos()

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

        dialogoAgregarArticulos.crearDialogArticulos(context,globalVariable, Activity())
        crearRecyclerView()

        asignarRecursos()

        /*
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels

        mRecyclerViewArticulos.layoutManager = GridLayoutManager(context,width/360)
        */
    }

    fun asignarRecursos(){

        ventaTotalArticulos = findViewById(R.id.ventaTotalArticulos)
        ventaTotalVenta = findViewById(R.id.ventaTotalVenta)

        val ButtonNuevoArticulo = findViewById<ImageButton>(R.id.ventaNuevoArticulo)
        ButtonNuevoArticulo.setOnClickListener {
            dialogoAgregarArticulos.showDialogAgregarArticulo()
        }

        val ButtonTerminarVenta = findViewById<ImageButton>(R.id.ventaTerminarVenta)
        ButtonTerminarVenta.setOnClickListener {
            subirVenta()
        }

        actualizarCantidadPrecio()
    }

    fun actualizarCantidadPrecio(){

        var totalCantidad = 0
        var totalPrecio = 0.0

        for(articulos in listaArticulos){
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
        mViewVenta.RecyclerAdapter(listaArticulos, context)
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
        val url = urls.url+urls.endPointVenta

        val articulos: MutableList<ArticuloObjeto> = ArrayList()

        for (articuloTmp in listaArticulos) {

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

        val venta = VentaObjeto(globalVariable.token.toString(), currentDate.toString(), articulos)

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
                    listaArticulos.clear()
                    mViewVenta.notifyDataSetChanged()
                    actualizarCantidadPrecio()
                    Toast.makeText(context, getString(R.string.mensaje_venta_exitosa), Toast.LENGTH_SHORT).show()
                }

            }
        })

    }

    override fun applyTexts(articulo : InventarioObjeto) {
        runOnUiThread{
            listaArticulos.add(articulo)
            mViewVenta.notifyDataSetChanged()
            actualizarCantidadPrecio()
        }
    }

    override fun obtenerNumero(numero : Int, posicion : Int) {
        runOnUiThread{
            listaArticulos[posicion].cantidadArticulo = numero
            mViewVenta.notifyDataSetChanged()
            actualizarCantidadPrecio()
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
                    dialogoAgregarArticulos.dialogAgregarArticuloCodigo.setText(java.lang.Long.parseLong(result.contents).toString())
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}
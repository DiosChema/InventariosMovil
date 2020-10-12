@file:Suppress("DEPRECATION")

package com.Aegina.PocketSale.Activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Dialogs.DialogAgregarArticulos
import com.Aegina.PocketSale.Dialogs.DialogAgregarNumero
import com.Aegina.PocketSale.Objets.ActualizarInventarioObject
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.InventarioObjeto
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.Aegina.PocketSale.RecyclerView.RecyclerItemClickListener
import com.Aegina.PocketSale.RecyclerView.RecyclerViewSurtido
import com.google.gson.GsonBuilder
import com.google.zxing.integration.android.IntentIntegrator
import okhttp3.*
import java.io.IOException

class Surtido : AppCompatActivity() , DialogAgregarArticulos.ExampleDialogListener, DialogAgregarNumero.ExampleDialogListener{
    lateinit var mViewEstadisticaArticulo : RecyclerViewSurtido
    lateinit var mRecyclerView : RecyclerView

    lateinit var globalVariable: GlobalClass
    lateinit var context :Context
    var listaArticulos: MutableList<InventarioObjeto> = ArrayList()
    val urls: Urls = Urls()

    var dialogoAgregarArticulos = DialogAgregarArticulos()

    lateinit var surtidoTotalArticulos : TextView
    lateinit var surtidoTotalVenta : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surtido)

        context = this
        globalVariable = applicationContext as GlobalClass
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        crearRecyclerView()

        dialogoAgregarArticulos.crearDialogArticulos(context,globalVariable, Activity())

        asignarRecursos()
    }

    fun asignarRecursos(){

        surtidoTotalArticulos = findViewById(R.id.surtidoTotalArticulos)
        surtidoTotalVenta = findViewById(R.id.surtidoTotalVenta)

        val surtidoAgregarCodigo = findViewById<ImageButton>(R.id.surtidoAgregarArticulo)
        surtidoAgregarCodigo.setOnClickListener{
            dialogoAgregarArticulos.showDialogAgregarArticulo()
        }

        val surtidoConfirmarArticulos = findViewById<ImageButton>(R.id.surtidoConfirmarArticulos)
        surtidoConfirmarArticulos.setOnClickListener{
            actualizarInventario()
        }

        actualizarCantidadCosto()
    }

    fun actualizarCantidadCosto(){
        var totalCantidad = 0
        var totalCosto = 0.0


        for(articulos in listaArticulos){
            totalCantidad += articulos.cantidadArticulo
            totalCosto += (articulos.costoArticulo * articulos.cantidadArticulo)
        }

        surtidoTotalArticulos.text = totalCantidad.toString()

        val textoSurtidoVenta = "$$totalCosto"
        surtidoTotalVenta.text = textoSurtidoVenta
    }

    fun crearRecyclerView(){
        mViewEstadisticaArticulo = RecyclerViewSurtido()
        mRecyclerView = findViewById(R.id.surtidoListView)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mViewEstadisticaArticulo.RecyclerAdapter(listaArticulos, context)
        mRecyclerView.adapter = mViewEstadisticaArticulo

        val dialogAgregarNumero = DialogAgregarNumero()

        mRecyclerView.addOnItemTouchListener(RecyclerItemClickListener(context, mRecyclerView, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                dialogAgregarNumero.crearDialog(context, position)
                //eventoClick(position)
            }

            override fun onLongItemClick(view: View?, position: Int) {}
        }))
    }

    fun actualizarInventario(){
        val url = urls.url+urls.endPointActualizarInventario

        val listaArticulosTmp : MutableList<InventarioObjeto> = ArrayList()

        for (articuloTmp in listaArticulos) {
            if(articuloTmp.cantidadArticulo > 0)
                listaArticulosTmp.add(articuloTmp)
        }

        val venta = ActualizarInventarioObject(globalVariable.token.toString(), listaArticulosTmp)

        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val jsonVenta: String = gsonPretty.toJson(venta)

        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonVenta)

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.mensaje_espera))
        progressDialog.show()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response)
            {
                progressDialog.dismiss()
                runOnUiThread { finish() }
            }
        })
    }

    override fun applyTexts(articulo : InventarioObjeto) {
        runOnUiThread{
            listaArticulos.add(articulo)
            mViewEstadisticaArticulo.notifyDataSetChanged()
            actualizarCantidadCosto()
        }
    }

    override fun obtenerNumero(numero : Int, posicion : Int) {
        runOnUiThread{
            listaArticulos[posicion].cantidadArticulo = numero
            mViewEstadisticaArticulo.notifyDataSetChanged()
            actualizarCantidadCosto()
        }
    }

    override fun abrirCamara() {
        val intentIntegrator = IntentIntegrator(context as Surtido)
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
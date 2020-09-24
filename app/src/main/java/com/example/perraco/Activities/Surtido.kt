package com.example.perraco.Activities

import android.app.AlertDialog
import android.app.Dialog
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
import com.example.perraco.Objets.*
import com.example.perraco.R
import com.example.perraco.RecyclerView.AdapterListSurtido
import com.example.perraco.RecyclerView.RecyclerItemClickListener
import com.example.perraco.RecyclerView.RecyclerViewEstadisticaArticulo
import com.example.perraco.RecyclerView.RecyclerViewSurtido
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.lang.Integer.parseInt


class Surtido : AppCompatActivity() {
    lateinit var mViewEstadisticaArticulo : RecyclerViewSurtido
    lateinit var mRecyclerView : RecyclerView

    lateinit var globalVariable: GlobalClass
    lateinit var context :Context
    var listaArticulos: MutableList<InventarioObjeto> = ArrayList()
    val urls: Urls = Urls()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surtido)

        context = this
        globalVariable = applicationContext as GlobalClass
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        crearRecyclerView()

        var surtidoAgregarCodigo = findViewById<Button>(R.id.surtidoAgregarCodigo)
        surtidoAgregarCodigo.setOnClickListener{ showDialogAgregarArticulo()}

        var surtidoConfirmarArticulos = findViewById<Button>(R.id.surtidoConfirmarArticulos)
        surtidoConfirmarArticulos.setOnClickListener{actualizarInventario()}

    }

    fun crearRecyclerView(){
        mViewEstadisticaArticulo = RecyclerViewSurtido()
        mRecyclerView = findViewById(R.id.surtidoListView)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mViewEstadisticaArticulo.RecyclerAdapter(listaArticulos, context)
        mRecyclerView.adapter = mViewEstadisticaArticulo

        mRecyclerView.addOnItemTouchListener(RecyclerItemClickListener(context, mRecyclerView, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                eventoClick(position)
            }

            override fun onLongItemClick(view: View?, position: Int) {}
        }))
    }

    private fun showDialogAgregarArticulo() {
        val dialog = Dialog(this)

        dialog.setTitle(title)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_agregar_articulo)
        val dialogAgregarArticuloCodigo = dialog.findViewById(R.id.dialogAgregarArticuloCodigo) as EditText
        val dialogAgregarArticuloCantidad = dialog.findViewById(R.id.dialogAgregarArticuloCantidad) as EditText
        val dialogAgregarArticuloAceptar = dialog.findViewById(R.id.dialogAgregarArticuloAceptar) as Button
        val dialogAgregarArticuloCancelar = dialog.findViewById(R.id.dialogAgregarArticuloCancelar) as Button
        val dialogAgregarArticuloTitulo = dialog.findViewById(R.id.dialogAgregarArticuloTitulo) as TextView

        dialogAgregarArticuloTitulo.text = getString(R.string.surtido_texto_titulo)

        dialogAgregarArticuloAceptar.setOnClickListener {
            dialog.dismiss()
            agregarArticulo(parseInt(dialogAgregarArticuloCodigo.text.toString()),parseInt(dialogAgregarArticuloCantidad.text.toString()))
        }

        dialogAgregarArticuloCancelar.setOnClickListener {dialog.dismiss()}
        dialog.show()

    }

    fun agregarArticulo(idArticulo : Int, cantidad : Int){
        val url = urls.url+urls.endPointArticulo+"?token="+globalVariable.token+"&idArticulo="+idArticulo

        val request = Request.Builder()
            .url(url)
            .get()
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

                val body = response.body()?.string()
                val gson = GsonBuilder().create()
                val model = gson.fromJson(body, InventarioObjeto::class.java)

                model.cantidadArticulo = cantidad
                runOnUiThread {
                    listaArticulos.add(model)
                    mViewEstadisticaArticulo.notifyDataSetChanged()
                    //adapter?.notifyDataSetChanged()
                }
            }
        })
    }

    fun eventoClick(posicion : Int){
        val dialog: AlertDialog = AlertDialog.Builder(context).create()
        val inflater = layoutInflater
        val alertDialogView: View = inflater.inflate(R.layout.dialog_numero, null)
        dialog.setView(alertDialogView)
        val dialogNumeroTitulo = alertDialogView.findViewById<View>(R.id.dialogNumeroTitulo) as TextView
        val dialogNumeroText = alertDialogView.findViewById<View>(R.id.dialogNumeroText) as EditText
        val dialogNumeroAceptar = alertDialogView.findViewById<View>(R.id.dialogNumeroAceptar) as Button
        val dialogNumeroCancelar = alertDialogView.findViewById<View>(R.id.dialogNumeroCancelar) as Button

        dialogNumeroTitulo.text = getString(R.string.dialog_numero_text_cantidad)

        dialogNumeroAceptar.setOnClickListener {
            if(dialogNumeroText.length() > 0) {
                listaArticulos[posicion].cantidadArticulo = parseInt(dialogNumeroText.text.toString())
                mViewEstadisticaArticulo.notifyDataSetChanged()
                dialog.dismiss()
            }
        }

        dialogNumeroCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun actualizarInventario(){
        val url = urls.url+urls.endPointActualizarInventario

        val venta = ActualizarInventarioObject(globalVariable.token.toString(), listaArticulos)

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

}
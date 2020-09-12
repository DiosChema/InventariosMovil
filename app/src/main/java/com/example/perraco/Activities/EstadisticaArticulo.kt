package com.example.perraco.Activities

import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.perraco.Objets.EstadisticaArticuloObject
import com.example.perraco.Objets.GlobalClass
import com.example.perraco.Objets.Urls
import com.example.perraco.R
import com.example.perraco.RecyclerView.RecyclerViewEstadisticaArticulo
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EstadisticaArticulo : AppCompatActivity() {

    var listaTmp:MutableList<EstadisticaArticuloObject> = ArrayList()
    lateinit var context : Context;
    lateinit var mViewEstadisticaArticulo : RecyclerViewEstadisticaArticulo
    lateinit var mRecyclerView : RecyclerView
    lateinit var globalVariable: GlobalClass

    val formatoFecha = SimpleDateFormat("MM-dd-yyyy")
    val formatoFechaCompleta = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    lateinit var fechaInicial : Date
    lateinit var fechaFinal : Date
    lateinit var fechaInicialButton : TextView
    lateinit var fechaFinalButton : TextView

    val urls = Urls()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadistica_articulo)
        context = this
        globalVariable = context.applicationContext as GlobalClass

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23);// for 6 hour
        calendar.set(Calendar.MINUTE, 59);// for 0 min
        calendar.set(Calendar.SECOND, 59);// for 0 sec
        fechaFinal = calendar.time

        calendar.set(Calendar.HOUR_OF_DAY, 0);// for 6 hour
        calendar.set(Calendar.MINUTE, 0);// for 0 min
        calendar.set(Calendar.SECOND, 0);// for 0 sec

        fechaInicial = calendar.time

        mViewEstadisticaArticulo = RecyclerViewEstadisticaArticulo()
        mRecyclerView = findViewById(R.id.estadisticaArticuloRecyclerView)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mViewEstadisticaArticulo.RecyclerAdapter(listaTmp, context)
        mRecyclerView.adapter = mViewEstadisticaArticulo

        obtenerArticulos()
    }

    fun obtenerArticulos(){
        val url = urls.url+urls.endPointArticulosMasVendidos+
                "?token="+globalVariable.token +
                "&fechaInicial=" + formatoFechaCompleta.format(fechaInicial) +
                "&fechaFinal="+formatoFechaCompleta.format(fechaFinal)

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()

        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage(getString(R.string.mensaje_espera))
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response)
            {
                var body = response.body()?.string()

                if(body != null && body.isNotEmpty()) {
                    val gson = GsonBuilder().create()
                    var Model = gson.fromJson(body, Array<EstadisticaArticuloObject>::class.java).toList()

                    runOnUiThread {
                        mViewEstadisticaArticulo.RecyclerAdapter(Model.toMutableList(), context)
                        mViewEstadisticaArticulo.notifyDataSetChanged()
                    }
                }

                progressDialog.dismiss()

            }
        })

    }
}
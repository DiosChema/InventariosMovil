package com.example.perraco.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.perraco.Objets.ArticuloObjeto
import com.example.perraco.Objets.InventarioObjeto
import com.example.perraco.Objets.Urls
import com.example.perraco.Objets.VentaObjeto
import com.example.perraco.R
import com.example.perraco.RecyclerView.RecyclerViewVenta
import com.google.gson.GsonBuilder
import com.google.zxing.integration.android.IntentIntegrator
import okhttp3.*
import java.io.IOException
import java.lang.Double.parseDouble
import java.lang.Integer.parseInt
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Venta : AppCompatActivity() {

    var context = this
    var listaTmp:MutableList<InventarioObjeto> = ArrayList()
    val urls: Urls =
        Urls()
    lateinit var nombre : TextView
    lateinit var mViewVenta : RecyclerViewVenta
    lateinit var mRecyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venta)

        mViewVenta = RecyclerViewVenta()
        mRecyclerView = findViewById(R.id.rvVenta) as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mViewVenta.RecyclerAdapter(listaTmp, context)
        mRecyclerView.adapter = mViewVenta

        nombre = findViewById(R.id.VentaCodigoAgregar) as TextView

        val ButtonNuevoArticulo = findViewById<Button>(R.id.VentaNuevoArticulo)
        ButtonNuevoArticulo?.setOnClickListener() {
            getArticuloObjecto(nombre.text.toString())
        }

        val ButtonTerminarVenta = findViewById<Button>(R.id.VentaTerminarVenta)
        ButtonTerminarVenta?.setOnClickListener() {
            subirFactura()
        }

        val ButtonObtenerCodigoBarras = findViewById<Button>(R.id.VentaObtenerCodigo)
        ButtonObtenerCodigoBarras?.setOnClickListener() {
            val intentIntegrator = IntentIntegrator(this)
            intentIntegrator.setBeepEnabled(false)
            intentIntegrator.setCameraId(0)
            intentIntegrator.setPrompt("SCAN")
            intentIntegrator.setBarcodeImageEnabled(false)
            intentIntegrator.initiateScan()
        }

    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                runOnUiThread {
                    getArticuloObjecto(result.contents)
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    fun subirFactura() {

        actulizarExistencia()
        val url = urls.url+urls.endPointVenta

        val articulos: MutableList<ArticuloObjeto> = ArrayList()

        for (i in 0..mViewVenta.itemCount - 1) {
            val view: View = mRecyclerView.getChildAt(i)
            val textViewCantidad = view.findViewById(R.id.VentaCantidad) as EditText
            val textCantidad = textViewCantidad.text.toString()

            val textViewPrecio = view.findViewById(R.id.VentaPrecio) as TextView
            val textPrecio = textViewPrecio.text.toString()

            val textViewId = view.findViewById(R.id.VentaIdArticulo) as TextView
            val textId = textViewId.text.toString()

            val textViewNombre = view.findViewById(R.id.VentaNombre) as TextView
            val textNombre = textViewNombre.text.toString()


            val articulo = ArticuloObjeto(
                textId,
                parseInt(textCantidad),
                parseDouble(textPrecio),
                textNombre
            )

            if(articulo.cantidad > 0)
                articulos.add(articulo)

        }

        if(articulos.size == 0) {
            Toast.makeText(this, getString(R.string.mensaje_venta_sin_articulos), Toast.LENGTH_SHORT).show()
            return
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currentDate = sdf.format(Date())

        val venta = VentaObjeto("00001", currentDate.toString(), articulos)

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
                    listaTmp.clear()
                    mViewVenta.notifyDataSetChanged()
                    Toast.makeText(context, getString(R.string.mensaje_venta_exitosa), Toast.LENGTH_SHORT).show()
                }

            }
        })

    }

    fun obtenerArticulo(idArticulo: String) : InventarioObjeto?
    {
        val articuloTmp : InventarioObjeto? = null

        for(i in 0..listaTmp.size -1) {
            if(listaTmp[i].idArticulo == idArticulo) {
                return listaTmp[i]
            }
        }

        return articuloTmp

    }

    fun getArticuloObjecto(idArticulo: String){

        val url = urls.url+urls.endPointArticulo+"?tienda=00001&idArticulo="+idArticulo

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()

        actulizarExistencia()
        val elemento = obtenerArticulo(idArticulo)

        if(elemento == null || elemento.cantidadArticulo == 0) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage(getString(R.string.mensaje_espera))
            progressDialog.show()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    progressDialog.dismiss()
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body()?.string()
                    if (body != null && body.isNotEmpty()) {
                        val gson = GsonBuilder().create()

                        val articulo = gson.fromJson(
                            body,
                            InventarioObjeto::class.java
                        )
                        articulo.cantidadArticulo = 1

                        runOnUiThread {
                            actulizarExistencia()
                            listaTmp.add(articulo)
                            mViewVenta.notifyDataSetChanged()
                            nombre.text = ""
                        }
                    }

                    progressDialog.dismiss()

                }
            })
        }
        else
        {
            for(i in 0..listaTmp.size -1) {
                if(listaTmp[i].idArticulo == idArticulo) {
                    listaTmp[i].cantidadArticulo + 1
                    runOnUiThread {
                        mViewVenta.notifyDataSetChanged()
                    }
                }
            }
        }

    }

    fun actulizarExistencia() {
        if(mViewVenta.itemCount > 0)
        {
            for (i in 0..mViewVenta.itemCount - 1) {
                val view: View = mRecyclerView.getChildAt(i)
                val textViewCantidad = view.findViewById(R.id.VentaCantidad) as EditText
                val textCantidad = textViewCantidad.text.toString()

                listaTmp[i].cantidadArticulo = parseInt(textCantidad)
            }
        }

    }

}
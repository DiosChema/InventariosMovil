package com.example.perraco.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.gson.Gson
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

        context = this;
        mViewVenta = RecyclerViewVenta()
        mRecyclerView = findViewById(R.id.rvVenta) as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mViewVenta.RecyclerAdapter(listaTmp, context)
        mRecyclerView.adapter = mViewVenta

        nombre = findViewById(R.id.VentaCodigoAgregar) as TextView

        val button = findViewById<Button>(R.id.VentaNuevoArticulo)
        button?.setOnClickListener()
        {
            getArticuloObjecto(nombre.text.toString())
        }

        val button2 = findViewById<Button>(R.id.VentaTerminarVenta)
        button2?.setOnClickListener()
        {
            subirFactura()

        }

        val button3 = findViewById<Button>(R.id.VentaObtenerCodigo)
        button3?.setOnClickListener()
        {
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
            if (result.contents == null) {
                Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("MainActivity", "Scanned")
                Toast.makeText(this, "Scanned -> " + result.contents, Toast.LENGTH_SHORT)
                    .show()

                //nombre.text = result.contents
                //getArticuloObjecto(result.contents)
                runOnUiThread {
                    getArticuloObjecto(result.contents)
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    fun subirFactura() {

        val url = urls.url+urls.endPointVenta

        val urls: Urls =
            Urls()
        val articulos: MutableList<ArticuloObjeto> = ArrayList()

        for (i in 0..mViewVenta.itemCount - 1)
        {
            val view: View = mRecyclerView.getChildAt(i)
            val textViewCantidad = view.findViewById(R.id.VentaCantidad) as EditText
            val textCantidad = textViewCantidad.text.toString()

            val textViewPrecio = view.findViewById(R.id.VentaPrecio) as TextView
            val textPrecio = textViewPrecio.text.toString()

            val textViewId = view.findViewById(R.id.VentaIdArticulo) as TextView
            val textId = textViewId.text.toString()

            val textViewNombre = view.findViewById(R.id.VentaNombre) as TextView
            val textNombre = textViewNombre.text.toString()


            var articulo = ArticuloObjeto(
                textId,
                parseInt(textCantidad),
                parseDouble(textPrecio),
                textNombre
            )
            articulos.add(articulo)
        }

        if(articulos.size == 0)
        {
            Toast.makeText(this, "No hay articulos", Toast.LENGTH_SHORT).show()
            return
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currentDate = sdf.format(Date())

        //val currentTime: Date = Calendar.getInstance().time

        val venta: VentaObjeto =
            VentaObjeto(
                "00001",
                currentDate.toString(),
                articulos
            )

        val gson = Gson()
        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        //val jsonTut: String = gson.toJson(venta)
        val jsonVenta: String = gsonPretty.toJson(venta)

        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonVenta)


        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val client = OkHttpClient()
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Application is loading, please wait")
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response)
            {
                runOnUiThread {
                    progressDialog.dismiss()
                }

            }
        })



    }
    fun getArticuloObjecto(idArticulo: String){

        val url = urls.url+urls.endPointArticulo+"?tienda=00001&idArticulo="+idArticulo

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Application is loading, please wait")
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response)
            {
                var body = response.body()?.string()
                if(body != null && body.isNotEmpty())
                {
                    val gson = GsonBuilder().create()

                    var articulo = gson.fromJson(body,
                        InventarioObjeto::class.java)
                    articulo.cantidadArticulo = 1

                    listaTmp.add(articulo)

                    runOnUiThread {
                        actulizarExistencia()
                        mViewVenta.notifyDataSetChanged()
                        nombre.text = ""
                    }
                }

                progressDialog.dismiss()

            }
        })

    }

    fun actulizarExistencia() {
        if(mViewVenta.itemCount > 1)
        {
            for (i in 0..mViewVenta.itemCount - 2) {
                val view: View = mRecyclerView.getChildAt(i)
                val textViewCantidad = view.findViewById(R.id.VentaCantidad) as EditText
                val textCantidad = textViewCantidad.text.toString()

                val textViewId = view.findViewById(R.id.VentaIdArticulo) as TextView
                val textId = textViewId.text.toString()

                for (i in 0..listaTmp.size - 1)
                {
                    if(listaTmp[i].idArticulo == textId){
                        listaTmp[i].cantidadArticulo = parseInt(textCantidad)
                        break
                    }
                }
            }
        }



    }

}
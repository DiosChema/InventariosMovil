package com.example.perraco.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.perraco.Objets.*
import com.example.perraco.R
import com.example.perraco.RecyclerView.AdapterListVenta
import com.example.perraco.RecyclerView.RecyclerViewVenta
import com.google.gson.GsonBuilder
import com.google.zxing.integration.android.IntentIntegrator
import okhttp3.*
import java.io.IOException
import java.lang.Double.parseDouble
import java.lang.Integer.parseInt
import java.lang.Long.parseLong
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
    lateinit var mRecyclerView : ListView
    lateinit var arrayAdapter: ArrayAdapter<*>
    var listView: ListView? = null
    var adapter: AdapterListVenta? = null

    lateinit var globalVariable: GlobalClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venta)

        globalVariable = applicationContext as GlobalClass

        //mViewVenta = RecyclerViewVenta()
        //mRecyclerView = findViewById<ListView>(R.id.rvVenta)

        listView = findViewById(R.id.rvVenta) as ListView
        adapter = AdapterListVenta(this, listaTmp)
        (listView as ListView).adapter = adapter

        /*mRecyclerView.layoutManager = LinearLayoutManager(this)
        mViewVenta.RecyclerAdapter(listaTmp, context)
        mRecyclerView.adapter = mViewVenta*/

        nombre = findViewById(R.id.VentaCodigoAgregar)

        val ButtonNuevoArticulo = findViewById<Button>(R.id.VentaNuevoArticulo)
        ButtonNuevoArticulo?.setOnClickListener {
            getArticuloObjecto(parseLong(nombre.text.toString()))
        }

        val ButtonTerminarVenta = findViewById<Button>(R.id.VentaTerminarVenta)
        ButtonTerminarVenta?.setOnClickListener {
            subirFactura()
        }

        val ButtonObtenerCodigoBarras = findViewById<Button>(R.id.VentaObtenerCodigo)
        ButtonObtenerCodigoBarras?.setOnClickListener {
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
                    getArticuloObjecto(parseLong(result.contents))
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

        //for (i in 0..mViewVenta.itemCount - 1) {
        for (i in 0..adapter?.count!! - 1) {
            /*val view: View = mRecyclerView.getChildAt(i)
            val textViewCantidad = view.findViewById(R.id.VentaCantidad) as EditText
            val textCantidad = textViewCantidad.text.toString()

            val textViewPrecio = view.findViewById(R.id.VentaPrecio) as TextView
            val textPrecio = textViewPrecio.text.toString()

            val textViewId = view.findViewById(R.id.VentaIdArticulo) as TextView
            val textId = textViewId.text.toString()

            val textViewNombre = view.findViewById(R.id.VentaNombre) as TextView
            val textNombre = textViewNombre.text.toString()

            val textViewVentaCostoArticulo = view.findViewById(R.id.VentaCostoArticulo) as TextView
            val textVentaCostoArticulo = textViewVentaCostoArticulo.text.toString()


            val articulo = ArticuloObjeto(
                parseLong(textId),
                parseInt(textCantidad),
                parseDouble(textPrecio),
                textNombre,
                parseDouble(textVentaCostoArticulo)
            )*/

            val objeto  = adapter?.obtenerObjeto(i)
            if (objeto != null) {
                val articulo = ArticuloObjeto(
                    objeto.idArticulo,
                    objeto.cantidadArticulo,
                    objeto.precioArticulo,
                    objeto.nombreArticulo,
                    objeto.costoArticulo)

                if(articulo.cantidad > 0)
                    articulos.add(articulo)
            }
        }

        if(articulos.size == 0) {
            Toast.makeText(this, getString(R.string.mensaje_venta_sin_articulos), Toast.LENGTH_SHORT).show()
            return
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
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
                    listaTmp.clear()
                    adapter?.notifyDataSetChanged()
                    Toast.makeText(context, getString(R.string.mensaje_venta_exitosa), Toast.LENGTH_SHORT).show()
                }

            }
        })

    }

    fun obtenerArticulo(idArticulo: Long) : InventarioObjeto?
    {
        val articuloTmp : InventarioObjeto? = null

        for(i in 0..listaTmp.size -1) {
            if(listaTmp[i].idArticulo == idArticulo) {
                return listaTmp[i]
            }
        }

        return articuloTmp

    }

    fun getArticuloObjecto(idArticulo: Long){

        val url = urls.url+urls.endPointArticulo+"?token="+globalVariable.token+"&idArticulo="+idArticulo

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
                            adapter?.notifyDataSetChanged()
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
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
        }

    }

    fun actulizarExistencia() {
        if(adapter?.count!! > 0)
        {
            for (i in 0 until adapter?.count!!) {

                val objeto  = adapter?.obtenerObjeto(i)
                if (objeto != null) {
                    listaTmp[i].cantidadArticulo = objeto.cantidadArticulo
                }

                /*mRecyclerView.viewTreeObserver
                    .addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            val textViewCantidad: EditText = mRecyclerView.getChildAt(i)
                                .findViewById(R.id.VentaCantidad) as EditText
                            val textCantidad = textViewCantidad.text.toString()
                            listaTmp[i].cantidadArticulo = parseInt(textCantidad)
                        }
                    })*/

                /*mRecyclerView.post(Runnable {
                    val viewItem: View? = mRecyclerView.getLayoutManager()!!.findViewByPosition(i)
                    val textViewCantidad = viewItem?.findViewById<View>(R.id.VentaCantidad) as EditText

                    val textCantidad = textViewCantidad.text.toString()

                    listaTmp[i].cantidadArticulo = parseInt(textCantidad)
                })*/

                /*val holder = mRecyclerView.findViewHolderForAdapterPosition(i)
                if (null == holder) {
                    holder.itemView.findViewById<View>(R.id.VentaCantidad).visibility = View.VISIBLE
                }*/

                /*val title = (mRecyclerView.findViewHolderForAdapterPosition(i)?.itemView?.findViewById(
                    R.id.VentaCantidad) as EditText).text
                        .toString()*/
                //mRecyclerView.adapter.

                /*val v = mRecyclerView.getLayoutManager()?.findViewByPosition(i)
                if (v != null) {
                    v.visibility = View.INVISIBLE
                }
                val view: View = mRecyclerView.getChildAt(i)
                val textViewCantidad = view.findViewById(R.id.VentaCantidad) as EditText
                val textCantidad = textViewCantidad.text.toString()
                listaTmp[i].cantidadArticulo = parseInt(textCantidad)*/

                //listaTmp[i].cantidadArticulo = parseInt(title)
            }

            /*for (i in 0..mViewVenta.itemCount - 1) {
                val v = mRecyclerView.getLayoutManager()?.findViewByPosition(i)
                if (v != null) {
                    v.visibility = View.VISIBLE
                }
            }*/
        }

    }

}
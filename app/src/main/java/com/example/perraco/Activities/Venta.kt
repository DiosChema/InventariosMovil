package com.example.perraco.Activities

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.perraco.Objets.*
import com.example.perraco.R
import com.example.perraco.RecyclerView.AdapterListVenta
import com.example.perraco.RecyclerView.RecyclerItemClickListener
import com.example.perraco.RecyclerView.RecyclerViewArticulos
import com.google.gson.GsonBuilder
import com.google.zxing.integration.android.IntentIntegrator
import okhttp3.*
import java.io.IOException
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

    var listaTmpArticulos:MutableList<InventarioObjeto> = ArrayList()

    lateinit var arrayAdapter: ArrayAdapter<*>
    var listView: ListView? = null
    var adapter: AdapterListVenta? = null

    lateinit var mViewArticulos : RecyclerViewArticulos
    lateinit var mRecyclerViewArticulos : RecyclerView

    var listaFamilia:MutableList<String> = ArrayList()
    var listaFamiliaCompleta:MutableList<FamiliaObjeto> = ArrayList()
    var listaSubFamilia:MutableList<String> = ArrayList()
    var listaSubFamiliaCompleta:MutableList<SubFamiliaObjeto> = ArrayList()



    lateinit var dialog : Dialog

    lateinit var dialogArticulosFamiliaSpinner: Spinner
    lateinit var dialogArticulosSubFamiliaSpinner: Spinner
    lateinit var dialogArticulosFamiliaSpinnerButton: ImageButton
    lateinit var dialogArticulosSubFamiliaSpinnerButton: ImageButton
    lateinit var dialogArticulosSalir: ImageButton
    var subFamiliaId = -1


    lateinit var globalVariable: GlobalClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venta)

        globalVariable = applicationContext as GlobalClass
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //mViewVenta = RecyclerViewVenta()
        //mRecyclerView = findViewById<ListView>(R.id.rvVenta)

        listView = findViewById(R.id.rvVenta) as ListView
        adapter = AdapterListVenta(this, listaTmp)
        (listView as ListView).adapter = adapter



        dialog = Dialog(this)
        dialog.setTitle(title)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_articulos)

        dialogArticulosFamiliaSpinner = dialog.findViewById(R.id.dialogArticulosFamiliaSpinner) as Spinner
        dialogArticulosSubFamiliaSpinner = dialog.findViewById(R.id.dialogArticulosSubFamiliaSpinner) as Spinner
        dialogArticulosFamiliaSpinnerButton = dialog.findViewById(R.id.dialogArticulosFamiliaSpinnerButton) as ImageButton
        dialogArticulosSubFamiliaSpinnerButton = dialog.findViewById(R.id.dialogArticulosSubFamiliaSpinnerButton) as ImageButton
        dialogArticulosSalir = dialog.findViewById(R.id.dialogArticulosSalir) as ImageButton
        mRecyclerViewArticulos = dialog.findViewById(R.id.dialogoArticulosRecyclerView) as RecyclerView

        dialogArticulosFamiliaSpinnerButton.setOnClickListener{
            obtenerArticulosFamilia()
        }

        dialogArticulosSubFamiliaSpinnerButton.setOnClickListener{
            obtenerArticulosSubFamilia()
        }

        mViewArticulos = RecyclerViewArticulos()

        //mRecyclerViewArticulos = dialogoArticulosRecyclerView as RecyclerView

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels

        mRecyclerViewArticulos.layoutManager = GridLayoutManager(context,width/360)

        /*mRecyclerView.layoutManager = LinearLayoutManager(this)
        mViewVenta.RecyclerAdapter(listaTmp, context)
        mRecyclerView.adapter = mViewVenta*/

        nombre = findViewById(R.id.VentaCodigoAgregar)


        val ButtonNuevoArticulo = findViewById<ImageButton>(R.id.VentaNuevoArticulo)
        ButtonNuevoArticulo?.setOnClickListener {
            if(nombre.text.isNotEmpty())
                getArticuloObjecto(parseLong(nombre.text.toString()))
        }

        val ventaObtenerArticulo = findViewById<ImageButton>(R.id.ventaObtenerArticulo)
        ventaObtenerArticulo?.setOnClickListener {
            showDialogArticulos()
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

    private fun showDialogArticulos() {
        mViewArticulos.RecyclerAdapter(listaTmpArticulos, context)
        mRecyclerViewArticulos.adapter = mViewArticulos

        if(listaFamiliaCompleta.size == 0)
            obtenerFamilias(context)

        mRecyclerViewArticulos.addOnItemTouchListener(RecyclerItemClickListener(context, mRecyclerViewArticulos, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                getArticuloObjecto(listaTmpArticulos[position].idArticulo)
                dialog.dismiss()
            }

            override fun onLongItemClick(view: View?, position: Int) {
                // do whatever
            }
        }))
        /*dialogAceptar.setOnClickListener {
            dialog.dismiss()
            agregarFamilia(this,dialogText.text.toString())
        }

        dialogCancelar.setOnClickListener {
            dialog.dismiss()
        }*/

        dialog.show()

    }

    fun obtenerArticulosFamilia(){
        val url = urls.url+urls.endPointArticulosPorFamilia+"?familiaId=" + listaFamiliaCompleta[dialogArticulosFamiliaSpinner.selectedItemPosition].familiaId + "&token="+globalVariable.token

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }
            override fun onResponse(call: Call, response: Response)
            {
                val body = response.body()?.string()
                val gson = GsonBuilder().create()

                var Model = gson.fromJson(body, Array<InventarioObjeto>::class.java).toList()
                runOnUiThread {
                    listaTmpArticulos = Model.toMutableList()
                    mViewArticulos.RecyclerAdapter(listaTmpArticulos, context)
                    mViewArticulos.notifyDataSetChanged()
                }
            }
        })
    }

    fun obtenerArticulosSubFamilia(){
        val url = urls.url+urls.endPointArticulosPorSubFamilia+"?subFamiliaId=" + listaSubFamiliaCompleta[dialogArticulosSubFamiliaSpinner.selectedItemPosition].subFamiliaId + "&token="+globalVariable.token

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }
            override fun onResponse(call: Call, response: Response)
            {
                val body = response.body()?.string()
                val gson = GsonBuilder().create()

                var Model = gson.fromJson(body, Array<InventarioObjeto>::class.java).toList()
                runOnUiThread {
                    listaTmpArticulos = Model.toMutableList()
                    mViewArticulos.RecyclerAdapter(listaTmpArticulos, context)
                    mViewArticulos.notifyDataSetChanged()
                }
            }
        })
    }

    fun obtenerFamilias(context: Context){

        val url = urls.url+urls.endpointObtenerFamilias+"?token="+globalVariable.token

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }
            override fun onResponse(call: Call, response: Response)
            {
                val body = response.body()?.string()
                val gson = GsonBuilder().create()
                listaFamiliaCompleta = gson.fromJson(body,Array<FamiliaObjeto>::class.java).toMutableList()

                for (familias in listaFamiliaCompleta) {
                    listaFamilia.add(familias.nombreFamilia)
                }

                runOnUiThread {
                    val adapter = ArrayAdapter(context,
                        android.R.layout.simple_spinner_item, listaFamilia)
                    dialogArticulosFamiliaSpinner.adapter = adapter
                    dialogArticulosFamiliaSpinner.setSelection(0)

                    dialogArticulosFamiliaSpinner.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>,
                                                    view: View, position: Int, id: Long) {
                            obtenerSubFamilias(context,listaFamiliaCompleta[position].familiaId)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // write code to perform some action
                        }
                    }
                }
            }
        })

    }

    fun obtenerSubFamilias(context: Context, familiaId: Int){
        var subFamiliaTmp = 0
        val url = urls.url+urls.endpointObtenerSubFamilias+"?token="+globalVariable.token+"&familiaId="+familiaId

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.mensaje_espera))
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response)
            {
                listaSubFamilia.clear()
                val body = response.body()?.string()
                val gson = GsonBuilder().create()

                runOnUiThread {
                    var adapter = ArrayAdapter(context,
                        android.R.layout.simple_spinner_item, listaSubFamilia)
                    dialogArticulosSubFamiliaSpinner.adapter = adapter

                    listaSubFamiliaCompleta = gson.fromJson(body,Array<SubFamiliaObjeto>::class.java).toMutableList()

                    for (familias in listaSubFamiliaCompleta) {
                        listaSubFamilia.add(familias.nombreSubFamilia)
                    }

                    adapter = ArrayAdapter(context,
                        android.R.layout.simple_spinner_item, listaSubFamilia)

                    dialogArticulosSubFamiliaSpinner.adapter = adapter

                    if(listaSubFamilia.size > 0) {
                        dialogArticulosSubFamiliaSpinner.setSelection(subFamiliaTmp)
                    }

                    dialogArticulosSubFamiliaSpinner.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>,
                                                    view: View, position: Int, id: Long) {
                            if(listaSubFamilia.size > 0) {
                                subFamiliaId = listaSubFamiliaCompleta[position].subFamiliaId
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // write code to perform some action
                        }
                    }

                    progressDialog.dismiss()
                }
            }
        })

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
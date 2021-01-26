@file:Suppress("DEPRECATION")

package com.Aegina.PocketSale.Dialogs

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Metodos.Paginado
import com.Aegina.PocketSale.Objets.*
import com.Aegina.PocketSale.Objets.Inventory.ListEstadisticaArticuloObject
import com.Aegina.PocketSale.Objets.Inventory.ListInventoryNoSells
import com.Aegina.PocketSale.Objets.Inventory.ListInventoryObject
import com.Aegina.PocketSale.R
import com.Aegina.PocketSale.RecyclerView.*
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.lang.Double
import java.lang.Exception
import java.lang.Integer.parseInt
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DialogAgregarArticulos : AppCompatDialogFragment(){

    lateinit var contextTmp :Context
    val urls: Urls = Urls()

    lateinit var agregarArticulo: DialogAgregarArticulo
    lateinit var activityTmp: Activity
    lateinit var globalVariable: GlobalClass

    lateinit var mViewArticulos : RecyclerViewArticulos
    lateinit var mViewFamilias : RecyclerViewFamilias
    lateinit var mViewSubFamilias : RecyclerViewSubFamilias
    lateinit var mRecyclerViewArticulos : RecyclerView
    lateinit var mRecyclerViewFamilias : RecyclerView
    lateinit var mRecyclerViewSubFamilias : RecyclerView

    lateinit var mViewListaArticulos : RecyclerViewListaArticulos
    lateinit var mRecyclerViewListaArticulos : RecyclerView

    var listaFamiliaCompleta:MutableList<FamiliasSubFamiliasObject> = ArrayList()

    lateinit var dialogFiltrarArticulos:Dialog

    lateinit var dialogArticulosFiltro: ImageButton
    lateinit var dialogFiltroArticulosCancelar: Button

    lateinit var dialogFiltroArticulosAceptar: Button
    lateinit var dialogFiltroInventarioOptimo: CheckBox
    lateinit var dialogFiltroNombre: EditText
    lateinit var dialogFiltroCantidadMinimo: EditText
    lateinit var dialogFiltroCantidadMaximo: EditText
    lateinit var dialogFiltroPrecioMinimo: EditText
    lateinit var dialogFiltroPrecioMaximo: EditText
    lateinit var dialogFiltroReestablecerCampos: ImageButton
    lateinit var dialogArticulosCamara: ImageButton
    lateinit var dialogArticulosLeft: ImageButton
    lateinit var dialogArticulosRight: ImageButton
    var esDialogSurtido = false

    lateinit var dialogArticulos : Dialog

    lateinit var dialogArticulosSalir: Button
    lateinit var dialogArticulosAceptar: Button
    var listaArticulos:MutableList<ArticuloInventarioObjeto> = ArrayList()
    var listaArticulosCarrito:MutableList<ArticuloInventarioObjeto> = ArrayList()
    var pagina = 0
    var limiteArticulos = 10
    var totalArticulos = 0

    lateinit var fechaInicial : Date
    lateinit var fechaFinal : Date
    val formatoFechaCompleta = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    val paginado = Paginado()

    var selectedFamily = -1
    var selectedSubFamily = -1
    var selectedFamilyPos = 0

    fun crearDialogInicial(context: Context, globalVariableTmp: GlobalClass, activity : Activity)
    {
        contextTmp = context
        activityTmp = activity
        globalVariable = globalVariableTmp
        agregarArticulo = contextTmp as DialogAgregarArticulo
    }

    fun crearDialogArticulos(tipoArticulos: Int){
        dialogArticulos = Dialog(this.contextTmp)
        dialogArticulos.setCancelable(false)
        dialogArticulos.setContentView(R.layout.dialog_articulos)

        dialogArticulos.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogArticulos.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)

        dialogArticulosSalir = dialogArticulos.findViewById(R.id.dialogArticulosSalir) as Button
        dialogArticulosAceptar = dialogArticulos.findViewById(R.id.dialogArticulosAceptar) as Button
        dialogArticulosFiltro = dialogArticulos.findViewById(R.id.dialogArticulosFiltro) as ImageButton
        dialogArticulosCamara = dialogArticulos.findViewById(R.id.dialogArticulosCamara) as ImageButton
        dialogArticulosLeft = dialogArticulos.findViewById(R.id.dialogArticulosLeft) as ImageButton
        dialogArticulosRight = dialogArticulos.findViewById(R.id.dialogArticulosRight) as ImageButton
        mRecyclerViewArticulos = dialogArticulos.findViewById(R.id.dialogoArticulosRecyclerView) as RecyclerView
        mRecyclerViewListaArticulos = dialogArticulos.findViewById(R.id.dialogoArticulosRecyclerViewLista) as RecyclerView

        mViewArticulos = RecyclerViewArticulos()
        mViewArticulos.tipoArticulos(tipoArticulos)
        mRecyclerViewArticulos.layoutManager = LinearLayoutManager(this.contextTmp)

        mViewListaArticulos = RecyclerViewListaArticulos()
        mRecyclerViewListaArticulos.layoutManager = LinearLayoutManager(this.contextTmp, LinearLayoutManager.HORIZONTAL ,false)

        mRecyclerViewArticulos.addOnItemTouchListener(RecyclerItemClickListener(contextTmp, mRecyclerViewArticulos, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int)
            {
                if(listaArticulosCarrito.size == 0)
                {
                    dialogArticulos.dismiss()

                    val articuloTmp = listaArticulos[position]
                    articuloTmp.cantidad = 1
                    agregarArticulo.numeroArticulo(crearArticulos(articuloTmp))
                }
                else
                {
                    val articuloTmp = listaArticulos[position]

                    if(!comprobarArticuloEnCarrito(articuloTmp))
                    {
                        articuloTmp.cantidad = 1
                        listaArticulosCarrito.add(articuloTmp)
                        mViewListaArticulos.RecyclerAdapter(listaArticulosCarrito, contextTmp)
                        mViewListaArticulos.notifyDataSetChanged()
                    }
                }
            }

            override fun onLongItemClick(view: View?, position: Int)
            {
                val articuloTmp = listaArticulos[position]

                if(!comprobarArticuloEnCarrito(articuloTmp))
                {
                    articuloTmp.cantidad = 1
                    listaArticulosCarrito.add(articuloTmp)
                    mViewListaArticulos.RecyclerAdapter(listaArticulosCarrito, contextTmp)
                    mViewListaArticulos.notifyDataSetChanged()
                }

                dialogArticulosAceptar.visibility = View.VISIBLE
            }
        }))

        dialogArticulosCamara.setOnClickListener()
        {
            agregarArticulo.abrirCamara()
        }

        dialogArticulosFiltro.setOnClickListener()
        {
            mostrarDialogFiltros()
        }

        dialogArticulosSalir.setOnClickListener()
        {
            dialogArticulos.dismiss()
        }

        dialogArticulosAceptar.setOnClickListener()
        {
            pagina = 0
            agregarArticulo.agregarArticulos(crearListaArticulos(listaArticulosCarrito))
            dialogArticulos.dismiss()
        }

        dialogArticulosLeft.setOnClickListener()
        {
            if(totalArticulos > limiteArticulos && pagina > 0)
            {
                pagina--
                when(tipoArticulos)
                {
                    0 ->
                    {
                        buscarArticulos()
                    }
                    1 ->
                    {
                        buscarArticulosMasVendidos()
                    }
                }
            }
        }

        dialogArticulosRight.setOnClickListener()
        {
            val maximoPaginas = paginado.obtenerPaginadoMaximo(totalArticulos,limiteArticulos)

            if(totalArticulos > limiteArticulos && pagina < maximoPaginas)
            {
                pagina++
                when(tipoArticulos)
                {
                    0 ->
                    {
                        buscarArticulos()
                    }
                    1 ->
                    {
                        buscarArticulosMasVendidos()
                    }
                }
            }
        }

        mRecyclerViewListaArticulos.addOnItemTouchListener(RecyclerItemClickListener(contextTmp, mRecyclerViewListaArticulos, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int)
            {
                listaArticulosCarrito[position].cantidad--

                if(listaArticulosCarrito[position].cantidad <= 0)
                {
                    listaArticulosCarrito.removeAt(position)
                }

                mViewListaArticulos.notifyDataSetChanged()
            }

            override fun onLongItemClick(view: View?, position: Int)
            {
                /* levantar ventanita */
            }
        }))

    }

    fun llenarListaArticulos(listaArticulosTmp: MutableList<ArticuloInventarioObjeto>)
    {
        listaArticulos = listaArticulosTmp

        activityTmp.runOnUiThread {
            mViewArticulos.RecyclerAdapter(listaArticulos, contextTmp)
            mViewArticulos.notifyDataSetChanged()
        }
    }

    fun crearDialogFiltros(){

        if(listaFamiliaCompleta.size == 0)
            obtenerFamilias()

        dialogFiltrarArticulos = Dialog(this.contextTmp)

        dialogFiltrarArticulos.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogFiltrarArticulos.setCancelable(false)
        dialogFiltrarArticulos.setContentView(R.layout.dialog_filtro_articulos)

        dialogFiltrarArticulos.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogFiltrarArticulos.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)

        dialogFiltroArticulosCancelar = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroArticulosCancelar) as Button
        dialogFiltroArticulosAceptar = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroArticulosAceptar) as Button
        dialogFiltroInventarioOptimo = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroInventarioOptimo) as CheckBox
        dialogFiltroNombre = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroNombre) as EditText
        dialogFiltroCantidadMinimo = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroCantidadMinimo) as EditText
        dialogFiltroCantidadMaximo = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroCantidadMaximo) as EditText
        dialogFiltroPrecioMinimo = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroPrecioMinimo) as EditText
        dialogFiltroPrecioMaximo = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroPrecioMaximo) as EditText
        dialogFiltroReestablecerCampos = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroReestablecerCampos) as ImageButton

        mRecyclerViewFamilias = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroRecyclerViewFamilia) as RecyclerView
        mRecyclerViewSubFamilias = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroRecyclerViewSubFamilia) as RecyclerView

        asignarFuncionFamilias()

        dialogFiltroReestablecerCampos.setOnClickListener{
            reestablecerFiltros()
        }

        dialogFiltroArticulosCancelar.setOnClickListener{
            dialogFiltrarArticulos.dismiss()
        }

        dialogFiltroArticulosAceptar.setOnClickListener{
            pagina = 0
            buscarArticulos()
            dialogFiltrarArticulos.dismiss()

        }

    }

    fun asignarFuncionFamilias()
    {
        mViewFamilias = RecyclerViewFamilias()
        mRecyclerViewFamilias.layoutManager = LinearLayoutManager(this.contextTmp, LinearLayoutManager.HORIZONTAL ,false)

        mViewSubFamilias = RecyclerViewSubFamilias()
        mRecyclerViewSubFamilias.layoutManager = LinearLayoutManager(this.contextTmp, LinearLayoutManager.HORIZONTAL ,false)

        mRecyclerViewFamilias.addOnItemTouchListener(RecyclerItemClickListener(contextTmp, mRecyclerViewFamilias, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int)
            {
                if(selectedFamily != position)
                {
                    selectedFamilyPos = position
                    selectedFamily = position
                    mViewFamilias.positionSelected(position)
                    val adapter = mRecyclerViewFamilias.adapter
                    mRecyclerViewFamilias.adapter = adapter
                    mViewFamilias.notifyDataSetChanged()

                    if(listaFamiliaCompleta[position].SubFamilia.isNotEmpty())
                    {
                        selectedSubFamily = -1
                        mViewSubFamilias.positionSelected(selectedSubFamily)
                        mViewSubFamilias.RecyclerAdapter(listaFamiliaCompleta[position].SubFamilia, contextTmp)
                        mRecyclerViewSubFamilias.adapter = mViewSubFamilias
                        mViewSubFamilias.notifyDataSetChanged()
                    }
                }
                else
                {
                    selectedFamily = -1
                    mViewFamilias.positionSelected(selectedFamily)
                    val adapter = mRecyclerViewFamilias.adapter
                    mRecyclerViewFamilias.adapter = adapter
                    mViewFamilias.notifyDataSetChanged()
                }

            }

            override fun onLongItemClick(view: View?, position: Int){}
        }))

        mRecyclerViewSubFamilias.addOnItemTouchListener(RecyclerItemClickListener(contextTmp, mRecyclerViewSubFamilias, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int)
            {
                val positionTmp =
                    if(selectedSubFamily != position)
                    {
                        position
                    }
                    else
                    {
                        -1
                    }

                selectedSubFamily = positionTmp
                mViewSubFamilias.positionSelected(positionTmp)
                val adapter = mRecyclerViewSubFamilias.adapter
                mRecyclerViewSubFamilias.adapter = adapter
                mViewSubFamilias.notifyDataSetChanged()

                selectedFamily = -1
                mViewFamilias.positionSelected(selectedFamily)
                val adapter2 = mRecyclerViewFamilias.adapter
                mRecyclerViewFamilias.adapter = adapter2
                mViewFamilias.notifyDataSetChanged()
            }

            override fun onLongItemClick(view: View?, position: Int){}
        }))
    }

    fun crearDialogFiltrosArticulosMasVendidos(tipoArticulos: Int){

        if(listaFamiliaCompleta.size == 0)
            obtenerFamilias()

        dialogFiltrarArticulos = Dialog(this.contextTmp)

        dialogFiltrarArticulos.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogFiltrarArticulos.setCancelable(false)
        dialogFiltrarArticulos.setContentView(R.layout.dialog_filtro_articulos)

        dialogFiltrarArticulos.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogFiltrarArticulos.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)

        dialogFiltroArticulosCancelar = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroArticulosCancelar) as Button
        dialogFiltroArticulosAceptar = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroArticulosAceptar) as Button
        dialogFiltroInventarioOptimo = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroInventarioOptimo) as CheckBox
        dialogFiltroNombre = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroNombre) as EditText
        dialogFiltroCantidadMinimo = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroCantidadMinimo) as EditText
        dialogFiltroCantidadMaximo = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroCantidadMaximo) as EditText
        dialogFiltroPrecioMinimo = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroPrecioMinimo) as EditText
        dialogFiltroPrecioMaximo = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroPrecioMaximo) as EditText
        dialogFiltroReestablecerCampos = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroReestablecerCampos) as ImageButton

        mRecyclerViewFamilias = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroRecyclerViewFamilia) as RecyclerView
        mRecyclerViewSubFamilias = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroRecyclerViewSubFamilia) as RecyclerView

        asignarFuncionFamilias()

        dialogFiltroInventarioOptimo.visibility = View.GONE

        dialogFiltroReestablecerCampos.setOnClickListener{
            reestablecerFiltros()
        }

        dialogFiltroArticulosCancelar.setOnClickListener{
            dialogFiltrarArticulos.dismiss()
        }

        dialogFiltroArticulosAceptar.setOnClickListener{
            pagina = 0
            when(tipoArticulos)
            {
                1 ->
                {
                    buscarArticulosMasVendidos()
                }
                2 ->
                {
                    buscarArticulosNoVendidos()
                }
            }
            dialogFiltrarArticulos.dismiss()
        }
    }

    fun asignarFechas(fechaInicialTmp: Date, fechaFinalTmp: Date)
    {
        fechaInicial = fechaInicialTmp
        fechaFinal = fechaFinalTmp
    }

    fun mostrarDialogFiltros(){
        dialogFiltrarArticulos.show()
    }

    fun dialogSurtido()
    {
        esDialogSurtido = true
    }

    fun asignarBotones()
    {
        activityTmp.runOnUiThread()
        {
            if(totalArticulos > limiteArticulos)
            {
                dialogArticulosLeft.visibility = View.VISIBLE
                dialogArticulosRight.visibility = View.VISIBLE
            }
            else
            {
                dialogArticulosLeft.visibility = View.GONE
                dialogArticulosRight.visibility = View.GONE
            }
        }
    }

    fun buscarArticulosMasVendidos()
    {
        val urls = Urls()

        var url = urls.url+urls.endPointEstadisticas.endPointArticulosMasVendidos+
                "?token="+globalVariable.usuario!!.token+
                "&fechaInicial=" + formatoFechaCompleta.format(fechaInicial) +
                "&fechaFinal="+formatoFechaCompleta.format(fechaFinal) +
                "&pagina="+pagina +
                "&limit="+limiteArticulos

        if(selectedFamily != -1) url += "&familiaId=" + listaFamiliaCompleta[selectedFamily].familiaId
        if(selectedSubFamily != -1) url += "&subFamiliaId=" + listaFamiliaCompleta[selectedFamilyPos].SubFamilia[selectedSubFamily].subFamiliaId
        if(dialogFiltroCantidadMinimo.text.isNotEmpty()) url += "&minimoCantidad=" + parseInt(dialogFiltroCantidadMinimo.text.toString())
        if(dialogFiltroCantidadMaximo.text.isNotEmpty()) url += "&maximoCantidad=" + parseInt(dialogFiltroCantidadMaximo.text.toString())
        if(dialogFiltroPrecioMinimo.text.isNotEmpty()) url += "&minimoPrecio=" + Double.parseDouble(dialogFiltroPrecioMinimo.text.toString())
        if(dialogFiltroPrecioMaximo.text.isNotEmpty()) url += "&maximoPrecio=" + Double.parseDouble(dialogFiltroPrecioMaximo.text.toString())
        if(dialogFiltroNombre.text.trim().isNotEmpty()) url += "&nombre=" + dialogFiltroNombre.text.toString()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()

        val progressDialog = ProgressDialog(contextTmp)
        progressDialog.setMessage(contextTmp.getString(R.string.mensaje_espera))
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                agregarArticulo.lanzarMensaje(contextTmp.getString(R.string.mensaje_error_intentear_mas_tarde))
            }
            override fun onResponse(call: Call, response: Response)
            {
                val body = response.body()?.string()

                if(body != null && body.isNotEmpty()) {
                    try
                    {
                        val gson = GsonBuilder().create()
                        val model = gson.fromJson(body, ListEstadisticaArticuloObject::class.java)
                        totalArticulos = model.count

                        agregarArticulo.listaArticulosMasVendidos(model)
                    }
                    catch(e:Exception)
                    {
                        agregarArticulo.procesarError(body)
                    }
                }

                progressDialog.dismiss()

            }
        })
    }

    fun buscarArticulosNoVendidos()
    {
        val urls = Urls()

        var url = urls.url+urls.endPointsInventario.endPointArticulosNoVendidos+
                "?token="+globalVariable.usuario!!.token+
                "&pagina="+pagina +
                "&limit="+limiteArticulos

        if(selectedFamily != -1) url += "&familiaId=" + listaFamiliaCompleta[selectedFamily].familiaId
        if(selectedSubFamily != -1) url += "&subFamiliaId=" + listaFamiliaCompleta[selectedFamilyPos].SubFamilia[selectedSubFamily].subFamiliaId
        if(dialogFiltroCantidadMinimo.text.isNotEmpty()) url += "&minimoCantidad=" + parseInt(dialogFiltroCantidadMinimo.text.toString())
        if(dialogFiltroCantidadMaximo.text.isNotEmpty()) url += "&maximoCantidad=" + parseInt(dialogFiltroCantidadMaximo.text.toString())
        if(dialogFiltroPrecioMinimo.text.isNotEmpty()) url += "&minimoPrecio=" + Double.parseDouble(dialogFiltroPrecioMinimo.text.toString())
        if(dialogFiltroPrecioMaximo.text.isNotEmpty()) url += "&maximoPrecio=" + Double.parseDouble(dialogFiltroPrecioMaximo.text.toString())
        if(dialogFiltroNombre.text.trim().isNotEmpty()) url += "&nombre=" + dialogFiltroNombre.text.toString()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()

        val progressDialog = ProgressDialog(contextTmp)
        progressDialog.setMessage(contextTmp.getString(R.string.mensaje_espera))
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                agregarArticulo.lanzarMensaje(contextTmp.getString(R.string.mensaje_error_intentear_mas_tarde))
            }
            override fun onResponse(call: Call, response: Response)
            {
                val body = response.body()?.string()

                if(body != null && body.isNotEmpty()) {
                    try
                    {
                        val gson = GsonBuilder().create()
                        val model = gson.fromJson(body, ListInventoryNoSells::class.java)
                        totalArticulos = model.count

                        agregarArticulo.listaArticulosNoVendidos(model)
                    }
                    catch(e:Exception)
                    {
                        agregarArticulo.procesarError(body)
                    }
                }

                progressDialog.dismiss()

            }
        })
    }

    fun buscarArticulos()
    {
        val urls = Urls()

        var url = urls.url+urls.endPointsInventario.endPointInventario+"?token="+globalVariable.usuario!!.token +
                "&pagina="+pagina +
                "&limit="+limiteArticulos

        if(selectedFamily != -1) url += "&familiaId=" + listaFamiliaCompleta[selectedFamily].familiaId
        if(selectedSubFamily != -1) url += "&subFamiliaId=" + listaFamiliaCompleta[selectedFamilyPos].SubFamilia[selectedSubFamily].subFamiliaId
        if(dialogFiltroInventarioOptimo.isChecked) url += "&inventarioOptimo=" + "true"
        if(dialogFiltroCantidadMinimo.text.isNotEmpty()) url += "&minimoCantidad=" + parseInt(dialogFiltroCantidadMinimo.text.toString())
        if(dialogFiltroCantidadMaximo.text.isNotEmpty()) url += "&maximoCantidad=" + parseInt(dialogFiltroCantidadMaximo.text.toString())
        if(dialogFiltroPrecioMinimo.text.isNotEmpty()) url += "&minimoPrecio=" + Double.parseDouble(dialogFiltroPrecioMinimo.text.toString())
        if(dialogFiltroPrecioMaximo.text.isNotEmpty()) url += "&maximoPrecio=" + Double.parseDouble(dialogFiltroPrecioMaximo.text.toString())
        if(dialogFiltroNombre.text.trim().isNotEmpty()) url += "&nombre=" + dialogFiltroNombre.text.toString()
        if(esDialogSurtido) url += "&modificaInventario=true"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()

        val progressDialog = ProgressDialog(contextTmp)
        progressDialog.setMessage(contextTmp.getString(R.string.mensaje_espera))
        progressDialog.setCancelable(false)
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                agregarArticulo.lanzarMensaje(contextTmp.getString(R.string.mensaje_error_intentear_mas_tarde))
            }
            override fun onResponse(call: Call, response: Response)
            {
                val body = response.body()?.string()

                if(body != null && body.isNotEmpty())
                {
                    val gson = GsonBuilder().create()

                    try
                    {
                        val model = gson.fromJson(body, ListInventoryObject::class.java)
                        agregarArticulo.listaArticulos(model)
                        asignarBotones()
                        totalArticulos = model.count
                    }
                    catch(e:Exception)
                    {
                        agregarArticulo.procesarError(body)
                    }

                }

                progressDialog.dismiss()

            }
        })
    }

    fun buscarArticulo(idArticulo: Long)
    {
        val urls = Urls()

        val url = urls.url+urls.endPointsInventario.endPointArticulo+"?token="+globalVariable.usuario!!.token+"&idArticulo="+idArticulo

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()

        val progressDialog = ProgressDialog(contextTmp)
        progressDialog.setMessage(contextTmp.getString(R.string.mensaje_espera))
        progressDialog.setCancelable(false)
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                agregarArticulo.lanzarMensaje(contextTmp.getString(R.string.mensaje_error_intentear_mas_tarde))
            }
            override fun onResponse(call: Call, response: Response)
            {
                val body = response.body()?.string()

                if(body != null && body.isNotEmpty()) {

                    try
                    {
                        val gson = GsonBuilder().create()
                        val model = gson.fromJson(body, InventarioObjeto::class.java)

                        if(model.nombre == null)
                        {
                            Exception("")
                        }

                        model.cantidad = 1

                        agregarArticulo.numeroArticulo(model)
                    }
                    catch(e:Exception)
                    {
                        agregarArticulo.procesarError(body)
                    }

                    dialogArticulos.dismiss()

                }
                else
                {
                    activityTmp.runOnUiThread {
                        Toast.makeText(contextTmp, contextTmp.getString(R.string.dialog_lista_articulo_no_existe), Toast.LENGTH_SHORT).show()
                    }
                }

                progressDialog.dismiss()

            }
        })
    }

    fun reestablecerFiltros() {
        if(listaFamiliaCompleta.size > 0)
        {
            selectedSubFamily = -1
            mViewSubFamilias.positionSelected(selectedSubFamily)
            val adapter = mRecyclerViewSubFamilias.adapter
            mRecyclerViewSubFamilias.adapter = adapter
            mViewSubFamilias.notifyDataSetChanged()

            selectedFamily = -1
            mViewFamilias.positionSelected(selectedFamily)
            val adapter2 = mRecyclerViewFamilias.adapter
            mRecyclerViewFamilias.adapter = adapter2
            mViewFamilias.notifyDataSetChanged()
        }

        dialogFiltroCantidadMinimo.setText("")
        dialogFiltroCantidadMaximo.setText("")
        dialogFiltroPrecioMinimo.setText("")
        dialogFiltroPrecioMaximo.setText("")
        dialogFiltroNombre.setText("")

        dialogFiltroInventarioOptimo.isChecked = false

    }

    fun obtenerFamilias(){

        val url = urls.url+urls.endPointFamilias.endPointConsultarFamiliasSubFamilias+"?token="+globalVariable.usuario!!.token

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()


        val progressDialog = ProgressDialog(contextTmp)
        progressDialog.setMessage(contextTmp.getString(R.string.mensaje_espera))
        progressDialog.setCancelable(false)
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                agregarArticulo.lanzarMensaje(contextTmp.getString(R.string.mensaje_error_intentear_mas_tarde))
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response)
            {
                val body = response.body()?.string()

                if(body != null && body.isNotEmpty()) {
                    try
                    {
                        val gson = GsonBuilder().create()
                        listaFamiliaCompleta = gson.fromJson(body,Array<FamiliasSubFamiliasObject>::class.java).toMutableList()

                        if(listaFamiliaCompleta.isNotEmpty())
                        {
                            mViewFamilias.RecyclerAdapter(listaFamiliaCompleta, contextTmp)
                            mRecyclerViewFamilias.adapter = mViewFamilias
                            mViewFamilias.notifyDataSetChanged()

                            if(listaFamiliaCompleta[0].SubFamilia.isNotEmpty())
                            {
                                mViewSubFamilias.RecyclerAdapter(listaFamiliaCompleta[0].SubFamilia, contextTmp)
                                mRecyclerViewSubFamilias.adapter = mViewSubFamilias
                                mViewSubFamilias.notifyDataSetChanged()
                            }
                        }

                        /*for (familias in listaFamiliaCompleta)
                        {
                            listaFamilia.add(familias.nombreFamilia)
                        }

                        activityTmp.runOnUiThread {
                            val adapter = ArrayAdapter(context,
                                R.layout.item_spinner, listaFamilia)
                            dialogArticulosFamiliaSpinner.adapter = adapter
                            dialogArticulosFamiliaSpinner.setSelection(0)

                            dialogArticulosFamiliaSpinner.onItemSelectedListener = object :
                                AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>,
                                                            view: View, position: Int, id: Long) {
                                    obtenerSubFamilias(context,position)
                                }

                                override fun onNothingSelected(parent: AdapterView<*>) {}
                            }
                        }*/
                    }
                    catch(e:Exception)
                    {
                        agregarArticulo.procesarError(body)
                    }
                }

                progressDialog.dismiss()
            }
        })

    }

    /*fun obtenerSubFamilias(context: Context, familiaId: Int){
        listaSubFamilia.clear()
        listaSubFamiliaCompleta = listaFamiliaCompleta[familiaId].SubFamilia.toMutableList()

        for (familias in listaSubFamiliaCompleta) {
            listaSubFamilia.add(familias.nombreSubFamilia)
        }

        val adapter = ArrayAdapter(context,
            R.layout.item_spinner, listaSubFamilia)
        dialogArticulosSubFamiliaSpinner.adapter = adapter

        if(listaSubFamilia.size > 0) {
            dialogArticulosSubFamiliaSpinner.setSelection(0)
        }

        dialogArticulosSubFamiliaSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if(listaSubFamilia.size > 0) {
                    subFamiliaId = listaSubFamiliaCompleta[position].subFamiliaId
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }*/

    fun comprobarArticuloEnCarrito(articuloTmp: ArticuloInventarioObjeto) : Boolean
    {
        for (i in 0 until listaArticulosCarrito.size)
        {
            if(articuloTmp.idArticulo == listaArticulosCarrito[i].idArticulo)
            {
                listaArticulosCarrito[i].cantidad++
                mViewListaArticulos.notifyDataSetChanged()
                return true
            }
        }

        return false
    }

    fun mostrarDialogArticulos(){

        if(listaArticulos.size == 0)
        {
            buscarArticulos()
        }

        listaArticulosCarrito.clear()
        mViewArticulos.RecyclerAdapter(listaArticulos, contextTmp)
        mRecyclerViewArticulos.adapter = mViewArticulos

        mViewListaArticulos.RecyclerAdapter(listaArticulosCarrito, contextTmp)
        mRecyclerViewListaArticulos.adapter = mViewListaArticulos
        dialogArticulos.show()
    }

    fun crearListaArticulos(articuloInventarioObjeto: MutableList<ArticuloInventarioObjeto>): MutableList<InventarioObjeto>
    {
        val listaTmp:MutableList<InventarioObjeto> = ArrayList()

        for(i in 0 until articuloInventarioObjeto.size)
        {
            listaTmp.add(
                InventarioObjeto(
                    articuloInventarioObjeto[i].idArticulo,
                    articuloInventarioObjeto[i].nombre,
                    articuloInventarioObjeto[i].descripcion,
                    articuloInventarioObjeto[i].cantidad,
                    articuloInventarioObjeto[i].precio,
                    articuloInventarioObjeto[i].familia,
                    articuloInventarioObjeto[i].costo,
                    articuloInventarioObjeto[i].inventarioOptimo,
                    articuloInventarioObjeto[i].modificaInventario)
            )
        }

        return listaTmp
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun crearArticulos(articuloInventarioObjeto: ArticuloInventarioObjeto): InventarioObjeto
    {
        return InventarioObjeto(
            articuloInventarioObjeto.idArticulo,
            articuloInventarioObjeto.nombre,
            articuloInventarioObjeto.descripcion,
            articuloInventarioObjeto.cantidad,
            articuloInventarioObjeto.precio,
            articuloInventarioObjeto.familia,
            articuloInventarioObjeto.costo,
            articuloInventarioObjeto.inventarioOptimo,
            articuloInventarioObjeto.modificaInventario)
    }

    interface DialogAgregarArticulo {
        fun numeroArticulo(articulo : InventarioObjeto)
        fun agregarArticulos(articulosCarrito : MutableList<InventarioObjeto>)
        fun abrirCamara()
        fun listaArticulos(listInventoryObject: ListInventoryObject)
        fun listaArticulosNoVendidos(listInventoryObject: ListInventoryNoSells)
        fun listaArticulosMasVendidos(listaArticulos: ListEstadisticaArticuloObject)
        fun lanzarMensaje(mensaje: String)
        fun procesarError(json: String)
    }

}
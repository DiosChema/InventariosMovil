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
import android.widget.*
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Objets.*
import com.Aegina.PocketSale.R
import com.Aegina.PocketSale.RecyclerView.RecyclerItemClickListener
import com.Aegina.PocketSale.RecyclerView.RecyclerViewArticulos
import com.Aegina.PocketSale.RecyclerView.RecyclerViewListaArticulos
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.lang.Double


class DialogAgregarArticulos : AppCompatDialogFragment(){

    lateinit var contextTmp :Context
    val urls: Urls = Urls()

    lateinit var agregarArticulo: DialogAgregarArticulo
    lateinit var activityTmp: Activity
    lateinit var globalVariable: GlobalClass

    lateinit var mViewArticulos : RecyclerViewArticulos
    lateinit var mRecyclerViewArticulos : RecyclerView

    lateinit var mViewListaArticulos : RecyclerViewListaArticulos
    lateinit var mRecyclerViewListaArticulos : RecyclerView

    var listaFamilia:MutableList<String> = ArrayList()
    var listaFamiliaCompleta:MutableList<FamiliasSubFamiliasObject> = ArrayList()
    var listaSubFamilia:MutableList<String> = ArrayList()
    var listaSubFamiliaCompleta:MutableList<SubFamiliaObjeto> = ArrayList()

    lateinit var dialogFiltrarArticulos:Dialog

    lateinit var dialogArticulosFiltro: ImageButton
    lateinit var dialogFiltroArticulosCancelar: Button

    lateinit var dialogFiltroArticulosAceptar: Button
    lateinit var checkBoxFamilia: CheckBox
    lateinit var checkBoxSubFamilia: CheckBox
    lateinit var dialogFiltroInventarioOptimo: CheckBox
    lateinit var dialogFiltroNombre: EditText
    lateinit var dialogFiltroCantidadMinimo: EditText
    lateinit var dialogFiltroCantidadMaximo: EditText
    lateinit var dialogFiltroPrecioMinimo: EditText
    lateinit var dialogFiltroPrecioMaximo: EditText
    lateinit var dialogFiltroReestablecerCampos: ImageButton
    lateinit var dialogArticulosCamara: ImageButton

    lateinit var dialogInstance : Dialog

    lateinit var dialogArticulosFamiliaSpinner: Spinner
    lateinit var dialogArticulosSubFamiliaSpinner: Spinner
    lateinit var dialogArticulosSalir: ImageButton
    lateinit var dialogArticulosAceptar: ImageView
    var subFamiliaId = -1
    var listaArticulos:MutableList<ArticuloInventarioObjeto> = ArrayList()
    var listaArticulosCarrito:MutableList<ArticuloInventarioObjeto> = ArrayList()

    fun crearDialogInicial(context: Context, globalVariableTmp: GlobalClass, activity : Activity)
    {
        contextTmp = context
        activityTmp = activity
        globalVariable = globalVariableTmp
        agregarArticulo = contextTmp as DialogAgregarArticulo
    }

    fun crearDialogArticulos(){
        dialogInstance = Dialog(this.contextTmp)
        dialogInstance.setCancelable(false)
        dialogInstance.setContentView(R.layout.dialog_articulos)

        dialogInstance.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogInstance.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)

        dialogArticulosSalir = dialogInstance.findViewById(R.id.dialogArticulosSalir) as ImageButton
        dialogArticulosAceptar = dialogInstance.findViewById(R.id.dialogArticulosAceptar) as ImageView
        dialogArticulosFiltro = dialogInstance.findViewById(R.id.dialogArticulosFiltro) as ImageButton
        dialogArticulosCamara = dialogInstance.findViewById(R.id.dialogArticulosCamara) as ImageButton
        mRecyclerViewArticulos = dialogInstance.findViewById(R.id.dialogoArticulosRecyclerView) as RecyclerView
        mRecyclerViewListaArticulos = dialogInstance.findViewById(R.id.dialogoArticulosRecyclerViewLista) as RecyclerView

        mViewArticulos = RecyclerViewArticulos()
        mRecyclerViewArticulos.layoutManager = LinearLayoutManager(this.contextTmp)

        mViewListaArticulos = RecyclerViewListaArticulos()
        mRecyclerViewListaArticulos.layoutManager = LinearLayoutManager(this.contextTmp, LinearLayoutManager.HORIZONTAL ,false)

        mRecyclerViewArticulos.addOnItemTouchListener(RecyclerItemClickListener(contextTmp, mRecyclerViewArticulos, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int)
            {
                if(listaArticulosCarrito.size == 0)
                {
                    dialogInstance.dismiss()

                    var articuloTmp = listaArticulos[position]
                    articuloTmp.cantidadArticulo = 1
                    agregarArticulo.numeroArticulo(crearArticulos(articuloTmp))
                }
                else
                {
                    var articuloTmp = listaArticulos[position]

                    if(!comprobarArticuloEnCarrito(articuloTmp))
                    {
                        articuloTmp.cantidadArticulo = 1
                        listaArticulosCarrito.add(articuloTmp)
                        mViewListaArticulos.RecyclerAdapter(listaArticulosCarrito, contextTmp)
                        mViewListaArticulos.notifyDataSetChanged()
                    }
                }
            }

            override fun onLongItemClick(view: View?, position: Int)
            {
                var articuloTmp = listaArticulos[position]

                if(!comprobarArticuloEnCarrito(articuloTmp))
                {
                    articuloTmp.cantidadArticulo = 1
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
            dialogInstance.dismiss()
        }

        dialogArticulosAceptar.setOnClickListener()
        {
            agregarArticulo.agregarArticulos(crearListaArticulos(listaArticulosCarrito))
            dialogInstance.dismiss()
        }

        mRecyclerViewListaArticulos.addOnItemTouchListener(RecyclerItemClickListener(contextTmp, mRecyclerViewListaArticulos, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int)
            {
                listaArticulosCarrito[position].cantidadArticulo--

                if(listaArticulosCarrito[position].cantidadArticulo <= 0)
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
            obtenerFamilias(contextTmp)

        dialogFiltrarArticulos = Dialog(this.contextTmp)

        dialogFiltrarArticulos.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogFiltrarArticulos.setCancelable(false)
        dialogFiltrarArticulos.setContentView(R.layout.dialog_filtro_articulos)

        dialogArticulosFamiliaSpinner = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroArticulosFamiliaSpinner) as Spinner
        dialogArticulosSubFamiliaSpinner = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroArticulosSubFamiliaSpinner) as Spinner
        dialogFiltroArticulosCancelar = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroArticulosCancelar) as Button
        dialogFiltroArticulosAceptar = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroArticulosAceptar) as Button
        checkBoxFamilia = dialogFiltrarArticulos.findViewById(R.id.checkBoxFamilia) as CheckBox
        checkBoxSubFamilia = dialogFiltrarArticulos.findViewById(R.id.checkBoxSubFamilia) as CheckBox
        dialogFiltroInventarioOptimo = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroInventarioOptimo) as CheckBox
        dialogFiltroNombre = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroNombre) as EditText
        dialogFiltroCantidadMinimo = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroCantidadMinimo) as EditText
        dialogFiltroCantidadMaximo = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroCantidadMaximo) as EditText
        dialogFiltroPrecioMinimo = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroPrecioMinimo) as EditText
        dialogFiltroPrecioMaximo = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroPrecioMaximo) as EditText
        dialogFiltroReestablecerCampos = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroReestablecerCampos) as ImageButton

        dialogArticulosFamiliaSpinner.isEnabled = false
        dialogArticulosSubFamiliaSpinner.isEnabled = false

        checkBoxFamilia.setOnClickListener{
            dialogArticulosFamiliaSpinner.isEnabled = checkBoxFamilia.isChecked
        }

        checkBoxSubFamilia.setOnClickListener{
            dialogArticulosSubFamiliaSpinner.isEnabled = checkBoxSubFamilia.isChecked
        }

        dialogFiltroReestablecerCampos.setOnClickListener{
            reestablecerFiltros()
        }

        dialogFiltroArticulosCancelar.setOnClickListener{
            dialogFiltrarArticulos.dismiss()
        }

        dialogFiltroArticulosAceptar.setOnClickListener{
            buscarArticulos()

            dialogFiltrarArticulos.dismiss()

        }

    }

    fun mostrarDialogFiltros(){
        dialogFiltrarArticulos.show()
    }

    fun buscarArticulos()
    {
        val urls = Urls()

        var url = urls.url+urls.endPointsInventario.endPointInventario+"?token="+globalVariable.usuario!!.token

        if(checkBoxFamilia.isChecked) url += "&familiaId=" + listaFamiliaCompleta[dialogArticulosFamiliaSpinner.selectedItemPosition].familiaId
        if(checkBoxSubFamilia.isChecked) url += "&subFamiliaId=" + listaSubFamiliaCompleta[dialogArticulosSubFamiliaSpinner.selectedItemPosition].subFamiliaId
        if(dialogFiltroInventarioOptimo.isChecked) url += "&inventarioOptimo=" + "true"
        if(dialogFiltroCantidadMinimo.text.isNotEmpty()) url += "&minimoCantidad=" + Integer.parseInt(dialogFiltroCantidadMinimo.text.toString())
        if(dialogFiltroCantidadMaximo.text.isNotEmpty()) url += "&maximoCantidad=" + Integer.parseInt(dialogFiltroCantidadMaximo.text.toString())
        if(dialogFiltroPrecioMinimo.text.isNotEmpty()) url += "&minimoPrecio=" + Double.parseDouble(dialogFiltroPrecioMinimo.text.toString())
        if(dialogFiltroPrecioMaximo.text.isNotEmpty()) url += "&maximoPrecio=" + Double.parseDouble(dialogFiltroPrecioMaximo.text.toString())
        if(dialogFiltroNombre.text.trim().isNotEmpty()) url += "&nombreArticulo=" + dialogFiltroNombre.text.toString()

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
            }
            override fun onResponse(call: Call, response: Response)
            {
                val body = response.body()?.string()

                if(body != null && body.isNotEmpty()) {
                    val gson = GsonBuilder().create()
                    val model = gson.fromJson(body, Array<ArticuloInventarioObjeto>::class.java).toList()

                    agregarArticulo.listaArticulos(model.toMutableList())
                }

                progressDialog.dismiss()

            }
        })
    }

    fun buscarArticulo(idArticulo: Long)
    {
        val urls = Urls()

        var url = urls.url+urls.endPointsInventario.endPointArticulo+"?token="+globalVariable.usuario!!.token+"&idArticulo="+idArticulo

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
            }
            override fun onResponse(call: Call, response: Response)
            {
                val body = response.body()?.string()

                if(body != null && body.isNotEmpty()) {
                    val gson = GsonBuilder().create()
                    val model = gson.fromJson(body, InventarioObjeto::class.java)

                    model.cantidadArticulo = 1

                    agregarArticulo.numeroArticulo(model)
                    dialogInstance.dismiss()

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
        if(listaFamilia.size > 0)
            dialogArticulosFamiliaSpinner.setSelection(0)

        dialogFiltroCantidadMinimo.setText("")
        dialogFiltroCantidadMaximo.setText("")

        checkBoxFamilia.isChecked = false
        checkBoxSubFamilia.isChecked = false
        dialogFiltroInventarioOptimo.isChecked = false

    }

    fun obtenerFamilias(context: Context){

        val url = urls.url+urls.endPointFamilias.endPointConsultarFamiliasSubFamilias+"?token="+globalVariable.usuario!!.token

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
                listaFamiliaCompleta = gson.fromJson(body,Array<FamiliasSubFamiliasObject>::class.java).toMutableList()

                for (familias in listaFamiliaCompleta) {
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
                }
            }
        })

    }

    fun obtenerSubFamilias(context: Context, familiaId: Int){
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
    }

    fun comprobarArticuloEnCarrito(articuloTmp: ArticuloInventarioObjeto) : Boolean
    {
        for (i in 0 until listaArticulosCarrito.size)
        {
            if(articuloTmp.idArticulo == listaArticulosCarrito[i].idArticulo)
            {
                listaArticulosCarrito[i].cantidadArticulo++
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
        dialogInstance.show()
    }

    fun crearListaArticulos(articuloInventarioObjeto: MutableList<ArticuloInventarioObjeto>): MutableList<InventarioObjeto>
    {
        var listaTmp:MutableList<InventarioObjeto> = ArrayList()

        for(i in 0 until articuloInventarioObjeto.size)
        {
            listaTmp.add(
                InventarioObjeto(
                    articuloInventarioObjeto[i].idArticulo,
                    articuloInventarioObjeto[i].nombreArticulo,
                    articuloInventarioObjeto[i].descripcionArticulo,
                    articuloInventarioObjeto[i].cantidadArticulo,
                    articuloInventarioObjeto[i].precioArticulo,
                    articuloInventarioObjeto[i].familiaArticulo,
                    articuloInventarioObjeto[i].costoArticulo,
                    articuloInventarioObjeto[i].inventarioOptimo)
            )
        }

        return listaTmp
    }

    fun crearArticulos(articuloInventarioObjeto: ArticuloInventarioObjeto): InventarioObjeto
    {
        return InventarioObjeto(
            articuloInventarioObjeto.idArticulo,
            articuloInventarioObjeto.nombreArticulo,
            articuloInventarioObjeto.descripcionArticulo,
            articuloInventarioObjeto.cantidadArticulo,
            articuloInventarioObjeto.precioArticulo,
            articuloInventarioObjeto.familiaArticulo,
            articuloInventarioObjeto.costoArticulo,
            articuloInventarioObjeto.inventarioOptimo)
    }

    interface DialogAgregarArticulo {
        fun numeroArticulo(articulo : InventarioObjeto)
        fun agregarArticulos(articulosCarrito : MutableList<InventarioObjeto>)
        fun abrirCamara()
        fun listaArticulos(listaArticulos: MutableList<ArticuloInventarioObjeto>)
    }

}
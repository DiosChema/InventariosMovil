@file:Suppress("DEPRECATION")

package com.Aegina.PocketSale.Dialogs

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Objets.*
import com.Aegina.PocketSale.R
import com.Aegina.PocketSale.RecyclerView.RecyclerItemClickListener
import com.Aegina.PocketSale.RecyclerView.RecyclerViewArticulos
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.lang.Long.parseLong

class DialogAgregarArticulos : AppCompatDialogFragment(){

    lateinit var contextTmp :Context
    val urls: Urls = Urls()
    lateinit var dialogAgregarArticulo : Dialog

    lateinit var listener: ExampleDialogListener
    lateinit var activityTmp: Activity
    lateinit var globalVariable: GlobalClass

    lateinit var dialogAgregarArticuloCodigo : EditText
    lateinit var dialogAgregarArticuloCantidad :EditText
    lateinit var dialogAgregarArticuloAceptar :Button
    lateinit var dialogAgregarArticuloCancelar :Button
    lateinit var dialogAgregarArticuloTitulo :TextView
    lateinit var dialogAgregarArticuloBuscarArticulo:ImageButton
    lateinit var dialogAgregarArticuloObtenerCodigo :ImageButton



    lateinit var mViewArticulos : RecyclerViewArticulos
    lateinit var mRecyclerViewArticulos : RecyclerView

    var listaFamilia:MutableList<String> = ArrayList()
    var listaFamiliaCompleta:MutableList<FamiliasSubFamiliasObject> = ArrayList()
    var listaSubFamilia:MutableList<String> = ArrayList()
    var listaSubFamiliaCompleta:MutableList<SubFamiliaObjeto> = ArrayList()

    lateinit var dialogInstance : Dialog
    //lateinit var listener: DialogArticulos.ExampleDialogListenerArticulos

    lateinit var dialogArticulosFamiliaSpinner: Spinner
    lateinit var dialogArticulosSubFamiliaSpinner: Spinner
    lateinit var dialogArticulosSalir: ImageButton
    var subFamiliaId = -1
    var listaTmpArticulos:MutableList<InventarioObjeto> = ArrayList()
    lateinit var articuloTmp : InventarioObjeto

    fun crearDialogArticulos(context: Context, globalVariableTmp: GlobalClass, activity : Activity){

        contextTmp = context
        activityTmp = activity
        globalVariable = globalVariableTmp

        listener = contextTmp as ExampleDialogListener
        dialogAgregarArticulo = Dialog(contextTmp)

        dialogAgregarArticulo.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //dialogArticulos.iniciarDialog(globalVariable, context, activityTmp)

        dialogAgregarArticulo.setCancelable(false)
        dialogAgregarArticulo.setContentView(R.layout.dialog_agregar_articulo)
        dialogAgregarArticuloCodigo = dialogAgregarArticulo.findViewById(R.id.dialogAgregarArticuloCodigo) as EditText
        dialogAgregarArticuloCantidad = dialogAgregarArticulo.findViewById(R.id.dialogAgregarArticuloCantidad) as EditText
        dialogAgregarArticuloAceptar = dialogAgregarArticulo.findViewById(R.id.dialogAgregarArticuloAceptar) as Button
        dialogAgregarArticuloCancelar = dialogAgregarArticulo.findViewById(R.id.dialogAgregarArticuloCancelar) as Button
        dialogAgregarArticuloTitulo = dialogAgregarArticulo.findViewById(R.id.dialogAgregarArticuloTitulo) as TextView
        dialogAgregarArticuloBuscarArticulo = dialogAgregarArticulo.findViewById<View>(R.id.dialogAgregarArticuloBuscarArticulo) as ImageButton
        dialogAgregarArticuloObtenerCodigo = dialogAgregarArticulo.findViewById<View>(R.id.dialogAgregarArticuloObtenerCodigo) as ImageButton


        dialogAgregarArticuloCantidad.setText("1")
        val dialogAgregarArticuloObtenerCodigo = dialogAgregarArticulo.findViewById<ImageButton>(R.id.dialogAgregarArticuloObtenerCodigo)
        dialogAgregarArticuloObtenerCodigo?.setOnClickListener {
            listener.abrirCamara()
        }

        //dialogArticulos.onAttach(contextTmp)
        dialogInstance = Dialog(this.contextTmp)
        dialogInstance.setCancelable(false)
        dialogInstance.setContentView(R.layout.dialog_articulos)

        dialogInstance.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogArticulosFamiliaSpinner = dialogInstance.findViewById(R.id.dialogArticulosFamiliaSpinner) as Spinner
        dialogArticulosSubFamiliaSpinner = dialogInstance.findViewById(R.id.dialogArticulosSubFamiliaSpinner) as Spinner
        dialogArticulosSalir = dialogInstance.findViewById(R.id.dialogArticulosSalir) as ImageButton
        mRecyclerViewArticulos = dialogInstance.findViewById(R.id.dialogoArticulosRecyclerView) as RecyclerView

        mViewArticulos = RecyclerViewArticulos()
        mRecyclerViewArticulos.layoutManager = GridLayoutManager(this.contextTmp,2)

    }

    fun showDialogArticulos(){
        mViewArticulos.RecyclerAdapter(listaTmpArticulos, contextTmp)
        mRecyclerViewArticulos.adapter = mViewArticulos

        if(listaFamiliaCompleta.size == 0)
            obtenerFamilias(contextTmp)

        mRecyclerViewArticulos.addOnItemTouchListener(RecyclerItemClickListener(contextTmp, mRecyclerViewArticulos, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                getArticuloObjecto(contextTmp, listaTmpArticulos[position].idArticulo)
                dialogInstance.dismiss()
            }

            override fun onLongItemClick(view: View?, position: Int) {}
        }))

        dialogArticulosSalir.setOnClickListener{
            dialogInstance.dismiss()
        }

        dialogAgregarArticuloCodigo.setText("")
        dialogAgregarArticuloCantidad.setText("1")
        dialogInstance.show()
    }

    fun showDialogAgregarArticulo() {

        dialogAgregarArticuloBuscarArticulo.setOnClickListener{
            showDialogArticulos()
        }

        dialogAgregarArticuloTitulo.text = contextTmp.getString(R.string.surtido_texto_titulo)

        dialogAgregarArticuloAceptar.setOnClickListener {


            if(dialogAgregarArticuloCodigo.text.isNotEmpty() && dialogAgregarArticuloCantidad.text.isNotEmpty()) {
                val codigoTmp = parseLong(dialogAgregarArticuloCodigo.text.toString())
                val cantidadTmp = Integer.parseInt(dialogAgregarArticuloCantidad.text.toString())

                dialogAgregarArticulo.dismiss()
                agregarArticulo(codigoTmp, cantidadTmp)
            }
        }

        dialogAgregarArticuloCancelar.setOnClickListener {dialogAgregarArticulo.dismiss()}
        dialogAgregarArticulo.show()

    }

    fun agregarArticulo(idArticulo : Long, cantidad : Int){
        val url = urls.url+urls.endPointArticulo+"?token="+globalVariable.token+"&idArticulo="+idArticulo

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val progressDialog = ProgressDialog(contextTmp)
        progressDialog.setMessage(contextTmp.getString(R.string.mensaje_espera))
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
                activityTmp.runOnUiThread {
                    listener.applyTexts(model)
                }
            }
        })
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

                val Model = gson.fromJson(body, Array<InventarioObjeto>::class.java).toList()
                activityTmp.runOnUiThread {
                    listaTmpArticulos = Model.toMutableList()
                    mViewArticulos.RecyclerAdapter(listaTmpArticulos, contextTmp)
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

                val Model = gson.fromJson(body, Array<InventarioObjeto>::class.java).toList()
                activityTmp.runOnUiThread {
                    listaTmpArticulos = Model.toMutableList()
                    mViewArticulos.RecyclerAdapter(listaTmpArticulos, contextTmp)
                    mViewArticulos.notifyDataSetChanged()
                }
            }
        })
    }

    fun obtenerFamilias(context: Context){

        val url = urls.url+urls.endPointConsultarFamiliasSubFamilias+"?token="+globalVariable.token

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
                        android.R.layout.simple_spinner_item, listaFamilia)
                    dialogArticulosFamiliaSpinner.adapter = adapter
                    dialogArticulosFamiliaSpinner.setSelection(0)

                    dialogArticulosFamiliaSpinner.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>,
                                                    view: View, position: Int, id: Long) {
                            obtenerSubFamilias(context,position)
                            obtenerArticulosFamilia()
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

        var adapter = ArrayAdapter(context,
            android.R.layout.simple_spinner_item, listaSubFamilia)
        dialogArticulosSubFamiliaSpinner.adapter = adapter

        adapter = ArrayAdapter(context,
            android.R.layout.simple_spinner_item, listaSubFamilia)

        dialogArticulosSubFamiliaSpinner.adapter = adapter

        if(listaSubFamilia.size > 0) {
            dialogArticulosSubFamiliaSpinner.setSelection(0)
        }

        dialogArticulosSubFamiliaSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if(listaSubFamilia.size > 0) {
                    subFamiliaId = listaSubFamiliaCompleta[position].subFamiliaId
                    obtenerArticulosSubFamilia()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    fun getArticuloObjecto(context : Context, idArticulo: Long){

        val url = urls.url+urls.endPointArticulo+"?token="+globalVariable.token+"&idArticulo="+idArticulo

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()

        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage(context.getString(R.string.mensaje_espera))
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                articuloTmp = InventarioObjeto(0,"","",0, 0.0, "", 0.0, "")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body()?.string()
                if (body != null && body.isNotEmpty()) {
                    val gson = GsonBuilder().create()

                    val articulo = gson.fromJson(body,InventarioObjeto::class.java)
                    articulo.cantidadArticulo = 1
                    //listener.applyTexts(articulo)
                    activityTmp.runOnUiThread {
                        dialogAgregarArticuloCodigo.setText(articulo.idArticulo.toString())
                    }
                }

                progressDialog.dismiss()
            }
        })

    }

    interface ExampleDialogListener {
        fun applyTexts(articulo : InventarioObjeto)
        fun abrirCamara()
    }

}
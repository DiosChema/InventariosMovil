@file:Suppress("DEPRECATION")

package com.Aegina.PocketSale.Dialogs

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
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
import java.lang.Long.parseLong


class DialogAgregarArticulos : AppCompatDialogFragment(){

    lateinit var contextTmp :Context
    val urls: Urls = Urls()
    lateinit var dialogAgregarArticulo : Dialog

    lateinit var agregarArticulo: DialogAgregarArticulo
    lateinit var activityTmp: Activity
    lateinit var globalVariable: GlobalClass

    lateinit var dialogAgregarArticuloCodigo : EditText
    lateinit var dialogAgregarArticuloCantidad :EditText
    lateinit var dialogAgregarArticuloAceptar :Button
    //lateinit var dialogAgregarArticuloMenuLateral :ImageButton
    lateinit var dialogAgregarArticuloCancelar :Button
    lateinit var dialogAgregarArticuloTitulo :TextView
    lateinit var dialogAgregarArticuloBuscarArticulo:ImageButton
    lateinit var dialogAgregarArticuloObtenerCodigo :ImageButton
    //lateinit var dialogArticulosCarrito :LinearLayout

    //lateinit var dialogArticulosSeparador:View

    lateinit var mViewArticulos : RecyclerViewArticulos
    lateinit var mRecyclerViewArticulos : RecyclerView

    lateinit var mViewListaArticulos : RecyclerViewListaArticulos
    lateinit var mRecyclerViewListaArticulos : RecyclerView

    var listaFamilia:MutableList<String> = ArrayList()
    var listaFamiliaCompleta:MutableList<FamiliasSubFamiliasObject> = ArrayList()
    var listaSubFamilia:MutableList<String> = ArrayList()
    var listaSubFamiliaCompleta:MutableList<SubFamiliaObjeto> = ArrayList()

    lateinit var dialogInstance : Dialog
    //lateinit var listener: DialogArticulos.ExampleDialogListenerArticulos

    lateinit var dialogArticulosFamiliaSpinner: Spinner
    lateinit var dialogArticulosSubFamiliaSpinner: Spinner
    lateinit var dialogArticulosSalir: ImageButton
    lateinit var dialogArticulosAceptar: ImageButton
    var subFamiliaId = -1
    var listaArticuloFamilias:MutableList<ArticuloInventarioObjeto> = ArrayList()
    var listaArticulosCarrito:MutableList<ArticuloInventarioObjeto> = ArrayList()
    lateinit var articuloTmp : ArticuloInventarioObjeto

    var tamañoMenuLateral = 0

    fun crearDialogArticulos(context: Context, globalVariableTmp: GlobalClass, activity : Activity){

        contextTmp = context
        activityTmp = activity
        globalVariable = globalVariableTmp

        agregarArticulo = contextTmp as DialogAgregarArticulo
        /*dialogAgregarArticulo = Dialog(contextTmp)

        dialogAgregarArticulo.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogAgregarArticulo.setCancelable(false)
        dialogAgregarArticulo.setContentView(R.layout.dialog_agregar_articulo)
        dialogAgregarArticuloCodigo = dialogAgregarArticulo.findViewById(R.id.dialogAgregarArticuloCodigo) as EditText
        dialogAgregarArticuloCantidad = dialogAgregarArticulo.findViewById(R.id.dialogAgregarArticuloCantidad) as EditText
        dialogAgregarArticuloAceptar = dialogAgregarArticulo.findViewById(R.id.dialogAgregarArticuloAceptar) as Button
        dialogAgregarArticuloCancelar = dialogAgregarArticulo.findViewById(R.id.dialogTerminarVentaCancelar) as Button
        dialogAgregarArticuloTitulo = dialogAgregarArticulo.findViewById(R.id.dialogTerminarVentaTitulo) as TextView
        dialogAgregarArticuloBuscarArticulo = dialogAgregarArticulo.findViewById<View>(R.id.dialogAgregarArticuloBuscarArticulo) as ImageButton
        dialogAgregarArticuloObtenerCodigo = dialogAgregarArticulo.findViewById<View>(R.id.dialogAgregarArticuloObtenerCodigo) as ImageButton


        dialogAgregarArticuloCantidad.setText("1")
        val dialogAgregarArticuloObtenerCodigo = dialogAgregarArticulo.findViewById<ImageButton>(R.id.dialogAgregarArticuloObtenerCodigo)
        dialogAgregarArticuloObtenerCodigo?.setOnClickListener {
            agregarArticulo.abrirCamara()
        }*/

        //dialogArticulos.onAttach(contextTmp)
        dialogInstance = Dialog(this.contextTmp)
        dialogInstance.setCancelable(false)
        dialogInstance.setContentView(R.layout.dialog_articulos)

        dialogInstance.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogArticulosFamiliaSpinner = dialogInstance.findViewById(R.id.dialogArticulosFamiliaSpinner) as Spinner
        dialogArticulosSubFamiliaSpinner = dialogInstance.findViewById(R.id.dialogArticulosSubFamiliaSpinner) as Spinner
        dialogArticulosSalir = dialogInstance.findViewById(R.id.dialogArticulosSalir) as ImageButton
        dialogArticulosAceptar = dialogInstance.findViewById(R.id.dialogArticulosAceptar) as ImageButton
        //dialogAgregarArticuloMenuLateral = dialogInstance.findViewById(R.id.dialogArticulosMenuLateral) as ImageButton
        mRecyclerViewArticulos = dialogInstance.findViewById(R.id.dialogoArticulosRecyclerView) as RecyclerView
        mRecyclerViewListaArticulos = dialogInstance.findViewById(R.id.dialogoArticulosRecyclerViewLista) as RecyclerView
        //dialogArticulosCarrito = dialogInstance.findViewById(R.id.dialogArticulosCarrito) as LinearLayout
        //dialogArticulosSeparador = dialogInstance.findViewById(R.id.dialogArticulosSeparador) as View

        mViewArticulos = RecyclerViewArticulos()
        mRecyclerViewArticulos.layoutManager = LinearLayoutManager(this.contextTmp)

        mViewListaArticulos = RecyclerViewListaArticulos()
        mRecyclerViewListaArticulos.layoutManager = LinearLayoutManager(this.contextTmp, LinearLayoutManager.HORIZONTAL ,false)




        mRecyclerViewArticulos.addOnItemTouchListener(RecyclerItemClickListener(contextTmp, mRecyclerViewArticulos, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int)
            {
                /*getArticuloObjecto(contextTmp, listaTmpArticulos[position].idArticulo)
                dialogInstance.dismiss()*/
                if(listaArticulosCarrito.size == 0)
                {
                    dialogInstance.dismiss()

                    var articuloTmp = listaArticuloFamilias[position]
                    articuloTmp.cantidadArticulo = 1
                    agregarArticulo.numeroArticulo(crearArticulos(articuloTmp))

                    //dialogAgregarArticulo.dismiss()
                }
                else
                {
                    var articuloTmp = listaArticuloFamilias[position]

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
                var articuloTmp = listaArticuloFamilias[position]

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

        dialogArticulosSalir.setOnClickListener()
        {
            dialogInstance.dismiss()
        }

        dialogArticulosAceptar.setOnClickListener()
        {
            agregarArticulo.agregarArticulos(crearListaArticulos(listaArticulosCarrito))
            dialogInstance.dismiss()
            //dialogAgregarArticulo.dismiss()
        }

        /*dialogAgregarArticuloMenuLateral.setOnClickListener()
        {
            moverCampo()
        }*/

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

    /*fun moverCampo() {
        /*val animation1 = TranslateAnimation(0.0f,
            mRecyclerViewListaArticulos.width.toFloat(),
            0.0f,
            0.0f)

        animation1.fillAfter = false
        animation1.duration = 350 // animation duration*/

        if(tamañoMenuLateral == 0)
        {
            tamañoMenuLateral = dialogArticulosSeparador.width
        }


        if(tamañoMenuLateral == dialogArticulosSeparador.width)
        {
            val animation1 = TranslateAnimation(0.0f,
                mRecyclerViewListaArticulos.width.toFloat() * -1,
                0.0f,
                0.0f)

            animation1.fillAfter = false
            animation1.duration = 350 // animation duration

            animation1.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationRepeat(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {

                    var layoutParams = dialogArticulosSeparador.layoutParams
                    layoutParams.width = layoutParams.width -  mRecyclerViewListaArticulos.width
                    dialogArticulosSeparador.layoutParams = layoutParams
                    /*val params =
                        view!!.layoutParams as FrameLayout.LayoutParams
                    params.topMargin += amountToMoveDown
                    params.leftMargin += amountToMoveRight
                    view!!.layoutParams = params*/
                }
            })


            dialogArticulosCarrito.startAnimation(animation1)
        }
        else
        {
            val animation1 = TranslateAnimation(0.0f,
                mRecyclerViewListaArticulos.width.toFloat(),
                0.0f,
                0.0f)

            animation1.fillAfter = false
            animation1.duration = 350 // animation duration

            animation1.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationRepeat(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {

                    var layoutParams = dialogArticulosSeparador.layoutParams
                    layoutParams.width = tamañoMenuLateral
                    dialogArticulosSeparador.layoutParams = layoutParams
                    /*val params =
                        view!!.layoutParams as FrameLayout.LayoutParams
                    params.topMargin += amountToMoveDown
                    params.leftMargin += amountToMoveRight
                    view!!.layoutParams = params*/
                }
            })


            dialogArticulosCarrito.startAnimation(animation1)
        }


    }*/

    fun showDialogArticulos(){
        mViewArticulos.RecyclerAdapter(listaArticuloFamilias, contextTmp)
        mRecyclerViewArticulos.adapter = mViewArticulos

        mViewListaArticulos.RecyclerAdapter(listaArticulosCarrito, contextTmp)
        mRecyclerViewListaArticulos.adapter = mViewListaArticulos

        if(listaFamiliaCompleta.size == 0)
            obtenerFamilias(contextTmp)


        /*val animation1 = TranslateAnimation(0.0f,
            dialogArticulosCarrito.width.toFloat(),
            0.0f,
            0.0f)

        animation1.duration = 0 // animation duration
        dialogArticulosCarrito.startAnimation(animation1)*/

        //moverCampo(dialogArticulosCarrito, 1)
        /*dialogAgregarArticuloCodigo.setText("")
        dialogAgregarArticuloCantidad.setText("1")
        dialogInstance.show()*/

        dialogInstance.show()
    }

    fun showDialogAgregarArticulo() {

        listaArticulosCarrito.clear()

        /*dialogAgregarArticuloBuscarArticulo.setOnClickListener{
            showDialogArticulos()
        }*/

        //dialogAgregarArticuloTitulo.text = contextTmp.getString(R.string.surtido_texto_titulo)

        /*dialogAgregarArticuloAceptar.setOnClickListener {


            if(dialogAgregarArticuloCodigo.text.isNotEmpty() && dialogAgregarArticuloCantidad.text.isNotEmpty()) {
                val codigoTmp = parseLong(dialogAgregarArticuloCodigo.text.toString())
                val cantidadTmp = Integer.parseInt(dialogAgregarArticuloCantidad.text.toString())

                agregarArticulo(codigoTmp, cantidadTmp)
            }
        }*/

        //dialogAgregarArticuloCancelar.setOnClickListener {dialogAgregarArticulo.dismiss()}
        showDialogArticulos()

    }

    fun agregarArticulo(idArticulo : Long, cantidad : Int){
        val url = urls.url+urls.endPointsInventario.endPointArticulo+"?token="+globalVariable.usuario!!.token+"&idArticulo="+idArticulo

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
                val model = gson.fromJson(body, ArticuloInventarioObjeto::class.java)

                if(model != null) {
                    model.cantidadArticulo = cantidad
                    activityTmp.runOnUiThread {
                        agregarArticulo.numeroArticulo(crearArticulos(model))
                        //dialogAgregarArticulo.dismiss()
                    }
                }else{
                    activityTmp.runOnUiThread {
                        val animation1 = TranslateAnimation(0.0f,
                            dialogAgregarArticuloCodigo.width.toFloat()/15,
                            0.0f,
                            0.0f)

                        animation1.duration = 150 // animation duration
                        animation1.repeatCount = 2
                        animation1.repeatMode = 2
                        dialogAgregarArticuloCodigo.startAnimation(animation1)
                    }
                }
            }
        })
    }

    fun obtenerArticulosFamilia(){
        val url = urls.url+urls.endPointsInventario.endPointArticulosPorFamilia+"?familiaId=" + listaFamiliaCompleta[dialogArticulosFamiliaSpinner.selectedItemPosition].familiaId + "&token="+globalVariable.usuario!!.token

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

                val Model = gson.fromJson(body, Array<ArticuloInventarioObjeto>::class.java).toList()
                activityTmp.runOnUiThread {
                    listaArticuloFamilias = Model.toMutableList()
                    mViewArticulos.RecyclerAdapter(listaArticuloFamilias, contextTmp)
                    mViewArticulos.notifyDataSetChanged()
                }
            }
        })
    }

    fun obtenerArticulosSubFamilia(){
        val url = urls.url+urls.endPointsInventario.endPointArticulosPorSubFamilia+"?subFamiliaId=" + listaSubFamiliaCompleta[dialogArticulosSubFamiliaSpinner.selectedItemPosition].subFamiliaId + "&token="+globalVariable.usuario!!.token

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

                val Model = gson.fromJson(body, Array<ArticuloInventarioObjeto>::class.java).toList()
                activityTmp.runOnUiThread {
                    listaArticuloFamilias = Model.toMutableList()
                    mViewArticulos.RecyclerAdapter(listaArticuloFamilias, contextTmp)
                    mViewArticulos.notifyDataSetChanged()
                }
            }
        })
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
                    obtenerArticulosSubFamilia()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
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
                    "")
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
            "")
    }

    fun getArticuloObjecto(context : Context, idArticulo: Long){

        val url = urls.url+urls.endPointsInventario.endPointArticulo+"?token="+globalVariable.usuario!!.token+"&idArticulo="+idArticulo

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
                articuloTmp = ArticuloInventarioObjeto(0,"","",0, 0.0, "", 0.0, "")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body()?.string()
                if (body != null && body.isNotEmpty()) {
                    val gson = GsonBuilder().create()

                    val articulo = gson.fromJson(body,ArticuloInventarioObjeto::class.java)
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

    interface DialogAgregarArticulo {
        fun numeroArticulo(articulo : InventarioObjeto)
        fun agregarArticulos(articulosCarrito : MutableList<InventarioObjeto>)
        fun abrirCamara()
    }

}
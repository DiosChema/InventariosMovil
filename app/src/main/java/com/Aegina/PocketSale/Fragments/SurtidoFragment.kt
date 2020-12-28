package com.Aegina.PocketSale.Fragments

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Dialogs.DialogAgregarArticulos
import com.Aegina.PocketSale.Dialogs.DialogAgregarNumero
import com.Aegina.PocketSale.Dialogs.DialogFecha
import com.Aegina.PocketSale.Objets.ActualizarInventarioObject
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.InventarioObjeto
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.Aegina.PocketSale.RecyclerView.RecyclerViewSurtido
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_surtido.*
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SurtidoFragment : Fragment() {

    lateinit var mViewEstadisticaArticulo : RecyclerViewSurtido
    lateinit var mRecyclerView : RecyclerView
    lateinit var globalVariable: GlobalClass

    val urls = Urls()

    var dialogFecha = DialogFecha()
    val context = activity

    lateinit var txtViewSurtidoTotalArticulos : TextView
    lateinit var txtViewSurtidoTotalVenta : TextView

    var dialogoAgregarArticulos = DialogAgregarArticulos()
    var listaArticulos: MutableList<InventarioObjeto> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_surtido, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialogFecha.crearVentana(activity!!)
        globalVariable = activity?.applicationContext as GlobalClass

        dialogoAgregarArticulos.crearDialogInicial(activity!!,globalVariable, Activity())
        dialogoAgregarArticulos.crearDialogArticulos()
        dialogoAgregarArticulos.crearDialogFiltros()

        asignarRecursos()
        crearRecyclerView()
    }

    fun asignarRecursos(){

        txtViewSurtidoTotalArticulos = surtidoTotalArticulos
        txtViewSurtidoTotalVenta = surtidoTotalVenta

        val imgButtonSurtidoConfirmarArticulos = surtidoConfirmarArticulos
        imgButtonSurtidoConfirmarArticulos.setOnClickListener{
            actualizarInventario()
        }

        val imgButtonSurtidoAgregarCodigo = surtidoAgregarArticulo
        imgButtonSurtidoAgregarCodigo.setOnClickListener{
            dialogoAgregarArticulos.mostrarDialogArticulos()
        }

        actualizarCantidadCosto()
    }

    fun actualizarCantidadCosto(){
        var totalCantidad = 0
        var totalCosto = 0.0


        for(articulos in listaArticulos){
            totalCantidad += articulos.cantidadArticulo
            totalCosto += (articulos.costoArticulo * articulos.cantidadArticulo)
        }

        txtViewSurtidoTotalArticulos.text = totalCantidad.toString()

        val textoSurtidoVenta = "$$totalCosto"
        txtViewSurtidoTotalVenta.text = textoSurtidoVenta
    }

    fun crearRecyclerView(){
        mViewEstadisticaArticulo = RecyclerViewSurtido()
        mRecyclerView = surtidoRecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mViewEstadisticaArticulo.RecyclerAdapter(listaArticulos, activity!!)
        mRecyclerView.adapter = mViewEstadisticaArticulo

        val dialogAgregarNumero = DialogAgregarNumero()

        /*mRecyclerView.addOnItemTouchListener(RecyclerItemClickListener(activity, mRecyclerView, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                dialogAgregarNumero.crearDialog(activity!!, position)
                //eventoClick(position)
            }

            override fun onLongItemClick(view: View?, position: Int) {}
        }))*/
    }

    fun actualizarInventario(){
        val url = urls.url+urls.endPointSurtidos.endPointActualizarInventario

        val listaArticulosTmp : MutableList<InventarioObjeto> = ArrayList()

        for (articuloTmp in listaArticulos) {
            if(articuloTmp.cantidadArticulo > 0)
                listaArticulosTmp.add(articuloTmp)
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val currentDate = sdf.format(Date())

        val venta = ActualizarInventarioObject(globalVariable.usuario!!.token, currentDate.toString(), listaArticulosTmp)

        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val jsonVenta: String = gsonPretty.toJson(venta)

        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonVenta)

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val progressDialog = ProgressDialog(activity)
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
                activity?.runOnUiThread {
                    listaArticulos.clear()
                    mViewEstadisticaArticulo.notifyDataSetChanged()
                }
            }
        })
    }

    fun comprobarArticuloEnVenta(articuloTmp: InventarioObjeto): Boolean
    {
        for (i in 0 until listaArticulos.size)
        {
            if(articuloTmp.idArticulo == listaArticulos[i].idArticulo)
            {
                listaArticulos[i].cantidadArticulo += articuloTmp.cantidadArticulo
                return true
            }
        }

        return false
    }

    fun numeroArticulo(articuloCarrito : InventarioObjeto) {
        activity?.runOnUiThread{
            if(!comprobarArticuloEnVenta(articuloCarrito))
            {
                listaArticulos.add(articuloCarrito)
            }

            mViewEstadisticaArticulo.notifyDataSetChanged()
            actualizarCantidadCosto()
        }
        /*var articuloNuevo = true

        for (i in 0 until listaArticulos.size)
        {
            if(listaArticulos[i].idArticulo == articulo.idArticulo)
            {
                listaArticulos[i].cantidadArticulo++
                articuloNuevo = false
            }
        }

        if(articuloNuevo)
        {
            listaArticulos.add(articulo)
        }

        activity?.runOnUiThread{
            mViewEstadisticaArticulo.notifyDataSetChanged()
            actualizarCantidadCosto()
        }*/
    }

    fun obtenerNumero(numero : Int, posicion : Int) {
        activity?.runOnUiThread{
            listaArticulos[posicion].cantidadArticulo = numero
            mViewEstadisticaArticulo.notifyDataSetChanged()
            actualizarCantidadCosto()
        }
    }

    fun agregarArticulos(articulosCarrito: MutableList<InventarioObjeto>) {
        activity?.runOnUiThread()
        {
            for(i in 0 until articulosCarrito.size)
            {
                if(!comprobarArticuloEnVenta(articulosCarrito[i]))
                {
                    listaArticulos.add(articulosCarrito[i])
                }
            }

            //listaArticulosVenta.addAll(articulosCarrito)

            mViewEstadisticaArticulo.notifyDataSetChanged()
            actualizarCantidadCosto()
        }

        /*activity?.runOnUiThread{
            listaArticulos.addAll(articulos)
            mViewEstadisticaArticulo.notifyDataSetChanged()
            actualizarCantidadCosto()
        }*/
    }

    /*override fun abrirCamara() {
        val intentIntegrator = IntentIntegrator(context as Surtido)
        intentIntegrator.setBeepEnabled(false)
        intentIntegrator.setCameraId(0)
        intentIntegrator.setPrompt("SCAN")
        intentIntegrator.setBarcodeImageEnabled(false)
        intentIntegrator.initiateScan()
    }*/

    /*override fun onActivityResult(requestCode: Int,resultCode: Int,data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                runOnUiThread {
                    dialogoAgregarArticulos.dialogAgregarArticuloCodigo.setText(java.lang.Long.parseLong(result.contents).toString())
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }*/

    fun buscarArticulo(codigo : Long){
        dialogoAgregarArticulos.buscarArticulo(codigo)
    }

}
package com.Aegina.PocketSale.Fragments

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Dialogs.DialogAgregarArticulos
import com.Aegina.PocketSale.Dialogs.DialogAgregarNumero
import com.Aegina.PocketSale.Dialogs.DialogFecha
import com.Aegina.PocketSale.Dialogs.DialogModificarArticulo
import com.Aegina.PocketSale.Metodos.Errores
import com.Aegina.PocketSale.Objets.*
import com.Aegina.PocketSale.R
import com.Aegina.PocketSale.RecyclerView.RecyclerItemClickListener
import com.Aegina.PocketSale.RecyclerView.RecyclerViewSurtido
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_surtido.*
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class LossFragment : Fragment() {

    lateinit var mViewArticulo : RecyclerViewSurtido
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
        dialogoAgregarArticulos.crearDialogArticulos(1)
        dialogoAgregarArticulos.crearDialogFiltros()

        asignarRecursos()
        crearRecyclerView()
    }

    fun asignarRecursos(){

        txtViewSurtidoTotalArticulos = surtidoTotalArticulos
        txtViewSurtidoTotalVenta = surtidoTotalVenta

        val imgButtonSurtidoConfirmarArticulos = surtidoConfirmarArticulos
        imgButtonSurtidoConfirmarArticulos.setOnClickListener{
            if(globalVariable.usuario!!.permisosPerdidas)
            {
                if(listaArticulos.size > 0)
                {
                    actualizarInventario()
                }
            }
            else
            {
                activity?.runOnUiThread()
                {
                    Toast.makeText(activity,getString(R.string.permisos_denegado),Toast.LENGTH_SHORT).show()
                }
            }
        }

        val imgButtonSurtidoAgregarCodigo = surtidoAgregarArticulo
        imgButtonSurtidoAgregarCodigo.setOnClickListener{
            if(globalVariable.usuario!!.permisosPerdidas)
            {
                dialogoAgregarArticulos.mostrarDialogArticulos()
            }
            else
            {
                activity?.runOnUiThread()
                {
                    Toast.makeText(activity,getString(R.string.permisos_denegado),Toast.LENGTH_SHORT).show()
                }
            }
        }

        actualizarCantidadCosto()
    }

    fun actualizarCantidadCosto(){
        var totalCantidad = 0
        var totalCosto = 0.0


        for(articulos in listaArticulos){
            totalCantidad += articulos.cantidad
            totalCosto += (articulos.costo * articulos.cantidad)
        }

        txtViewSurtidoTotalArticulos.text = totalCantidad.toString()

        val textoSurtidoVenta = "$$totalCosto"
        txtViewSurtidoTotalVenta.text = textoSurtidoVenta
    }

    fun crearRecyclerView(){
        mViewArticulo = RecyclerViewSurtido()
        mRecyclerView = surtidoRecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mViewArticulo.RecyclerAdapter(listaArticulos, activity!!)
        mRecyclerView.adapter = mViewArticulo

        val dialogAgregarNumero = DialogAgregarNumero()
        val dialogModificarArticulo = DialogModificarArticulo()

        mRecyclerView.addOnItemTouchListener(RecyclerItemClickListener(activity, mRecyclerView, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int)
            {
                dialogAgregarNumero.crearDialogNumero(activity!!, position)
            }

            override fun onLongItemClick(view: View?, position: Int)
            {
                dialogModificarArticulo.crearDialogModificarArticulo(activity!!, position, listaArticulos[position].precio, listaArticulos[position].costo, 1)
            }
        }))
    }

    fun actualizarInventario(){
        val url = urls.url+urls.endPointLoss.endPointLossItems

        val articulos: MutableList<ArticuloObjeto> = ArrayList()

        for (articuloTmp in listaArticulos) {

            val articulo = ArticuloObjeto(
                articuloTmp.idArticulo,
                articuloTmp.cantidad,
                articuloTmp.precio,
                articuloTmp.nombre,
                articuloTmp.costo,
                articuloTmp.modificaInventario)

            if(articulo.cantidad > 0)
                articulos.add(articulo)

        }

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val currentDate = sdf.format(Date())

        val venta = VentaObjeto(globalVariable.usuario!!.token, currentDate.toString(), articulos)

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
        progressDialog.setCancelable(false)
        progressDialog.show()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                activity?.runOnUiThread()
                {
                    Toast.makeText(context, context!!.getString(R.string.mensaje_error_intentear_mas_tarde), Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response)
            {
                var body = response.body()?.string()
                if(body != null && body.isNotEmpty())
                {
                    try
                    {
                        val gson = GsonBuilder().create()
                        val respuesta = gson.fromJson(body, Respuesta::class.java)
                        if(respuesta.status == 0)
                        {
                            globalVariable.actualizarVentana!!.updateLoss = true
                            activity?.runOnUiThread()
                            {
                                listaArticulos.clear()
                                mViewArticulo.notifyDataSetChanged()
                                actualizarCantidadCosto()
                            }
                        }
                        else
                        {

                            val errores = Errores()
                            errores.procesarError(activity!!.applicationContext,body,activity!!)
                        }
                    }
                    catch(e:Exception){}

                }
                progressDialog.dismiss()
            }
        })
    }

    fun comprobarArticuloEnVenta(articuloTmp: InventarioObjeto): Boolean
    {
        for (i in 0 until listaArticulos.size)
        {
            if(articuloTmp.idArticulo == listaArticulos[i].idArticulo)
            {
                listaArticulos[i].cantidad += articuloTmp.cantidad
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

            mViewArticulo.notifyDataSetChanged()
            actualizarCantidadCosto()
        }
    }

    fun obtenerNumero(numero : Int, posicion : Int) {
        activity?.runOnUiThread{
            listaArticulos[posicion].cantidad = numero
            mViewArticulo.notifyDataSetChanged()
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
            mViewArticulo.notifyDataSetChanged()
            actualizarCantidadCosto()
        }
    }

    fun buscarArticulo(codigo : Long){
        dialogoAgregarArticulos.buscarArticulo(codigo)
    }

    fun cambiarPrecioCosto(precio: Double, costo: Double, posicion: Int) {
        activity?.runOnUiThread{
            listaArticulos[posicion].precio = precio
            listaArticulos[posicion].costo = costo
            mViewArticulo.notifyDataSetChanged()
            actualizarCantidadCosto()
        }
    }

}
@file:Suppress("DEPRECATION")

package com.Aegina.PocketSale.Activities

import android.app.Dialog
import android.app.ProgressDialog
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Dialogs.DialogAgregarNumero
import com.Aegina.PocketSale.Objets.*
import com.Aegina.PocketSale.R
import com.Aegina.PocketSale.RecyclerView.RecyclerItemClickListener
import com.Aegina.PocketSale.RecyclerView.RecyclerViewVenta
import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class VentaDetalle : AppCompatActivity(),
    DialogAgregarNumero.DialogAgregarNumero {

    lateinit var venta: VentasObjeto
    lateinit var mViewArticulosVenta : RecyclerViewVenta
    lateinit var mRecyclerView : RecyclerView

    lateinit var VentaDetalleNumero : TextView
    lateinit var VentaDetalleFecha : TextView
    lateinit var VentaDetalleEliminarVenta : ImageButton
    lateinit var VentaDetalleCancelar : Button
    lateinit var VentaDetalleEditar : Button
    var editar = false

    val dialogAgregarNumero = DialogAgregarNumero()

    lateinit var globalVariable: GlobalClass

    lateinit var listaArticulos : MutableList<InventarioObjeto>

    var context = this

    val urls: Urls = Urls()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venta_detalle)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        globalVariable = applicationContext as GlobalClass
        val simpleDate = SimpleDateFormat("dd/MM/yyyy")
        val simpleDateHours = SimpleDateFormat("HH:mm:ss")

        context = this
        venta = intent.getSerializableExtra("venta") as VentasObjeto

        VentaDetalleNumero = findViewById(R.id.VentaDetalleNumero)
        VentaDetalleFecha = findViewById(R.id.VentaDetalleFecha)
        VentaDetalleEliminarVenta = findViewById(R.id.VentaDetalleEliminarVenta)
        VentaDetalleCancelar = findViewById(R.id.VentaDetalleCancelar)
        VentaDetalleEditar = findViewById(R.id.VentaDetalleEditar)

        mRecyclerView = findViewById(R.id.ventasFragmentRecyclerViewArticulos)

        var textTmp = getString(R.string.mensaje_numero_venta) + System.getProperty ("line.separator") +  venta._id
        VentaDetalleNumero.text = textTmp
        textTmp = simpleDate.format(venta.fecha) + System.getProperty ("line.separator") + simpleDateHours.format(venta.fecha)
        VentaDetalleFecha.text = textTmp

        VentaDetalleEliminarVenta.visibility = View.INVISIBLE
        VentaDetalleCancelar.visibility = View.INVISIBLE
        editar = false

        mViewArticulosVenta = RecyclerViewVenta()
        mRecyclerView = findViewById(R.id.ventasFragmentRecyclerViewArticulos)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)

        listaArticulos = listaInventarioObjeto(venta)
        mViewArticulosVenta.RecyclerAdapter(listaArticulos, context)
        mRecyclerView.adapter = mViewArticulosVenta

        VentaDetalleEliminarVenta.setOnClickListener{
            showDialogEliminarVenta()
        }

        VentaDetalleEditar.setOnClickListener{
            if(!editar) {
                VentaDetalleEditar.text = getString(R.string.mensaje_actualizar_articulo)
                VentaDetalleEliminarVenta.visibility = View.VISIBLE
                VentaDetalleCancelar.visibility = View.VISIBLE
                editar = true
                habilitarEdicion()

                mRecyclerView.addOnItemTouchListener(RecyclerItemClickListener(context, mRecyclerView, object :
                    RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        if(editar) dialogAgregarNumero.crearDialog(context, position)
                    }

                    override fun onLongItemClick(view: View?, position: Int) {}
                }))
            }
            else
            {
                showDialogActualizarVenta()
            }
        }

        /*VentaDetalleConfirmar.setOnClickListener{
            showDialogActualizarVenta()
        }*/

        VentaDetalleCancelar.setOnClickListener{
            VentaDetalleEditar.text = getString(R.string.mensaje_editar)
            VentaDetalleEliminarVenta.visibility = View.INVISIBLE
            VentaDetalleCancelar.visibility = View.INVISIBLE
            editar = false
            habilitarEdicion()
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

    }

    fun listaInventarioObjeto(venta: VentasObjeto): MutableList<InventarioObjeto> {

        val listatmp : MutableList<InventarioObjeto> = arrayListOf()

        for(articulos in venta.articulos){
            listatmp.add(
                InventarioObjeto(articulos.idArticulo,
                    articulos.articulosDetalle[0].nombreArticulo,
                    articulos.articulosDetalle[0].descripcionArticulo,
                    articulos.cantidad,
                    articulos.precio,
                    articulos.articulosDetalle[0].familiaArticulo,
                    articulos.costo,
                    ""
                )
            )
        }

        return listatmp
    }

    private fun eliminarVenta(){
        val url = urls.url+urls.endPointVentas.endPointEliminarVenta

        val jsonObject = JSONObject()
        try {
            jsonObject.put("token", globalVariable.usuario!!.token)
            jsonObject.put("idVenta", venta._id)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonObject.toString())

        val request = Request.Builder()
            .url(url)
            .delete(body)
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
                progressDialog.dismiss()
                runOnUiThread {
                    Toast.makeText(context, getString(R.string.mensaje_eliminacion_exitosa), Toast.LENGTH_SHORT).show()
                }
                finish()
            }
        })
    }

    private fun actualizarVenta(){
        val url = urls.url+urls.endPointVentas.endPointActualizarVenta

        val listaArticulosTmp: MutableList<ActualizarArticuloObjeto> = ArrayList()

        for (articulo in listaArticulos) {
            if ( articulo.cantidadArticulo > 0) {
                listaArticulosTmp.add(ActualizarArticuloObjeto(
                    venta._id,
                    articulo.idArticulo,
                    articulo.cantidadArticulo,
                    articulo.precioArticulo,
                    articulo.nombreArticulo,
                    articulo.costoArticulo
                ))
            }
        }

        val ventaActualizada = ActualizarVenta(globalVariable.usuario!!.token,venta._id,listaArticulosTmp)

        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val jsonVenta: String = gsonPretty.toJson(ventaActualizada)

        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonVenta)

        val request = Request.Builder()
            .url(url)
            .put(body)
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
                    Toast.makeText(context, getString(R.string.mensaje_actualizacion_exitosa), Toast.LENGTH_SHORT).show()
                    finish()
                }

            }
        })

    }

    private fun showDialogEliminarVenta() {
        val dialog = Dialog(this)
        dialog.setTitle(title)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_text)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogText = dialog.findViewById(R.id.dialogText) as EditText
        val dialogAceptar = dialog.findViewById(R.id.dialogAceptar) as Button
        val dialogCancelar = dialog.findViewById(R.id.dialogCancelar) as Button
        val dialogTitulo = dialog.findViewById(R.id.dialogTitulo) as TextView

        dialogTitulo.text = getString(R.string.dialog_eliminar_articulo)
        dialogText.visibility = View.INVISIBLE

        dialogAceptar.setOnClickListener {
            dialog.dismiss()
            eliminarVenta()
        }

        dialogCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDialogActualizarVenta() {
        val dialog = Dialog(this)
        dialog.setTitle(title)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_text)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogText = dialog.findViewById(R.id.dialogText) as EditText
        val dialogAceptar = dialog.findViewById(R.id.dialogAceptar) as Button
        val dialogCancelar = dialog.findViewById(R.id.dialogCancelar) as Button
        val dialogTitulo = dialog.findViewById(R.id.dialogTitulo) as TextView

        dialogTitulo.text = getString(R.string.dialog_confirmar_actualizacion_venta)
        dialogText.visibility = View.INVISIBLE

        dialogAceptar.setOnClickListener {
            dialog.dismiss()
            actualizarVenta()
        }

        dialogCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun habilitarEdicion() {
        listaArticulos = listaInventarioObjeto(venta)
        mViewArticulosVenta.RecyclerAdapter(listaArticulos, context)
        mRecyclerView.adapter = mViewArticulosVenta
    }

    override fun obtenerNumero(numero : Int, posicion : Int) {
        runOnUiThread{
            listaArticulos[posicion].cantidadArticulo = numero
            mViewArticulosVenta.RecyclerAdapter(listaArticulos, context)
            mRecyclerView.adapter = mViewArticulosVenta
        }
    }

}
package com.Aegina.PocketSale.Activities

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Dialogs.DialogAgregarNumero
import com.Aegina.PocketSale.Metodos.Errores
import com.Aegina.PocketSale.Objets.*
import com.Aegina.PocketSale.R
import com.Aegina.PocketSale.RecyclerView.RecyclerItemClickListener
import com.Aegina.PocketSale.RecyclerView.RecyclerViewVenta
import com.google.gson.GsonBuilder
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat

class SurtidoDetalle : AppCompatActivity(),
    DialogAgregarNumero.DialogAgregarNumero {

    lateinit var surtido: VentasObjeto
    lateinit var mViewArticulosSurtido : RecyclerViewVenta
    lateinit var mRecyclerView : RecyclerView

    lateinit var VentaDetalleNumero : TextView
    lateinit var VentaDetalleFecha : TextView
    lateinit var VentaDetalleTotalArticulos : TextView
    lateinit var VentaDetalleTotalVenta : TextView
    lateinit var ventaDetalleTitulo : TextView
    lateinit var VentaDetalleEliminarVenta : CircleImageView
    lateinit var VentaDetalleCancelar : ImageButton
    lateinit var VentaDetalleEditar : ImageButton
    lateinit var ventaDetalleBack : ImageButton
    lateinit var VentaDetalleConfirmar : Button

    val dialogAgregarNumero = DialogAgregarNumero()

    lateinit var globalVariable: GlobalClass

    lateinit var listaArticulos : MutableList<InventarioObjeto>

    lateinit var context : Context
    lateinit var activity: Activity

    val urls: Urls = Urls()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venta_detalle)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        globalVariable = applicationContext as GlobalClass
        val simpleDate = SimpleDateFormat("dd/MM/yyyy")
        val simpleDateHours = SimpleDateFormat("HH:mm:ss")

        context = this
        activity = this
        surtido = intent.getSerializableExtra("surtido") as VentasObjeto

        VentaDetalleNumero = findViewById(R.id.VentaDetalleNumero)
        VentaDetalleFecha = findViewById(R.id.VentaDetalleFecha)
        VentaDetalleEliminarVenta = findViewById(R.id.VentaDetalleEliminarVenta)
        VentaDetalleCancelar = findViewById(R.id.VentaDetalleCancelar)
        VentaDetalleEditar = findViewById(R.id.VentaDetalleEditar)
        VentaDetalleTotalArticulos = findViewById(R.id.VentaDetalleTotalArticulos)
        VentaDetalleTotalVenta = findViewById(R.id.VentaDetalleTotalVenta)
        ventaDetalleTitulo = findViewById(R.id.ventaDetalleTitulo)
        VentaDetalleConfirmar = findViewById(R.id.VentaDetalleConfirmar)
        ventaDetalleBack = findViewById(R.id.ventaDetalleBack)

        mRecyclerView = findViewById(R.id.ventasFragmentRecyclerViewArticulos)

        var textTmp = getString(R.string.surtido_detalle_titulo) + System.getProperty ("line.separator") +  surtido._id
        VentaDetalleNumero.text = textTmp
        textTmp = simpleDate.format(surtido.fecha) /*+ System.getProperty ("line.separator")*/ + " " + simpleDateHours.format(surtido.fecha)
        VentaDetalleFecha.text = textTmp

        VentaDetalleEliminarVenta.visibility = View.INVISIBLE
        VentaDetalleCancelar.visibility = View.INVISIBLE
        VentaDetalleConfirmar.visibility = View.INVISIBLE

        ventaDetalleTitulo.text = getString(R.string.venta_detalle_assortment)

        mViewArticulosSurtido = RecyclerViewVenta()
        mRecyclerView = findViewById(R.id.ventasFragmentRecyclerViewArticulos)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)

        listaArticulos = listaInventarioObjeto(surtido)
        mViewArticulosSurtido.RecyclerAdapter(listaArticulos, context)
        mRecyclerView.adapter = mViewArticulosSurtido

        VentaDetalleEliminarVenta.setOnClickListener{
            showDialogEliminarSurtido()
        }

        VentaDetalleEditar.setOnClickListener{
            VentaDetalleEliminarVenta.visibility = View.VISIBLE
            VentaDetalleCancelar.visibility = View.VISIBLE
            VentaDetalleConfirmar.visibility = View.VISIBLE
            habilitarEdicion()

            mRecyclerView.addOnItemTouchListener(RecyclerItemClickListener(context, mRecyclerView, object :
                RecyclerItemClickListener.OnItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    if(VentaDetalleConfirmar.isVisible)
                    {
                        dialogAgregarNumero.crearDialogNumero(context, position, listaArticulos[position].cantidad)
                    }
                }

                override fun onLongItemClick(view: View?, position: Int) {}
            }))
        }

        VentaDetalleConfirmar.setOnClickListener()
        {
            showDialogActualizarSurtido()
        }

        /*VentaDetalleConfirmar.setOnClickListener{
            showDialogActualizarVenta()
        }*/

        ventaDetalleBack.setOnClickListener()
        {
            finish()
        }

        VentaDetalleCancelar.setOnClickListener{
            VentaDetalleEliminarVenta.visibility = View.INVISIBLE
            VentaDetalleCancelar.visibility = View.INVISIBLE
            VentaDetalleConfirmar.visibility = View.INVISIBLE
            habilitarEdicion()
            actualizarCantidadPrecio()
        }

        actualizarCantidadPrecio()
    }

    fun listaInventarioObjeto(surtido: VentasObjeto): MutableList<InventarioObjeto> {

        val listatmp : MutableList<InventarioObjeto> = arrayListOf()

        for(articulos in surtido.articulos){
            if(articulos.articulosDetalle.isNotEmpty())
            {
                listatmp.add(
                    InventarioObjeto(articulos.idArticulo,
                        articulos.articulosDetalle[0].nombre,
                        articulos.articulosDetalle[0].descripcion,
                        articulos.cantidad,
                        articulos.precio,
                        articulos.articulosDetalle[0].familia,
                        articulos.costo,
                        articulos.articulosDetalle[0].inventarioOptimo,
                        articulos.articulosDetalle[0].modificaInventario
                    )
                )
            }
            else
            {
                listatmp.add(
                    InventarioObjeto(articulos.idArticulo,
                        articulos.nombre,
                        "",
                        articulos.cantidad,
                        articulos.precio,
                        "0",
                        articulos.costo,
                        0,
                        false)
                )
            }
        }

        return listatmp
    }

    private fun eliminarSurtido(){
        val url = urls.url+urls.endPointSurtidos.endPointEliminarSurtido

        val jsonObject = JSONObject()
        try {
            jsonObject.put("token", globalVariable.usuario!!.token)
            jsonObject.put("idSurtido", surtido._id)
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
        progressDialog.setCancelable(false)
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                runOnUiThread()
                {
                    Toast.makeText(context, context.getString(R.string.mensaje_error_intentear_mas_tarde), Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response)
            {
                val body = response.body()!!.string()

                if(body != null && body.isNotEmpty())
                {
                    try
                    {
                        val gson = GsonBuilder().create()
                        val respuesta = gson.fromJson(body, Respuesta::class.java)

                        if(respuesta.status == 0)
                        {
                            globalVariable.actualizarVentana!!.actualizarSurtido = true
                            runOnUiThread()
                            {
                                Toast.makeText(context, getString(R.string.supply_details_delete_successful), Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                        else
                        {
                            val errores = Errores()
                            errores.procesarError(context,body,activity)
                        }

                    }
                    catch(e:Exception){}
                }
                progressDialog.dismiss()

            }
        })
    }

    private fun actualizarSurtido(){
        val url = urls.url+urls.endPointSurtidos.endPointActualizarSurtido

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val currentDate = sdf.format(surtido.fecha)

        var listaArticulosTmp = mutableListOf<InventarioObjeto>()

        for(i in 0 until listaArticulos.size )
        {
            if(listaArticulos[i].cantidad > 0)
            {
                listaArticulosTmp.add(listaArticulos[i])
            }
        }

        if(listaArticulosTmp.size == 0)
        {
            eliminarSurtido()
            return
        }

        val surtidoActualizada = ActualizarSurtido(globalVariable.usuario!!.token,surtido._id,currentDate,listaArticulosTmp)

        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val jsonSurtido: String = gsonPretty.toJson(surtidoActualizada)

        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonSurtido)

        val request = Request.Builder()
            .url(url)
            .put(body)
            .build()

        val client = OkHttpClient()
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.mensaje_espera))
        progressDialog.setCancelable(false)
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                runOnUiThread()
                {
                    Toast.makeText(context, context.getString(R.string.mensaje_error_intentear_mas_tarde), Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {

                val body = response.body()!!.string()

                if(body != null && body.isNotEmpty())
                {
                    try
                    {
                        val gson = GsonBuilder().create()
                        val respuesta = gson.fromJson(body, Respuesta::class.java)

                        if(respuesta.status == 0)
                        {
                            globalVariable.actualizarVentana!!.actualizarSurtido = true
                            runOnUiThread {
                                progressDialog.dismiss()
                                Toast.makeText(context, getString(R.string.supply_details_update_successful), Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                        else
                        {
                            val errores = Errores()
                            errores.procesarError(context,body,activity)
                        }
                    }
                    catch(e:Exception){}
                }

                progressDialog.dismiss()
            }
        })
    }

    private fun showDialogEliminarSurtido() {
        val dialog = Dialog(this)
        dialog.setTitle(title)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_text)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogText = dialog.findViewById(R.id.dialogTextEntrada) as EditText
        val dialogAceptar = dialog.findViewById(R.id.dialogTextAceptar) as Button
        val dialogCancelar = dialog.findViewById(R.id.dialogTextCancelar) as Button
        val dialogTitulo = dialog.findViewById(R.id.dialogTextTitulo) as TextView

        dialogTitulo.text = getString(R.string.dialog_eliminar_articulo)
        dialogText.visibility = View.INVISIBLE

        dialogAceptar.setOnClickListener {
            dialog.dismiss()
            eliminarSurtido()
        }

        dialogCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDialogActualizarSurtido() {
        val dialog = Dialog(this)
        dialog.setTitle(title)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_text)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogText = dialog.findViewById(R.id.dialogTextEntrada) as EditText
        val dialogAceptar = dialog.findViewById(R.id.dialogTextAceptar) as Button
        val dialogCancelar = dialog.findViewById(R.id.dialogTextCancelar) as Button
        val dialogTitulo = dialog.findViewById(R.id.dialogTextTitulo) as TextView

        dialogTitulo.text = getString(R.string.dialog_confirmar_actualizacion_venta)
        dialogText.visibility = View.INVISIBLE

        dialogAceptar.setOnClickListener {
            dialog.dismiss()
            actualizarSurtido()
        }

        dialogCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun habilitarEdicion() {
        listaArticulos = listaInventarioObjeto(surtido)
        mViewArticulosSurtido.RecyclerAdapter(listaArticulos, context)
        mRecyclerView.adapter = mViewArticulosSurtido
    }

    fun actualizarCantidadPrecio(){

        var totalCantidad = 0
        var totalPrecio = 0.0

        for(articulos in listaArticulos){
            totalCantidad += articulos.cantidad
            totalPrecio += (articulos.precio * articulos.cantidad).round(2)
        }

        VentaDetalleTotalArticulos.text = totalCantidad.toString()
        val textTmp = "$" + totalPrecio.round(2)
        VentaDetalleTotalVenta.text = textTmp
    }

    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }

    override fun obtenerNumero(numero : Int, posicion : Int) {
        runOnUiThread{
            listaArticulos[posicion].cantidad = numero
            mViewArticulosSurtido.RecyclerAdapter(listaArticulos, context)
            mRecyclerView.adapter = mViewArticulosSurtido
            actualizarCantidadPrecio()
        }
    }

}
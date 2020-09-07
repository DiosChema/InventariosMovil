package com.example.perraco.Activities

import android.app.Dialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.view.get
import com.example.perraco.Objets.*
import com.example.perraco.R
import com.example.perraco.RecyclerView.AdapterListEditarArticulosVenta
import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class VentaDetalle : AppCompatActivity() {

    lateinit var venta: VentasObjeto
    /*lateinit var mViewArticulosVenta : RecyclerViewEditarArticulosVenta
    lateinit var mRecyclerView : RecyclerView*/

    lateinit var VentaDetalleNumero : TextView
    lateinit var VentaDetalleFecha : TextView
    lateinit var VentaDetalleEliminarVenta : ImageButton
    lateinit var VentaDetalleCancelar : ImageButton
    lateinit var VentaDetalleEditar : ImageButton
    lateinit var VentaDetalleConfirmar : ImageButton

    lateinit var globalVariable: GlobalClass

    var listView: ListView? = null
    var adapter: AdapterListEditarArticulosVenta? = null

    var context = this;

    val urls: Urls = Urls()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venta_detalle)

        globalVariable = applicationContext as GlobalClass

        context = this;
        venta = intent.getSerializableExtra("venta") as VentasObjeto

        VentaDetalleNumero = findViewById(R.id.VentaDetalleNumero)
        VentaDetalleFecha = findViewById(R.id.VentaDetalleFecha)
        VentaDetalleEliminarVenta = findViewById(R.id.VentaDetalleEliminarVenta)
        VentaDetalleCancelar = findViewById(R.id.VentaDetalleCancelar)
        VentaDetalleEditar = findViewById(R.id.VentaDetalleEditar)
        VentaDetalleConfirmar = findViewById(R.id.VentaDetalleConfirmar)
        //mRecyclerView = findViewById(R.id.VentaDetalleArticulos)

        listView = findViewById(R.id.VentaDetalleArticulos)
        adapter = AdapterListEditarArticulosVenta(this, venta.articulos.toMutableList())
        (listView as ListView).adapter = adapter

        VentaDetalleNumero.text = venta._id.toString()
        VentaDetalleFecha.text = venta.fecha

        VentaDetalleEliminarVenta.visibility = View.INVISIBLE
        VentaDetalleCancelar.visibility = View.INVISIBLE
        VentaDetalleConfirmar.visibility = View.INVISIBLE

        /*mViewArticulosVenta = RecyclerViewEditarArticulosVenta()

        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mViewArticulosVenta.RecyclerAdapter(venta.articulos.toMutableList(), this)
        mRecyclerView.adapter = mViewArticulosVenta*/

        VentaDetalleEliminarVenta.setOnClickListener{
            showDialogEliminarVenta()
        }

        VentaDetalleEditar.setOnClickListener{
            VentaDetalleEliminarVenta.visibility = View.VISIBLE
            VentaDetalleCancelar.visibility = View.VISIBLE
            VentaDetalleEditar.visibility = View.INVISIBLE
            VentaDetalleConfirmar.visibility = View.VISIBLE
            habilitarEdicion(true)
        }

        VentaDetalleConfirmar.setOnClickListener{
            showDialogActualizarVenta()
        }

        VentaDetalleCancelar.setOnClickListener{
            VentaDetalleEliminarVenta.visibility = View.INVISIBLE
            VentaDetalleCancelar.visibility = View.INVISIBLE
            VentaDetalleEditar.visibility = View.VISIBLE
            VentaDetalleConfirmar.visibility = View.INVISIBLE
            habilitarEdicion(false)

            /*mViewArticulosVenta.RecyclerAdapter(venta.articulos.toMutableList(), this)
            mRecyclerView.adapter = mViewArticulosVenta*/
            adapter?.notifyDataSetChanged()
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

    }

    private fun eliminarVenta(){
        val url = urls.url+urls.endPointEliminarVenta


        val jsonObject = JSONObject()
        try {
            jsonObject.put("token", globalVariable.token)
            jsonObject.put("idFactura", venta._id)
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
        val url = urls.url+urls.endPointActualizarVenta

        val listaArticulos: MutableList<ActualizarArticuloObjeto> = ArrayList()

        //for (i in 0..mViewArticulosVenta.itemCount - 1) {
        for (i in 0 until adapter?.count!!) {
            /*val view: View = mRecyclerView.getChildAt(i)

            val EditarArticuloVentaCantidad = view.findViewById(R.id.EditarArticuloVentaCantidad) as EditText
            val textCantidad = EditarArticuloVentaCantidad.text.toString()

            val EditarArticuloVentaPrecio = view.findViewById(R.id.EditarArticuloVentaPrecio) as TextView
            val textPrecio = EditarArticuloVentaPrecio.text.toString()

            val EditarArticuloVentaCosto = view.findViewById(R.id.EditarArticuloVentaCosto) as TextView
            val textCosto = EditarArticuloVentaCosto.text.toString()

            val articulo = ActualizarArticuloObjeto(
                venta._id,
                venta.articulos[i].idArticulo,
                parseInt(textCantidad),
                parseDouble(textPrecio),
                venta.articulos[i].nombre,
                parseDouble(textCosto)
            )

            if(articulo.cantidad > 0)
                listaArticulos.add(articulo)

             */
            val objeto  = adapter?.obtenerObjeto(i)

            if (objeto != null && objeto.cantidad > 0) {
                listaArticulos.add(ActualizarArticuloObjeto(
                    venta._id,
                    objeto.idArticulo,
                    objeto.cantidad,
                    objeto.precio,
                    objeto.nombre,
                    objeto.costo
                ))
            }
        }

        var ventaActualizada : ActualizarVenta = ActualizarVenta(globalVariable.token.toString(),venta._id,listaArticulos)

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currentDate = sdf.format(Date())

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
                }

            }
        })

    }

    private fun showDialogEliminarVenta() {
        val dialog = Dialog(this)
        dialog.setTitle(title)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_text)
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

    private fun habilitarEdicion(habilitar : Boolean) {

        //for (i in 0..mViewArticulosVenta.itemCount - 1) {
        for (i in 0 until adapter?.count!!) {

            var view = listView?.get(i)

            /*val view: View = mRecyclerView.getChildAt(i)*/

            if(view != null){
                val EditarArticuloVentaCantidad =
                    view.findViewById(R.id.EditarArticuloVentaCantidad) as EditText
                val EditarArticuloVentaPrecio =
                    view.findViewById(R.id.EditarArticuloVentaPrecio) as EditText
                val EditarArticuloVentaCosto =
                    view.findViewById(R.id.EditarArticuloVentaCosto) as EditText
                val EditarArticuloVentaDisminuirCantidad =
                    view.findViewById(R.id.EditarArticuloVentaDisminuirCantidad) as ImageButton
                val EditarArticuloVentaAnadirCantidad =
                    view.findViewById(R.id.EditarArticuloVentaAnadirCantidad) as ImageButton
                val EditarArticuloEliminarArticulo =
                    view.findViewById(R.id.EditarArticuloEliminarArticulo) as ImageButton


                EditarArticuloVentaCantidad.isEnabled = habilitar
                EditarArticuloVentaPrecio.isEnabled = habilitar
                EditarArticuloVentaCosto.isEnabled = habilitar
                EditarArticuloEliminarArticulo.isEnabled = habilitar
                adapter!!.setHabilitar(habilitar)
                if(habilitar) {
                    EditarArticuloVentaDisminuirCantidad.visibility = View.VISIBLE
                    EditarArticuloVentaAnadirCantidad.visibility = View.VISIBLE
                }else{
                    EditarArticuloVentaDisminuirCantidad.visibility = View.INVISIBLE
                    EditarArticuloVentaAnadirCantidad.visibility = View.INVISIBLE
                }
            }

        }

    }


}
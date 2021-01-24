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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Dialogs.DialogAgregarNumero
import com.Aegina.PocketSale.Metodos.Errores
import com.Aegina.PocketSale.Objets.*
import com.Aegina.PocketSale.Objets.Loss.UpdateLoss
import com.Aegina.PocketSale.R
import com.Aegina.PocketSale.RecyclerView.RecyclerItemClickListener
import com.Aegina.PocketSale.RecyclerView.RecyclerViewVenta
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.item_ventas.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat

class LossDetails : AppCompatActivity(),
    DialogAgregarNumero.DialogAgregarNumero {

    lateinit var lossObject: VentasObjeto
    lateinit var mViewArticulosLoss : RecyclerViewVenta
    lateinit var mRecyclerView : RecyclerView

    lateinit var VentaDetalleNumero : TextView
    lateinit var VentaDetalleFecha : TextView
    lateinit var VentaDetalleEliminarVenta : ImageButton
    lateinit var VentaDetalleCancelar : Button
    lateinit var VentaDetalleEliminarVentaCardView : CardView
    lateinit var VentaDetalleEditar : Button
    var editar = false

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
        lossObject = intent.getSerializableExtra("loss") as VentasObjeto

        VentaDetalleNumero = findViewById(R.id.VentaDetalleNumero)
        VentaDetalleFecha = findViewById(R.id.VentaDetalleFecha)
        VentaDetalleEliminarVenta = findViewById(R.id.VentaDetalleEliminarVenta)
        VentaDetalleCancelar = findViewById(R.id.VentaDetalleCancelar)
        VentaDetalleEliminarVentaCardView = findViewById(R.id.VentaDetalleEliminarVentaCardView)
        VentaDetalleEditar = findViewById(R.id.VentaDetalleEditar)

        mRecyclerView = findViewById(R.id.ventasFragmentRecyclerViewArticulos)

        var textTmp = getString(R.string.loss_details_title) + System.getProperty ("line.separator") +  lossObject._id
        VentaDetalleNumero.text = textTmp
        textTmp = simpleDate.format(lossObject.fecha) + System.getProperty ("line.separator") + simpleDateHours.format(lossObject.fecha)
        VentaDetalleFecha.text = textTmp

        VentaDetalleEliminarVenta.visibility = View.INVISIBLE
        VentaDetalleEliminarVentaCardView.visibility = View.INVISIBLE
        VentaDetalleCancelar.visibility = View.INVISIBLE
        editar = false

        mViewArticulosLoss = RecyclerViewVenta()
        mRecyclerView = findViewById(R.id.ventasFragmentRecyclerViewArticulos)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)

        listaArticulos = listaInventarioObjeto(lossObject)
        mViewArticulosLoss.RecyclerAdapter(listaArticulos, context)
        mRecyclerView.adapter = mViewArticulosLoss

        VentaDetalleEliminarVenta.setOnClickListener{
            showDialogDeleteLoss()
        }

        VentaDetalleEditar.setOnClickListener{
            if(!editar) {
                VentaDetalleEditar.text = getString(R.string.mensaje_actualizar_articulo)
                VentaDetalleEliminarVenta.visibility = View.VISIBLE
                VentaDetalleEliminarVentaCardView.visibility = View.VISIBLE
                VentaDetalleCancelar.visibility = View.VISIBLE
                editar = true
                habilitarEdicion()

                mRecyclerView.addOnItemTouchListener(RecyclerItemClickListener(context, mRecyclerView, object :
                    RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        if(editar) dialogAgregarNumero.crearDialogNumero(context, position, listaArticulos[position].cantidad)
                    }

                    override fun onLongItemClick(view: View?, position: Int) {}
                }))
            }
            else
            {
                showDialogUpdateLoss()
            }
        }

        /*VentaDetalleConfirmar.setOnClickListener{
            showDialogActualizarVenta()
        }*/

        VentaDetalleCancelar.setOnClickListener{
            VentaDetalleEditar.text = getString(R.string.mensaje_editar)
            VentaDetalleEliminarVenta.visibility = View.INVISIBLE
            VentaDetalleEliminarVentaCardView.visibility = View.INVISIBLE
            VentaDetalleCancelar.visibility = View.INVISIBLE
            editar = false
            habilitarEdicion()
        }

    }

    fun listaInventarioObjeto(loss: VentasObjeto): MutableList<InventarioObjeto> {

        val listatmp : MutableList<InventarioObjeto> = arrayListOf()

        for(articulos in loss.articulos){
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

    private fun deleteLoss(){
        val url = urls.url+urls.endPointLoss.endPointRemoveLoss

        val jsonObject = JSONObject()
        try {
            jsonObject.put("token", globalVariable.usuario!!.token)
            jsonObject.put("idLoss", lossObject._id)
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
                            globalVariable.actualizarVentana!!.updateLoss = true
                            runOnUiThread()
                            {
                                Toast.makeText(context, getString(R.string.loss_details_update_successful), Toast.LENGTH_SHORT).show()
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

    private fun updateLoss(){
        val url = urls.url+urls.endPointLoss.endPointUpdateLoss

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val currentDate = sdf.format(lossObject.fecha)

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
            deleteLoss()
            return
        }

        val UpdatedLoss = UpdateLoss(globalVariable.usuario!!.token,lossObject._id,currentDate,listaArticulosTmp)

        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val jsonLoss: String = gsonPretty.toJson(UpdatedLoss)

        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonLoss)

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
                            globalVariable.actualizarVentana!!.updateLoss = true
                            runOnUiThread {
                                progressDialog.dismiss()
                                Toast.makeText(context, getString(R.string.loss_details_update_successful), Toast.LENGTH_SHORT).show()
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

    private fun showDialogDeleteLoss() {
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
            deleteLoss()
        }

        dialogCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDialogUpdateLoss() {
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
            updateLoss()
        }

        dialogCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun habilitarEdicion() {
        listaArticulos = listaInventarioObjeto(lossObject)
        mViewArticulosLoss.RecyclerAdapter(listaArticulos, context)
        mRecyclerView.adapter = mViewArticulosLoss
    }

    override fun obtenerNumero(numero : Int, posicion : Int) {
        runOnUiThread{
            listaArticulos[posicion].cantidad = numero
            mViewArticulosLoss.RecyclerAdapter(listaArticulos, context)
            mRecyclerView.adapter = mViewArticulosLoss
        }
    }

}
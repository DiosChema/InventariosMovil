package com.Aegina.PocketSale.Activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Metodos.Errores
import com.Aegina.PocketSale.Metodos.Meses
import com.Aegina.PocketSale.Objets.*
import com.Aegina.PocketSale.R
import com.Aegina.PocketSale.RecyclerView.RecyclerItemClickListener
import com.Aegina.PocketSale.RecyclerView.RecyclerViewSuscripcion
import com.android.billingclient.api.*
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Double.parseDouble
import java.lang.Integer.parseInt
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Suscripcion : AppCompatActivity(), PurchasesUpdatedListener {

    private val urls: Urls = Urls()
    lateinit var billingClient: BillingClient
    lateinit var globalVariable: GlobalClass
    lateinit var context : Context
    var listaArticulos: MutableList<SuscripcionObject> = ArrayList()
    lateinit var mViewVenta : RecyclerViewSuscripcion
    lateinit var mRecyclerView : RecyclerView
    lateinit var activity : Activity

    lateinit var suscripcionNombreTienda : TextView
    lateinit var suscripcionFechaLimiteTienda : TextView
    lateinit var tiendaDetalleFoto : ImageView

    lateinit var progressDialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suscripcion)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        context = this
        activity = this
        globalVariable = context.applicationContext as GlobalClass

        progressDialog = ProgressDialog(context)
        progressDialog.setMessage(getString(R.string.mensaje_espera))
        progressDialog.setCancelable(false)

        asignarRecursos()
        setupBillingClient()
        obtenerDatosTienda()
    }

    fun asignarRecursos()
    {
        suscripcionNombreTienda = findViewById(R.id.suscripcionNombreTienda)
        suscripcionFechaLimiteTienda = findViewById(R.id.suscripcionFechaLimiteTienda)
        tiendaDetalleFoto = findViewById(R.id.tiendaDetalleFoto)
        mRecyclerView = findViewById(R.id.suscripcionRecyclerView)
    }

    private fun crearRecyclerView(){
        mViewVenta = RecyclerViewSuscripcion()
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL ,false)
        mViewVenta.RecyclerAdapter(listaArticulos, context)
        mRecyclerView.adapter = mViewVenta
        mViewVenta.notifyDataSetChanged()
    }

    private fun displayProduct(skuDetailsList: List<SkuDetails>?) {

        var precioMes = 0.0

        for(skuTmp in skuDetailsList!!){
            if(parseInt(obtenerNumeros(skuTmp.sku)) == 1) {
                precioMes = parseDouble(obtenerNumeros(skuTmp.price))
            }
        }

        for(skuTmp in skuDetailsList!!){
            listaArticulos.add(SuscripcionObject(
                skuTmp.sku, parseInt(obtenerNumeros(skuTmp.sku)),
                skuTmp.price,
                precioMes
            ))
        }

        crearRecyclerView()

        mRecyclerView.addOnItemTouchListener(RecyclerItemClickListener(context, mRecyclerView, object :
            RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetailsList[position])
                    .build()
                progressDialog.show()
                billingClient.launchBillingFlow(context as Activity,billingFlowParams).responseCode
            }

            override fun onLongItemClick(view: View?, position: Int) {
                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetailsList[position])
                    .build()
                progressDialog.show()
                billingClient.launchBillingFlow(context as Activity,billingFlowParams).responseCode
            }
        }))
    }

    fun obtenerNumeros(cadena: String) : String
    {
        var cadenaRecortada = ""

        for (i in cadena.indices)
        {
            if(cadena[i].isDigit())
            {
                cadenaRecortada = cadena.substring(i)
                break
            }
        }

        return cadenaRecortada
    }

    fun obtenerNumerosPunto(cadena: String) : Int
    {
        val num = parseInt(cadena.replace(Regex("[^0-9.]"), ""))
        return num
    }

    fun llenarCampos(tiendaObjeto: TiendaObjeto)
    {
        suscripcionNombreTienda.text = tiendaObjeto.nombre

        val nombreMes = Meses()
        val calendar = Calendar.getInstance()
        val formatoFecha = SimpleDateFormat("yyyy-MM-dd")

        calendar.time = formatoFecha.parse(tiendaObjeto.fechaLimiteSuscripcion)// all done

        val fechaTienda = if(parseInt(getString(R.string.numero_idioma))  > 1)
        {
            "" + calendar.get(Calendar.DAY_OF_MONTH) + " " + nombreMes.obtenerMesCorto((calendar.get(
                Calendar.MONTH) + 1),context) + " " + calendar.get(Calendar.YEAR)
        }
        else
        {
            "" + nombreMes.obtenerMesCorto((calendar.get(Calendar.MONTH) + 1),context) + " " + calendar.get(
                Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.YEAR)
        }

        suscripcionFechaLimiteTienda.text = fechaTienda

        if(globalVariable.usuario == null)
        {
            tiendaDetalleFoto.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+"t"+parseInt(tiendaObjeto.tienda)+".jpeg"+"&token="+globalVariable.tokenEspecial+"&tipoImagen=2")
        }
        else
        {
            tiendaDetalleFoto.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+"t"+globalVariable.usuario!!.tienda+".jpeg"+"&token="+globalVariable.usuario!!.token+"&tipoImagen=2")
        }

    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        //Toast.makeText(this@Suscripcion,"Gracias por tu dinero :v",Toast.LENGTH_LONG).show()
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            progressDialog.dismiss()
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
            progressDialog.dismiss()
            // Handle any other error codes.
        }
    }

    fun handlePurchase(purchase: Purchase) {
        // Purchase retrieved from BillingClient#queryPurchases or your PurchasesUpdatedListener.

        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.

        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

        billingClient.consumeAsync(consumeParams)
        { billingResult, outToken ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {

                when (purchase.sku) {
                    "pocketsale_ms00000001" -> obtenerTokenCompra(1, outToken)
                    "pocketsale_ms00000002" -> obtenerTokenCompra(2, outToken)
                    "pocketsale_ms00000004" -> obtenerTokenCompra(4, outToken)
                    else -> {
                    }
                }

                //Toast.makeText(this@Suscripcion,"Se compro " + purchase.sku + " " + purchase.purchaseToken,Toast.LENGTH_LONG).show()
            }
            else
            {
                progressDialog.dismiss()
            }
        }
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(this)
            .build()

        billingClient.startConnection(object:BillingClientStateListener{
            override fun onBillingServiceDisconnected()
            {
            }

            override fun onBillingSetupFinished(responseCode: BillingResult) {
                if(responseCode.responseCode == BillingClient.BillingResponseCode.OK) {
                    if(billingClient.isReady){

                        val skuList = ArrayList<String>()
                        skuList.add("pocketsale_ms00000001")
                        skuList.add("pocketsale_ms00000002")
                        skuList.add("pocketsale_ms00000004")

                        val params = SkuDetailsParams.newBuilder()
                            .setSkusList(skuList)
                            .setType(BillingClient.SkuType.INAPP)
                            .build()

                        billingClient.querySkuDetailsAsync(params){
                                responseCode, skuDetailsList ->
                            if(responseCode.responseCode == BillingClient.BillingResponseCode.OK) {
                                if(skuDetailsList != null && skuDetailsList.size > 0) {
                                    displayProduct(skuDetailsList)
                                }
                            }
                            else
                            {
                                Toast.makeText(this@Suscripcion,getString(R.string.mensaje_error_intentear_mas_tarde),Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(this@Suscripcion,getString(R.string.mensaje_error_intentear_mas_tarde),Toast.LENGTH_LONG).show()
                    }
                }
                else
                {
                    Toast.makeText(this@Suscripcion,getString(R.string.mensaje_error_intentear_mas_tarde),Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    fun obtenerTokenCompra(tipoCompra: Int, outToken: String)
    {
        val url = urls.url+urls.endPointUsers.endPointObtenerTokenCompra+
                "?token="+globalVariable.tokenEspecial

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread()
                {
                    progressDialog.dismiss()
                    Toast.makeText(context, context.getString(R.string.mensaje_error_intentear_mas_tarde), Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response)
            {

                val body = response.body()?.string()

                if(body != null && body.isNotEmpty())
                {
                    try
                    {
                        val gson = GsonBuilder().create()
                        val respuesta = gson.fromJson(body, Respuesta::class.java)

                        if(respuesta.status == 0)
                        {
                            runOnUiThread()
                            {
                                actualizarSuscripcion(tipoCompra,respuesta.respuesta, outToken)
                            }
                        }
                        else
                        {
                            val errores = Errores()
                            errores.procesarError(context,body,activity)
                        }

                    }
                    catch(e: java.lang.Exception)
                    {
                        val errores = Errores()
                        errores.procesarErrorCerrarVentana(context,body,activity)
                    }
                }
            }
        })
    }

    fun actualizarSuscripcion(tipoCompra: Int, tokenCompra: String, outToken: String){
        val url = urls.url+urls.endPointUsers.endPointActualizarFechaCompra

        val jsonObject = JSONObject()
        try
        {
            var verificador = (globalVariable.usuario!!.tienda * 9) + tipoCompra

            jsonObject.put("token", globalVariable.tokenEspecial)
            jsonObject.put("tipoCompra", tipoCompra)
            jsonObject.put("tokenCompra", tokenCompra)
            jsonObject.put("outToken", outToken)
            jsonObject.put("verificador", verificador)
        }
        catch (e: JSONException)
        {
            e.printStackTrace()
        }

        val client = OkHttpClient()
        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonObject.toString())

        val request = Request.Builder()
            .url(url)
            .put(body)
            .build()

        /*val progressDialog = ProgressDialog(context)
        progressDialog.setMessage(getString(R.string.mensaje_espera))
        progressDialog.setCancelable(false)
        try
        {
            progressDialog.show()
        }
        catch (e:Exception)
        {

        }*/

        //progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                runOnUiThread()
                {
                    progressDialog.dismiss()
                    Toast.makeText(context, context.getString(R.string.mensaje_error_suscripcion), Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response)
            {
                val body = response.body()?.string()

                if(body != null && body.isNotEmpty())
                {
                    try
                    {
                        val gson = GsonBuilder().create()
                        val respuesta = gson.fromJson(body, Respuesta::class.java)

                        if(respuesta.status == 0)
                        {
                            runOnUiThread()
                            {
                                Toast.makeText(this@Suscripcion,context.getString(R.string.suscripcion_actualizacion),Toast.LENGTH_LONG).show()
                                if(globalVariable.usuario == null)
                                {
                                    val errores = Errores()
                                    errores.crearDialologIniciarSesion(context,activity)
                                }
                                else
                                {
                                    obtenerDatosTienda()
                                }
                            }
                        }
                        else
                        {
                            val errores = Errores()
                            errores.procesarError(context,body,activity)
                            progressDialog.dismiss()
                        }

                    }
                    catch(e:Exception)
                    {
                        progressDialog.dismiss()
                    }
                }


            }
        })
    }

    private fun obtenerDatosTienda() {
        val url = urls.url+urls.endPointUsers.endPointObtenerDatosTiendaTokenEspecial +
                "?token=" + globalVariable.tokenEspecial

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        /*val progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.mensaje_espera))
        progressDialog.setCancelable(false)

        try
        {
            progressDialog.show()
        }
        catch(e: Exception)
        {

        }*/

        if(!progressDialog.isShowing)
        {
            progressDialog.show()
        }

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                runOnUiThread()
                {
                    Toast.makeText(context, context.getString(R.string.mensaje_error_intentear_mas_tarde), Toast.LENGTH_LONG).show()
                    finish()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()!!.string()

                if(body != null && body.isNotEmpty())
                {
                    try
                    {
                        val gson = GsonBuilder().create()
                        val tiendaObjeto = gson.fromJson(body, TiendaObjeto::class.java)

                        if(tiendaObjeto.fechaLimiteSuscripcion == null)
                        {
                            throw java.lang.Exception("")
                        }
                        runOnUiThread()
                        {
                            llenarCampos(tiendaObjeto)
                        }
                    }
                    catch(e: java.lang.Exception)
                    {
                        val errores = Errores()
                        errores.procesarErrorCerrarVentana(context,body,activity)
                    }
                }

                progressDialog.dismiss()
            }
        })
    }

    fun ImageView.loadUrl(url: String) {
        try {
            Picasso.with(context).load(url).into(this)}
        catch(e: java.lang.Exception){}
    }

}
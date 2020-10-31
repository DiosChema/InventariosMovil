package com.Aegina.PocketSale.Activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.SuscripcionObject
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.Aegina.PocketSale.RecyclerView.RecyclerItemClickListener
import com.Aegina.PocketSale.RecyclerView.RecyclerViewSuscripcion
import com.android.billingclient.api.*
import okhttp3.*
import java.io.IOException
import java.lang.Double.parseDouble
import java.lang.Integer.parseInt


class Suscripcion : AppCompatActivity(), PurchasesUpdatedListener {

    private val urls: Urls = Urls()
    lateinit var billingClient: BillingClient
    lateinit var globalVariable: GlobalClass
    lateinit var context : Context
    var listaArticulos: MutableList<SuscripcionObject> = ArrayList()
    lateinit var mViewVenta : RecyclerViewSuscripcion
    lateinit var mRecyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suscripcion)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        context = this
        globalVariable = context.applicationContext as GlobalClass

        setupBillingClient()

    }

    private fun crearRecyclerView(){
        mViewVenta = RecyclerViewSuscripcion()
        mRecyclerView = findViewById(R.id.suscripcionRecyclerView)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL ,false)
        mViewVenta.RecyclerAdapter(listaArticulos, context)
        mRecyclerView.adapter = mViewVenta
    }

    private fun displayProduct(skuDetailsList: List<SkuDetails>?) {

        var precioMes = 0.0

        for(skuTmp in skuDetailsList!!){
            if(parseInt(skuTmp.sku.substring(skuTmp.sku.length - 2, skuTmp.sku.length)) == 1) {
                precioMes = parseDouble(skuTmp.price.substring(1, skuTmp.price.length))
            }
        }

        for(skuTmp in skuDetailsList!!){
            listaArticulos.add(SuscripcionObject(
                skuTmp.sku, parseInt(skuTmp.sku.substring(skuTmp.sku.length - 2, skuTmp.sku.length)),
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

                billingClient.launchBillingFlow(context as Activity,billingFlowParams).responseCode
            }

            override fun onLongItemClick(view: View?, position: Int) {
                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetailsList[position])
                    .build()

                billingClient.launchBillingFlow(context as Activity,billingFlowParams).responseCode
            }
        }))


    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        //Toast.makeText(this@Suscripcion,"Gracias por tu dinero :v",Toast.LENGTH_LONG).show()
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        } else {
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

        billingClient.consumeAsync(consumeParams) { billingResult, outToken ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {

                when (purchase.sku) {
                    "pocketsale_ms00000001" -> obtenerArticulos(1)
                    "pocketsale_ms00000006" -> obtenerArticulos(6)
                    "pocketsale_ms00000012" -> obtenerArticulos(12)
                    else -> {
                    }
                }

                //Toast.makeText(this@Suscripcion,"Se compro " + purchase.sku + " " + purchase.purchaseToken,Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(this)
            .build()

        billingClient.startConnection(object:BillingClientStateListener{
            override fun onBillingServiceDisconnected() {
                Toast.makeText(this@Suscripcion,"se desconecto",Toast.LENGTH_LONG).show()
            }

            override fun onBillingSetupFinished(responseCode: BillingResult) {
                if(responseCode.responseCode == BillingClient.BillingResponseCode.OK) {
                    Toast.makeText(this@Suscripcion, "Se conecto", Toast.LENGTH_LONG).show()
                    if(billingClient.isReady){

                        val skuList = ArrayList<String>()
                        skuList.add("pocketsale_ms00000001")
                        skuList.add("pocketsale_ms00000006")
                        skuList.add("pocketsale_ms00000012")

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
                                Toast.makeText(this@Suscripcion,"No se puede desplegar los productos",Toast.LENGTH_LONG).show()
                        }
                    }
                    else{
                        Toast.makeText(this@Suscripcion,"Aun no se conecta",Toast.LENGTH_LONG).show()
                    }
                }
                else
                    Toast.makeText(this@Suscripcion,"No se conecto "+responseCode.debugMessage,Toast.LENGTH_LONG).show()
            }

        })
    }

    fun obtenerArticulos(tipoCompra: Int){
        val url = urls.url+urls.endPointActualizarFechaCompra+
                "?token="+globalVariable.token +
                "&tipoCompra=" + tipoCompra

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()

        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage(getString(R.string.mensaje_espera))
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response)
            {
                val body = response.body()?.string()

                if(body != null && body.isNotEmpty()) {
                    runOnUiThread {
                        Toast.makeText(this@Suscripcion,"Se ha actualizado tu suscripcion",Toast.LENGTH_LONG).show()
                    }
                }

                progressDialog.dismiss()

            }
        })
    }

}
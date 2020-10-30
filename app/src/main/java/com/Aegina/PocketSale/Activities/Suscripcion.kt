package com.Aegina.PocketSale.Activities

import android.app.ProgressDialog
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.Aegina.PocketSale.Objets.EstadisticaArticuloObject
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.android.billingclient.api.*
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class Suscripcion : AppCompatActivity(), PurchasesUpdatedListener {

    private val urls: Urls = Urls()
    lateinit var suscripcionMesSucripcion : Button
    lateinit var billingClient: BillingClient
    lateinit var globalVariable: GlobalClass
    lateinit var context : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suscripcion)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        context = this
        globalVariable = context.applicationContext as GlobalClass

        setupBillingClient()

        suscripcionMesSucripcion = findViewById(R.id.suscripcionMesSucripcion)

        suscripcionMesSucripcion.setOnClickListener{
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
                    if(responseCode.responseCode == BillingClient.BillingResponseCode.OK)
                        displayProduct(skuDetailsList)
                    else
                        Toast.makeText(this@Suscripcion,"No se puede desplegar los productos",Toast.LENGTH_LONG).show()
                }
            }
            else{
                Toast.makeText(this@Suscripcion,"Aun no se conecta",Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun displayProduct(skuDetailsList: List<SkuDetails>?) {
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetailsList!![0])
            .build()

        billingClient.launchBillingFlow(this,billingFlowParams).responseCode
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
                        print("x no es 1 o 2")
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
                if(responseCode.responseCode == BillingClient.BillingResponseCode.OK)
                    Toast.makeText(this@Suscripcion,"Se conecto",Toast.LENGTH_LONG).show()
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
                        Toast.makeText(this@Suscripcion,"Gracias por tu dinero :v",Toast.LENGTH_LONG).show()
                    }
                }

                progressDialog.dismiss()

            }
        })
    }

}
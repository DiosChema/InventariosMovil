@file:Suppress("DEPRECATION")

package com.Aegina.PocketSale.Activities

import android.app.ProgressDialog
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.Aegina.PocketSale.Objets.*
import com.Aegina.PocketSale.R
import com.google.gson.GsonBuilder
import okhttp3.*
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import java.io.IOException


class EstadisticaInventario : AppCompatActivity() {

    val urls: Urls = Urls()
    lateinit var globalVariable: GlobalClass
    lateinit var pieChart : PieChart
    lateinit var estadisticaInventarioTotalArticulos : TextView
    lateinit var estadisticaInventarioTotalCostos : TextView
    lateinit var estadisticaInventarioTotal : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadistica_inventario)

        globalVariable = applicationContext as GlobalClass
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        asignarRecursos()
        obtenerTotales()

    }

    fun asignarRecursos(){
        pieChart = findViewById(R.id.estadisticaInventarioPiechart)
        estadisticaInventarioTotalArticulos = findViewById(R.id.estadisticaInventarioTotalArticulos)
        estadisticaInventarioTotalCostos = findViewById(R.id.estadisticaInventarioTotalCostos)
        estadisticaInventarioTotal = findViewById(R.id.estadisticaInventarioTotal)
    }

    fun obtenerTotales() {

        val url = urls.url+urls.endPointEstadisticasInventario+"?token="+globalVariable.token

        val request = Request.Builder()
            .url(url)
            .get()
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
                progressDialog.dismiss()

                val body = response.body()?.string()
                val gson = GsonBuilder().create()

                val modelTmp = gson.fromJson(body, Array<EstadisticaInventarioObject>::class.java)
                val Model = modelTmp[0]

                runOnUiThread {

                    estadisticaInventarioTotalArticulos.text = (Model.totalCostoVenta - Model.totalCostoProovedor).toString()
                    estadisticaInventarioTotalCostos.text = Model.totalCostoProovedor.toString()
                    estadisticaInventarioTotal.text = Model.totalCostoVenta.toString()

                    pieChart.addPieSlice(
                        PieModel(
                            "Total Articulos", (Model.totalCostoVenta - Model.totalCostoProovedor).toFloat(),
                            Color.parseColor("#228B22")
                        )
                    )
                    pieChart.addPieSlice(
                        PieModel(
                            "Total Costo", Model.totalCostoProovedor.toFloat(),
                            Color.parseColor("#FF0000")
                        )
                    )

                    pieChart.startAnimation()
                }

            }
        })

    }

}
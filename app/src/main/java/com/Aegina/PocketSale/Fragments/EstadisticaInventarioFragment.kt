@file:Suppress("DEPRECATION")

package com.Aegina.PocketSale.Fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.Aegina.PocketSale.Metodos.Errores
import com.Aegina.PocketSale.Objets.EstadisticaInventarioObject
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_estadistica_inventario_fragment.*
import okhttp3.*
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import java.io.IOException

class EstadisticaInventarioFragment : Fragment() {

    val urls: Urls = Urls()
    lateinit var globalVariable: GlobalClass
    lateinit var pieChart : PieChart
    lateinit var estadisticaInventarioTotalArticulosText : TextView
    lateinit var estadisticaInventarioTotalCostosText : TextView
    lateinit var estadisticaInventarioTotalText : TextView

    lateinit var contextTmp : Context
    lateinit var activityTmp: Activity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_estadistica_inventario_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        globalVariable = activity?.applicationContext as GlobalClass

        contextTmp = activity!!.applicationContext


        asignarRecursos()
    }

    fun asignarRecursos(){
        pieChart = estadisticaInventarioPiechart
        estadisticaInventarioTotalArticulosText = estadisticaInventarioTotalArticulos
        estadisticaInventarioTotalCostosText = estadisticaInventarioTotalCostos
        estadisticaInventarioTotalText = estadisticaInventarioTotal

        obtenerTotales()
    }

    fun obtenerTotales() {

        val url = urls.url+urls.endPointEstadisticas.endPointEstadisticasInventario+"?token="+globalVariable.usuario!!.token

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage(getString(R.string.mensaje_espera))
        progressDialog.setCancelable(false)
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                activity?.runOnUiThread()
                {
                    Toast.makeText(contextTmp, contextTmp.getString(R.string.mensaje_error_intentear_mas_tarde), Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {

                val body = response.body()?.string()

                if(body != null && body.isNotEmpty())
                {
                    try
                    {
                        val gson = GsonBuilder().create()

                        val modelTmp = gson.fromJson(body, Array<EstadisticaInventarioObject>::class.java)
                        val Model = modelTmp[0]

                        activity?.runOnUiThread {

                            estadisticaInventarioTotalArticulosText.text = (Model.totalCostoVenta - Model.totalCostoProovedor).round(2).toString()
                            estadisticaInventarioTotalCostosText.text = Model.totalCostoProovedor.round(2).toString()
                            estadisticaInventarioTotalText.text = Model.totalCostoVenta.round(2).toString()

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
                    catch(e:Exception)
                    {
                        val errores = Errores()
                        errores.procesarError(activity!!.applicationContext,body,activity!!)
                    }
                }

                progressDialog.dismiss()
            }
        })

    }

    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }

}
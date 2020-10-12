@file:Suppress("DEPRECATION")

package com.Aegina.PocketSale.Fragments

import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_estadistica_inventario_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        globalVariable = getActivity()?.applicationContext as GlobalClass

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

        val url = urls.url+urls.endPointEstadisticasInventario+"?token="+globalVariable.token

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()
        val progressDialog = ProgressDialog(activity)
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

                activity?.runOnUiThread {

                    estadisticaInventarioTotalArticulosText.text = (Model.totalCostoVenta - Model.totalCostoProovedor).toString()
                    estadisticaInventarioTotalCostosText.text = Model.totalCostoProovedor.toString()
                    estadisticaInventarioTotalText.text = Model.totalCostoVenta.toString()

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
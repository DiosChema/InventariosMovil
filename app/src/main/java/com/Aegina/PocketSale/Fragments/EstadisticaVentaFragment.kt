@file:Suppress("DEPRECATION")

package com.Aegina.PocketSale.Fragments

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.Aegina.PocketSale.Dialogs.DialogFecha
import com.Aegina.PocketSale.Metodos.Errores
import com.Aegina.PocketSale.Metodos.Meses
import com.Aegina.PocketSale.Objets.EstadisticaVentasObject
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_estadistica_venta_fragment.*
import okhttp3.*
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EstadisticaVentaFragment() : Fragment() {

    val formatoFechaCompleta = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    lateinit var fechaInicial : Date
    lateinit var fechaFinal : Date
    lateinit var estadisticaVentasFechaInicial : Button
    lateinit var estadisticaVentasFechaFinal : Button
    val context = activity;

    val urls: Urls = Urls()
    lateinit var globalVariable: GlobalClass
    lateinit var pieChart : PieChart
    lateinit var estadisticaVentaTotalArticulosText : TextView
    lateinit var estadisticaVentaTotalCostosText : TextView
    lateinit var estadisticaVentaLossText : TextView
    lateinit var estadisticaVentaTotalText : TextView

    var dialogFecha = DialogFecha()
    val nombreMes = Meses()
    lateinit var contextTmp : Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_estadistica_venta_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        dialogFecha.crearVentana(activity!!)
        globalVariable = activity?.applicationContext as GlobalClass

        contextTmp = activity!!.applicationContext
        asignarBotones()
        asignarRecursos()
        asignarFechaSemana()
        obtenerTotales()
    }

    fun asignarRecursos(){
        pieChart = estadisticaVentaPiechart
        estadisticaVentaTotalArticulosText = estadisticaVentaTotalArticulos
        estadisticaVentaTotalCostosText = estadisticaVentaTotalCostos
        estadisticaVentaTotalText = estadisticaVentaTotal
        estadisticaVentaLossText = estadisticaVentaLoss
    }

    fun asignarBotones(){
        estadisticaVentasFechaInicial = activity!!.findViewById(R.id.estadisticaVentasFechaInicial)
        estadisticaVentasFechaInicial.setOnClickListener {
            showDialogFechaInicial()
        }

        estadisticaVentasFechaFinal = activity!!.findViewById(R.id.estadisticaVentasFechaFinal)
        estadisticaVentasFechaFinal.setOnClickListener {
            showDialogFechaFinal()
        }
    }

    fun showDialogFechaInicial() {
        dialogFecha.abrirDialogFechaInicial(fechaInicial, fechaFinal)
    }

    fun showDialogFechaFinal() {
        dialogFecha.abrirDialogFechaFinal(fechaInicial, fechaFinal)
    }

    fun obtenerTotales() {

        val url = urls.url+urls.endPointVentas.endPointEstadisticasPorFecha+
                "?token="+globalVariable.usuario!!.token +
                "&fechaInicial=" + formatoFechaCompleta.format(fechaInicial) +
                "&fechaFinal="+formatoFechaCompleta.format(fechaFinal)

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
                    Toast.makeText(context, context!!.getString(R.string.mensaje_error_intentear_mas_tarde), Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {

                val body = response.body()?.string()

                if(body != null && body.isNotEmpty())
                {
                    try
                    {
                        val gson = GsonBuilder().create()
                        val model = gson.fromJson(body, EstadisticaVentasObject::class.java)

                        activity?.runOnUiThread {
                            estadisticaVentaTotalArticulosText.text = (model.totalCostoVenta - model.totalCostoProovedor).round(2).toString()
                            estadisticaVentaTotalCostosText.text = model.totalCostoProovedor.round(2).toString()
                            estadisticaVentaLossText.text = model.totalLoss.round(2).toString()
                            estadisticaVentaTotalText.text = (model.totalCostoVenta - model.totalLoss).round(2).toString()

                            pieChart.clearChart()

                            pieChart.addPieSlice(PieModel(
                                "Total Articulos", (model.totalCostoVenta - model.totalCostoProovedor - model.totalLoss).toFloat(),
                                Color.parseColor("#228B22")))

                            pieChart.addPieSlice(PieModel(
                                "Loss", model.totalLoss.toFloat(),
                                Color.parseColor("#add8e6")))

                            pieChart.addPieSlice(PieModel(
                                "Total Costo", model.totalCostoProovedor.toFloat(),
                                Color.parseColor("#FF0000")))

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

    fun asignarHoraCalendar(calendar : Calendar, hora : Int, minuto : Int, segundo : Int) : Calendar{
        calendar.set(Calendar.HOUR_OF_DAY, hora)
        calendar.set(Calendar.MINUTE, minuto)
        calendar.set(Calendar.SECOND, segundo)

        return calendar
    }

    fun asignarFechaHoy(){
        var calendar = Calendar.getInstance()
        calendar = asignarHoraCalendar(calendar, 23, 59, 59)
        fechaFinal = calendar.time

        calendar = asignarHoraCalendar(calendar, 0, 0, 0)
        fechaInicial = calendar.time

        buscarEstadisticaVentas()
    }

    fun asignarFechaSemana(){
        var calendar = Calendar.getInstance()
        calendar = asignarHoraCalendar(calendar, 23, 59, 59)
        fechaFinal = calendar.time

        calendar.add(Calendar.DAY_OF_MONTH, -6);
        calendar = asignarHoraCalendar(calendar, 0, 0, 0)
        fechaInicial = calendar.time

        buscarEstadisticaVentas()
    }

    fun asignarFechaMes(){
        var calendar = Calendar.getInstance()
        calendar = asignarHoraCalendar(calendar, 23, 59, 59)
        fechaFinal = calendar.time

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar = asignarHoraCalendar(calendar, 0, 0, 0)
        fechaInicial = calendar.time

        buscarEstadisticaVentas()
    }

    fun asignarFechaInicial(){
        val calendar = Calendar.getInstance()
        calendar.time = fechaInicial

        val text = if(Integer.parseInt(getString(R.string.numero_idioma)) > 1)
        {
            "" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + nombreMes.obtenerMesCorto((calendar.get(Calendar.MONTH) + 1),contextTmp) + "-" + calendar.get(Calendar.YEAR)
        }
        else
        {
            "" + nombreMes.obtenerMesCorto((calendar.get(Calendar.MONTH) + 1),contextTmp) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.YEAR)
        }

        estadisticaVentasFechaInicial.text = text
    }

    fun asignarFechaFinal(){
        val calendar = Calendar.getInstance()
        calendar.time = fechaFinal

        val text = if(Integer.parseInt(getString(R.string.numero_idioma)) > 1)
        {
            "" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + nombreMes.obtenerMesCorto((calendar.get(Calendar.MONTH) + 1),contextTmp) + "-" + calendar.get(Calendar.YEAR)
        }
        else
        {
            "" + nombreMes.obtenerMesCorto((calendar.get(Calendar.MONTH) + 1),contextTmp) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.YEAR)
        }

        estadisticaVentasFechaFinal.text = text
    }

    fun buscarEstadisticaVentas(){
        asignarFechaInicial()
        asignarFechaFinal()
        obtenerTotales()
    }

    fun obtenerFechaInicial() {
        fechaInicial = dialogFecha.fechaInicial
        buscarEstadisticaVentas()
    }

    fun obtenerFechaFinal() {
        fechaFinal = dialogFecha.fechaFinal
        buscarEstadisticaVentas()
    }

    fun obtenerFecha() {
        fechaInicial = dialogFecha.fechaInicial
        fechaFinal = dialogFecha.fechaFinal
        buscarEstadisticaVentas()
    }

    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }

}
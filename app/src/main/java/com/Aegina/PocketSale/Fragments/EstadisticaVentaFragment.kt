@file:Suppress("DEPRECATION")

package com.Aegina.PocketSale.Fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.Fragment
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

class EstadisticaVentaFragment : Fragment() {

    val formatoFecha = SimpleDateFormat("MM-dd-yyyy")
    val formatoFechaCompleta = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    lateinit var fechaInicial : Date
    lateinit var fechaFinal : Date
    lateinit var estadisticaVentasFechaInicial : Button
    lateinit var estadisticaVentasFechaFinal : Button

    val urls: Urls = Urls()
    lateinit var globalVariable: GlobalClass
    lateinit var pieChart : PieChart
    lateinit var estadisticaVentaTotalArticulosText : TextView
    lateinit var estadisticaVentaTotalCostosText : TextView
    lateinit var estadisticaVentaTotalText : TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_estadistica_venta_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        globalVariable = getActivity()?.applicationContext as GlobalClass

        asignarBotones()
        asignarRecursos()
        asignarFechaHoy()
        obtenerTotales()
    }

    fun asignarRecursos(){
        pieChart = estadisticaVentaPiechart
        estadisticaVentaTotalArticulosText = estadisticaVentaTotalArticulos
        estadisticaVentaTotalCostosText = estadisticaVentaTotalCostos
        estadisticaVentaTotalText = estadisticaVentaTotal

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

        val estadisticaVentasBuscarHoy = activity!!.findViewById<Button>(R.id.estadisticaVentasBuscarHoy)
        estadisticaVentasBuscarHoy.setOnClickListener {
            asignarFechaHoy()
        }
        val estadisticaVentasBuscarSemana = activity!!.findViewById<Button>(R.id.estadisticaVentasBuscarSemana)
        estadisticaVentasBuscarSemana.setOnClickListener {
            asignarFechaSemana()
        }
        val estadisticaVentasBuscarMes = activity!!.findViewById<Button>(R.id.estadisticaVentasBuscarMes)
        estadisticaVentasBuscarMes.setOnClickListener {
            asignarFechaMes()
        }

    }

    fun showDialogFechaInicial() {
        val dialog: AlertDialog = AlertDialog.Builder(context).create()
        val inflater = layoutInflater
        val alertDialogView: View = inflater.inflate(R.layout.dialog_fecha, null)
        dialog.setView(alertDialogView)
        val dialogFechaTitulo = alertDialogView.findViewById<View>(R.id.dialogFechaTitulo) as TextView
        val dialogFechaBotonAceptar = alertDialogView.findViewById<View>(R.id.dialogFechaBotonAceptar) as Button
        val dialogFechaBotonCancelar = alertDialogView.findViewById<View>(R.id.dialogFechaBotonCancelar) as Button
        val dialogFechaDatePicker = alertDialogView.findViewById<View>(R.id.dialoFechaDatePicker) as DatePicker

        var calendar = Calendar.getInstance()
        calendar.time = fechaInicial
        calendar = asignarHoraCalendar(calendar, 0, 0, 0)

        dialogFechaDatePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogFechaDatePicker.minDate = formatoFecha.parse("01-01-2020").time
        dialogFechaDatePicker.maxDate = fechaFinal.time

        dialogFechaTitulo.text = getString(R.string.dialog_fecha_inicial)

        dialogFechaBotonAceptar.setOnClickListener {
            val day: Int = dialogFechaDatePicker.dayOfMonth
            val month: Int = dialogFechaDatePicker.month
            val year: Int = dialogFechaDatePicker.year

            var calendarTmp = Calendar.getInstance()
            calendarTmp.set(year, month, day)
            calendarTmp = asignarHoraCalendar(calendarTmp, 0, 0, 0)
            calendarTmp.timeZone = Calendar.getInstance().timeZone;

            fechaInicial = calendarTmp.time
            asignarFechaInicial()
            dialog.dismiss()

            buscarEstadisticaVentas()
        }

        dialogFechaBotonCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun showDialogFechaFinal() {
        val dialog: AlertDialog = AlertDialog.Builder(context).create()
        val inflater = layoutInflater
        val alertDialogView: View = inflater.inflate(R.layout.dialog_fecha, null)
        dialog.setView(alertDialogView)
        val dialogFechaTitulo = alertDialogView.findViewById<View>(R.id.dialogFechaTitulo) as TextView
        val dialogFechaBotonAceptar = alertDialogView.findViewById<View>(R.id.dialogFechaBotonAceptar) as Button
        val dialogFechaBotonCancelar = alertDialogView.findViewById<View>(R.id.dialogFechaBotonCancelar) as Button
        val dialogFechaDatePicker = alertDialogView.findViewById<View>(R.id.dialoFechaDatePicker) as DatePicker

        var calendar = Calendar.getInstance()
        calendar.time = fechaFinal
        calendar = asignarHoraCalendar(calendar, 0, 0, 0)

        dialogFechaDatePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var currentTime = Calendar.getInstance()

        currentTime = asignarHoraCalendar(currentTime, 0, 0, 0)

        dialogFechaDatePicker.minDate = fechaInicial.time
        dialogFechaDatePicker.maxDate = currentTime.time.time


        dialogFechaTitulo.text = getString(R.string.dialog_fecha_final)

        dialogFechaBotonAceptar.setOnClickListener {

            val day: Int = dialogFechaDatePicker.dayOfMonth
            val month: Int = dialogFechaDatePicker.month
            val year: Int = dialogFechaDatePicker.year

            var calendarTmp = Calendar.getInstance()
            calendarTmp.set(year, month, day)
            calendarTmp.timeZone = Calendar.getInstance().timeZone;
            calendarTmp = asignarHoraCalendar(calendarTmp, 23, 59, 59)

            fechaFinal = calendarTmp.time

            asignarFechaFinal()
            dialog.dismiss()

            buscarEstadisticaVentas()
        }

        dialogFechaBotonCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun obtenerTotales() {

        val url = urls.url+urls.endPointEstadisticasPorFecha+
                "?token="+globalVariable.token +
                "&fechaInicial=" + formatoFechaCompleta.format(fechaInicial) +
                "&fechaFinal="+formatoFechaCompleta.format(fechaFinal)

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

                val model = gson.fromJson(body, EstadisticaVentasObject::class.java)

                activity?.runOnUiThread {

                    estadisticaVentaTotalArticulosText.text = (model.totalVentas - model.totalCostos).toString()
                    estadisticaVentaTotalCostosText.text = model.totalCostos.toString()
                    estadisticaVentaTotalText.text = model.totalVentas.toString()


                    pieChart.clearChart()

                    pieChart.addPieSlice(
                        PieModel(
                            "Total Articulos", (model.totalVentas - model.totalCostos).toFloat(),
                            Color.parseColor("#228B22")
                        )
                    )
                    pieChart.addPieSlice(
                        PieModel(
                            "Total Costo", model.totalCostos.toFloat(),
                            Color.parseColor("#FF0000")
                        )
                    )

                    pieChart.startAnimation()
                }

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
        val text = "" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR)
        estadisticaVentasFechaInicial.text = text
    }

    fun asignarFechaFinal(){
        val calendar = Calendar.getInstance()
        calendar.time = fechaFinal
        val text = "" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR)
        estadisticaVentasFechaFinal.text = text
    }

    fun buscarEstadisticaVentas(){
        asignarFechaInicial()
        asignarFechaFinal()
        obtenerTotales()
    }


}
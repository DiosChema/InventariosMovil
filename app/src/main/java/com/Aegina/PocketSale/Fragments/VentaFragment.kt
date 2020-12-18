@file:Suppress("DEPRECATION")

package com.Aegina.PocketSale.Fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Dialogs.DialogFecha
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.Objets.VentasObjeto
import com.Aegina.PocketSale.R
import com.Aegina.PocketSale.RecyclerView.RecyclerViewVentas
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_venta.*
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class VentaFragment : Fragment() {

    var listaTmp:MutableList<VentasObjeto> = ArrayList()
    val context = activity;

    lateinit var mViewVentas : RecyclerViewVentas
    lateinit var mRecyclerView : RecyclerView

    lateinit var globalVariable: GlobalClass
    val urls = Urls()
    val formatoFechaCompleta = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    lateinit var fechaInicial : Date
    lateinit var fechaFinal : Date
    lateinit var fechaInicialButton : Button
    lateinit var fechaFinalButton : Button

    var dialogFecha = DialogFecha()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_venta, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialogFecha.crearVentana(activity!!)
        globalVariable = activity?.applicationContext as GlobalClass

        asignarFechas()
        asignarBotones()
        crearRecyclerView()
        asignarFechaHoy()
        //obtenerVentas()
    }

    fun crearRecyclerView(){
        mViewVentas = RecyclerViewVentas()
        mRecyclerView = rvVentas as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        if (context != null) {
            mViewVentas.RecyclerAdapter(listaTmp, context)
        }
        mRecyclerView.adapter = mViewVentas
    }

    fun asignarBotones(){
        fechaInicialButton = fragmentBuscarPorFechaInicio
        fechaInicialButton.setOnClickListener {
            showDialogFechaInicial()
        }

        fechaFinalButton = fragmentBuscarPorFechaFinal
        fechaFinalButton.setOnClickListener {
            showDialogFechaFinal()
        }

        /*val fragmentVentasBuscarHoy = activity!!.findViewById<Button>(R.id.fragmentVentasBuscarHoy)
        fragmentVentasBuscarHoy.setOnClickListener {
            asignarFechaHoy()
        }
        val fragmentVentasBuscarSemana = activity!!.findViewById<Button>(R.id.fragmentVentasBuscarSemana)
        fragmentVentasBuscarSemana.setOnClickListener {
            asignarFechaSemana()
        }
        val fragmentVentasBuscarMes = activity!!.findViewById<Button>(R.id.fragmentVentasBuscarMes)
        fragmentVentasBuscarMes.setOnClickListener {
            asignarFechaMes()
        }*/
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

    fun buscarEstadisticaVentas(){
        asignarFechaInicial()
        asignarFechaFinal()
        obtenerVentas()
    }

    fun obtenerVentas(){

        val url = urls.url+urls.endPointVentas.endPointBuscarVentaPorFecha+
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
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response)
            {
                var body = response.body()?.string()

                if(body != null && body.isNotEmpty()) {

                    val gson = GsonBuilder().create()
                    var model = gson.fromJson(body, Array<VentasObjeto>::class.java).toList()

                    activity?.runOnUiThread {
                        mViewVentas.RecyclerAdapter(model.reversed().toMutableList(), activity!!)
                        mViewVentas.notifyDataSetChanged()
                        progressDialog.dismiss()
                    }
                }

                progressDialog.dismiss()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        obtenerVentas()
    }

    fun showDialogFechaInicial() {
        dialogFecha.abrirDialogFechaInicial(fechaInicial, fechaFinal)
        /*val dialog: AlertDialog = AlertDialog.Builder(activity).create()
        val inflater = activity!!.layoutInflater
        val alertDialogView: View = inflater.inflate(R.layout.dialog_fecha2, null)
        dialog.setView(alertDialogView)
        val dialogFechaTitulo = alertDialogView.findViewById<View>(R.id.dialogFechaTitulo2) as TextView
        val dialogFechaBotonAceptar = alertDialogView.findViewById<View>(R.id.dialogFechaBotonAceptar2) as Button
        val dialogFechaBotonCancelar = alertDialogView.findViewById<View>(R.id.dialogFechaBotonCancelar2) as Button
        val dialogFechaDatePicker = alertDialogView.findViewById<View>(R.id.dialogFechaDatePicker2) as DatePicker

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

            obtenerVentas()
        }

        dialogFechaBotonCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()*/
    }

    fun showDialogFechaFinal() {
        dialogFecha.abrirDialogFechaFinal(fechaInicial, fechaFinal)
        /*val dialog: AlertDialog = AlertDialog.Builder(activity).create()
        val inflater = activity!!.layoutInflater
        val alertDialogView: View = inflater.inflate(R.layout.dialog_fecha2, null)
        dialog.setView(alertDialogView)
        val dialogFechaTitulo = alertDialogView.findViewById<View>(R.id.dialogFechaTitulo2) as TextView
        val dialogFechaBotonAceptar = alertDialogView.findViewById<View>(R.id.dialogFechaBotonAceptar2) as Button
        val dialogFechaBotonCancelar = alertDialogView.findViewById<View>(R.id.dialogFechaBotonCancelar2) as Button
        val dialogFechaDatePicker = alertDialogView.findViewById<View>(R.id.dialogFechaDatePicker2) as DatePicker

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

            obtenerVentas()
        }

        dialogFechaBotonCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()*/
    }

    fun asignarFechaInicial(){
        val calendar = Calendar.getInstance()
        calendar.time = fechaInicial
        val text = "" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR)
        fechaInicialButton.text = text
    }

    fun asignarFechaFinal(){
        val calendar = Calendar.getInstance()
        calendar.time = fechaFinal
        val text = "" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR)
        fechaFinalButton.text = text
    }

    fun asignarHoraCalendar(calendar : Calendar, hora : Int, minuto : Int, segundo : Int) : Calendar{
        calendar.set(Calendar.HOUR_OF_DAY, hora)
        calendar.set(Calendar.MINUTE, minuto)
        calendar.set(Calendar.SECOND, segundo)

        return calendar
    }

    fun asignarFechas(){
        var calendar = Calendar.getInstance()
        calendar = asignarHoraCalendar(calendar, 23, 59, 59)
        fechaFinal = calendar.time

        calendar = asignarHoraCalendar(calendar, 0, 0, 0)
        fechaInicial = calendar.time
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


}
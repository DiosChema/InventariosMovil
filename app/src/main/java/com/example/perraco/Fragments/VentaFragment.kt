package com.example.perraco.Fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.perraco.Objets.GlobalClass
import com.example.perraco.Objets.Urls
import com.example.perraco.Objets.VentasObjeto
import com.example.perraco.R
import com.example.perraco.RecyclerView.RecyclerViewVentas
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
    val formatoFecha = SimpleDateFormat("MM-dd-yyyy")
    val formatoFechaCompleta = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    lateinit var fechaInicial : Date
    lateinit var fechaFinal : Date
    lateinit var fechaInicialButton : Button
    lateinit var fechaFinalButton : Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_venta, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        globalVariable = activity?.applicationContext as GlobalClass

        asignarFechas()
        asignarBotones()
        crearRecyclerView()
        asignarFechaInicial()
        asignarFechaFinal()
        getVentasObjecto()
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
    }

    fun getVentasObjecto(){

        val url = urls.url+urls.endPointBuscarVentaPorFecha+
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
            override fun onResponse(call: Call, response: Response)
            {
                var body = response.body()?.string()

                if(body != null && body.isNotEmpty()) {

                    val gson = GsonBuilder().create()
                    var Model = gson.fromJson(body, Array<VentasObjeto>::class.java).toList()

                    activity?.runOnUiThread {
                        mViewVentas.RecyclerAdapter(Model.toMutableList(), activity!!)
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
        getVentasObjecto()
    }

    fun showDialogFechaInicial() {
        val dialog: AlertDialog = AlertDialog.Builder(activity).create()
        val inflater = activity!!.layoutInflater
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

        dialogFechaDatePicker.minDate = formatoFecha.parse("01-01-2020").time
        dialogFechaDatePicker.maxDate = fechaFinal.time

        dialogFechaTitulo.text = getString(R.string.dialog_fecha_inicial)

        dialogFechaBotonAceptar.setOnClickListener {
            val day: Int = dialogFechaDatePicker.dayOfMonth
            val month: Int = dialogFechaDatePicker.month
            val year: Int = dialogFechaDatePicker.year

            var calendar = Calendar.getInstance()
            calendar.set(year, month, day)
            calendar = asignarHoraCalendar(calendar, 0, 0, 0)
            calendar.timeZone = Calendar.getInstance().timeZone;

            fechaInicial = calendar.time
            asignarFechaInicial()
            dialog.dismiss()

            getVentasObjecto()
        }

        dialogFechaBotonCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun showDialogFechaFinal() {
        val dialog: AlertDialog = AlertDialog.Builder(activity).create()
        val inflater = activity!!.layoutInflater
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

        var currentTime = Calendar.getInstance()

        currentTime = asignarHoraCalendar(currentTime, 0, 0, 0)

        dialogFechaDatePicker.minDate = fechaInicial.time
        dialogFechaDatePicker.maxDate = currentTime.time.time


        dialogFechaTitulo.text = getString(R.string.dialog_fecha_final)

        dialogFechaBotonAceptar.setOnClickListener {

            val day: Int = dialogFechaDatePicker.dayOfMonth
            val month: Int = dialogFechaDatePicker.month
            val year: Int = dialogFechaDatePicker.year

            var calendar = Calendar.getInstance()
            calendar.set(year, month, day)
            calendar.timeZone = Calendar.getInstance().timeZone;
            calendar = asignarHoraCalendar(calendar, 23, 59, 59)

            fechaFinal = calendar.time

            asignarFechaFinal()
            dialog.dismiss()

            getVentasObjecto()
        }

        dialogFechaBotonCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun asignarFechaInicial(){
        val calendar = Calendar.getInstance()
        calendar.time = fechaInicial
        var text = "" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR)
        fechaInicialButton.text = text
    }

    fun asignarFechaFinal(){
        val calendar = Calendar.getInstance()
        calendar.time = fechaFinal
        var text = "" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR)
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


}
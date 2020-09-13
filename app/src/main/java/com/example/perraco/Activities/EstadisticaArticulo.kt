package com.example.perraco.Activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.perraco.Objets.EstadisticaArticuloObject
import com.example.perraco.Objets.GlobalClass
import com.example.perraco.Objets.Urls
import com.example.perraco.R
import com.example.perraco.RecyclerView.RecyclerViewEstadisticaArticulo
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EstadisticaArticulo : AppCompatActivity() {

    var listaTmp:MutableList<EstadisticaArticuloObject> = ArrayList()
    lateinit var context : Context;
    lateinit var mViewEstadisticaArticulo : RecyclerViewEstadisticaArticulo
    lateinit var mRecyclerView : RecyclerView
    lateinit var globalVariable: GlobalClass

    val formatoFecha = SimpleDateFormat("MM-dd-yyyy")
    val formatoFechaCompleta = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    lateinit var fechaInicial : Date
    lateinit var fechaFinal : Date
    lateinit var estadisticaArticuloFechaInicial : TextView
    lateinit var estadisticaArticuloFechaFinal : TextView

    val urls = Urls()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadistica_articulo)
        context = this
        globalVariable = context.applicationContext as GlobalClass

        asignarBotones()
        crearRecyclerView()
        asignarFechaHoy()
    }

    fun crearRecyclerView(){
        mViewEstadisticaArticulo = RecyclerViewEstadisticaArticulo()
        mRecyclerView = findViewById(R.id.estadisticaArticuloRecyclerView)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mViewEstadisticaArticulo.RecyclerAdapter(listaTmp, context)
        mRecyclerView.adapter = mViewEstadisticaArticulo
    }

    fun asignarBotones(){
        estadisticaArticuloFechaInicial = findViewById(R.id.estadisticaArticuloFechaInicial)
        estadisticaArticuloFechaInicial.setOnClickListener {
            showDialogFechaInicial()
        }

        estadisticaArticuloFechaFinal = findViewById(R.id.estadisticaArticuloFechaFinal)
        estadisticaArticuloFechaFinal.setOnClickListener {
            showDialogFechaFinal()
        }

        val estadisticaArticuloBuscarHoy = findViewById<Button>(R.id.estadisticaArticuloBuscarHoy)
        estadisticaArticuloBuscarHoy.setOnClickListener {
            asignarFechaHoy()
        }
        val estadisticaArticuloBuscarSemana = findViewById<Button>(R.id.estadisticaArticuloBuscarSemana)
        estadisticaArticuloBuscarSemana.setOnClickListener {
            asignarFechaSemana()
        }
        val estadisticaArticuloBuscarMes = findViewById<Button>(R.id.estadisticaArticuloBuscarMes)
        estadisticaArticuloBuscarMes.setOnClickListener {
            asignarFechaMes()
        }


    }

    fun obtenerArticulos(){
        val url = urls.url+urls.endPointArticulosMasVendidos+
                "?token="+globalVariable.token +
                "&fechaInicial=" + formatoFechaCompleta.format(fechaInicial) +
                "&fechaFinal="+formatoFechaCompleta.format(fechaFinal)

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
                var body = response.body()?.string()

                if(body != null && body.isNotEmpty()) {
                    val gson = GsonBuilder().create()
                    var Model = gson.fromJson(body, Array<EstadisticaArticuloObject>::class.java).toList()

                    runOnUiThread {
                        mViewEstadisticaArticulo.RecyclerAdapter(Model.toMutableList(), context)
                        mViewEstadisticaArticulo.notifyDataSetChanged()
                    }
                }

                progressDialog.dismiss()

            }
        })

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

            obtenerArticulos()
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

            obtenerArticulos()
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
        estadisticaArticuloFechaInicial.text = text
    }

    fun asignarFechaFinal(){
        val calendar = Calendar.getInstance()
        calendar.time = fechaFinal
        var text = "" + calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR)
        estadisticaArticuloFechaFinal.text = text
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

        buscarArticulo()
    }

    fun asignarFechaSemana(){
        var calendar = Calendar.getInstance()
        calendar = asignarHoraCalendar(calendar, 23, 59, 59)
        fechaFinal = calendar.time

        calendar.add(Calendar.DAY_OF_MONTH, -6);
        calendar = asignarHoraCalendar(calendar, 0, 0, 0)
        fechaInicial = calendar.time

        buscarArticulo()
    }

    fun asignarFechaMes(){
        var calendar = Calendar.getInstance()
        calendar = asignarHoraCalendar(calendar, 23, 59, 59)
        fechaFinal = calendar.time

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar = asignarHoraCalendar(calendar, 0, 0, 0)
        fechaInicial = calendar.time

        buscarArticulo()
    }

    fun buscarArticulo(){
        asignarFechaInicial()
        asignarFechaFinal()
        obtenerArticulos()
    }
}
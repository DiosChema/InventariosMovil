package com.Aegina.PocketSale.Fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Dialogs.DialogFecha
import com.Aegina.PocketSale.Metodos.Errores
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.Objets.VentasObjeto
import com.Aegina.PocketSale.R
import com.Aegina.PocketSale.RecyclerView.RecyclerViewSurtidos
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_historial_surtido.*
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class HistorialSurtidosFragment : Fragment() {

    var listaTmp:MutableList<VentasObjeto> = ArrayList()
    val context = activity;

    lateinit var globalVariable: GlobalClass
    val urls = Urls()
    val formatoFechaCompleta = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    lateinit var fechaInicial : Date
    lateinit var fechaFinal : Date
    lateinit var fechaInicialButton : Button
    lateinit var fechaFinalButton : Button

    lateinit var mViewVentas : RecyclerViewSurtidos
    lateinit var mRecyclerView : RecyclerView
    var dialogFecha = DialogFecha()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_historial_surtido, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialogFecha.crearVentana(activity!!)
        globalVariable = activity?.applicationContext as GlobalClass

        asignarFechas()
        asignarBotones()
        crearRecyclerView()
        asignarFechaHoy()
        obtenerVentas()
    }

    fun asignarFechas(){
        var calendar = Calendar.getInstance()
        calendar = asignarHoraCalendar(calendar, 23, 59, 59)
        fechaFinal = calendar.time

        calendar = asignarHoraCalendar(calendar, 0, 0, 0)
        fechaInicial = calendar.time
    }

    fun asignarBotones(){
        fechaInicialButton = fragmentHistorialSurtidoBuscarPorFechaInicio
        fechaInicialButton.setOnClickListener {
            dialogFecha.abrirDialogFechaInicial(fechaInicial, fechaFinal)
        }

        fechaFinalButton = fragmentHistorialSurtidoBuscarPorFechaFinal
        fechaFinalButton.setOnClickListener {
            dialogFecha.abrirDialogFechaFinal(fechaInicial, fechaFinal)
        }
    }

    fun crearRecyclerView(){
        mViewVentas = RecyclerViewSurtidos()
        mRecyclerView = fragmentHistorialSurtidoRecyclerView as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        if (context != null) {
            mViewVentas.RecyclerAdapter(listaTmp, context)
        }
        mRecyclerView.adapter = mViewVentas
    }

    fun asignarFechaHoy(){
        var calendar = Calendar.getInstance()
        calendar = asignarHoraCalendar(calendar, 23, 59, 59)
        fechaFinal = calendar.time

        calendar = asignarHoraCalendar(calendar, 0, 0, 0)
        fechaInicial = calendar.time

        buscarSurtidos()
    }

    fun buscarSurtidos(){
        asignarFechaInicial()
        asignarFechaFinal()
        obtenerVentas()
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

    fun obtenerVentas(){

        val url = urls.url+urls.endPointSurtidos.endPointObtenerSurtidos+
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
                activity?.runOnUiThread()
                {
                    Toast.makeText(context, context!!.getString(R.string.mensaje_error), Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response)
            {
                var body = response.body()?.string()

                if(body != null && body.isNotEmpty())
                {
                    try
                    {
                        val gson = GsonBuilder().create()
                        var model = gson.fromJson(body, Array<VentasObjeto>::class.java).toList()

                        activity?.runOnUiThread {
                            mViewVentas.RecyclerAdapter(model.reversed().toMutableList(), activity!!)
                            mViewVentas.notifyDataSetChanged()
                            progressDialog.dismiss()
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

    fun obtenerFechaInicial() {
        fechaInicial = dialogFecha.fechaInicial
        buscarSurtidos()
    }

    fun obtenerFechaFinal() {
        fechaFinal = dialogFecha.fechaFinal
        buscarSurtidos()
    }

    fun obtenerFecha() {
        fechaInicial = dialogFecha.fechaInicial
        fechaFinal = dialogFecha.fechaFinal
        buscarSurtidos()
    }

}
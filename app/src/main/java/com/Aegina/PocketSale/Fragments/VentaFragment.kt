@file:Suppress("DEPRECATION")

package com.Aegina.PocketSale.Fragments

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Dialogs.DialogFecha
import com.Aegina.PocketSale.Metodos.Errores
import com.Aegina.PocketSale.Metodos.Meses
import com.Aegina.PocketSale.Metodos.Paginado
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.Objets.Ventas.ListVentasObject
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
    val context = activity

    lateinit var mViewVentas : RecyclerViewVentas
    lateinit var mRecyclerView : RecyclerView

    lateinit var globalVariable: GlobalClass
    val urls = Urls()
    val formatoFechaCompleta = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    lateinit var fechaInicial : Date
    lateinit var fechaFinal : Date
    lateinit var fechaInicialButton : Button
    lateinit var fechaFinalButton : Button
    lateinit var fragmentVentaLeftButton : ImageButton
    lateinit var fragmentVentaRightButton : ImageButton

    var dialogFecha = DialogFecha()
    val nombreMes = Meses()
    lateinit var contextTmp : Context
    val paginado = Paginado()

    var pagina = 0
    var limiteArticulos = 10
    var totalArticulos = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_venta, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialogFecha.crearVentana(activity!!)
        globalVariable = activity?.applicationContext as GlobalClass

        contextTmp = activity!!.applicationContext

        asignarFechas()
        asignarBotones()
        crearRecyclerView()
        asignarFechaSemana()
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
            dialogFecha.abrirDialogFechaInicial(fechaInicial, fechaFinal)
        }

        fechaFinalButton = fragmentBuscarPorFechaFinal
        fechaFinalButton.setOnClickListener {
            dialogFecha.abrirDialogFechaFinal(fechaInicial, fechaFinal)
        }

        fragmentVentaLeftButton = fragmentVentaLeft
        fragmentVentaLeftButton.setOnClickListener()
        {
            if(totalArticulos > limiteArticulos && pagina > 0)
            {
                pagina--
                obtenerVentas()
            }
        }

        fragmentVentaRightButton = fragmentVentaRight
        fragmentVentaRightButton.setOnClickListener()
        {
            val maximoPaginas = paginado.obtenerPaginadoMaximo(totalArticulos,limiteArticulos)

            if(totalArticulos > limiteArticulos && pagina < maximoPaginas)
            {
                pagina++
                obtenerVentas()
            }
        }

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
        pagina = 0
        obtenerVentas()
    }

    fun obtenerVentas(){

        val url = urls.url+urls.endPointVentas.endPointBuscarVentaPorFecha+
                "?token="+globalVariable.usuario!!.token +
                "&fechaInicial=" + formatoFechaCompleta.format(fechaInicial) +
                "&fechaFinal="+formatoFechaCompleta.format(fechaFinal) +
                "&pagina="+ pagina +
                "&limit="+ limiteArticulos

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
            override fun onResponse(call: Call, response: Response)
            {
                var body = response.body()?.string()

                if(body != null && body.isNotEmpty()) {
                    try
                    {
                        val gson = GsonBuilder().create()
                        var model = gson.fromJson(body, ListVentasObject::class.java)

                        totalArticulos = model.count
                        activity?.runOnUiThread {
                            mViewVentas.RecyclerAdapter(model.sales.toMutableList(), activity!!)
                            resetAdapterState()
                            progressDialog.dismiss()

                            if(totalArticulos > limiteArticulos)
                            {
                                fragmentVentaLeftButton.visibility = View.VISIBLE
                                fragmentVentaRightButton.visibility = View.VISIBLE
                            }
                            else
                            {
                                fragmentVentaLeftButton.visibility = View.GONE
                                fragmentVentaRightButton.visibility = View.GONE
                            }
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

    private fun resetAdapterState() {
        val myAdapter = mRecyclerView.adapter
        mRecyclerView.adapter = myAdapter
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

        fechaInicialButton.text = text
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

    override fun onResume() {
        super.onResume()

        globalVariable = activity?.applicationContext as GlobalClass

        if(globalVariable.actualizarVentana!!.actualizarVentas)
        {
            globalVariable.actualizarVentana!!.actualizarVentas = false
            obtenerVentas()
        }
    }

}
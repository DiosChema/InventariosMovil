package com.Aegina.PocketSale.Dialogs

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentActivity
import com.Aegina.PocketSale.R
import java.text.SimpleDateFormat
import java.util.*

class DialogFecha : AppCompatDialogFragment(){

    val formatoFecha = SimpleDateFormat("MM-dd-yyyy")
    val formatoFechaCompleta = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    lateinit var fechaTmp : Date
    lateinit var fechaInicial : Date
    lateinit var fechaFinal : Date

    lateinit var dialogFechaTitulo : TextView
    lateinit var estadisticaVentasBuscarHoy : Button
    lateinit var estadisticaVentasBuscarSemana : Button
    lateinit var estadisticaVentasBuscarMes : Button
    lateinit var dialogFechaDatePicker : DatePicker
    lateinit var dialogFechaBotonCancelar : Button
    lateinit var dialogFechaBotonAceptar : Button

    lateinit var fecha: DialogFecha
    lateinit var dialogFecha : Dialog

    var tipoFechaInicial = false
    var dialogCreada = false

    lateinit var contextDialog : Context

    fun crearVentana(context: FragmentActivity){

        dialogCreada = true
        dialogFecha = Dialog(context)

        contextDialog = context

        dialogFecha.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogFecha.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        dialogFecha.setCancelable(false)
        dialogFecha.setContentView(R.layout.dialog_fecha)

        fecha = context as DialogFecha
        asignarBotones()
    }

    fun crearVentana(context: Context){

        dialogCreada = true
        dialogFecha = Dialog(context)

        contextDialog = context

        dialogFecha.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogFecha.setCancelable(false)
        dialogFecha.setContentView(R.layout.dialog_fecha)

        fecha = context as DialogFecha
        asignarBotones()
    }

    fun abrirDialogFechaInicial(fechaInicialTmp : Date, fechaFinalTmp : Date){
        dialogFechaTitulo.text = contextDialog.getString(R.string.dialog_fecha_inicial)

        tipoFechaInicial = true
        asignarFechaInicial(fechaInicialTmp, fechaFinalTmp)
        dialogFecha.show()
    }

    fun abrirDialogFechaFinal(fechaInicialTmp : Date, fechaFinalTmp : Date){
        dialogFechaTitulo.text = contextDialog.getString(R.string.dialog_fecha_final)

        tipoFechaInicial = false
        asignarFechaFinal(fechaInicialTmp, fechaFinalTmp)
        dialogFecha.show()
    }

    fun asignarBotones(){

        dialogFechaTitulo = dialogFecha.findViewById(R.id.dialogFechaTitulo)
        estadisticaVentasBuscarHoy = dialogFecha.findViewById(R.id.estadisticaVentasBuscarHoy)
        estadisticaVentasBuscarSemana = dialogFecha.findViewById(R.id.estadisticaVentasBuscarSemana)
        estadisticaVentasBuscarMes = dialogFecha.findViewById(R.id.estadisticaVentasBuscarMes)
        dialogFechaDatePicker = dialogFecha.findViewById(R.id.dialogFechaDatePicker)
        dialogFechaBotonCancelar = dialogFecha.findViewById(R.id.dialogFechaBotonCancelar)
        dialogFechaBotonAceptar = dialogFecha.findViewById(R.id.dialogFechaBotonAceptar)

        estadisticaVentasBuscarHoy.setOnClickListener {asignarFechaHoy()}
        estadisticaVentasBuscarSemana.setOnClickListener {asignarFechaSemana()}
        estadisticaVentasBuscarMes.setOnClickListener {asignarFechaMes()}

        dialogFechaBotonCancelar.setOnClickListener {dialogFecha.dismiss()}
        dialogFechaBotonAceptar.setOnClickListener {asignarFecha()}

    }

    fun asignarFecha(){
        if(tipoFechaInicial) {
            asignarFechaInicial()

            fecha.obtenerFechaInicial()
        }
        else {
            asignarFechaFinal()
            fecha.obtenerFechaFinal()
        }

        dialogFecha.dismiss()
    }

    fun asignarFechaInicial(){
        val day: Int = dialogFechaDatePicker.dayOfMonth
        val month: Int = dialogFechaDatePicker.month
        val year: Int = dialogFechaDatePicker.year

        var calendarTmp = Calendar.getInstance()
        calendarTmp.set(year, month, day)
        calendarTmp = asignarHoraCalendar(calendarTmp, 0, 0, 0)
        calendarTmp.timeZone = Calendar.getInstance().timeZone

        fechaInicial = calendarTmp.time
    }

    fun asignarFechaFinal() {
        val day: Int = dialogFechaDatePicker.dayOfMonth
        val month: Int = dialogFechaDatePicker.month
        val year: Int = dialogFechaDatePicker.year

        var calendarTmp = Calendar.getInstance()
        calendarTmp.set(year, month, day)
        calendarTmp = asignarHoraCalendar(calendarTmp, 23, 59, 59)
        calendarTmp.timeZone = Calendar.getInstance().timeZone;

        fechaFinal = calendarTmp.time
    }

    fun asignarFechaInicial(fechaInicialTmp : Date, fechaFinalTmp : Date){

        fechaInicial = fechaInicialTmp
        fechaFinal = fechaFinalTmp

        dialogFechaDatePicker.minDate = formatoFecha.parse("01-01-2021").time
        dialogFechaDatePicker.maxDate = fechaFinal.time

        val calendar = Calendar.getInstance()
        calendar.time = fechaInicial
        dialogFechaDatePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    }

    fun asignarFechaFinal(fechaInicialTmp : Date, fechaFinalTmp : Date){
        fechaInicial = fechaInicialTmp
        fechaFinal = fechaFinalTmp

        var currentTime = Calendar.getInstance()
        currentTime = asignarHoraCalendar(currentTime, 0, 0, 0)

        dialogFechaDatePicker.minDate = fechaInicial.time
        dialogFechaDatePicker.maxDate = currentTime.time.time

        val calendar = Calendar.getInstance()
        calendar.time = fechaFinal
        dialogFechaDatePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    }

    fun asignarFechaHoy(){
        var calendar = Calendar.getInstance()
        calendar = asignarHoraCalendar(calendar, 0, 0, 0)
        fechaInicial = calendar.time

        calendar = asignarHoraCalendar(calendar, 23, 59, 59)
        fechaFinal = calendar.time

        fecha.obtenerFecha()
        dialogFecha.dismiss()
    }

    fun asignarFechaSemana(){
        var calendar = Calendar.getInstance()
        calendar = asignarHoraCalendar(calendar, 0, 0, 0)
        fechaFinal = calendar.time

        calendar.add(Calendar.DAY_OF_MONTH, -6)

        calendar = asignarHoraCalendar(calendar, 23, 59, 59)
        fechaInicial = calendar.time

        fecha.obtenerFecha()
        dialogFecha.dismiss()
    }

    fun asignarFechaMes(){
        var calendar = Calendar.getInstance()
        calendar = asignarHoraCalendar(calendar, 0, 0, 0)
        fechaFinal = calendar.time
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        calendar = asignarHoraCalendar(calendar, 23, 59, 59)
        fechaInicial = calendar.time

        fecha.obtenerFecha()
        dialogFecha.dismiss()
    }



    fun asignarHoraCalendar(calendar : Calendar, hora : Int, minuto : Int, segundo : Int) : Calendar{
        calendar.set(Calendar.HOUR_OF_DAY, hora)
        calendar.set(Calendar.MINUTE, minuto)
        calendar.set(Calendar.SECOND, segundo)

        return calendar
    }

    interface DialogFecha {
        fun obtenerFechaInicial()
        fun obtenerFechaFinal()
        fun obtenerFecha()
    }

}
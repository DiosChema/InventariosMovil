package com.Aegina.PocketSale.Metodos

import android.content.Context
import android.widget.Switch
import com.Aegina.PocketSale.R

class Meses
{
    fun obtenerMes(mes: Int, context: Context) : String
    {
        var textoMes = ""

        when (mes) {
            1 -> textoMes = context.getString(R.string.mes_enero)
            2 -> textoMes = context.getString(R.string.mes_febrero)
            3 -> textoMes = context.getString(R.string.mes_marzo)
            4 -> textoMes = context.getString(R.string.mes_abril)
            5 -> textoMes = context.getString(R.string.mes_mayo)
            6 -> textoMes = context.getString(R.string.mes_junio)
            7 -> textoMes = context.getString(R.string.mes_julio)
            8 -> textoMes = context.getString(R.string.mes_agosto)
            9 -> textoMes = context.getString(R.string.mes_septiempre)
            10 -> textoMes = context.getString(R.string.mes_octubre)
            11 -> textoMes = context.getString(R.string.mes_noviembre)
            12 -> textoMes = context.getString(R.string.mes_diciembre)
        }

        return textoMes
    }

    fun obtenerMesCorto(mes: Int, context: Context) : String
    {
        var textoMes = ""

        when (mes) {
            1 -> textoMes = context.getString(R.string.mes_enero)
            2 -> textoMes = context.getString(R.string.mes_febrero)
            3 -> textoMes = context.getString(R.string.mes_marzo)
            4 -> textoMes = context.getString(R.string.mes_abril)
            5 -> textoMes = context.getString(R.string.mes_mayo)
            6 -> textoMes = context.getString(R.string.mes_junio)
            7 -> textoMes = context.getString(R.string.mes_julio)
            8 -> textoMes = context.getString(R.string.mes_agosto)
            9 -> textoMes = context.getString(R.string.mes_septiempre)
            10 -> textoMes = context.getString(R.string.mes_octubre)
            11 -> textoMes = context.getString(R.string.mes_noviembre)
            12 -> textoMes = context.getString(R.string.mes_diciembre)
        }

        return textoMes.substring(0,3)
    }
}
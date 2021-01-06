package com.Aegina.PocketSale.RecyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.SuscripcionObject
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.squareup.picasso.Picasso
import java.lang.Double.parseDouble
import java.lang.Exception

class RecyclerViewSuscripcion : RecyclerView.Adapter<RecyclerViewSuscripcion.ViewHolder>() {

    var suscripcion: MutableList<SuscripcionObject> = ArrayList()
    lateinit var context: Context

    fun RecyclerAdapter(suscripcion: MutableList<SuscripcionObject>, context: Context) {
        this.suscripcion = suscripcion
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = suscripcion[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_suscripcion,parent,false))
    }

    override fun getItemCount(): Int {return suscripcion.size}

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val urls = Urls()
        var globalVariable = view.context.applicationContext as GlobalClass

        val itemSuscripcionMeses = view.findViewById(R.id.itemSuscripcionMeses) as TextView
        val itemSuscripcionMesesText = view.findViewById(R.id.itemSuscripcionMesesText) as TextView
        val itemSuscripcionPrecioMeses = view.findViewById(R.id.itemSuscripcionPrecioMeses) as TextView
        val itemSuscripcionPrecioMesesText = view.findViewById(R.id.itemSuscripcionPrecioMesesText) as TextView
        val itemSuscripcionPrecioTotal = view.findViewById(R.id.itemSuscripcionPrecioTotal) as TextView
        val itemSuscripcionPrecioTotalText = view.findViewById(R.id.itemSuscripcionPrecioTotalText) as TextView

        fun bind(suscripcion: SuscripcionObject) {
            var textTmp = ""
            itemSuscripcionMeses.text = suscripcion.numeroMeses.toString()

            textTmp =
                if(suscripcion.numeroMeses > 1)
                    itemView.context.getString(R.string.suscripcion_meses)
                else
                    itemView.context.getString(R.string.suscripcion_mes)

            itemSuscripcionMesesText.text = textTmp

            if(suscripcion.numeroMeses > 1) {
                val precioSuscripcion = parseDouble(suscripcion.precioSuscripcion.substring(1,suscripcion.precioSuscripcion.length))
                textTmp = (precioSuscripcion / suscripcion.numeroMeses).toString().substring(0,5) + "/" + itemView.context.getString(R.string.suscripcion_meses_diminutivo)
                itemSuscripcionPrecioMeses.text = textTmp

                var porcensajeDescuento = (precioSuscripcion * 100) / (suscripcion.precioMes * suscripcion.numeroMeses)

                var porcentajeAhorro = (porcensajeDescuento.toInt() - 100) * -1
                textTmp = itemView.context.getString(R.string.suscripcion_meses_descuento) + ": " + porcentajeAhorro + "%"
                itemSuscripcionPrecioMesesText.text = textTmp
            }
            else{
                itemSuscripcionPrecioMeses.text = ""
                itemSuscripcionPrecioMesesText.text = ""
            }


            textTmp = suscripcion.precioSuscripcion
            itemSuscripcionPrecioTotal.text = textTmp

            textTmp = itemView.context.getString(R.string.suscripcion_meses_total)
            itemSuscripcionPrecioTotalText.text = textTmp
        }

        fun ImageView.loadUrl(url: String) {
            try {Picasso.with(context).load(url).into(this)}
            catch(e: Exception){}
        }
    }
}
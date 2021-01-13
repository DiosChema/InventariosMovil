package com.Aegina.PocketSale.RecyclerView

import android.content.Context
import android.text.SpannableString
import android.text.style.StrikethroughSpan
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

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {return suscripcion.size}

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val urls = Urls()
        var globalVariable = view.context.applicationContext as GlobalClass

        val itemSuscripcionMeses = view.findViewById(R.id.itemSuscripcionMeses) as TextView
        val itemSuscripcionPrecio = view.findViewById(R.id.itemSuscripcionPrecio) as TextView
        val itemSuscripcionPorcentajeDescuento = view.findViewById(R.id.itemSuscripcionPorcentajeDescuento) as TextView
        val itemSuscripcionPrecioTotal = view.findViewById(R.id.itemSuscripcionPrecioTotal) as TextView

        fun bind(suscripcion: SuscripcionObject) {
            var textTmp = ""


            textTmp = suscripcion.numeroMeses.toString() + " "

            textTmp +=
                if(suscripcion.numeroMeses > 1)
                    itemView.context.getString(R.string.suscripcion_meses)
                else
                    itemView.context.getString(R.string.suscripcion_mes)


            itemSuscripcionMeses.text = textTmp

            if(suscripcion.numeroMeses > 1) {
                textTmp = "$" + (suscripcion.precioMes * suscripcion.numeroMeses).toString()
                itemSuscripcionPrecioTotal.text = textTmp

                val spannableString2 = SpannableString(textTmp)
                spannableString2.setSpan(StrikethroughSpan(),1,textTmp.length,0)
                itemSuscripcionPrecioTotal.text = spannableString2

                val precioSuscripcion = parseDouble(suscripcion.precioSuscripcion.substring(1,suscripcion.precioSuscripcion.length))
                var porcentajeDescuento = precioSuscripcion * 100 / (suscripcion.precioMes * suscripcion.numeroMeses)
                val porcentajeAhorro = (porcentajeDescuento.toInt() - 100) * -1

                itemSuscripcionPorcentajeDescuento.text = itemView.context.getString(R.string.suscripcion_meses_descuento) + " " + porcentajeAhorro + "%"
            }
            else{
                itemSuscripcionPorcentajeDescuento.text = ""
                itemSuscripcionPrecioTotal.text = ""
            }


            textTmp = suscripcion.precioSuscripcion
            itemSuscripcionPrecio.text = textTmp

            /*textTmp = itemView.context.getString(R.string.suscripcion_meses_total)
            itemSuscripcionPrecioTotal.text = textTmp*/
        }

        fun ImageView.loadUrl(url: String) {
            try {Picasso.with(context).load(url).into(this)}
            catch(e: Exception){}
        }

        fun Double.round(decimals: Int): Double {
            var multiplier = 1.0
            repeat(decimals) { multiplier *= 10 }
            return kotlin.math.round(this * multiplier) / multiplier
        }
    }
}
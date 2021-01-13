package com.Aegina.PocketSale.RecyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.InventarioObjeto
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.squareup.picasso.Picasso
import java.lang.Exception
import kotlin.math.round

class RecyclerViewVenta : RecyclerView.Adapter<RecyclerViewVenta.ViewHolder>() {

    var articulos: MutableList<InventarioObjeto> = ArrayList()
    lateinit var context: Context

    fun RecyclerAdapter(articulos: MutableList<InventarioObjeto>, context: Context) {
        this.articulos = articulos
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articulos.get(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_articulo,parent,false))
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {return articulos.size}

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val urls = Urls()
        var globalVariable = view.context.applicationContext as GlobalClass

        val itemArticuloImagen = view.findViewById(R.id.itemArticuloImagen) as ImageView
        val itemArticuloNombre = view.findViewById(R.id.itemArticuloNombre) as TextView
        val itemArticuloDescipcion = view.findViewById(R.id.itemArticuloDescipcion) as TextView
        val itemArticuloPrecio = view.findViewById(R.id.itemArticuloPrecio) as TextView
        val itemArticuloPrecioTotalText = view.findViewById(R.id.itemArticuloPrecioTotalText) as TextView
        val itemArticuloPrecioTotal = view.findViewById(R.id.itemArticuloPrecioTotal) as TextView
        val itemArticuloCantidad = view.findViewById(R.id.itemArticuloCantidad) as TextView
        val itemArticuloContainer = view.findViewById(R.id.itemArticuloContainer) as LinearLayout

        fun bind(venta: InventarioObjeto) {
            when (position % 2) {
                1 -> itemArticuloContainer.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.backgroundventa1))
                0 -> itemArticuloContainer.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.backgroundventa2))
                else -> {
                }
            }

            itemArticuloNombre.text = venta.nombre
            itemArticuloDescipcion.text = venta.descripcion

            var textTmp = itemView.context.getString(R.string.venta_precio_por_articulo) + " $" + venta.precio.round(2)
            itemArticuloPrecio.text = textTmp

            textTmp = itemView.context.getString(R.string.ventas_inventario_cantidad) + " " + venta.cantidad
            itemArticuloCantidad.text = textTmp

            itemArticuloPrecioTotalText.text = itemView.context.getString(R.string.venta_total)
            textTmp = "$" + (venta.precio * venta.cantidad).round(2).toString()
            itemArticuloPrecioTotal.text =  textTmp

            itemArticuloImagen.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+venta.idArticulo+".jpeg"+"&token="+globalVariable.usuario!!.token)
        }

        fun ImageView.loadUrl(url: String) {
            try {Picasso.with(context).load(url).into(this)}
            catch(e: Exception){}
        }

        fun Double.round(decimals: Int): Double {
            var multiplier = 1.0
            repeat(decimals) { multiplier *= 10 }
            return round(this * multiplier) / multiplier
        }
    }
}
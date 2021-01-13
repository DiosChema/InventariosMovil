package com.Aegina.PocketSale.RecyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Objets.EstadisticaArticuloObject
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.squareup.picasso.Picasso
import java.lang.Exception

class RecyclerViewEstadisticaArticulo : RecyclerView.Adapter<RecyclerViewEstadisticaArticulo.ViewHolder>() {

    var articulos: MutableList<EstadisticaArticuloObject>  = ArrayList()
    lateinit var context: Context

    fun RecyclerAdapter(articulos : MutableList<EstadisticaArticuloObject>, context: Context){
        this.articulos = articulos
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articulos.get(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                R.layout.item_articulo_inventario,
                parent,
                false
            )
        )
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return articulos.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemArticuloInventarioFoto = view.findViewById(R.id.itemArticuloInventarioFoto) as ImageView
        val itemArticuloInventarioNombre = view.findViewById(R.id.itemArticuloInventarioNombre) as TextView
        val itemArticuloInventarioDescipcion = view.findViewById(R.id.itemArticuloInventarioDescipcion) as TextView
        val itemArticuloInventarioCantidad = view.findViewById(R.id.itemArticuloInventarioCantidad) as TextView
        val itemArticuloInventarioPrecio = view.findViewById(R.id.itemArticuloInventarioPrecio) as TextView
        val itemArticuloInventarioContainer = view.findViewById(R.id.itemArticuloInventarioContainer) as LinearLayout

        var globalVariable = itemView.context.applicationContext as GlobalClass

        fun bind(articulo: EstadisticaArticuloObject) {
            itemArticuloInventarioNombre.text = articulo.articulo[0].nombre
            itemArticuloInventarioDescipcion.text = articulo.articulo[0].descripcion
            var textTmp = itemView.context.getString(R.string.ventas_inventario_cantidad) + " " + articulo.cantidad.toString()
            itemArticuloInventarioCantidad.text = textTmp
            textTmp = itemView.context.getString(R.string.venta_total) + " " + articulo.total.round(2).toString()
            itemArticuloInventarioPrecio.text = textTmp
            val urls = Urls()
            itemArticuloInventarioFoto.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+articulo.articulo[0].idArticulo+".jpeg"+"&token="+globalVariable.usuario!!.token)

            when (position % 2) {
                1 -> itemArticuloInventarioContainer.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.backgroundventa2))
                0 -> itemArticuloInventarioContainer.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.backgroundventa1))
                else -> {
                }
            }
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


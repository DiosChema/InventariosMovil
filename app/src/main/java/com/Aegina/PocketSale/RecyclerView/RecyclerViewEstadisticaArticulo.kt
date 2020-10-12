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
                R.layout.item_estadistica_articulo,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return articulos.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val estadisticaArticuloTitulo = view.findViewById(R.id.itemEstadisticaArticuloTitulo) as TextView
        val estadisticaArticuloPrecio = view.findViewById(R.id.itemEstadisticaArticuloPrecio) as TextView
        val estadisticaArticuloDescripcion = view.findViewById(R.id.itemEstadisticaArticuloDescripcion) as TextView
        val estadisticaArticuloCantidad = view.findViewById(R.id.itemEstadisticaArticuloCantidad) as TextView
        val estadisticaArticuloTotal = view.findViewById(R.id.itemEstadisticaArticuloTotal) as TextView
//        val itemSurtidoArticuloCantidadText = view.findViewById(R.id.itemSurtidoArticuloCantidadText) as TextView
//        val itemSurtidoArticuloTotalText = view.findViewById(R.id.itemSurtidoArticuloTotalText) as TextView
        val estadisticaArticuloFoto = view.findViewById(R.id.itemEstadisticaArticuloFoto) as ImageView
        val estadisticaArticuloContenedor = view.findViewById(R.id.estadisticaArticuloContenedor) as LinearLayout

        var globalVariable = itemView.context.applicationContext as GlobalClass

        fun bind(articulo: EstadisticaArticuloObject) {
            estadisticaArticuloTitulo.text = articulo._id.nombreArticulo
            var textTmp = itemView.context.getString(R.string.venta_precio_por_articulo) + ":$" + articulo._id.precioArticulo
            estadisticaArticuloPrecio.text = textTmp
            estadisticaArticuloDescripcion.text = articulo._id.descripcionArticulo
            textTmp = itemView.context.getString(R.string.ventas_inventario_cantidad) + articulo.cantidad.toString()
            estadisticaArticuloCantidad.text = textTmp
            textTmp = itemView.context.getString(R.string.venta_total) + articulo.total.toString()
            estadisticaArticuloTotal.text = textTmp
            val urls = Urls()
            estadisticaArticuloFoto.loadUrl(urls.url+urls.endPointImagenes+articulo._id.idArticulo+".jpeg"+"&token="+globalVariable.token)

            when (position % 2) {
                1 -> estadisticaArticuloContenedor.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.backgroundventa2))
                0 -> estadisticaArticuloContenedor.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.backgroundventa1))
                else -> {
                }
            }
        }

        fun ImageView.loadUrl(url: String) {
            Picasso.with(context).load(url).into(this)
        }
    }
}


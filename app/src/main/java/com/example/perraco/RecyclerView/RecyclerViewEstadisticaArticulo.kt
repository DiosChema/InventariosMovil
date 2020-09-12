package com.example.perraco.RecyclerView

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.perraco.Activities.InventarioDetalle
import com.example.perraco.Objets.EstadisticaArticuloObject
import com.example.perraco.Objets.GlobalClass
import com.example.perraco.Objets.Urls
import com.example.perraco.R
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
        holder.bind(item, context)
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
        val estadisticaArticuloTitulo = view.findViewById(R.id.estadisticaArticuloTitulo) as TextView
        val estadisticaArticuloPrecio = view.findViewById(R.id.estadisticaArticuloPrecio) as TextView
        val estadisticaArticuloDescripcion = view.findViewById(R.id.estadisticaArticuloDescripcion) as TextView
        val estadisticaArticuloCantidad = view.findViewById(R.id.estadisticaArticuloCantidad) as TextView
        val estadisticaArticuloTotal = view.findViewById(R.id.estadisticaArticuloTotal) as TextView
        val estadisticaArticuloFoto = view.findViewById(R.id.estadisticaArticuloFoto) as ImageView

        var globalVariable = itemView.context.applicationContext as GlobalClass

        fun bind(articulo: EstadisticaArticuloObject, context: Context) {
            estadisticaArticuloTitulo.text = articulo._id.nombreArticulo
            estadisticaArticuloPrecio.text = articulo._id.precioArticulo.toString()
            estadisticaArticuloDescripcion.text = articulo._id.descripcionArticulo
            estadisticaArticuloCantidad.text = itemView.context.getString(R.string.mensaje_cantidad_articulo_diminutivo) + articulo.cantidad
            estadisticaArticuloTotal.text = itemView.context.getText(R.string.mensaje_total_venta).toString() + articulo.total.toString()
            val urls = Urls()
            estadisticaArticuloFoto.loadUrl(urls.url+urls.endPointImagenes+articulo._id.idArticulo+".jpeg"+"&token="+globalVariable.token)
        }

        fun ImageView.loadUrl(url: String) {
            Picasso.with(context).load(url).into(this)
        }
    }
}


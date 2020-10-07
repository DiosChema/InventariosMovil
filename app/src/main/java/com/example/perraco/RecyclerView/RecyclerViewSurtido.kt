package com.example.perraco.RecyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.perraco.Objets.GlobalClass
import com.example.perraco.Objets.InventarioObjeto
import com.example.perraco.Objets.Urls
import com.example.perraco.R
import com.squareup.picasso.Picasso

class RecyclerViewSurtido : RecyclerView.Adapter<RecyclerViewSurtido.ViewHolder>() {

    var articulos: MutableList<InventarioObjeto> = ArrayList()
    lateinit var context: Context

    fun RecyclerAdapter(articulos: MutableList<InventarioObjeto>, context: Context) {
        this.articulos = articulos
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articulos.get(position)
        holder.bind(item, context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_surtido_articulo,parent,false))
    }

    override fun getItemCount(): Int {return articulos.size}

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val urls = Urls()
        var globalVariable = view.context.applicationContext as GlobalClass

        val itemSurtidoArticuloFoto = view.findViewById(R.id.itemSurtidoArticuloFoto) as ImageView
        val itemSurtidoArticuloTitulo = view.findViewById(R.id.itemSurtidoArticuloTitulo) as TextView
        val itemSurtidoArticuloCantidad = view.findViewById(R.id.itemSurtidoArticuloCantidad) as TextView
        val itemSurtidoArticuloTotal = view.findViewById(R.id.itemSurtidoArticuloTotal) as TextView
        val itemSurtidoArticuloPrecioText = view.findViewById(R.id.itemSurtidoArticuloPrecioText) as TextView
        val itemSurtidoArticuloPrecio = view.findViewById(R.id.itemSurtidoArticuloPrecio) as TextView

        fun bind(venta: InventarioObjeto, context: Context) {
            itemSurtidoArticuloTitulo.text = venta.nombreArticulo
            itemSurtidoArticuloPrecio.text = "$" +venta.costoArticulo.toString()
            itemSurtidoArticuloPrecioText.text = itemView.context.getString(R.string.surtido_costo)
            itemSurtidoArticuloCantidad.text = itemView.context.getString(R.string.venta_cantidad_articulo_diminutivo) + venta.cantidadArticulo.toString()
            itemSurtidoArticuloTotal.text = itemView.context.getString(R.string.venta_total) + (venta.costoArticulo * venta.cantidadArticulo).toString()
            itemSurtidoArticuloFoto.loadUrl(urls.url+urls.endPointImagenes+venta.idArticulo+".jpeg"+"&token="+globalVariable.token)
        }

        fun ImageView.loadUrl(url: String) {
            Picasso.with(context).load(url).into(this)
        }
    }
}
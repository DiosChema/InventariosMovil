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
        //val itemSurtidoArticuloPrecioText = view.findViewById(R.id.itemSurtidoArticuloPrecioText) as TextView
        val itemSurtidoArticuloPrecioTotal = view.findViewById(R.id.itemSurtidoArticuloPrecioTotal) as TextView
        val itemSurtidoArticuloPrecio = view.findViewById(R.id.itemSurtidoArticuloPrecio) as TextView
        val itemArticuloInventarioContenedor = view.findViewById(R.id.itemArticuloInventarioContenedor) as LinearLayout

        fun bind(venta: InventarioObjeto) {

            when (position % 2) {
                //1 -> itemArticuloInventarioContenedor.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.backgroundarticulo2))
                //0 -> itemArticuloInventarioContenedor.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.backgroundarticulo1))
                else -> {
                }
            }

            itemSurtidoArticuloTitulo.text = venta.nombreArticulo
            var textTmp = itemView.context.getString(R.string.venta_precio_articulo) + "\n$" +venta.precioArticulo
            itemSurtidoArticuloPrecio.text = textTmp
            textTmp = itemView.context.getString(R.string.ventas_inventario_cantidad) + " " + venta.cantidadArticulo.toString()
            //itemSurtidoArticuloPrecioText.text = itemView.context.getString(R.string.venta_precio_articulo)
            itemSurtidoArticuloCantidad.text = textTmp
            textTmp = itemView.context.getString(R.string.venta_total) + "\n$" + (venta.precioArticulo * venta.cantidadArticulo).toString()
            itemSurtidoArticuloPrecioTotal.text = textTmp
            //itemSurtidoArticuloTotal.text = textTmp
            itemSurtidoArticuloFoto.loadUrl(urls.url+urls.endPointImagenes+venta.idArticulo+".jpeg"+"&token="+globalVariable.token)
        }

        fun ImageView.loadUrl(url: String) {
            Picasso.with(context).load(url).into(this)
        }
    }
}
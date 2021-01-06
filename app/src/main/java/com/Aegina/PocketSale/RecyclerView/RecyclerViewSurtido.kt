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

class RecyclerViewSurtido : RecyclerView.Adapter<RecyclerViewSurtido.ViewHolder>() {

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
        return ViewHolder(layoutInflater.inflate(R.layout.item_articulo_surtido,parent,false))
    }

    override fun getItemCount(): Int {return articulos.size}

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val urls = Urls()
        var globalVariable = view.context.applicationContext as GlobalClass

        val itemSurtidoImagen = view.findViewById(R.id.itemSurtidoImagen) as ImageView
        val itemSurtidoNombre = view.findViewById(R.id.itemSurtidoNombre) as TextView
        val itemSurtidoDescipcion = view.findViewById(R.id.itemSurtidoDescipcion) as TextView
        val itemSurtidoPrecio = view.findViewById(R.id.itemSurtidoPrecio) as TextView
        val itemSurtidoPrecioTotalText = view.findViewById(R.id.itemSurtidoPrecioTotalText) as TextView
        val itemSurtidoPrecioTotal = view.findViewById(R.id.itemSurtidoPrecioTotal) as TextView
        val itemSurtidoCantidad = view.findViewById(R.id.itemSurtidoCantidad) as TextView
        val itemSurtidoContainer = view.findViewById(R.id.itemSurtidoContainer) as LinearLayout

        fun bind(venta: InventarioObjeto) {
            when (position % 2) {
                1 -> itemSurtidoContainer.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.backgroundventa1))
                0 -> itemSurtidoContainer.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.backgroundventa2))
                else -> {
                }
            }

            itemSurtidoNombre.text = venta.nombreArticulo
            itemSurtidoDescipcion.text = venta.descripcionArticulo

            var textTmp = itemView.context.getString(R.string.venta_precio_por_articulo) + "$" + venta.costoArticulo
            itemSurtidoPrecio.text = textTmp

            textTmp = itemView.context.getString(R.string.ventas_inventario_cantidad) + " " + venta.cantidadArticulo
            itemSurtidoCantidad.text = textTmp

            itemSurtidoPrecioTotalText.text = itemView.context.getString(R.string.venta_total)
            textTmp = "$" + (venta.costoArticulo * venta.cantidadArticulo).toString()
            itemSurtidoPrecioTotal.text =  textTmp

            itemSurtidoImagen.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+venta.idArticulo+".jpeg"+"&token="+globalVariable.usuario!!.token)
        }

        fun ImageView.loadUrl(url: String) {
            try {Picasso.with(context).load(url).into(this)}
            catch(e: Exception){}
        }
    }
}
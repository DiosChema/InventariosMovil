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
        return ViewHolder(layoutInflater.inflate(R.layout.item_articulo_venta,parent,false))
    }

    override fun getItemCount(): Int {return articulos.size}

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val urls = Urls()
        var globalVariable = view.context.applicationContext as GlobalClass

        val itemArticuloVentaImagen = view.findViewById(R.id.itemArticuloVentaImagen) as ImageView
        val itemArticuloVentaNombre = view.findViewById(R.id.itemArticuloVentaNombre) as TextView
        val itemArticuloVentaDescipcion = view.findViewById(R.id.itemArticuloVentaDescipcion) as TextView
        val itemArticuloVentaPrecio = view.findViewById(R.id.ventaArticulosPrecio) as TextView
        val itemArticuloVentaPrecioTotalText = view.findViewById(R.id.itemArticuloVentaPrecioTotalText) as TextView
        val itemArticuloVentaPrecioTotal = view.findViewById(R.id.itemArticuloVentaPrecioTotal) as TextView
        val itemArticuloVentaCantidad = view.findViewById(R.id.itemArticuloVentaCantidad) as TextView
        val itemArticuloVentaContainer = view.findViewById(R.id.itemArticuloVentaContainer) as LinearLayout

        fun bind(venta: InventarioObjeto) {

            when (position % 2) {
                1 -> itemArticuloVentaContainer.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.backgroundventa1))
                0 -> itemArticuloVentaContainer.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.backgroundventa2))
                else -> {
                }
            }

            itemArticuloVentaNombre.text = venta.nombreArticulo
            itemArticuloVentaDescipcion.text = venta.descripcionArticulo

            var textTmp = itemView.context.getString(R.string.venta_precio_por_articulo) + "$" + venta.precioArticulo
            itemArticuloVentaPrecio.text = textTmp

            textTmp = itemView.context.getString(R.string.ventas_inventario_cantidad) + " " + venta.cantidadArticulo
            itemArticuloVentaCantidad.text = textTmp

            itemArticuloVentaPrecioTotalText.text = itemView.context.getString(R.string.venta_total)
            textTmp = "$" + (venta.precioArticulo * venta.cantidadArticulo).toString()
            itemArticuloVentaPrecioTotal.text =  textTmp

            itemArticuloVentaImagen.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+venta.idArticulo+".jpeg"+"&token="+globalVariable.usuario!!.token)
        }

        fun ImageView.loadUrl(url: String) {
            Picasso.with(context).load(url).into(this)
        }
    }
}
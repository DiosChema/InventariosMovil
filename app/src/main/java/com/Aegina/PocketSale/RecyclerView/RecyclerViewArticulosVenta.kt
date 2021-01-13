package com.Aegina.PocketSale.RecyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Objets.ArticuloVentaObject
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.squareup.picasso.Picasso
import java.lang.Exception


class RecyclerViewArticulosVenta : RecyclerView.Adapter<RecyclerViewArticulosVenta.ViewHolder>() {

    var articulosInventario: MutableList<ArticuloVentaObject>  = ArrayList()
    lateinit var context:Context

    fun RecyclerAdapter(articulos : MutableList<ArticuloVentaObject>, context: Context){
        this.articulosInventario = articulos
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articulosInventario.get(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                R.layout.item_articulos_lista,
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
        return articulosInventario.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemArticulosListaFoto = view.findViewById(R.id.itemArticulosListaFoto) as ImageView

        val itemArticulosListaNombre = view.findViewById(R.id.itemArticulosListaNombre) as TextView
        val itemArticulosListaDescipcion = view.findViewById(R.id.itemArticulosListaDescipcion) as TextView
        val itemArticulosListaCantidad = view.findViewById(R.id.itemArticulosListaCantidad) as TextView
        val itemArticulosListaPrecio = view.findViewById(R.id.itemArticulosListaPrecio) as TextView

        var globalVariable = itemView.context.applicationContext as GlobalClass
        val urls = Urls()

        fun bind(articulo: ArticuloVentaObject) {

            itemArticulosListaNombre.text = articulo.articulosDetalle[0].nombre
            itemArticulosListaDescipcion.text = articulo.articulosDetalle[0].descripcion

            var textTmp = itemView.context.getString(R.string.ventas_inventario_cantidad) + " " + articulo.cantidad
            itemArticulosListaCantidad.text = textTmp

            textTmp = itemView.context.getString(R.string.venta_total) + " $" + (articulo.precio * articulo.cantidad).round(2)
            itemArticulosListaPrecio.text = textTmp

            itemArticulosListaFoto.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+articulo.idArticulo+".jpeg"+"&token="+globalVariable.usuario!!.token)
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
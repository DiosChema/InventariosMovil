package com.Aegina.PocketSale.RecyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Objets.ArticuloInventarioObjeto
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.squareup.picasso.Picasso
import java.lang.Exception


class RecyclerViewArticulos : RecyclerView.Adapter<RecyclerViewArticulos.ViewHolder>() {

    var articulosVenta: MutableList<ArticuloInventarioObjeto>  = ArrayList()
    lateinit var context: Context
    var tipoArticulos = 0

    fun RecyclerAdapter(articulos : MutableList<ArticuloInventarioObjeto>, context: Context){
        this.articulosVenta = articulos
        this.context = context
    }

    fun tipoArticulos(tipoArticulo: Int)
    {
        tipoArticulos = tipoArticulo
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articulosVenta.get(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                com.Aegina.PocketSale.R.layout.item_articulo_inventario,
                parent,
                false
            ), tipoArticulos
        )
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return articulosVenta.size
    }

    class ViewHolder(view: View, tipoArticulos: Int) : RecyclerView.ViewHolder(view) {
        val itemArticuloInventarioFoto = view.findViewById(R.id.itemArticuloInventarioFoto) as ImageView

        val itemArticuloInventarioNombre = view.findViewById(R.id.itemArticuloInventarioNombre) as TextView
        val itemArticuloInventarioDescipcion = view.findViewById(R.id.itemArticuloInventarioDescipcion) as TextView
        val itemArticuloInventarioCantidad = view.findViewById(R.id.itemArticuloInventarioCantidad) as TextView
        val itemArticuloInventarioPrecio = view.findViewById(R.id.itemArticuloInventarioPrecio) as TextView
        val itemArticuloInventarioContainer = view.findViewById(R.id.itemArticuloInventarioContainer) as LinearLayout

        val tipoArticulo = tipoArticulos

        var globalVariable = itemView.context.applicationContext as GlobalClass
        val urls = Urls()

        fun bind(articulo: ArticuloInventarioObjeto) {

            when (position % 2) {
                1 -> itemArticuloInventarioContainer.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.background_item_lista1))
                0 -> itemArticuloInventarioContainer.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.background_item_lista2))
                else -> {
                }
            }

            itemArticuloInventarioNombre.text = articulo.nombre
            itemArticuloInventarioDescipcion.text = articulo.descripcion

            var textTmp = itemView.context.getString(R.string.ventas_inventario_cantidad) + articulo.cantidad.toString()
            itemArticuloInventarioCantidad.text = textTmp

            textTmp = if(tipoArticulo == 0)
                {
                    itemView.context.getString(R.string.venta_precio_por_articulo) + " $" + articulo.precio.round(2)
                }
                else
                {
                    itemView.context.getString(R.string.venta_precio_por_articulo) + " $" + articulo.costo.round(2)
                }

            itemArticuloInventarioPrecio.text = textTmp
            itemArticuloInventarioFoto.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+articulo.idArticulo+".jpeg"+"&token="+globalVariable.usuario!!.token)
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
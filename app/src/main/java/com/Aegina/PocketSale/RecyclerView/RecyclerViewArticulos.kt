package com.Aegina.PocketSale.RecyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.InventarioObjeto
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_articulo.view.*


class RecyclerViewArticulos : RecyclerView.Adapter<RecyclerViewArticulos.ViewHolder>() {

    var articulosVenta: MutableList<InventarioObjeto>  = ArrayList()
    lateinit var context: Context

    fun RecyclerAdapter(articulos : MutableList<InventarioObjeto>, context: Context){
        this.articulosVenta = articulos
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articulosVenta.get(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                com.Aegina.PocketSale.R.layout.item_articulo,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return articulosVenta.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val urls = Urls()
        val articuloFoto = view.findViewById(com.Aegina.PocketSale.R.id.itemArticuloFoto) as ImageView
        val articuloNombre = view.findViewById(com.Aegina.PocketSale.R.id.itemArticuloNombre) as TextView
        val itemArticuloDescripcion = view.findViewById(com.Aegina.PocketSale.R.id.itemArticuloDescripcion) as TextView
        val itemArticuloPrecio = view.findViewById(com.Aegina.PocketSale.R.id.itemArticuloPrecio) as TextView

        var globalVariable = itemView.context.applicationContext as GlobalClass

        fun bind(articulo: InventarioObjeto) {
            articuloNombre.text = articulo.nombreArticulo
            itemArticuloDescripcion.text = articulo.descripcionArticulo
            val textTmp = itemView.context.getString(R.string.venta_precio_articulo) + articulo.precioArticulo
            itemArticuloPrecio.text = textTmp
            articuloFoto.loadUrl(urls.url+urls.endPointImagenes+articulo.idArticulo+".jpeg"+"&token="+globalVariable.token)
        }

        fun ImageView.loadUrl(url: String) {
            Picasso.with(context).load(url).into(this)
        }
    }
}
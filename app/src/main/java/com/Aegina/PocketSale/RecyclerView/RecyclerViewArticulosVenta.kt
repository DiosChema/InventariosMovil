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
                R.layout.item_articulos_en_venta,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return articulosInventario.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre = view.findViewById(R.id.ventaArticulosNombre) as TextView
        val ventaArticulosPrecioTotalText = view.findViewById(R.id.itemArticuloVentaPrecioTotalText) as TextView
        val ventaArticulosPrecioTotal = view.findViewById(R.id.itemArticuloVentaPrecioTotal) as TextView
        val ventaArticulosDescipcion = view.findViewById(R.id.ventaArticulosDescipcion) as TextView
        val ventaArticulosDescipcionText = view.findViewById(R.id.ventaArticulosDescipcionText) as TextView
        val imagen = view.findViewById(R.id.ventaArticulosFoto) as ImageView

        var globalVariable = itemView.context.applicationContext as GlobalClass

        fun bind(articulo: ArticuloVentaObject) {
            nombre.text = articulo.articulosDetalle[0].nombreArticulo
            ventaArticulosDescipcion.text = articulo.articulosDetalle[0].descripcionArticulo
            var textTmp = itemView.context.getString(R.string.ventas_inventario_cantidad) + " " + articulo.cantidad
            ventaArticulosDescipcionText.text = textTmp
            ventaArticulosPrecioTotalText.text = itemView.context.getText(R.string.venta_total)
            textTmp = "$" + (articulo.precio * articulo.cantidad)
            ventaArticulosPrecioTotal.text = textTmp

            val urls = Urls()
            imagen.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+articulo.idArticulo+".jpeg"+"&token="+globalVariable.usuario!!.token)
        }

        fun ImageView.loadUrl(url: String) {
            try {Picasso.with(context).load(url).into(this)}
            catch(e: Exception){}
        }
    }
}
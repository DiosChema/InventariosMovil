package com.example.perraco.RecyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.perraco.Objets.ArticuloVentaObject
import com.example.perraco.Objets.GlobalClass
import com.example.perraco.Objets.Urls
import com.example.perraco.R
import com.squareup.picasso.Picasso


class RecyclerViewArticulosVenta : RecyclerView.Adapter<RecyclerViewArticulosVenta.ViewHolder>() {

    var articulosInventario: MutableList<ArticuloVentaObject>  = ArrayList()
    lateinit var context:Context

    fun RecyclerAdapter(articulos : MutableList<ArticuloVentaObject>, context: Context){
        this.articulosInventario = articulos
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articulosInventario.get(position)
        holder.bind(item, context)
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
        /*val precio = view.findViewById(R.id.ventaArticulosPrecio) as TextView
        val precioText = view.findViewById(R.id.ventaArticulosPrecioText) as TextView*/
        val ventaArticulosPrecioTotalText = view.findViewById(R.id.ventaArticulosPrecioTotalText) as TextView
        val ventaArticulosPrecioTotal = view.findViewById(R.id.ventaArticulosPrecioTotal) as TextView
        val ventaArticulosDescipcion = view.findViewById(R.id.ventaArticulosDescipcion) as TextView
        val ventaArticulosDescipcionText = view.findViewById(R.id.ventaArticulosDescipcionText) as TextView
        val imagen = view.findViewById(R.id.ventaArticulosFoto) as ImageView

        var globalVariable = itemView.context.applicationContext as GlobalClass

        fun bind(articulo: ArticuloVentaObject, context: Context) {
            nombre.text = articulo.articulosDetalle[0].nombreArticulo
            /*precio.text = "$" + articulo.precio.toString()
            precioText.text = itemView.context.getString(R.string.mensaje_precio_articulo);*/
            ventaArticulosDescipcion.text = articulo.articulosDetalle[0].descripcionArticulo
            ventaArticulosDescipcionText.text = itemView.context.getString(R.string.venta_cantidad_articulo_diminutivo) + " " + articulo.cantidad
            ventaArticulosPrecioTotalText.text = itemView.context.getText(R.string.venta_total)
            ventaArticulosPrecioTotal.text = "$" + (articulo.precio * articulo.cantidad)

            val urls = Urls()
            imagen.loadUrl(urls.url+urls.endPointImagenes+articulo.idArticulo+".jpeg"+"&token="+globalVariable.token)
        }

        fun ImageView.loadUrl(url: String) {
            Picasso.with(context).load(url).into(this)
        }
    }
}
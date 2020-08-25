package com.example.perraco.RecyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.perraco.Objets.InventarioObjeto
import com.example.perraco.Objets.Urls
import com.example.perraco.R
import com.squareup.picasso.Picasso

class RecyclerViewVenta : RecyclerView.Adapter<RecyclerViewVenta.ViewHolder>() {

    var articulosVenta: MutableList<InventarioObjeto>  = ArrayList()
    lateinit var context: Context

    fun RecyclerAdapter(articulos : MutableList<InventarioObjeto>, context: Context){
        this.articulosVenta = articulos
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articulosVenta.get(position)
        holder.bind(item, context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                R.layout.item_articulo_venta,
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
        val nombreArticulo = view.findViewById(R.id.VentaNombre) as TextView
        val precio = view.findViewById(R.id.VentaPrecio) as TextView
        val cantidad = view.findViewById(R.id.VentaCantidad) as EditText
        val imagenArticulo = view.findViewById(R.id.VentaFoto) as ImageView
        var disminuirCantidad = view.findViewById(R.id.VentaDisminuirCantidad) as ImageButton
        var aumentarCantidad = view.findViewById(R.id.VentaAnadirCantidad) as ImageButton
        var ventaIdArticulo = view.findViewById(R.id.VentaIdArticulo) as TextView

        fun bind(articulo: InventarioObjeto, context: Context) {
            nombreArticulo.text = articulo.nombreArticulo
            precio.text = /*"$" + */articulo.precioArticulo.toString()
            cantidad.setText(articulo.cantidadArticulo.toString())
            ventaIdArticulo.text = articulo.idArticulo
            disminuirCantidad.setOnClickListener(View.OnClickListener {
                var cantidadArticulo = Integer.parseInt(cantidad.text.toString()) - 1
                if(cantidadArticulo >= 0)
                    cantidad.setText((cantidadArticulo.toString()))
            })

            aumentarCantidad.setOnClickListener(View.OnClickListener {
                var cantidadArticulo = Integer.parseInt(cantidad.text.toString()) + 1
                cantidad.setText((cantidadArticulo.toString()))
            })

            imagenArticulo.loadUrl(urls.url+urls.endPointImagenes+articulo.idArticulo+".png")
        }

        fun ImageView.loadUrl(url: String) {
            Picasso.with(context).load(url).into(this)
        }
    }
}
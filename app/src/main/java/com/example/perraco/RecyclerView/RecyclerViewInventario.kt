package com.example.perraco.RecyclerView

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.perraco.Activities.InventarioDetalle
import com.example.perraco.Objets.InventarioObjeto
import com.example.perraco.Objets.Urls
import com.example.perraco.R
import com.squareup.picasso.Picasso


class RecyclerViewInventario : RecyclerView.Adapter<RecyclerViewInventario.ViewHolder>() {

    var articulosInventario: MutableList<InventarioObjeto>  = ArrayList()
    lateinit var context:Context

    fun RecyclerAdapter(articulos : MutableList<InventarioObjeto>, context: Context){
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
                R.layout.item_inventario,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return articulosInventario.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre = view.findViewById(R.id.invNombre) as TextView
        val descripcion = view.findViewById(R.id.invDescipcion) as TextView
        val precio = view.findViewById(R.id.invPrecio) as TextView
        val precioText = view.findViewById(R.id.invPrecioText) as TextView
        val imagen = view.findViewById(R.id.invFoto) as ImageView

        fun bind(articulo: InventarioObjeto, context: Context) {
            nombre.text = articulo.nombreArticulo
            descripcion.text = articulo.descripcionArticulo
            precio.setText("$" + articulo.precioArticulo)
            precioText.text = itemView.context.getString(R.string.mensaje_precio_articulo);
            itemView.setOnClickListener(View.OnClickListener {
                val intent = Intent(context, InventarioDetalle::class.java).apply {
                    putExtra("articulo", articulo)//tienda
                }
                context.startActivity(intent)
            })
            val urls = Urls()
            imagen.loadUrl(urls.url+urls.endPointImagenes+articulo.idArticulo+".png")
        }

        fun ImageView.loadUrl(url: String) {
            Picasso.with(context).load(url).into(this)
        }
    }
}
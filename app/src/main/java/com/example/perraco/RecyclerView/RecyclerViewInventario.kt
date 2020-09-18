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
import com.example.perraco.Objets.GlobalClass
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
        val item = articulosInventario[position]
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
        val itemArticuloInventarioNombre = view.findViewById(R.id.itemArticuloInventarioNombre) as TextView
        val itemArticuloInventarioDescipcion = view.findViewById(R.id.itemArticuloInventarioDescipcion) as TextView
        val itemArticuloInventarioPrecio = view.findViewById(R.id.itemArticuloInventarioPrecio) as TextView
        val itemArticuloInventarioPrecioText = view.findViewById(R.id.itemArticuloInventarioPrecioText) as TextView
        val itemArticuloInventarioFoto = view.findViewById(R.id.itemArticuloInventarioFoto) as ImageView
        val itemArticuloInventarioCantidad = view.findViewById(R.id.itemArticuloInventarioCantidad) as TextView

        var globalVariable = itemView.context.applicationContext as GlobalClass
        val urls = Urls()

        fun bind(articulo: InventarioObjeto, context: Context) {
            itemArticuloInventarioNombre.text = articulo.nombreArticulo
            itemArticuloInventarioDescipcion.text = articulo.descripcionArticulo
            itemArticuloInventarioPrecio.text = "$" + articulo.precioArticulo
            itemArticuloInventarioPrecioText.text = itemView.context.getString(R.string.mensaje_precio_articulo);
            itemArticuloInventarioCantidad.text = itemView.context.getString(R.string.mensaje_cantidad_articulo_diminutivo) + articulo.cantidadArticulo
            itemView.setOnClickListener(View.OnClickListener {
                val intent = Intent(context, InventarioDetalle::class.java).apply {
                    putExtra("articulo", articulo)
                }
                context.startActivity(intent)
            })
            itemArticuloInventarioFoto.loadUrl(urls.url+urls.endPointImagenes+articulo.idArticulo+".jpeg"+"&token="+globalVariable.token)
        }

        fun ImageView.loadUrl(url: String) {
            Picasso.with(context).load(url).into(this)
        }
    }
}
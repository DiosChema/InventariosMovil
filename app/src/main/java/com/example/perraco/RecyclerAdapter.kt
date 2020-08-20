package com.example.perraco

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    var superheros: MutableList<InventarioObjeto>  = ArrayList()
    lateinit var context:Context

    fun RecyclerAdapter(superheros : MutableList<InventarioObjeto>, context: Context){
        this.superheros = superheros
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = superheros.get(position)
        holder.bind(item, context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_inventario, parent, false))
    }

    override fun getItemCount(): Int {
        return superheros.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val superheroName = view.findViewById(R.id.invNombre) as TextView
        val realName = view.findViewById(R.id.invDescipcion) as TextView
        val precio = view.findViewById(R.id.invPrecio) as TextView
        val precioText = view.findViewById(R.id.invPrecioText) as TextView
        val avatar = view.findViewById(R.id.invFoto) as ImageView

        fun bind(articulo: InventarioObjeto, context: Context) {
            superheroName.text = articulo.nombreArticulo
            realName.text = articulo.descripcionArticulo
            precio.text = "$" + articulo.precioArticulo.toString()
            precioText.text = "Precio:";
            itemView.setOnClickListener(View.OnClickListener {
                val intent = Intent(context, InventarioDetalle::class.java).apply {
                    putExtra("articulo", articulo)//tienda
                }
                context.startActivity(intent)
            })
            val urls = Urls()
            avatar.loadUrl(urls.url+urls.endPointImagenes)
        }

        fun ImageView.loadUrl(url: String) {
            Picasso.with(context).load(url).into(this)
        }
    }
}
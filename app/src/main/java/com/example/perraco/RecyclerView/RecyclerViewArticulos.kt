package com.example.perraco.RecyclerView

import android.R
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.perraco.Objets.GlobalClass
import com.example.perraco.Objets.InventarioObjeto
import com.example.perraco.Objets.Urls
import com.squareup.picasso.Picasso
import java.lang.Double


class RecyclerViewArticulos : RecyclerView.Adapter<RecyclerViewArticulos.ViewHolder>() {

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
                com.example.perraco.R.layout.item_articulo,
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
        val articuloFoto = view.findViewById(com.example.perraco.R.id.articuloFoto) as ImageView
        val articuloNombre = view.findViewById(com.example.perraco.R.id.articuloNombre) as TextView

        var globalVariable = itemView.context.applicationContext as GlobalClass

        fun bind(articulo: InventarioObjeto, context: Context) {
            articuloNombre.text = articulo.nombreArticulo
            articuloFoto.loadUrl(urls.url+urls.endPointImagenes+articulo.idArticulo+".jpeg"+"&token="+globalVariable.token)
        }

        fun ImageView.loadUrl(url: String) {
            Picasso.with(context).load(url).into(this)
        }
    }
}
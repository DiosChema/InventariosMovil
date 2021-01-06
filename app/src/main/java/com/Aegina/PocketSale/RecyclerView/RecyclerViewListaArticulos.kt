package com.Aegina.PocketSale.RecyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Objets.ArticuloInventarioObjeto
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.InventarioObjeto
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.squareup.picasso.Picasso
import java.lang.Exception


class RecyclerViewListaArticulos : RecyclerView.Adapter<RecyclerViewListaArticulos.ViewHolder>() {

    var articulosInventario: MutableList<ArticuloInventarioObjeto>  = ArrayList()
    lateinit var context:Context

    fun RecyclerAdapter(articulos : MutableList<ArticuloInventarioObjeto>, context: Context){
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
                R.layout.item_lista_articulos,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return articulosInventario.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemListaFoto = view.findViewById(R.id.itemListaFoto) as ImageView
        val itemListaCantidad = view.findViewById(R.id.itemListaCantidad) as TextView

        var globalVariable = itemView.context.applicationContext as GlobalClass
        val urls = Urls()

        fun bind(articulo: ArticuloInventarioObjeto, context: Context) {

            itemListaCantidad.text = articulo.cantidadArticulo.toString()

            /*itemView.setOnClickListener {
                if(globalVariable.usuario!!.permisosModificarInventario)
                {
                    val intent = Intent(context, InventarioDetalle::class.java).apply {
                        putExtra("articulo", articulo)
                    }
                    context.startActivity(intent)
                }
            }*/
            itemListaFoto.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+articulo.idArticulo+".jpeg"+"&token="+globalVariable.usuario!!.token)
        }

        fun ImageView.loadUrl(url: String) {
            try {Picasso.with(context).load(url).into(this)}
            catch(e: Exception){}
        }
    }
}
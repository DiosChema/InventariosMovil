package com.Aegina.PocketSale.RecyclerView

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Activities.PerfilDetalle
import com.Aegina.PocketSale.Activities.bar_chart
import com.Aegina.PocketSale.Objets.EstadisticaArticuloObject
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.squareup.picasso.Picasso
import java.lang.Exception

class RecyclerViewEstadisticaArticulo : RecyclerView.Adapter<RecyclerViewEstadisticaArticulo.ViewHolder>() {

    var articulos: MutableList<EstadisticaArticuloObject>  = ArrayList()
    lateinit var context: Context

    fun RecyclerAdapter(articulos : MutableList<EstadisticaArticuloObject>, context: Context){
        this.articulos = articulos
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articulos.get(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                R.layout.item_articulo_inventario,
                parent,
                false
            )
        )
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return articulos.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemArticuloInventarioFoto = view.findViewById(R.id.itemArticuloInventarioFoto) as ImageView
        val itemArticuloInventarioNombre = view.findViewById(R.id.itemArticuloInventarioNombre) as TextView
        val itemArticuloInventarioDescipcion = view.findViewById(R.id.itemArticuloInventarioDescipcion) as TextView
        val itemArticuloInventarioCantidad = view.findViewById(R.id.itemArticuloInventarioCantidad) as TextView
        val itemArticuloInventarioPrecio = view.findViewById(R.id.itemArticuloInventarioPrecio) as TextView

        var globalVariable = itemView.context.applicationContext as GlobalClass

        fun bind(articulo: EstadisticaArticuloObject) {
            itemArticuloInventarioNombre.text = articulo.articulo.nombre
            itemArticuloInventarioDescipcion.text = articulo.articulo.descripcion
            var textTmp = articulo.cantidad.toString()
            itemArticuloInventarioCantidad.text = textTmp
            textTmp = articulo.total.round(2).toString()
            itemArticuloInventarioPrecio.text = textTmp
            val urls = Urls()
            itemArticuloInventarioFoto.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+articulo.articulo.idArticulo+".jpeg"+"&token="+globalVariable.usuario!!.token)

            /*itemView.setOnClickListener()
            {
                val intent = Intent(context, bar_chart::class.java)
                context.startActivity(intent)
            }*/
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


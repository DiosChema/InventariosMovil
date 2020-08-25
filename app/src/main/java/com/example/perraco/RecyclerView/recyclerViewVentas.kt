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
import com.example.perraco.Objets.VentasObjeto
import com.example.perraco.R
import com.squareup.picasso.Picasso

class RecyclerViewVentas : RecyclerView.Adapter<RecyclerViewVentas.ViewHolder>() {

    var ventas: MutableList<VentasObjeto> = ArrayList()
    lateinit var context: Context

    fun RecyclerAdapter(articulos: MutableList<VentasObjeto>, context: Context) {
        this.ventas = articulos
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = ventas.get(position)
        holder.bind(item, context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                R.layout.item_ventas,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return ventas.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val urls = Urls()
        val VentasNumeroVenta = view.findViewById(R.id.VentasNumeroVenta) as TextView
        val VentasFecha = view.findViewById(R.id.VentasFecha) as TextView
        val VentasTotalVenta = view.findViewById(R.id.VentasTotalVenta) as TextView

        fun bind(articulo: VentasObjeto, context: Context) {
            VentasNumeroVenta.text = "Venta No. "+articulo._id.toString()
            VentasFecha.text = articulo.fecha
            VentasTotalVenta.text = "Total: \$"+articulo.totalVenta.toString()
        }

        fun ImageView.loadUrl(url: String) {
            Picasso.with(context).load(url).into(this)
        }
    }
}
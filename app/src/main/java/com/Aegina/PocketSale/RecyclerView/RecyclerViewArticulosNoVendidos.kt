package com.Aegina.PocketSale.RecyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.Inventory.InventoryDateObject
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.text.SimpleDateFormat

class RecyclerViewArticulosNoVendidos : RecyclerView.Adapter<RecyclerViewArticulosNoVendidos.ViewHolder>() {

    var articulos: MutableList<InventoryDateObject>  = ArrayList()
    lateinit var context: Context

    fun RecyclerAdapter(articulos : MutableList<InventoryDateObject>, context: Context){
        this.articulos = articulos
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articulos[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                R.layout.item_articulo_no_vendidos,
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
        val itemArticuloNoVendidosFoto = view.findViewById(R.id.itemArticuloNoVendidosFoto) as ImageView
        val itemArticuloNoVendidosNombre = view.findViewById(R.id.itemArticuloNoVendidosNombre) as TextView
        val itemArticuloNoVendidosDescipcion = view.findViewById(R.id.itemArticuloNoVendidosDescipcion) as TextView
        val itemArticuloNoVendidosFecha = view.findViewById(R.id.itemArticuloNoVendidosFecha) as TextView
        val itemArticuloNoVendidosContainer = view.findViewById(R.id.itemArticuloNoVendidosContainer) as LinearLayout

        var simpleDate: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        var simpleDateHours: SimpleDateFormat = SimpleDateFormat("HH:mm:ss")
        var globalVariable = itemView.context.applicationContext as GlobalClass

        fun bind(articulo: InventoryDateObject) {
            itemArticuloNoVendidosNombre.text = articulo.nombre
            itemArticuloNoVendidosDescipcion.text = articulo.descripcion
            val textTmp = itemView.context.getString(R.string.ventas_inventario_fecha) + " " + simpleDate.format(articulo.fecha) + " " + simpleDateHours.format(articulo.fecha)
            itemArticuloNoVendidosFecha.text = textTmp

            val urls = Urls()
            itemArticuloNoVendidosFoto.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+articulo.idArticulo+".jpeg"+"&token="+globalVariable.usuario!!.token)

            when (position % 2) {
                1 -> itemArticuloNoVendidosContainer.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.backgroundventa2))
                0 -> itemArticuloNoVendidosContainer.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.backgroundventa1))
                else -> {
                }
            }

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


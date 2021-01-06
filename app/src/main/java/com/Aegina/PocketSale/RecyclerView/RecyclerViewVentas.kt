package com.Aegina.PocketSale.RecyclerView

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Activities.VentaDetalle
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.Objets.VentasObjeto
import com.Aegina.PocketSale.R
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.text.SimpleDateFormat
import androidx.core.view.isVisible as isVisible1

class RecyclerViewVentas : RecyclerView.Adapter<RecyclerViewVentas.ViewHolder>() {

    var ventas: MutableList<VentasObjeto> = ArrayList()
    lateinit var context: Context

    fun RecyclerAdapter(articulos: MutableList<VentasObjeto>, context: Context) {
        this.ventas = articulos
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = ventas[position]
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
        val ventasFragmentNumeroVenta = view.findViewById(R.id.ventasFragmentNumeroVenta) as TextView
        val ventasFragmentText = view.findViewById(R.id.ventasFragmentText) as TextView
        val ventasFragmentFecha = view.findViewById(R.id.ventasFragmentFecha) as TextView
        val ventasFragmentHora = view.findViewById(R.id.ventasFragmentHora) as TextView
        val ventasFragmentTotalVenta = view.findViewById(R.id.ventasFragmentTotalVenta) as TextView
        val ventasFragmentTotalCantidad = view.findViewById(R.id.ventasFragmentTotalCantidad) as TextView
        val ventasFragmentRecyclerViewArticulos = view.findViewById(R.id.ventasFragmentRecyclerViewArticulos) as RecyclerView
        val ventasFragmentBotonEditar = view.findViewById(R.id.ventasFragmentBotonEditar) as ImageView
        val ventasFragmentContenedor = view.findViewById(R.id.ventasFragmentContenedor) as LinearLayout

        var simpleDate: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        var simpleDateHours: SimpleDateFormat = SimpleDateFormat("HH:mm:ss")

        fun bind(venta: VentasObjeto, context: Context) {
            var tamanoOriginal = 0
            var cantidadArticulos = 0
            val mViewVentas = RecyclerViewArticulosVenta()
            val mRecyclerView : RecyclerView = ventasFragmentRecyclerViewArticulos
            mRecyclerView.setHasFixedSize(true)
            mRecyclerView.layoutManager = LinearLayoutManager(context)
            if (context != null) {
                mViewVentas.RecyclerAdapter(venta.articulos.toMutableList(), context)
            }
            mRecyclerView.adapter = mViewVentas

            when (position % 2) {
                1 -> ventasFragmentContenedor.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.backgroundventa2))
                0 -> ventasFragmentContenedor.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.backgroundventa1))
                else -> {
                }
            }

            itemView.setOnClickListener {
                if(ventasFragmentRecyclerViewArticulos.isVisible1) {
                    ventasFragmentRecyclerViewArticulos.visibility = View.INVISIBLE
                    val params: ViewGroup.LayoutParams = mRecyclerView.layoutParams
                    params.height = tamanoOriginal
                    mRecyclerView.layoutParams = params
                } else {
                    if(tamanoOriginal == 0) tamanoOriginal = ventasFragmentRecyclerViewArticulos.layoutParams.height

                    ventasFragmentRecyclerViewArticulos.visibility = View.VISIBLE
                    val params: ViewGroup.LayoutParams = mRecyclerView.layoutParams
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    mRecyclerView.layoutParams = params
                }
            }

            val nuevoTamano: ViewGroup.LayoutParams = ventasFragmentRecyclerViewArticulos.layoutParams
            nuevoTamano.height = 0
            ventasFragmentRecyclerViewArticulos.layoutParams = nuevoTamano
            ventasFragmentRecyclerViewArticulos.visibility = View.INVISIBLE

            ventasFragmentNumeroVenta.text = venta._id.toString()
            ventasFragmentText.text = itemView.context.getString(R.string.ventas_inventario_fecha)
            ventasFragmentFecha.text = simpleDate.format(venta.fecha)
            ventasFragmentHora.text = simpleDateHours.format(venta.fecha)

            for(articulos in venta.articulos)
                cantidadArticulos += articulos.cantidad

            var textTmp = itemView.context.getString(R.string.ventas_inventario_cantidad) + " " + cantidadArticulos.toString()
            ventasFragmentTotalCantidad.text = textTmp

            textTmp = itemView.context.getString(R.string.venta_total) + " $" +venta.totalVenta.toString()
            ventasFragmentTotalVenta.text = textTmp

            ventasFragmentBotonEditar.setOnClickListener{
                val intent = Intent(context, VentaDetalle::class.java).apply {
                    putExtra("venta", venta)
                }
                context.startActivity(intent)
            }
        }

        fun ImageView.loadUrl(url: String) {
            try {Picasso.with(context).load(url).into(this)}
            catch(e: Exception){}
        }
    }
}
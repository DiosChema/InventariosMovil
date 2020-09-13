package com.example.perraco.RecyclerView

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.perraco.Activities.VentaDetalle
import com.example.perraco.Objets.Urls
import com.example.perraco.Objets.VentasObjeto
import com.example.perraco.R
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat

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
        val ventasFragmentNumeroVenta = view.findViewById(R.id.ventasFragmentNumeroVenta) as TextView
        val ventasFragmentNumeroVentaText = view.findViewById(R.id.ventasFragmentNumeroVentaText) as TextView
        val ventasFragmentFecha = view.findViewById(R.id.ventasFragmentFecha) as TextView
        val ventasFragmentTotalVenta = view.findViewById(R.id.ventasFragmentTotalVenta) as TextView
        val ventasFragmentRecyclerViewArticulos = view.findViewById(R.id.ventasFragmentRecyclerViewArticulos) as RecyclerView
        val ventasFragmentBotonEditar = view.findViewById(R.id.ventasFragmentBotonEditar) as ImageView

        var simpleDate: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")


        fun bind(venta: VentasObjeto, context: Context) {
            var mViewVentas = RecyclerViewArticulosVenta()
            var mRecyclerView : RecyclerView = ventasFragmentRecyclerViewArticulos
            mRecyclerView.setHasFixedSize(true)
            mRecyclerView.layoutManager = LinearLayoutManager(context)
            if (context != null) {
                mViewVentas.RecyclerAdapter(venta.articulos.toMutableList(), context)
            }
            mRecyclerView.adapter = mViewVentas


            itemView.setOnClickListener(View.OnClickListener {
                if(ventasFragmentRecyclerViewArticulos.isVisible) {
                    ventasFragmentRecyclerViewArticulos.visibility = View.INVISIBLE
                    val params: ViewGroup.LayoutParams = mRecyclerView.layoutParams
                    params.height = 60
                    mRecyclerView.setLayoutParams(params)
                }
                else {
                    ventasFragmentRecyclerViewArticulos.visibility = View.VISIBLE
                    val params: ViewGroup.LayoutParams = mRecyclerView.layoutParams
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    mRecyclerView.layoutParams = params
                }
            })

            val nuevoTamano: ViewGroup.LayoutParams = mRecyclerView.layoutParams
            nuevoTamano.height = 60
            mRecyclerView.layoutParams = nuevoTamano

            ventasFragmentRecyclerViewArticulos.visibility = View.INVISIBLE

            ventasFragmentNumeroVenta.text = venta._id.toString()
            ventasFragmentNumeroVentaText.text = itemView.context.getString(R.string.mensaje_numero_venta)
            ventasFragmentFecha.text = simpleDate.format(venta.fecha)
            ventasFragmentTotalVenta.text = itemView.context.getString(R.string.mensaje_total_venta) + " $" +venta.totalVenta.toString()

            ventasFragmentBotonEditar.setOnClickListener{
                val intent = Intent(context, VentaDetalle::class.java).apply {
                    putExtra("venta", venta)
                }
                context.startActivity(intent)
            }
        }

        fun ImageView.loadUrl(url: String) {
            Picasso.with(context).load(url).into(this)
        }
    }
}
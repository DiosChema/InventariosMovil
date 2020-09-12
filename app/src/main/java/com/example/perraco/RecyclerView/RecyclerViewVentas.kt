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
        val VentasNumeroVentaText = view.findViewById(R.id.VentasNumeroVentaText) as TextView
        val VentasFecha = view.findViewById(R.id.VentasFecha) as TextView
        val VentasTotalVenta = view.findViewById(R.id.VentasTotalVenta) as TextView
        val rvArticulosEnVenta = view.findViewById(R.id.VentaDetalleArticulos) as RecyclerView
        val VentasBotonEditar = view.findViewById(R.id.VentasBotonEditar) as ImageView


        fun bind(venta: VentasObjeto, context: Context) {
            var mViewVentas = RecyclerViewArticulosVenta()
            var mRecyclerView : RecyclerView = rvArticulosEnVenta as RecyclerView
            mRecyclerView.setHasFixedSize(true)
            mRecyclerView.layoutManager = LinearLayoutManager(context)
            if (context != null) {
                mViewVentas.RecyclerAdapter(venta.articulos.toMutableList(), context)
            }
            mRecyclerView.adapter = mViewVentas


            itemView.setOnClickListener(View.OnClickListener {
                if(rvArticulosEnVenta.isVisible) {
                    rvArticulosEnVenta.setVisibility(View.INVISIBLE)
                    val params: ViewGroup.LayoutParams = mRecyclerView.getLayoutParams()
                    params.height = 60
                    mRecyclerView.setLayoutParams(params)
                }
                else {
                    rvArticulosEnVenta.setVisibility(View.VISIBLE)
                    val params: ViewGroup.LayoutParams = mRecyclerView.getLayoutParams()
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    mRecyclerView.setLayoutParams(params)
                }
            })

            val params2: ViewGroup.LayoutParams = mRecyclerView.getLayoutParams()
            params2.height = 60
            mRecyclerView.setLayoutParams(params2)

            rvArticulosEnVenta.setVisibility(View.INVISIBLE)

            VentasNumeroVenta.text = venta._id.toString()
            VentasNumeroVentaText.text = itemView.context.getString(R.string.mensaje_numero_venta)
            VentasFecha.text = venta.fecha.toString()
            VentasTotalVenta.text = itemView.context.getString(R.string.mensaje_total_venta) + " $" +venta.totalVenta.toString()

            VentasBotonEditar.setOnClickListener{
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
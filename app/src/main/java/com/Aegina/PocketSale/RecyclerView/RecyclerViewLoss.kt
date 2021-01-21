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
import com.Aegina.PocketSale.Activities.LossDetails
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.Objets.VentasObjeto
import com.Aegina.PocketSale.R
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.text.SimpleDateFormat
import androidx.core.view.isVisible as isVisible1

class RecyclerViewLoss : RecyclerView.Adapter<RecyclerViewLoss.ViewHolder>() {

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

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return ventas.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val urls = Urls()
        var globalVariable = view.context.applicationContext as GlobalClass

        val ventasFragmentNumeroVenta = view.findViewById(R.id.ventasFragmentNumeroVenta) as TextView
        val ventasFragmentText = view.findViewById(R.id.ventasFragmentText) as TextView
        val ventasFragmentFecha = view.findViewById(R.id.ventasFragmentFecha) as TextView
        val ventasFragmentHora = view.findViewById(R.id.ventasFragmentHora) as TextView
        val ventasFragmentTotalVenta = view.findViewById(R.id.ventasFragmentTotalVenta) as TextView
        //val ventasFragmentTotalCantidad = view.findViewById(R.id.ventasFragmentTotalCantidad) as TextView
        val ventasFragmentRecyclerViewArticulos = view.findViewById(R.id.ventasFragmentRecyclerViewArticulos) as RecyclerView
        val ventasFragmentBotonEditar = view.findViewById(R.id.ventasFragmentBotonEditar) as ImageView
        val ventasFragmentContenedor = view.findViewById(R.id.ventasFragmentContenedor) as LinearLayout

        var simpleDate: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        var simpleDateHours: SimpleDateFormat = SimpleDateFormat("HH:mm:ss")

        fun bind(loss: VentasObjeto, context: Context) {
            var cantidadArticulos = 0
            val mViewVentas = RecyclerViewArticulosSurtido()
            val mRecyclerView : RecyclerView = ventasFragmentRecyclerViewArticulos
            mRecyclerView.setHasFixedSize(true)
            mRecyclerView.layoutManager = LinearLayoutManager(context)
            if (context != null) {
                mViewVentas.RecyclerAdapter(loss.articulos.toMutableList(), context)
            }
            mRecyclerView.adapter = mViewVentas
            var llenarRecyclerView = true

            when (position % 2) {
                1 -> ventasFragmentContenedor.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.backgroundventa2))
                0 -> ventasFragmentContenedor.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.backgroundventa1))
                else -> {
                }
            }

            itemView.setOnClickListener {
                if(llenarRecyclerView)
                {
                    if (context != null) {
                        mViewVentas.RecyclerAdapter(loss.articulos.toMutableList(), context)
                    }
                    mRecyclerView.adapter = mViewVentas
                    llenarRecyclerView = false
                }

                if(ventasFragmentRecyclerViewArticulos.isVisible1) {
                    ventasFragmentRecyclerViewArticulos.visibility = View.GONE
                } else {
                    ventasFragmentRecyclerViewArticulos.visibility = View.VISIBLE
                }
            }

            ventasFragmentNumeroVenta.text = loss._id.toString()
            ventasFragmentText.text = itemView.context.getString(R.string.ventas_inventario_fecha)
            ventasFragmentFecha.text = simpleDate.format(loss.fecha)
            ventasFragmentHora.text = simpleDateHours.format(loss.fecha)

            for(articulos in loss.articulos)
                cantidadArticulos += articulos.cantidad

            var textTmp = itemView.context.getString(R.string.ventas_inventario_cantidad) + " " + cantidadArticulos.toString()
            //ventasFragmentTotalCantidad.text = textTmp

            textTmp = itemView.context.getString(R.string.venta_total) + " $" +loss.totalCosto.round(2).toString()
            ventasFragmentTotalVenta.text = textTmp

            if(globalVariable.usuario!!.permisosModificarPerdidas)
            {
                ventasFragmentBotonEditar.setOnClickListener{
                    val intent = Intent(context, LossDetails::class.java).apply {
                        putExtra("loss", loss)
                    }
                    context.startActivity(intent)
                }
            }
            else
            {
                ventasFragmentBotonEditar.visibility = View.GONE
            }
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
package com.Aegina.PocketSale.RecyclerView

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
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
import com.Aegina.PocketSale.Activities.SurtidoDetalle
import com.Aegina.PocketSale.Activities.VentaDetalle
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.Objets.VentasObjeto
import com.Aegina.PocketSale.R
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.text.SimpleDateFormat
import androidx.core.view.isVisible as isVisible1

class RecyclerViewSurtidos : RecyclerView.Adapter<RecyclerViewSurtidos.ViewHolder>() {

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
        val ventasFragmentFecha = view.findViewById(R.id.ventasFragmentFecha) as TextView
        val ventasFragmentTotalArticulos = view.findViewById(R.id.ventasFragmentTotalArticulos) as TextView
        val ventasFragmentTotalVenta = view.findViewById(R.id.ventasFragmentTotalVenta) as TextView
        //val ventasFragmentTotalCantidad = view.findViewById(R.id.ventasFragmentTotalCantidad) as TextView
        val ventasFragmentRecyclerViewArticulos = view.findViewById(R.id.ventasFragmentRecyclerViewArticulos) as RecyclerView
        val ventasFragmentRecyclerViewItemsContainer = view.findViewById(R.id.ventasFragmentRecyclerViewItemsContainer) as LinearLayout

        var simpleDate: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        var simpleDateHours: SimpleDateFormat = SimpleDateFormat("HH:mm:ss")

        fun bind(surtido: VentasObjeto, context: Context) {
            var cantidadArticulos = 0
            val mViewVentas = RecyclerViewArticulosSurtido()
            val mRecyclerView : RecyclerView = ventasFragmentRecyclerViewArticulos
            mRecyclerView.setHasFixedSize(true)
            mRecyclerView.layoutManager = LinearLayoutManager(context)
            if (context != null) {
                mViewVentas.RecyclerAdapter(surtido.articulos.toMutableList(), context)
            }
            mRecyclerView.adapter = mViewVentas
            var llenarRecyclerView = true

            itemView.setOnClickListener {
                if(llenarRecyclerView)
                {
                    if (context != null) {
                        mViewVentas.RecyclerAdapter(surtido.articulos.toMutableList(), context)
                    }
                    mRecyclerView.adapter = mViewVentas
                    llenarRecyclerView = false
                }

                if(ventasFragmentRecyclerViewItemsContainer.isVisible1) {
                    hideMenu()
                } else {
                    showMenu()
                }
            }

            ventasFragmentNumeroVenta.text = "# " + surtido._id
            ventasFragmentFecha.text = simpleDate.format(surtido.fecha) + " " + simpleDateHours.format(surtido.fecha)

            for(articulos in surtido.articulos)
                cantidadArticulos += articulos.cantidad

            ventasFragmentTotalArticulos.text = cantidadArticulos.toString()

            var textTmp = itemView.context.getString(R.string.ventas_inventario_cantidad) + " " + cantidadArticulos.toString()
            //ventasFragmentTotalCantidad.text = textTmp

            textTmp = surtido.totalCosto.round(2).toString()
            ventasFragmentTotalVenta.text = textTmp

            if(globalVariable.usuario!!.permisosModificarProovedor)
            {

                itemView.setOnLongClickListener{
                    val intent = Intent(context, SurtidoDetalle::class.java).apply {
                        putExtra("surtido", surtido)
                    }
                    context.startActivity(intent)
                    true
                }
            }
        }

        fun showMenu() {
            ventasFragmentRecyclerViewItemsContainer.visibility = View.VISIBLE
            ventasFragmentRecyclerViewItemsContainer.animate()
                .alpha(1f)
                .setDuration(200)
                .setListener(null)
        }

        private fun hideMenu() {
            ventasFragmentRecyclerViewItemsContainer.animate()
                .alpha(0f)
                .setDuration(200)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        ventasFragmentRecyclerViewItemsContainer.visibility = View.GONE
                    }
                })
            //ventasFragmentRecyclerViewItemsContainer.visibility = View.GONE
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
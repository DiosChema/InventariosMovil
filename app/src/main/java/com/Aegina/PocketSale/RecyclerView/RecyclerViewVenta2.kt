package com.Aegina.PocketSale.RecyclerView

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.InventarioObjeto
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.squareup.picasso.Picasso
import java.lang.Double.parseDouble
import java.lang.Integer.parseInt


class RecyclerViewVenta2 : RecyclerView.Adapter<RecyclerViewVenta2.ViewHolder>() {

    var articulosVenta: MutableList<InventarioObjeto>  = ArrayList()
    lateinit var context: Context

    fun RecyclerAdapter(articulos : MutableList<InventarioObjeto>, context: Context){
        this.articulosVenta = articulos
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articulosVenta.get(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                R.layout.item_articulos_de_venta,
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
        val nombreArticulo = view.findViewById(R.id.itemVentaNombre) as TextView
        val precio = view.findViewById(R.id.itemVentaPrecio) as TextView
        val cantidad = view.findViewById(R.id.itemVentaCantidad) as EditText
        val imagenArticulo = view.findViewById(R.id.itemVentaFoto) as ImageView
        var disminuirCantidad = view.findViewById(R.id.itemVentaArticulo) as ImageButton
        var aumentarCantidad = view.findViewById(R.id.itemVentaAnadirCantidad) as ImageButton
        var ventaIdArticulo = view.findViewById(R.id.itemVentaIdArticulo) as TextView
        var VentaCostoTotal = view.findViewById(R.id.itemVentaCostoTotal) as TextView
        var VentaCostoArticulo = view.findViewById(R.id.itemVentaCostoArticulos) as TextView

        var globalVariable = itemView.context.applicationContext as GlobalClass

        fun bind(articulo: InventarioObjeto) {
            nombreArticulo.text = articulo.nombreArticulo
            precio.text = /*"$" + */articulo.precioArticulo.toString()
            cantidad.setText(articulo.cantidadArticulo.toString())
            ventaIdArticulo.setText(/*itemView.context.getString(R.string.mensaje_precio_articulo) + " :" + */articulo.idArticulo.toString())
            VentaCostoTotal.text = parseDouble(articulo.precioArticulo.toString()).toString()
            VentaCostoArticulo.text = parseDouble(articulo.costoArticulo.toString()).toString()

            disminuirCantidad.setOnClickListener {
                val tamanoString = cantidad.text.toString()

                if(tamanoString.length > 0) {
                    val cantidadArticulo = parseInt(cantidad.text.toString()) - 1
                    cantidad.setText((cantidadArticulo.toString()))

                    if(cantidadArticulo  <= 0) {
                        cantidad.setText((cantidadArticulo.toString()))
                        val params: ViewGroup.LayoutParams = itemView.getLayoutParams()
                        params.height = 0
                        itemView.setLayoutParams(params)
                    }
                }

            }

            aumentarCantidad.setOnClickListener {
                val cantidadArticulo = parseInt(cantidad.text.toString()) + 1
                cantidad.setText((cantidadArticulo.toString()))
            }

            cantidad.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {

                }
                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                    if (s.length != 0)
                        VentaCostoTotal.text = (parseInt(s.toString()) * parseDouble(articulo.precioArticulo.toString())).toString()
                }
            })

            imagenArticulo.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+articulo.idArticulo+".jpeg"+"&token="+globalVariable.usuario!!.token)
        }

        fun ImageView.loadUrl(url: String) {
            Picasso.with(context).load(url).into(this)
        }
    }
}
package com.Aegina.PocketSale.RecyclerView

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.InventarioObjeto
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.squareup.picasso.Picasso
import java.lang.Double.parseDouble

class AdapterListVenta(private val activity: Activity, articulosList: MutableList<InventarioObjeto>) : BaseAdapter() {

    var inventarioObjeto:MutableList<InventarioObjeto> = ArrayList()
    val urls = Urls()

    init {
        this.inventarioObjeto = articulosList
    }

    override fun getCount(): Int {
        return inventarioObjeto.size
    }

    override fun getItem(i: Int): Any {
        return i
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    fun obtenerObjeto(i : Int): InventarioObjeto{
        return inventarioObjeto[i]
    }

    @SuppressLint("InflateParams", "ViewHolder")
    override fun getView(i: Int, convertView: View?, viewGroup: ViewGroup): View {
        val view: View

        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.item_articulos_de_venta, null)

        if(inventarioObjeto[i].cantidadArticulo == 0)
            return inflater.inflate(R.layout.item_vacio, null)

        val nombreArticulo = view.findViewById(R.id.itemVentaNombre) as TextView
        val precio = view.findViewById(R.id.itemVentaPrecio) as TextView
        val cantidad = view.findViewById(R.id.itemVentaCantidad) as EditText
        val imagenArticulo = view.findViewById(R.id.itemVentaFoto) as ImageView
        val disminuirCantidad = view.findViewById(R.id.itemVentaArticulo) as ImageButton
        val aumentarCantidad = view.findViewById(R.id.itemVentaAnadirCantidad) as ImageButton
        val ventaIdArticulo = view.findViewById(R.id.itemVentaIdArticulo) as TextView
        val VentaCostoTotal = view.findViewById(R.id.itemVentaCostoTotal) as TextView
        val VentaCostoArticulo = view.findViewById(R.id.itemVentaCostoArticulos) as TextView

        val globalVariable = view.context.applicationContext as GlobalClass


        nombreArticulo.text = inventarioObjeto[i].nombreArticulo
        precio.text = /*"$" + */inventarioObjeto[i].precioArticulo.toString()
        cantidad.setText(inventarioObjeto[i].cantidadArticulo.toString())
        ventaIdArticulo.setText(/*itemView.context.getString(R.string.mensaje_precio_articulo) + " :" + */inventarioObjeto[i].idArticulo.toString())
        VentaCostoTotal.text = (parseDouble(inventarioObjeto[i].precioArticulo.toString()) * inventarioObjeto[i].cantidadArticulo).toString()
        VentaCostoArticulo.text = parseDouble(inventarioObjeto[i].costoArticulo.toString()).toString()

        disminuirCantidad.setOnClickListener {
            val tamanoString = cantidad.text.toString()

            if(tamanoString.isNotEmpty()) {
                val cantidadArticulo = Integer.parseInt(cantidad.text.toString()) - 1
                inventarioObjeto[i].cantidadArticulo = cantidadArticulo
                cantidad.setText((cantidadArticulo.toString()))

                if(cantidadArticulo  <= 0) {
                    cantidad.setText((cantidadArticulo.toString()))
                    notifyDataSetChanged()
                }
            }
        }

        aumentarCantidad.setOnClickListener {
            val cantidadArticulo = Integer.parseInt(cantidad.text.toString()) + 1
            inventarioObjeto[i].cantidadArticulo = cantidadArticulo
            cantidad.setText((cantidadArticulo.toString()))
        }

        cantidad.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int,count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int,before: Int, count: Int) {
                if (s.length != 0) {
                    VentaCostoTotal.text = (Integer.parseInt(s.toString()) * parseDouble(
                        inventarioObjeto[i].precioArticulo.toString()
                    )).toString()
                    inventarioObjeto[i].cantidadArticulo = (Integer.parseInt(s.toString()))
                }
            }
        })

        imagenArticulo.loadUrl(urls.url+urls.endPointImagenes+inventarioObjeto[i].idArticulo+".jpeg"+"&token="+globalVariable.token)

        return view
    }

    fun ImageView.loadUrl(url: String) {
        Picasso.with(context).load(url).into(this)
    }
}
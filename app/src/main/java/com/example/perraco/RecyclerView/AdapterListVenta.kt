package com.example.perraco.RecyclerView

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.perraco.Objets.GlobalClass
import com.example.perraco.Objets.InventarioObjeto
import com.example.perraco.Objets.Urls
import com.example.perraco.R
import com.squareup.picasso.Picasso
import java.lang.Double

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
        var view: View
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.item_articulos_de_venta, null)

        val nombreArticulo = view.findViewById(R.id.VentaNombre) as TextView
        val precio = view.findViewById(R.id.VentaPrecio) as TextView
        val cantidad = view.findViewById(R.id.VentaCantidad) as EditText
        val imagenArticulo = view.findViewById(R.id.EditarArticuloVentaFoto) as ImageView
        var disminuirCantidad = view.findViewById(R.id.EditarArticuloVentaDisminuirCantidad) as ImageButton
        var aumentarCantidad = view.findViewById(R.id.EditarArticuloVentaAnadirCantidad) as ImageButton
        var ventaIdArticulo = view.findViewById(R.id.VentaIdArticulo) as TextView
        var VentaCostoTotal = view.findViewById(R.id.VentaCostoTotal) as TextView
        var VentaCostoArticulo = view.findViewById(R.id.VentaCostoArticulo) as TextView

        var globalVariable = view.context.applicationContext as GlobalClass


        nombreArticulo.text = inventarioObjeto[i].nombreArticulo
        precio.text = /*"$" + */inventarioObjeto[i].precioArticulo.toString()
        cantidad.setText(inventarioObjeto[i].cantidadArticulo.toString())
        ventaIdArticulo.setText(/*itemView.context.getString(R.string.mensaje_precio_articulo) + " :" + */inventarioObjeto[i].idArticulo.toString())
        VentaCostoTotal.text = (Double.parseDouble(inventarioObjeto[i].precioArticulo.toString()) * inventarioObjeto[i].cantidadArticulo).toString()
        VentaCostoArticulo.text = Double.parseDouble(inventarioObjeto[i].costoArticulo.toString()).toString()

        disminuirCantidad.setOnClickListener(View.OnClickListener {
            val tamanoString = cantidad.text.toString()

            if(tamanoString.length > 0)
            {
                val cantidadArticulo = Integer.parseInt(cantidad.text.toString()) - 1
                inventarioObjeto[i].cantidadArticulo = cantidadArticulo
                cantidad.setText((cantidadArticulo.toString()))

                if(cantidadArticulo  <= 0) {
                    cantidad.setText((cantidadArticulo.toString()))
                    val params: ViewGroup.LayoutParams = view.layoutParams
                    params.height = 0
                    view.layoutParams = params
                }
            }

        })

        aumentarCantidad.setOnClickListener(View.OnClickListener {
            var cantidadArticulo = Integer.parseInt(cantidad.text.toString()) + 1
            inventarioObjeto[i].cantidadArticulo = cantidadArticulo
            cantidad.setText((cantidadArticulo.toString()))
        })

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
                if (s.length != 0) {
                    VentaCostoTotal.text = (Integer.parseInt(s.toString()) * Double.parseDouble(
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
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
import com.example.perraco.Objets.ArticuloObjeto
import com.example.perraco.Objets.GlobalClass
import com.example.perraco.Objets.Urls
import com.example.perraco.R
import com.squareup.picasso.Picasso
import java.lang.Double.parseDouble

class AdapterListEditarArticulosVenta(private val activity: Activity, articulosList: MutableList<ArticuloObjeto>) : BaseAdapter() {

    private var articuloVentaObjeto:MutableList<ArticuloObjeto> = ArrayList()
    val urls = Urls()
    private var editarHabilitado = false

    init {
        this.articuloVentaObjeto = articulosList
    }

    override fun getCount(): Int {
        return articuloVentaObjeto.size
    }

    override fun getItem(i: Int): Any {
        return i
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    fun obtenerObjeto(i : Int): ArticuloObjeto {
        return articuloVentaObjeto[i]
    }

    fun setHabilitar(habilitar : Boolean) {
        editarHabilitado = habilitar
    }

    @SuppressLint("InflateParams", "ViewHolder")
    override fun getView(i: Int, convertView: View?, viewGroup: ViewGroup): View {
        var view: View
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.item_venta_editar_articulo, null)

        if(articuloVentaObjeto[i].cantidad == 0)
            return inflater.inflate(R.layout.item_vacio, null)

        val editarArticuloVentaFoto = view.findViewById(R.id.editarArticuloVentaFoto) as ImageView
        val editarArticuloVentaNombre = view.findViewById(R.id.editarArticuloVentaNombre) as TextView
        val editarArticuloVentaCantidadText = view.findViewById(R.id.editarArticuloVentaCantidadText) as TextView
        val editarArticuloVentaCantidad = view.findViewById(R.id.editarArticuloVentaCantidad) as EditText
        val editarArticuloVentaDisminuirCantidad = view.findViewById(R.id.itemVentaArticulo) as ImageButton
        val editarArticuloVentaAnadirCantidad = view.findViewById(R.id.itemVentaAnadirCantidad) as ImageButton
        val editarArticuloVentaPrecio = view.findViewById(R.id.editarArticuloVentaPrecio) as EditText
        val editarArticuloVentaPrecioText = view.findViewById(R.id.editarArticuloVentaPrecioText) as TextView
        val editarArticuloVentaCosto = view.findViewById(R.id.editarArticuloVentaCosto) as EditText
        val editarArticuloVentaCostoText = view.findViewById(R.id.editarArticuloVentaCostoText) as TextView
        val editarArticuloEliminarArticulo = view.findViewById(R.id.editarArticuloEliminarArticulo) as ImageButton

        var globalVariable = view.context.applicationContext as GlobalClass

        editarArticuloVentaFoto.loadUrl(urls.url+urls.endPointImagenes+articuloVentaObjeto[i].idArticulo+".jpeg"+"&token="+globalVariable.token)
        editarArticuloVentaNombre.text = articuloVentaObjeto[i].nombre
        editarArticuloVentaCantidadText.text = view.context.getText(R.string.mensaje_cantidad_articulo)
        editarArticuloVentaCantidad.setText(articuloVentaObjeto[i].cantidad.toString())
        editarArticuloVentaPrecio.setText(articuloVentaObjeto[i].precio.toString())
        editarArticuloVentaPrecioText.text = view.context.getText(R.string.editar_articulo_venta)
        editarArticuloVentaCosto.setText(articuloVentaObjeto[i].costo.toString())
        editarArticuloVentaCostoText.text = view.context.getText(R.string.editar_articulo_costo)

        if(!editarHabilitado) {
            editarArticuloVentaCantidad.isEnabled = false
            editarArticuloVentaPrecio.isEnabled = false
            editarArticuloVentaCosto.isEnabled = false
            editarArticuloEliminarArticulo.isEnabled = false
            editarArticuloVentaDisminuirCantidad.visibility = View.INVISIBLE
            editarArticuloVentaAnadirCantidad.visibility = View.INVISIBLE
        }

        editarArticuloVentaDisminuirCantidad.setOnClickListener(View.OnClickListener {
            val valor = editarArticuloVentaCantidad.text.toString()

            if(valor.isNotEmpty()){
                val cantidadArticulo = Integer.parseInt(valor) - 1
                editarArticuloVentaCantidad.setText((cantidadArticulo.toString()))

                if(cantidadArticulo <= 0 ){
                    editarArticuloVentaCantidad.setText((cantidadArticulo.toString()))
                    notifyDataSetChanged()
                }
            }
        })

        editarArticuloVentaAnadirCantidad.setOnClickListener {
            var cantidadArticulo = Integer.parseInt(editarArticuloVentaCantidad.text.toString()) + 1
            editarArticuloVentaCantidad.setText((cantidadArticulo.toString()))
        }

        editarArticuloEliminarArticulo.setOnClickListener {
            editarArticuloVentaCantidad.setText("0")
            notifyDataSetChanged()
        }

        editarArticuloVentaCantidad.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int,count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    articuloVentaObjeto[i].cantidad = (Integer.parseInt(s.toString()))
                }
            }
        })

        editarArticuloVentaPrecio.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int,count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    articuloVentaObjeto[i].precio = parseDouble(s.toString())
                }
            }
        })

        editarArticuloVentaCosto.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int,count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    articuloVentaObjeto[i].costo = parseDouble(s.toString())
                }
            }
        })

        return view
    }

    fun ImageView.loadUrl(url: String) {
        Picasso.with(context).load(url).into(this)
    }
}
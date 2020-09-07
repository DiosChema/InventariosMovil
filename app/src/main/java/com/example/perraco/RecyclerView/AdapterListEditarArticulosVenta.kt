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
import java.lang.Double

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

        val EditarArticuloVentaFoto = view.findViewById(R.id.EditarArticuloVentaFoto) as ImageView
        val EditarArticuloVentaNombre = view.findViewById(R.id.EditarArticuloVentaNombre) as TextView
        val EditarArticuloVentaCantidadText = view.findViewById(R.id.EditarArticuloVentaCantidadText) as TextView
        val EditarArticuloVentaCantidad = view.findViewById(R.id.EditarArticuloVentaCantidad) as EditText
        val EditarArticuloVentaDisminuirCantidad = view.findViewById(R.id.EditarArticuloVentaDisminuirCantidad) as ImageButton
        val EditarArticuloVentaAnadirCantidad = view.findViewById(R.id.EditarArticuloVentaAnadirCantidad) as ImageButton
        val EditarArticuloVentaPrecio = view.findViewById(R.id.EditarArticuloVentaPrecio) as EditText
        val EditarArticuloVentaPrecioText = view.findViewById(R.id.EditarArticuloVentaPrecioText) as TextView
        val EditarArticuloVentaCosto = view.findViewById(R.id.EditarArticuloVentaCosto) as EditText
        val EditarArticuloVentaCostoText = view.findViewById(R.id.EditarArticuloVentaCostoText) as TextView
        val EditarArticuloEliminarArticulo = view.findViewById(R.id.EditarArticuloEliminarArticulo) as ImageButton

        var globalVariable = view.context.applicationContext as GlobalClass

        EditarArticuloVentaFoto.loadUrl(urls.url+urls.endPointImagenes+articuloVentaObjeto[i].idArticulo+".jpeg"+"&token="+globalVariable.token)
        EditarArticuloVentaNombre.text = articuloVentaObjeto[i].nombre
        EditarArticuloVentaCantidadText.text = view.context.getText(R.string.mensaje_cantidad_articulo)
        EditarArticuloVentaCantidad.setText(articuloVentaObjeto[i].cantidad.toString())
        EditarArticuloVentaPrecio.setText(articuloVentaObjeto[i].precio.toString())
        EditarArticuloVentaPrecioText.text = view.context.getText(R.string.mensaje_precio_venta)
        EditarArticuloVentaCosto.setText(articuloVentaObjeto[i].costo.toString())
        EditarArticuloVentaCostoText.text = view.context.getText(R.string.mensaje_precio_costo)

        if(!editarHabilitado) {
            EditarArticuloVentaCantidad.isEnabled = false
            EditarArticuloVentaPrecio.isEnabled = false
            EditarArticuloVentaCosto.isEnabled = false
            EditarArticuloEliminarArticulo.isEnabled = false
            EditarArticuloVentaDisminuirCantidad.visibility = View.INVISIBLE
            EditarArticuloVentaAnadirCantidad.visibility = View.INVISIBLE
        }

        EditarArticuloVentaDisminuirCantidad.setOnClickListener(View.OnClickListener {
            val valor = EditarArticuloVentaCantidad.text.toString()

            if(valor.isNotEmpty()){
                val cantidadArticulo = Integer.parseInt(valor) - 1
                if(cantidadArticulo >= 0 )
                    EditarArticuloVentaCantidad.setText((cantidadArticulo.toString()))
                else{
                    val params: ViewGroup.LayoutParams = view.layoutParams
                    params.height = 0
                    view.layoutParams = params
                }
            }
        })

        EditarArticuloVentaAnadirCantidad.setOnClickListener(View.OnClickListener {
            var cantidadArticulo = Integer.parseInt(EditarArticuloVentaCantidad.text.toString()) + 1
            EditarArticuloVentaCantidad.setText((cantidadArticulo.toString()))
        })

        EditarArticuloEliminarArticulo.setOnClickListener(View.OnClickListener{
            EditarArticuloVentaCantidad.setText("0")
            val params: ViewGroup.LayoutParams = view.layoutParams
            params.height = 0
            view.layoutParams = params
        })

        EditarArticuloVentaCantidad.addTextChangedListener(object : TextWatcher {
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
                if (s.isNotEmpty()) {
                    articuloVentaObjeto[i].cantidad = (Integer.parseInt(s.toString()))
                }
            }
        })

        return view
    }

    fun ImageView.loadUrl(url: String) {
        Picasso.with(context).load(url).into(this)
    }
}
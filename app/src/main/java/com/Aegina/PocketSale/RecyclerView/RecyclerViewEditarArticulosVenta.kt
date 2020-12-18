package com.Aegina.PocketSale.RecyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Objets.ArticuloObjeto
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.squareup.picasso.Picasso


class RecyclerViewEditarArticulosVenta : RecyclerView.Adapter<RecyclerViewEditarArticulosVenta.ViewHolder>() {

    var articulosInventario: MutableList<ArticuloObjeto>  = ArrayList()
    lateinit var context: Context

    fun RecyclerAdapter(articulos : MutableList<ArticuloObjeto>, context: Context){
        this.articulosInventario = articulos
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articulosInventario.get(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                R.layout.item_venta_editar_articulo,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return articulosInventario.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val EditarArticuloVentaFoto = view.findViewById(R.id.editarArticuloVentaFoto) as ImageView
        val EditarArticuloVentaNombre = view.findViewById(R.id.editarArticuloVentaNombre) as TextView
        val EditarArticuloVentaCantidadText = view.findViewById(R.id.editarArticuloVentaCantidadText) as TextView
        val EditarArticuloVentaCantidad = view.findViewById(R.id.editarArticuloVentaCantidad) as EditText
        val EditarArticuloVentaDisminuirCantidad = view.findViewById(R.id.itemVentaArticulo) as ImageButton
        val EditarArticuloVentaAnadirCantidad = view.findViewById(R.id.itemVentaAnadirCantidad) as ImageButton
        val EditarArticuloVentaPrecio = view.findViewById(R.id.editarArticuloVentaPrecio) as EditText
        val EditarArticuloVentaPrecioText = view.findViewById(R.id.editarArticuloVentaPrecioText) as TextView
        val EditarArticuloVentaCosto = view.findViewById(R.id.editarArticuloVentaCosto) as EditText
        val EditarArticuloVentaCostoText = view.findViewById(R.id.editarArticuloVentaCostoText) as TextView
        val EditarArticuloEliminarArticulo = view.findViewById(R.id.editarArticuloEliminarArticulo) as ImageButton

        val urls = Urls()
        var globalVariable = itemView.context.applicationContext as GlobalClass

        fun bind(articulo: ArticuloObjeto) {

            EditarArticuloVentaFoto.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+articulo.idArticulo+".jpeg"+"&token="+globalVariable.usuario!!.token)
            EditarArticuloVentaNombre.text = articulo.nombre
            EditarArticuloVentaCantidadText.text = itemView.context.getText(R.string.mensaje_cantidad_articulo)
            EditarArticuloVentaCantidad.setText(articulo.cantidad.toString())
            EditarArticuloVentaPrecio.setText(articulo.precio.toString())
            EditarArticuloVentaPrecioText.text = itemView.context.getText(R.string.editar_articulo_venta)
            EditarArticuloVentaCosto.setText(articulo.costo.toString())
            EditarArticuloVentaCostoText.text = itemView.context.getText(R.string.editar_articulo_costo)

            EditarArticuloVentaCantidad.isEnabled = false
            EditarArticuloVentaPrecio.isEnabled = false
            EditarArticuloVentaCosto.isEnabled = false
            EditarArticuloEliminarArticulo.isEnabled = false
            EditarArticuloVentaDisminuirCantidad.visibility = View.INVISIBLE
            EditarArticuloVentaAnadirCantidad.visibility = View.INVISIBLE

            EditarArticuloVentaDisminuirCantidad.setOnClickListener {
                val valor = EditarArticuloVentaCantidad.text.toString()

                if(valor.isNotEmpty()){
                    val cantidadArticulo = Integer.parseInt(valor) - 1
                    if(cantidadArticulo >= 0 )
                        EditarArticuloVentaCantidad.setText((cantidadArticulo.toString()))
                    else{
                        val params: ViewGroup.LayoutParams = itemView.getLayoutParams()
                        params.height = 0
                        itemView.setLayoutParams(params)
                    }
                }
            }

            EditarArticuloVentaAnadirCantidad.setOnClickListener {
                val cantidadArticulo = Integer.parseInt(EditarArticuloVentaCantidad.text.toString()) + 1
                EditarArticuloVentaCantidad.setText((cantidadArticulo.toString()))
            }

            EditarArticuloEliminarArticulo.setOnClickListener {
                EditarArticuloVentaCantidad.setText("0")
                val params: ViewGroup.LayoutParams = itemView.layoutParams
                params.height = 0
                itemView.layoutParams = params
            }


        }

        fun ImageView.loadUrl(url: String) {
            Picasso.with(context).load(url).into(this)
        }
    }
}
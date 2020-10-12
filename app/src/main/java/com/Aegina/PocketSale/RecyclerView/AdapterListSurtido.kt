package com.Aegina.PocketSale.RecyclerView

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.InventarioObjeto
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.squareup.picasso.Picasso
import kotlin.collections.ArrayList

class AdapterListSurtido(private val activity: Activity, articulosList: MutableList<InventarioObjeto>) : BaseAdapter() {

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
        view = inflater.inflate(R.layout.item_surtido_articulo, null)

        if(inventarioObjeto[i].cantidadArticulo == 0)
            return inflater.inflate(R.layout.item_vacio, null)

        val itemSurtidoArticuloFoto = view.findViewById(R.id.itemSurtidoArticuloFoto) as ImageView
        val itemSurtidoArticuloTitulo = view.findViewById(R.id.itemSurtidoArticuloTitulo) as TextView
        val itemSurtidoArticuloCantidad = view.findViewById(R.id.itemSurtidoArticuloCantidad) as TextView

        val globalVariable = view.context.applicationContext as GlobalClass

        itemSurtidoArticuloTitulo.text = inventarioObjeto[i].nombreArticulo
        itemSurtidoArticuloCantidad.setText(inventarioObjeto[i].cantidadArticulo.toString())
        itemSurtidoArticuloFoto.loadUrl(urls.url+urls.endPointImagenes+inventarioObjeto[i].idArticulo+".jpeg"+"&token="+globalVariable.token)

        /*view.setOnClickListener{
            eventoClick(view,i)
        }*/

        return view
    }

    fun ImageView.loadUrl(url: String) {
        Picasso.with(context).load(url).into(this)
    }

    /*fun eventoClick(view: View, posicion : Int){
        val dialog: AlertDialog = AlertDialog.Builder(view.context).create()
        val inflater = LayoutInflater.from(view.context)
        val alertDialogView: View = inflater.inflate(R.layout.dialog_numero, null)
        dialog.setView(alertDialogView)
        val dialogNumeroTitulo = alertDialogView.findViewById<View>(R.id.dialogNumeroTitulo) as TextView
        val dialogNumeroText = alertDialogView.findViewById<View>(R.id.dialogNumeroText) as EditText
        val dialogNumeroAceptar = alertDialogView.findViewById<View>(R.id.dialogNumeroAceptar) as Button
        val dialogNumeroCancelar = alertDialogView.findViewById<View>(R.id.dialogNumeroCancelar) as Button

        dialogNumeroTitulo.text = view.context.getString(R.string.dialog_numero_text_cantidad)

        dialogNumeroAceptar.setOnClickListener {
            if(dialogNumeroText.length() > 0) {
                inventarioObjeto[posicion].cantidadArticulo = parseInt(dialogNumeroText.text.toString())
                notifyDataSetChanged()
                dialog.dismiss()
            }
        }

        dialogNumeroCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }*/
}
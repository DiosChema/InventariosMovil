package com.Aegina.PocketSale.RecyclerView

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Activities.InventarioDetalle
import com.Aegina.PocketSale.Activities.PerfilDetalle
import com.Aegina.PocketSale.Objets.EmpleadoObject
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.squareup.picasso.Picasso
import java.lang.Exception


class RecyclerViewEmpleado : RecyclerView.Adapter<RecyclerViewEmpleado.ViewHolder>() {

    var empleados: MutableList<EmpleadoObject>  = ArrayList()
    lateinit var context:Context

    fun RecyclerAdapter(empleadosList : MutableList<EmpleadoObject>, context: Context){
        this.empleados = empleadosList
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = empleados[position]
        holder.bind(item, context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                R.layout.item_empleados,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return empleados.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val empleadoFoto = view.findViewById(R.id.empleadoFoto) as ImageView

        val empleadoNombre = view.findViewById(R.id.empleadoNombre) as TextView
        val empleadoCorreo = view.findViewById(R.id.empleadoCorreo) as TextView
        val empleadoContenedor = view.findViewById(R.id.empleadoContenedor) as LinearLayout

        var globalVariable = itemView.context.applicationContext as GlobalClass
        val urls = Urls()

        fun bind(empleado: EmpleadoObject, context: Context) {

            when (position % 2) {
                1 -> empleadoContenedor.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.backgroundventa2))
                0 -> empleadoContenedor.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.backgroundventa1))
                else -> {
                }
            }

            empleadoNombre.text = empleado.nombre
            empleadoCorreo.text = empleado.user

            itemView.setOnClickListener {
                val intent = Intent(context, PerfilDetalle::class.java).apply {
                    putExtra("empleado", empleado)
                }
                context.startActivity(intent)
            }

            //empleadoFoto.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+empleado.user+".jpeg"+"&token="+globalVariable.usuario!!.token)
            empleadoFoto.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+empleado.user+".jpeg"+"&token="+globalVariable.usuario!!.token+"&tipoImagen=1")
        }

        fun ImageView.loadUrl(url: String) {
            try {Picasso.with(context).load(url).into(this)}
            catch(e: Exception){}
        }
    }
}
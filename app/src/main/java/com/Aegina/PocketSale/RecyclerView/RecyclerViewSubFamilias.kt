package com.Aegina.PocketSale.RecyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Objets.SubFamiliaObjeto
import com.Aegina.PocketSale.R

class RecyclerViewSubFamilias : RecyclerView.Adapter<RecyclerViewSubFamilias.ViewHolder>() {

    var familias: MutableList<SubFamiliaObjeto>  = ArrayList()
    lateinit var context: Context
    var positionSelected = -1

    fun RecyclerAdapter(familias : MutableList<SubFamiliaObjeto>, context: Context){
        this.familias = familias
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = familias[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                R.layout.item_familia,
                parent,
                false
            ), positionSelected
        )
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return familias.size
    }

    fun positionSelected(position: Int)
    {
        positionSelected = position
    }

    class ViewHolder(view: View, val positionSelected: Int) : RecyclerView.ViewHolder(view) {

        val itemFamiliaNombre = view.findViewById(R.id.itemFamiliaNombre) as TextView
        val itemFamiliaContainer = view.findViewById(R.id.itemFamiliaContainer) as LinearLayout

        fun bind(articulo: SubFamiliaObjeto)
        {
            if(adapterPosition == positionSelected)
            {
                itemFamiliaContainer.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.border))
            }

            itemFamiliaNombre.text = articulo.nombreSubFamilia
        }

    }
}

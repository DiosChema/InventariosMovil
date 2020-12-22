package com.Aegina.PocketSale.Activities

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.Aegina.PocketSale.Dialogs.DialogFecha
import com.Aegina.PocketSale.Dialogs.DialogFiltrarArticulos
import com.Aegina.PocketSale.Fragments.InventarioFragment
import com.Aegina.PocketSale.Fragments.VentaFragment
import com.Aegina.PocketSale.Objets.InventarioObjeto
import com.Aegina.PocketSale.R
import kotlinx.android.synthetic.main.activity_inventario.*


class InventarioPager : AppCompatActivity(),
    DialogFecha.DialogFecha, DialogFiltrarArticulos.DialogFiltrarArticulos {

    lateinit var adapter : MyViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventario)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        asignarFragments()
    }

    fun asignarFragments(){
        adapter = MyViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(InventarioFragment() , getString(R.string.fragment_inventario))
        adapter.addFragment(VentaFragment() , getString(R.string.fragment_ventas))
        pager.adapter = adapter
        tab_layout.setupWithViewPager(pager)
    }

    class MyViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager){
        private val fragmentList : MutableList<Fragment> = ArrayList()
        private val titleList : MutableList<String> = ArrayList()

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        fun addFragment(fragment: Fragment,title:String){
            fragmentList.add(fragment)
            titleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titleList[position]
        }

    }

    override fun obtenerFechaInicial() {
        val fragment: Fragment = adapter.getItem(1)
        (fragment as VentaFragment).obtenerFechaInicial()
    }

    override fun obtenerFechaFinal() {
        val fragment: Fragment = adapter.getItem(1)
        (fragment as VentaFragment).obtenerFechaFinal()
    }

    override fun obtenerFecha() {
        val fragment: Fragment = adapter.getItem(1)
        (fragment as VentaFragment).obtenerFecha()
    }

    /*override fun filtrosArticulos(familiaIdTmp:Int,subFamiliaIdTmp:Int,minCantidad:Int,maxCantidad:Int,minPrecio:Double,maxPrecio:Double,nombre:String) {
        val fragment: Fragment = adapter.getItem(0)
        (fragment as InventarioFragment).filtroFamilia = familiaIdTmp
        (fragment).filtroSubFamilia = subFamiliaIdTmp
        (fragment).filtroMinCantidad = minCantidad
        (fragment).filtroMaxCantidad = maxCantidad
        (fragment).filtroMinPrecio = minPrecio
        (fragment).filtroMaxPrecio = maxPrecio
        (fragment).filtronombreArticulo = nombre
        (fragment).getInventarioObjecto()
    }*/

    override fun listaArticulos(listaArticulos: MutableList<InventarioObjeto>)
    {
        val fragment: Fragment = adapter.getItem(0)
        (fragment as InventarioFragment).obtenerListaArticulos(listaArticulos)
    }
}
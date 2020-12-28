package com.Aegina.PocketSale.Activities

import android.content.Intent
import com.Aegina.PocketSale.Fragments.HistorialSurtidosFragment
import com.Aegina.PocketSale.Fragments.SurtidoFragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.Aegina.PocketSale.Dialogs.DialogAgregarArticulos
import com.Aegina.PocketSale.Dialogs.DialogAgregarNumero
import com.Aegina.PocketSale.Dialogs.DialogFecha
import com.Aegina.PocketSale.Objets.ArticuloInventarioObjeto
import com.Aegina.PocketSale.Objets.InventarioObjeto
import com.Aegina.PocketSale.R
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_inventario.*


class SurtidoPager : AppCompatActivity(),
    DialogFecha.DialogFecha,
    DialogAgregarArticulos.DialogAgregarArticulo,
    DialogAgregarNumero.DialogAgregarNumero {

    lateinit var adapter : MyViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventario)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        asignarFragments()
    }

    fun asignarFragments(){
        adapter = MyViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(SurtidoFragment() , getString(R.string.surtido_proveedor))
        adapter.addFragment(HistorialSurtidosFragment() , getString(R.string.surtido_Registro))
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
        (fragment as HistorialSurtidosFragment).obtenerFechaInicial()
    }

    override fun obtenerFechaFinal() {
        val fragment: Fragment = adapter.getItem(1)
        (fragment as HistorialSurtidosFragment).obtenerFechaFinal()
    }

    override fun obtenerFecha() {
        val fragment: Fragment = adapter.getItem(1)
        (fragment as HistorialSurtidosFragment).obtenerFecha()
    }

    /*override fun filtrosArticulos(familiaIdTmp:Int,subFamiliaIdTmp:Int,minCantidad:Int,maxCantidad:Int) {
        val fragment: Fragment = adapter.getItem(0)
        (fragment as InventarioFragment).filtroFamilia = familiaIdTmp
        (fragment).filtroSubFamilia = subFamiliaIdTmp
        (fragment).filtroMinCantidad = minCantidad
        (fragment).filtroMaxCantidad = maxCantidad
        (fragment).getInventarioObjecto()


    }*/

    override fun numeroArticulo(articulo : InventarioObjeto) {
        val fragment: Fragment = adapter.getItem(0)
        (fragment as SurtidoFragment).numeroArticulo(articulo)
    }

    override fun obtenerNumero(numero : Int, posicion : Int) {
        val fragment: Fragment = adapter.getItem(0)
        (fragment as SurtidoFragment).obtenerNumero(numero,posicion)
    }

    override fun agregarArticulos(articulosCarrito: MutableList<InventarioObjeto>) {
        val fragment: Fragment = adapter.getItem(0)
        (fragment as SurtidoFragment).agregarArticulos(articulosCarrito)
    }

    override fun abrirCamara() {
        val intentIntegrator = IntentIntegrator(this)
        intentIntegrator.setBeepEnabled(false)
        intentIntegrator.setCameraId(0)
        intentIntegrator.setPrompt("SCAN")
        intentIntegrator.setBarcodeImageEnabled(false)
        intentIntegrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int,resultCode: Int,data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                runOnUiThread {
                    val fragment: Fragment = adapter.getItem(0)
                    (fragment as SurtidoFragment).buscarArticulo(java.lang.Long.parseLong(result.contents))
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun listaArticulos(listaArticulos: MutableList<ArticuloInventarioObjeto>) {
        val fragment: Fragment = adapter.getItem(0)
        (fragment as SurtidoFragment).dialogoAgregarArticulos.llenarListaArticulos(listaArticulos)
    }

}
package com.Aegina.PocketSale.Activities

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.Aegina.PocketSale.Fragments.EstadisticaInventarioFragment
import com.Aegina.PocketSale.Fragments.EstadisticaVentaFragment
import com.Aegina.PocketSale.R
import kotlinx.android.synthetic.main.activity_inventario.*

class EstadisticasPager : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estadisticas_pager)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        asignarFragments()
    }

    fun asignarFragments(){
        val adapter = MyViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(EstadisticaInventarioFragment() , getString(R.string.fragment_inventario))
        adapter.addFragment(EstadisticaVentaFragment() , getString(R.string.fragment_ventas))
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

        fun addFragment(fragment: Fragment, title:String){
            fragmentList.add(fragment)
            titleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titleList[position]
        }

    }
}
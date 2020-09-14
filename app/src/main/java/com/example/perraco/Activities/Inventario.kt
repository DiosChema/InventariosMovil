package com.example.perraco.Activities

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.perraco.Fragments.InventarioFragment
import com.example.perraco.Fragments.VentaFragment
import com.example.perraco.R
import kotlinx.android.synthetic.main.activity_inventario.*


class Inventario : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventario)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        asignarFragments()

    }

    fun asignarFragments(){
        val adapter = MyViewPagerAdapter(supportFragmentManager)
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
}
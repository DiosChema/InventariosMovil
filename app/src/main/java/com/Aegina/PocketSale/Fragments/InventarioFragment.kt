@file:Suppress("DEPRECATION")

package com.Aegina.PocketSale.Fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Activities.InventarioDetalle
import com.Aegina.PocketSale.Dialogs.DialogFiltrarArticulos
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.InventarioObjeto
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.Aegina.PocketSale.RecyclerView.RecyclerViewInventario
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_inventario.*
import okhttp3.*
import java.io.IOException


class InventarioFragment : Fragment() {

    var listaTmp:MutableList<InventarioObjeto> = ArrayList()
    val context = activity;
    lateinit var mViewInventario : RecyclerViewInventario
    lateinit var mRecyclerView : RecyclerView
    lateinit var globalVariable: GlobalClass

    /*var filtroFamilia = -1
    var filtroSubFamilia = -1
    var filtroMinCantidad = -1
    var filtroMaxCantidad = -1
    var filtroMinPrecio = -1.0
    var filtroMaxPrecio = -1.0
    var filtronombreArticulo = ""*/

    //var mostrarBotones = false


    lateinit var fragmentInventarioContenedorRecyclerView : LinearLayout

    var dialogFiltrarArticulos = DialogFiltrarArticulos()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_inventario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        globalVariable = activity?.applicationContext as GlobalClass

        crearRecyclerView()
        asignarBotones()
        crearDialog()
    }

    private fun crearDialog() {
        dialogFiltrarArticulos.crearDialog(activity!!,globalVariable, Activity())
    }

    fun crearRecyclerView(){
        mViewInventario = RecyclerViewInventario()
        mRecyclerView = fragmentInventarioRecyclerView as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        if (context != null) {
            mViewInventario.RecyclerAdapter(listaTmp, context)
        }
        mRecyclerView.adapter = mViewInventario
    }

    fun asignarBotones(){

        var tamanoOriginal = 0
        var tamanoNuevo = 0

        val fragmentInventarioBotones = fragmentInventarioBotones
        val fragmentInventarioContenedor = fragmentInventarioContenedor

        val fragmentInventarioNuevoArticulo = fragmentInventarioNuevoArticulo
        fragmentInventarioNuevoArticulo.setOnClickListener()
        {
            if(globalVariable.usuario!!.permisosAltaInventario)
            {
                val intent = Intent(activity, InventarioDetalle::class.java)
                startActivity(intent)
            }
            else
            {
                Toast.makeText(activity,getString(R.string.permisos_denegado), Toast.LENGTH_LONG).show()
            }
        }

        val fragmentInventarioFiltroArticulo = fragmentInventarioFiltroArticulo
        fragmentInventarioFiltroArticulo.setOnClickListener()
        {
            dialogFiltrarArticulos.mostrarDialog()
        }

        /*val fragmentInventarioMostrarVentana = fragmentInventarioMostrarVentana

        fragmentInventarioMostrarVentana.setOnClickListener()
        {

            val perro = mRecyclerView.layoutParams.height
            val perro2 = mRecyclerView.height

            if(tamanoOriginal == 0)
            {
                tamanoOriginal = mRecyclerView.height
                tamanoNuevo = mRecyclerView.height + fragmentInventarioBotones.height
            }

            if(!mostrarBotones)
            {
                fragmentInventarioMostrarVentana.setImageResource(R.drawable.upicon)

                var modificarTamano = mRecyclerView.layoutParams

                mRecyclerView.layoutParams = modificarTamano
                //fragmentInventarioBotones.visibility = View.GONE
                /*val animate = TranslateAnimation(
                    0F,
                    0F, fragmentInventarioBotones.height.toFloat(), 0F
                )
                animate.duration = 200
                animate.fillAfter = true
                fragmentInventarioBotones.startAnimation(animate)*/
                //Handler().postDelayed(Runnable { fragmentInventarioBotones.visibility = View.GONE }, 200)
                fragmentInventarioContenedor!!.animate()
                    .translationY(-fragmentInventarioBotones.height.toFloat())
                    .alpha(1.0f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)


                            var modificarTamano = mRecyclerView.layoutParams
                            //modificarTamano.height = tamanoOriginal
                            modificarTamano.height = ViewGroup.LayoutParams.WRAP_CONTENT

                            mRecyclerView.layoutParams = modificarTamano

                        }
                    })



                mostrarBotones = true
            }
            else
            {
                fragmentInventarioMostrarVentana.setImageResource(R.drawable.dropicon)
                //fragmentInventarioBotones.visibility = View.VISIBLE

                fragmentInventarioContenedor.animate()
                    .translationY(0F)
                    .alpha(1.0f)

                var modificarTamano = mRecyclerView.layoutParams
                modificarTamano.height = tamanoOriginal

                mRecyclerView.layoutParams = modificarTamano

                mostrarBotones = false
            }

        }*/
    }

    fun obtenerListaArticulos(listaArticulos: MutableList<InventarioObjeto>)
    {
        activity?.runOnUiThread {
            mViewInventario.RecyclerAdapter(listaArticulos, activity!!)
            mViewInventario.notifyDataSetChanged()
        }

    }

    fun getInventarioObjecto(){
        val urls = Urls()

        var url = urls.url+urls.endPointsInventario.endPointInventario+"?token="+globalVariable.usuario!!.token
/*
        if(filtroFamilia != -1) url += "&familiaId=" + filtroFamilia
        if(filtroSubFamilia != -1) url += "&subFamiliaId=" + filtroSubFamilia
        if(filtroMinCantidad != -1) url += "&minimoCantidad=" + filtroMinCantidad
        if(filtroMaxCantidad != -1) url += "&maximoCantidad=" + filtroMaxCantidad
        if(filtroMinPrecio != -1.0) url += "&minimoPrecio=" + filtroMinPrecio
        if(filtroMaxPrecio != -1.0) url += "&maximoPrecio=" + filtroMaxPrecio
        if(filtronombreArticulo != "") url += "&nombreArticulo=" + filtronombreArticulo
*/
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()

        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage(getString(R.string.mensaje_espera))
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response)
            {
                val body = response.body()?.string()

                if(body != null && body.isNotEmpty()) {
                    val gson = GsonBuilder().create()
                    val model = gson.fromJson(body, Array<InventarioObjeto>::class.java).toList()

                    activity?.runOnUiThread {
                        mViewInventario.RecyclerAdapter(model.toMutableList(), activity!!)
                        mViewInventario.notifyDataSetChanged()
                    }
                }

                progressDialog.dismiss()

            }
        })

    }

    override fun onResume() {
        super.onResume()
        getInventarioObjecto()
    }


}
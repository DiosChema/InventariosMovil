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
import com.Aegina.PocketSale.Dialogs.DialogAgregarArticulos
import com.Aegina.PocketSale.Dialogs.DialogFiltrarArticulos
import com.Aegina.PocketSale.Objets.ArticuloInventarioObjeto
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

    lateinit var fragmentInventarioContenedorRecyclerView : LinearLayout

    var dialogAgregarArticulos = DialogAgregarArticulos()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_inventario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        globalVariable = activity?.applicationContext as GlobalClass

        crearRecyclerView()
        asignarBotones()
        crearDialog()
        dialogAgregarArticulos.buscarArticulos()
    }

    private fun crearDialog() {
        dialogAgregarArticulos.crearDialogInicial(activity!!,globalVariable, Activity())
        dialogAgregarArticulos.crearDialogFiltros()
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
            dialogAgregarArticulos.mostrarDialogFiltros()
        }
    }

    fun obtenerListaArticulos(listaArticulos: MutableList<ArticuloInventarioObjeto>)
    {
        listaTmp.clear()

        for(i in 0 until listaArticulos.size)
        {
            listaTmp.add(
                InventarioObjeto(
                    listaArticulos[i].idArticulo,
                    listaArticulos[i].nombreArticulo,
                    listaArticulos[i].descripcionArticulo,
                    listaArticulos[i].cantidadArticulo,
                    listaArticulos[i].precioArticulo,
                    listaArticulos[i].familiaArticulo,
                    listaArticulos[i].costoArticulo,
                    listaArticulos[i].inventarioOptimo))
        }


        activity?.runOnUiThread()
        {
            mViewInventario.RecyclerAdapter(listaTmp, activity!!)
            mViewInventario.notifyDataSetChanged()
        }

    }

    fun getInventarioObjecto(){
        val urls = Urls()

        var url = urls.url+urls.endPointsInventario.endPointInventario+"?token="+globalVariable.usuario!!.token
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

}
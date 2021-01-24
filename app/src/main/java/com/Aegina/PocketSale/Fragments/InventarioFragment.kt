@file:Suppress("DEPRECATION")

package com.Aegina.PocketSale.Fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Activities.InventarioDetalle
import com.Aegina.PocketSale.Dialogs.DialogAgregarArticulos
import com.Aegina.PocketSale.Metodos.Paginado
import com.Aegina.PocketSale.Objets.ArticuloInventarioObjeto
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.InventarioObjeto
import com.Aegina.PocketSale.Objets.Inventory.ListInventoryObject
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
    val paginado = Paginado()

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
        dialogAgregarArticulos.crearDialogArticulos(0)
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

        val fragmentInventarioLeft = fragmentInventarioLeft
        fragmentInventarioLeft.setOnClickListener()
        {
            if(dialogAgregarArticulos.totalArticulos > dialogAgregarArticulos.limiteArticulos && dialogAgregarArticulos.pagina > 0)
            {
                dialogAgregarArticulos.pagina--
                dialogAgregarArticulos.buscarArticulos()
            }
        }

        val fragmentInventarioRight = fragmentInventarioRight
        fragmentInventarioRight.setOnClickListener()
        {
            val maximoPaginas = paginado.obtenerPaginadoMaximo(dialogAgregarArticulos.totalArticulos, dialogAgregarArticulos.limiteArticulos)

            if(dialogAgregarArticulos.totalArticulos > dialogAgregarArticulos.limiteArticulos && dialogAgregarArticulos.pagina < maximoPaginas)
            {
                dialogAgregarArticulos.pagina++
                dialogAgregarArticulos.buscarArticulos()
            }
        }

    }

    fun obtenerListaArticulos(listInventoryObject: ListInventoryObject)
    {

        listaTmp.clear()

        for(i in listInventoryObject.Inventory.indices)
        {
            listaTmp.add(
                InventarioObjeto(
                    listInventoryObject.Inventory[i].idArticulo,
                    listInventoryObject.Inventory[i].nombre,
                    listInventoryObject.Inventory[i].descripcion,
                    listInventoryObject.Inventory[i].cantidad,
                    listInventoryObject.Inventory[i].precio,
                    listInventoryObject.Inventory[i].familia,
                    listInventoryObject.Inventory[i].costo,
                    listInventoryObject.Inventory[i].inventarioOptimo,
                    listInventoryObject.Inventory[i].modificaInventario))
        }


        activity?.runOnUiThread()
        {
            mViewInventario.RecyclerAdapter(listaTmp, activity!!)
            mViewInventario.notifyDataSetChanged()

            if(listInventoryObject.count > dialogAgregarArticulos.limiteArticulos)
            {
                fragmentInventarioLeft.visibility = View.VISIBLE
                fragmentInventarioRight.visibility = View.VISIBLE
            }
            else
            {
                fragmentInventarioLeft.visibility = View.GONE
                fragmentInventarioRight.visibility = View.GONE
            }
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
        progressDialog.setCancelable(false)
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

        globalVariable = activity?.applicationContext as GlobalClass

        if(globalVariable.actualizarVentana!!.actualizarInventario)
        {
            globalVariable.actualizarVentana!!.actualizarInventario = false
            dialogAgregarArticulos.buscarArticulos()
        }
    }

}
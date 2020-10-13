@file:Suppress("DEPRECATION")

package com.Aegina.PocketSale.Fragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Activities.InventarioDetalle
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_inventario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        globalVariable = getActivity()?.applicationContext as GlobalClass

        crearRecyclerView()
        asignarBotones()
    }

    fun crearRecyclerView(){
        mViewInventario = RecyclerViewInventario()
        mRecyclerView = rvInventario as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        if (context != null) {
            mViewInventario.RecyclerAdapter(listaTmp, context)
        }
        mRecyclerView.adapter = mViewInventario
    }

    fun asignarBotones(){
        val button = rvNuevoArticulo
        button?.setOnClickListener()
        {
            val intent = Intent(activity, InventarioDetalle::class.java)
            startActivity(intent)
        }
    }

    fun getInventarioObjecto(mViewInventario : RecyclerViewInventario){
        val urls = Urls()

        val url = urls.url+urls.endPointInventario+"?token="+globalVariable.token

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
        getInventarioObjecto(mViewInventario)
    }


}
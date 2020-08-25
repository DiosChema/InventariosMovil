package com.example.perraco.Fragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.perraco.Activities.InventarioDetalle
import com.example.perraco.Objets.Urls
import com.example.perraco.Objets.VentasObjeto
import com.example.perraco.R
import com.example.perraco.RecyclerView.RecyclerViewVentas
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_inventario.*
import kotlinx.android.synthetic.main.fragment_venta.*
import okhttp3.*
import java.io.IOException

class VentaFragment : Fragment() {

    var listaTmp:MutableList<VentasObjeto> = ArrayList()
    val context = activity;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_venta, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var mViewVentas = RecyclerViewVentas()
        var mRecyclerView : RecyclerView = rvVentas as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        if (context != null) {
            mViewVentas.RecyclerAdapter(listaTmp, context)
        }
        mRecyclerView.adapter = mViewVentas

        getVentasObjecto(mViewVentas,mRecyclerView)

        val button = rvNuevoArticulo
        button?.setOnClickListener()
        {
            val intent = Intent(context, InventarioDetalle::class.java)
            startActivity(intent)
        }
    }

    fun getVentasObjecto(mViewVentas : RecyclerViewVentas, mRecyclerView : RecyclerView ){
        val urls: Urls =
            Urls()

        val url = urls.url+urls.endPointObtenerFacturas+"?tienda=00001"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()

        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Application is loading, please wait")
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response)
            {
                var body = response.body()?.string()
                val gson = GsonBuilder().create()
                var Model = gson.fromJson(body,Array<VentasObjeto>::class.java).toList()

                activity?.runOnUiThread {
                    mViewVentas.RecyclerAdapter(Model.toMutableList(), activity!!)
                    mViewVentas.notifyDataSetChanged()
                    progressDialog.dismiss()
                }

                progressDialog.dismiss()

            }
        })

    }
}
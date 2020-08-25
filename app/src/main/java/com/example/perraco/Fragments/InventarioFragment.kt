package com.example.perraco.Fragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.perraco.*
import com.example.perraco.Activities.InventarioDetalle
import com.example.perraco.Objets.InventarioObjeto
import com.example.perraco.Objets.Urls
import com.example.perraco.RecyclerView.RecyclerViewInventario
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_inventario.*
import okhttp3.*
import java.io.IOException

class InventarioFragment : Fragment() {

    var listaTmp:MutableList<InventarioObjeto> = ArrayList()
    val context = activity;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_inventario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var mViewInventario = RecyclerViewInventario()
        var mRecyclerView : RecyclerView = rvInventario as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        if (context != null) {
            mViewInventario.RecyclerAdapter(listaTmp, context)
        }
        mRecyclerView.adapter = mViewInventario

        getInventarioObjecto(mViewInventario,mRecyclerView)

        val button = rvNuevoArticulo
        button?.setOnClickListener()
        {
            val intent = Intent(context, InventarioDetalle::class.java)
            startActivity(intent)
        }
    }

    fun getInventarioObjecto(mViewInventario : RecyclerViewInventario, mRecyclerView : RecyclerView ){
        val urls: Urls =
            Urls()

        val url = urls.url+urls.endPointInventario+"?tienda=00001"

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
                var Model = gson.fromJson(body,Array<InventarioObjeto>::class.java).toList()

                activity?.runOnUiThread {
                    mViewInventario.RecyclerAdapter(Model.toMutableList(), activity!!)
                    mViewInventario.notifyDataSetChanged()
                    progressDialog.dismiss()
                }

                progressDialog.dismiss()

            }
        })

    }
}
package com.example.perraco

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*


class Inventario : AppCompatActivity() {

    var context = this;
    var listaTmp:MutableList<InventarioObjeto> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventario)

        context = this;
        var mAdapter : RecyclerAdapter = RecyclerAdapter()
        var mRecyclerView : RecyclerView = findViewById(R.id.rvInventario) as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter.RecyclerAdapter(listaTmp, context)
        mRecyclerView.adapter = mAdapter

        getInventarioObjecto(mAdapter,mRecyclerView)

        val button = findViewById<Button>(R.id.rvNuevoArticulo)
        button?.setOnClickListener()
        {
            val intent = Intent(this, InventarioDetalle::class.java)
            startActivity(intent)
        }

    }

    fun getInventarioObjecto(mAdapter : RecyclerAdapter, mRecyclerView : RecyclerView ){
        val urls: Urls = Urls()

        val url = urls.url+urls.endPointInventario+"?tienda=00001"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Application is loading, please wait")
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                var aaa = "aaaa"
                aaa = "aaaaaa"
                mAdapter.notifyDataSetChanged()
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response)
            {
                var body = response.body()?.string()
                val gson = GsonBuilder().create()
                var Model = gson.fromJson(body,Array<InventarioObjeto>::class.java).toList()

                runOnUiThread {
                    mAdapter.RecyclerAdapter(Model.toMutableList(), context)
                    mAdapter.notifyDataSetChanged()
                    progressDialog.dismiss()
                }

            }
        })

    }
}
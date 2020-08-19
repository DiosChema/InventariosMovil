package com.example.perraco

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException


class Inventario : AppCompatActivity() {

    val JSON = MediaType.parse("application/json; charset=utf-8")
    var context = this;
    var superheros:MutableList<InventarioObjeto> = ArrayList()
    var superheros2:MutableList<InventarioObjeto> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventario)

        context = this;
        /*var mAdapter : RecyclerAdapter = RecyclerAdapter()
        var mRecyclerView : RecyclerView = findViewById(R.id.rvInventario) as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter.RecyclerAdapter(getInventarioObjecto(), this)
        mRecyclerView.adapter = mAdapter*/

        var mAdapter : RecyclerAdapter = RecyclerAdapter()
        var mRecyclerView : RecyclerView = findViewById(R.id.rvInventario) as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter.RecyclerAdapter(superheros, context)
        mRecyclerView.adapter = mAdapter


        getInventarioObjecto(mAdapter,mRecyclerView)

    }

    //fun getInventarioObjecto(): MutableList<InventarioObjeto>{
    fun getInventarioObjecto(mAdapter : RecyclerAdapter, mRecyclerView : RecyclerView ){
        //var urlsss = "http://api.jsonbin.io/b/5f3b3d4f4d9399103616fc70"
        //val url = urlsss

        superheros2.add(InventarioObjeto("1", "Leche","1lt Lala", 2, "15.50","1","10.50","https://media.istockphoto.com/vectors/realistic-3d-milk-carton-packing-isolated-on-white-vector-id1028104580"))
        superheros2.add(InventarioObjeto("2", "Arroz","Del marquez oxxo oxxo oxxo oxxo oxxo oxxo oxxo oxxo", 3, "18.99", "3","12.50","https://resources.claroshop.com/medios-plazavip/s2/11073/1286431/5dfa53c9dfd7e-7501071301452-1600x1600.jpg"))
        superheros2.add(InventarioObjeto("3", "Salchicha","Food", 4, "666.66","2","15.00","https://previews.123rf.com/images/saddako/saddako1406/saddako140600002/29139005-salchichas-en-un-paquete-de-pl%C3%A1stico-aisladas-sobre-fondo-blanco.jpg"))



        /*val body: RequestBody = RequestBody.create(JSON,"{\n" +
                "   \"tienda\":\"00001\"\n" +
                "}")
        */


        val urls: Urls = Urls()

        val url = urls.url+urls.endPointInventario+"?tienda=00001"


        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()


        //val body = client.newCall(request).execute().body().toString()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                var aaa = "aaaa"
                aaa = "aaaaaa"
                mAdapter.notifyDataSetChanged()
            }
            override fun onResponse(call: Call, response: Response)
            {
                var body = response.body()?.string()
                val gson = GsonBuilder().create()
                var Model = gson.fromJson(body,Array<InventarioObjeto>::class.java).toList()

                runOnUiThread {
                    //mAdapter.RecyclerAdapter(Model.toMutableList(), context)
                    mAdapter.RecyclerAdapter(Model.toMutableList(), context)
                    //mAdapter.RecyclerAdapter(Model.toMutableList(), context)
                    mAdapter.notifyDataSetChanged()
                    /*listView.setVisibility(View.VISIBLE)
                    mProgressBar.setVisibility(View.GONE)*/
                }

            }
        })



        /*var superheros:MutableList<InventarioObjeto> = ArrayList()
        superheros.add(InventarioObjeto(1, "Leche","1lt Lala", 2, 15.50,1,10.50,"https://media.istockphoto.com/vectors/realistic-3d-milk-carton-packing-isolated-on-white-vector-id1028104580"))
        superheros.add(InventarioObjeto(2, "Arroz","Del marquez oxxo oxxo oxxo oxxo oxxo oxxo oxxo oxxo", 3, 18.99, 3,12.50,"https://resources.claroshop.com/medios-plazavip/s2/11073/1286431/5dfa53c9dfd7e-7501071301452-1600x1600.jpg"))
        superheros.add(InventarioObjeto(3, "Salchicha","Food", 4, 666.66,2,15.00,"https://previews.123rf.com/images/saddako/saddako1406/saddako140600002/29139005-salchichas-en-un-paquete-de-pl%C3%A1stico-aisladas-sobre-fondo-blanco.jpg"))
        superheros.add(InventarioObjeto(4, "Takis","Frituras", 0,18.00,5,4.00, "https://res.cloudinary.com/walmart-labs/image/upload/w_960,dpr_auto,f_auto,q_auto:good/gr/images/product-images/img_large/00750103042453L.jpg"))
        superheros.add(InventarioObjeto(5, "Sabritas","Frituras", 1,12.50,2,5.00, "https://www.chedraui.com.mx/medias/750101111560-00-CH1200Wx1200H?context=bWFzdGVyfHJvb3R8MTI5NDI4fGltYWdlL2pwZWd8aDg4L2g1Ny84ODIwOTM1OTUwMzY2LmpwZ3wyZDAyNWI4MGY4ODhiMTNiNGNiNWE5YTQ2YzY2MzlhZjE2MTA0NGM0OWZjZTc4N2NhMjQ4NmNlMmFlMzA4ODdm"))
        superheros.add(InventarioObjeto(6, "Atun","Maz atun", 3, 13.00,3,8.00,"https://resources.claroshop.com/medios-plazavip/s2/11073/1364131/5e86bd9bf27b5-0df8e081-7c06-4723-9227-082043e70c75-1600x1600.jpg"))
        superheros.add(InventarioObjeto(7, "Mayonesa","mccorney", 4,28.00,2, 14.00,"https://www.chedraui.com.mx/medias/750100334053-00-CH1200Wx1200H?context=bWFzdGVyfHJvb3R8MTA5ODYyfGltYWdlL2pwZWd8aGJiL2gyMC84ODIwNTk5MzU3NDcwLmpwZ3wxNTNjZWM2ZmY3ZTM0NTc5M2ZjMWE5NTIwZDJhMGU4NmQwYTI2ZTgwNWQ0ODAyMzBjYzQ3MzE5ODgxMDE4MDIw"))
        superheros.add(InventarioObjeto(8, "Venezuela","Hambre", 1,0.00,4,-15.00, "https://thumbs.dreamstime.com/z/mapa-pol%C3%ADtico-de-venezuela-102631415.jpg"))*/
        //return Model.toMutableList()
    }
}
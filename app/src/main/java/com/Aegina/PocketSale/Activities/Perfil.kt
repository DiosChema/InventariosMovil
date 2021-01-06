package com.Aegina.PocketSale.Activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.Aegina.PocketSale.Metodos.Errores
import com.Aegina.PocketSale.Objets.EmpleadoObject
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.Aegina.PocketSale.RecyclerView.RecyclerViewEmpleado
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class Perfil : AppCompatActivity() {

    var listaTmp:MutableList<EmpleadoObject> = ArrayList()
    lateinit var mViewEmpleados : RecyclerViewEmpleado
    lateinit var mRecyclerView : RecyclerView
    lateinit var globalVariable: GlobalClass
    lateinit var context : Context
    lateinit var activity: Activity
    lateinit var perfilAgregarEmpleado: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        globalVariable = this.applicationContext as GlobalClass

        context = this
        activity = this

        asignarRecursos()
        asignarBotones()
        crearRecyclerView()
        obtenerEmpleados()
    }

    private fun asignarRecursos() {
        perfilAgregarEmpleado = findViewById(R.id.perfilAgregarEmpleado)
    }

    private fun asignarBotones() {
        perfilAgregarEmpleado.setOnClickListener{
            val intent = Intent(this, PerfilDetalle::class.java)
            startActivity(intent)
        }
    }

    fun crearRecyclerView(){
        mViewEmpleados = RecyclerViewEmpleado()
        mRecyclerView = findViewById(R.id.recyclerViewEmpleados)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL ,false)
        mViewEmpleados.RecyclerAdapter(listaTmp, this)
        mRecyclerView.adapter = mViewEmpleados
    }

    fun obtenerEmpleados(){
        val urls = Urls()

        val url = urls.url+urls.endPointUsers.endPointObtenerEmpleados+"?token="+globalVariable.usuario!!.token

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.mensaje_espera))
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                runOnUiThread()
                {
                    Toast.makeText(context, context.getString(R.string.mensaje_error), Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response)
            {
                val body = response.body()?.string()

                if(body != null && body.isNotEmpty()) {
                    try
                    {
                        val gson = GsonBuilder().create()
                        val model = gson.fromJson(body, Array<EmpleadoObject>::class.java).toList()

                        runOnUiThread {
                            mViewEmpleados.RecyclerAdapter(model.toMutableList(), context)
                            mViewEmpleados.notifyDataSetChanged()
                        }
                    }
                    catch(e:Exception)
                    {
                        val errores = Errores()
                        errores.procesarError(context,body,activity)
                    }

                }

                progressDialog.dismiss()

            }
        })

    }

}
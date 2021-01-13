@file:Suppress("DEPRECATION")

package com.Aegina.PocketSale.Activities

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.Aegina.PocketSale.Dialogs.DialogSeleccionarFoto
import com.Aegina.PocketSale.Metodos.Errores
import com.Aegina.PocketSale.Objets.*
import com.Aegina.PocketSale.R
import com.google.gson.GsonBuilder
import com.google.zxing.integration.android.IntentIntegrator
import com.squareup.picasso.Picasso
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.lang.Double.parseDouble
import java.lang.Exception
import java.lang.Integer.parseInt
import java.lang.Long.parseLong
import java.util.*
import kotlin.collections.ArrayList


class InventarioDetalle : AppCompatActivity(),
    DialogSeleccionarFoto.DialogSeleccionarFoto {

    lateinit var invDetalleId : EditText
    lateinit var invDetalleFoto: ImageView
    lateinit var invDetalleNombre : EditText
    lateinit var invDetalleNombreDetalle : EditText
    lateinit var invDetalleCantidad : EditText
    lateinit var invDetallePrecio : EditText
    lateinit var invDetalleCosto : EditText
    lateinit var invDetalleInventarioOptimo : EditText
    lateinit var invDetalleFamiliaSpinner : Spinner
    lateinit var invDetalleSubFamiliaSpinner : Spinner
    lateinit var invDetalleDarDeAlta : Button
    lateinit var invBotonEliminarFamilia : ImageButton
    lateinit var invBotonAgregarFamilia : ImageButton
    lateinit var invBotonEliminarSubFamilia : ImageButton
    lateinit var invBotonAgregarSubFamilia : ImageButton
    lateinit var invBottonTomarCodigo : ImageButton
    lateinit var invDetalleCancelarEdicion : Button
    lateinit var invDetalleEliminarArticulo : ImageButton
    lateinit var invDetalleEliminarArticuloCardView : CardView
    var posicionFamilia = 0
    var posicionSubFamilia = 0
    var dialogSeleccionarFoto = DialogSeleccionarFoto()

    lateinit var globalVariable: GlobalClass

    private val urls: Urls = Urls()
    var subFamiliaPendiente = false
    var subFamiliaPendienteIndex = 0
    var aaa : String = "a"
    lateinit var inventarioObjeto:InventarioObjeto
    var listaFamilia:MutableList<String> = ArrayList()
    var listaFamiliaCompleta:MutableList<FamiliasSubFamiliasObject> = ArrayList()
    var listaSubFamilia:MutableList<String> = ArrayList()
    var listaSubFamiliaCompleta:MutableList<SubFamiliaObjeto> = ArrayList()

    var image_uri: Uri? = null
    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    var cambioFoto : Boolean = false

    var subFamiliaId = -1
    var posicionPendiente = false

    lateinit var context : Context
    lateinit var activity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_inventario_detalle)

        globalVariable = applicationContext as GlobalClass

        context = this
        activity = this

        asignarRecursos()

        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
            StrictMode.setThreadPolicy(policy)
        }

        if(intent.getSerializableExtra("articulo") != null)
        {
            inventarioObjeto = intent.getSerializableExtra("articulo") as InventarioObjeto

            habilitarEdicion(false)
            llenarCampos(inventarioObjeto)
            asignarFuncionBotones(true)
        }
        else
        {
            obtenerFamilias(this,-1)
            asignarFuncionBotones(false)
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        dialogSeleccionarFoto.crearDialog(this)
    }

    fun asignarRecursos() {
        invDetalleId = findViewById(R.id.invDetalleId)
        invDetalleFoto = findViewById(R.id.invDetalleFoto)
        invDetalleNombre = findViewById(R.id.invDetalleNombre)
        invDetalleNombreDetalle = findViewById(R.id.invDetalleNombreDetalle)
        invDetalleCantidad = findViewById(R.id.invDetalleCantidad)
        invDetallePrecio = findViewById(R.id.invDetallePrecio)
        invDetalleCosto = findViewById(R.id.invDetalleCosto)
        invDetalleInventarioOptimo = findViewById(R.id.invDetalleInventarioOptimo)
        invDetalleFamiliaSpinner = findViewById(R.id.invDetalleFamiliaSpinner)
        invDetalleSubFamiliaSpinner = findViewById(R.id.invDetalleSubFamiliaSpinner)
        invDetalleDarDeAlta = findViewById(R.id.invDetalleDarDeAlta)
        invBotonEliminarFamilia = findViewById(R.id.invBotonEliminarFamilia)
        invBotonAgregarFamilia = findViewById(R.id.invBotonAgregarFamilia)
        invBotonEliminarSubFamilia = findViewById(R.id.invBotonEliminarSubFamilia)
        invBotonAgregarSubFamilia = findViewById(R.id.invBotonAgregarSubFamilia)
        invBottonTomarCodigo = findViewById(R.id.dialogFiltroReestablecerCampos)
        invDetalleCancelarEdicion = findViewById(R.id.invDetalleCancelarEdicion)
        invDetalleEliminarArticulo = findViewById(R.id.invDetalleEliminarArticulo)
        invDetalleEliminarArticuloCardView = findViewById(R.id.invDetalleEliminarArticuloCardView)

        invDetalleCancelarEdicion.visibility = View.INVISIBLE
        invDetalleEliminarArticuloCardView.visibility = View.INVISIBLE
    }

    fun habilitarEdicion(activar : Boolean) {
        invDetalleId.isEnabled = false
        invDetalleFoto.isEnabled = activar
        invDetalleNombre.isEnabled = activar
        invDetalleNombreDetalle.isEnabled = activar
        invDetalleCantidad.isEnabled = activar
        invDetallePrecio.isEnabled = activar
        invDetalleCosto.isEnabled = activar
        invDetalleInventarioOptimo.isEnabled = activar
        invDetalleFamiliaSpinner.isEnabled = activar
        invDetalleSubFamiliaSpinner.isEnabled = activar
        invDetalleDarDeAlta.isEnabled = true
        invBotonEliminarFamilia.isEnabled = activar
        invBotonAgregarFamilia.isEnabled = activar
        invBotonEliminarSubFamilia.isEnabled = activar
        invBotonAgregarSubFamilia.isEnabled = activar
        invBottonTomarCodigo.isEnabled = false
        invDetalleCancelarEdicion.visibility = View.INVISIBLE
        invDetalleEliminarArticuloCardView.visibility = View.INVISIBLE

        if(!globalVariable.usuario!!.permisosModificarInventario)
        {
            invDetalleDarDeAlta.visibility = View.GONE
        }

        if (activar) {

            invDetalleCancelarEdicion.visibility = View.VISIBLE
            invDetalleEliminarArticuloCardView.visibility = View.VISIBLE

            invDetalleDarDeAlta.text = getString(R.string.mensaje_actualizar_articulo)
            invDetalleDarDeAlta.setOnClickListener{
                if (!datosVacios())
                    actualizarArticulo()
            }

            invDetalleEliminarArticulo.setOnClickListener{
                showDialogEliminarArticulo()
            }

        }
        else {
            invDetalleDarDeAlta.text = getString(R.string.mensaje_editar)
            invDetalleDarDeAlta.setOnClickListener{
                habilitarEdicion(true)
            }
        }

    }

    fun llenarCampos(articulo : InventarioObjeto){
        invDetalleId.setText(articulo.idArticulo.toString())
        invDetalleFoto.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+articulo.idArticulo+".jpeg"+"&token="+globalVariable.usuario!!.token)
        invDetalleNombre.setText(articulo.nombre)
        invDetalleNombreDetalle.setText(articulo.descripcion)
        invDetalleCantidad.setText(articulo.cantidad.toString())
        invDetallePrecio.setText(articulo.precio.toString())
        invDetalleCosto.setText(articulo.costo.toString())
        invDetalleInventarioOptimo.setText(articulo.inventarioOptimo.toString())

        listaFamiliaCompleta.clear()
        listaFamilia.clear()
        cambioFoto = false
        posicionPendiente = true
        obtenerFamilias(this,parseInt(articulo.subFamilia))
    }

    fun reLlenarCampos(articulo : InventarioObjeto){
        invDetalleId.setText(articulo.idArticulo.toString())
        invDetalleFoto.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+articulo.idArticulo+".jpeg"+"&token="+globalVariable.usuario!!.token)
        invDetalleNombre.setText(articulo.nombre)
        invDetalleNombreDetalle.setText(articulo.descripcion)
        invDetalleCantidad.setText(articulo.cantidad.toString())
        invDetallePrecio.setText(articulo.precio.toString())
        invDetalleCosto.setText(articulo.costo.toString())
        invDetalleInventarioOptimo.setText(articulo.inventarioOptimo.toString())

        cambioFoto = false
        posicionPendiente = true

        invDetalleFamiliaSpinner.setSelection(posicionFamilia)
        invDetalleSubFamiliaSpinner.setSelection(posicionSubFamilia)
    }



    fun asignarFuncionBotones(esEditar : Boolean){

        invDetalleFoto.setOnClickListener{
            dialogSeleccionarFoto.mostrarDialogoFoto()
        }
        invBotonAgregarFamilia.setOnClickListener{
            showDialogAgregarFamilia()
        }
        invBotonAgregarSubFamilia.setOnClickListener{
            showDialogAgregarSubFamilia()
        }
        invBotonEliminarFamilia.setOnClickListener{
            if (listaFamiliaCompleta.size > 0)
                eliminarFamilia(this)
        }
        invBotonEliminarSubFamilia.setOnClickListener {
            if (listaFamiliaCompleta.size > 0 && listaFamiliaCompleta[invDetalleFamiliaSpinner.selectedItemPosition].SubFamilia.size > 0)
                eliminarSubFamilia(this)
        }
        invBottonTomarCodigo.setOnClickListener{
            val intentIntegrator = IntentIntegrator(this)
            intentIntegrator.setBeepEnabled(false)
            intentIntegrator.setCameraId(0)
            intentIntegrator.setPrompt("SCAN")
            intentIntegrator.setBarcodeImageEnabled(false)
            intentIntegrator.initiateScan()
        }
        invDetalleCancelarEdicion.setOnClickListener{
            reLlenarCampos(inventarioObjeto)
            habilitarEdicion(false)
        }

        if(!esEditar){
            invDetalleDarDeAlta.setOnClickListener{
                if (!datosVacios())
                    darDeAltaArticulo()
            }
        }
    }

    private fun showDialogAgregarFamilia() {
        val dialog = Dialog(this)
        dialog.setTitle(title)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_text)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogText = dialog.findViewById(R.id.dialogTextEntrada) as EditText
        val dialogAceptar = dialog.findViewById(R.id.dialogTextAceptar) as Button
        val dialogCancelar = dialog.findViewById(R.id.dialogTextCancelar) as Button
        val dialogTitulo = dialog.findViewById(R.id.dialogTextTitulo) as TextView

        dialogTitulo.text = getString(R.string.dialog_agregar_familia)

        dialogAceptar.setOnClickListener {
            ocultarTeclado()
            dialog.dismiss()
            if(dialogText.text.isNotEmpty())
            {
                agregarFamilia(this,dialogText.text.toString())
            }
        }

        dialogCancelar.setOnClickListener {
            ocultarTeclado()
            dialog.dismiss()
        }

        dialog.show()

        dialogText.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun showDialogAgregarSubFamilia() {
        val dialog = Dialog(this)
        dialog.setTitle(title)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_text)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogText = dialog.findViewById(R.id.dialogTextEntrada) as EditText
        val dialogAceptar = dialog.findViewById(R.id.dialogTextAceptar) as Button
        val dialogCancelar = dialog.findViewById(R.id.dialogTextCancelar) as Button
        val dialogTitulo = dialog.findViewById(R.id.dialogTextTitulo) as TextView

        dialogTitulo.text = getString(R.string.dialog_agregar_subfamilia)

        dialogAceptar.setOnClickListener {
            ocultarTeclado()
            dialog.dismiss()
            if(dialogText.text.isNotEmpty())
            {
                agregarSubFamilia(this,dialogText.text.toString())
            }
        }

        dialogCancelar.setOnClickListener()
        {
            ocultarTeclado()
            dialog.dismiss()
        }

        dialog.show()

        dialogText.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

    }

    fun ocultarTeclado()
    {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isAcceptingText)
        {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }
    }

    private fun showDialogEliminarArticulo() {
        val dialog = Dialog(this)
        dialog.setTitle(title)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_text)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogText = dialog.findViewById(R.id.dialogTextEntrada) as EditText
        val dialogAceptar = dialog.findViewById(R.id.dialogTextAceptar) as Button
        val dialogCancelar = dialog.findViewById(R.id.dialogTextCancelar) as Button
        val dialogTitulo = dialog.findViewById(R.id.dialogTextTitulo) as TextView

        dialogTitulo.text = getString(R.string.dialog_eliminar_articulo)
        dialogText.visibility = View.INVISIBLE

        dialogAceptar.setOnClickListener {
            dialog.dismiss()
            eliminarArticulo()
        }

        dialogCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    fun agregarFamilia(context: Context, nombreFamilia : String){

        val url = urls.url+urls.endPointFamilias.endPointAgregarFamilia

        val jsonObject = JSONObject()
        try {
            jsonObject.put("token", globalVariable.usuario!!.token)
            jsonObject.put("nombreFamilia", nombreFamilia)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonObject.toString())

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.mensaje_espera))
        progressDialog.setCancelable(false)
        progressDialog.show()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                runOnUiThread()
                {
                    Toast.makeText(context, context.getString(R.string.mensaje_error_intentear_mas_tarde), Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response)
            {
                progressDialog.dismiss()

                val body = response.body()?.string()

                if(body != null && body.isNotEmpty())
                {
                    try
                    {
                        val gson = GsonBuilder().create()
                        val familia = gson.fromJson(body,FamiliaObjeto::class.java)

                        if(familia.nombreFamilia == null)
                        {
                            throw Exception("")
                        }

                        listaFamilia.add(familia.nombreFamilia)
                        listaFamiliaCompleta.add(FamiliasSubFamiliasObject(familia.familiaId, familia.nombreFamilia, ArrayList()))

                        runOnUiThread {
                            val adapter = ArrayAdapter(context,
                                android.R.layout.simple_spinner_item, listaFamilia)
                            invDetalleFamiliaSpinner.adapter = adapter
                            invDetalleFamiliaSpinner.setSelection(listaFamilia.size-1)

                            invDetalleFamiliaSpinner.onItemSelectedListener = object :
                                AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>,view: View, position: Int, id: Long) {
                                    obtenerSubFamilias(context,listaFamiliaCompleta[position].SubFamilia, position)
                                }

                                override fun onNothingSelected(parent: AdapterView<*>) {
                                    // write code to perform some action
                                }
                            }
                        }
                    }
                    catch(e:Exception)
                    {
                        val errores = Errores()
                        errores.procesarError(context,body,activity)
                    }
                }
            }
        })
    }

    fun agregarSubFamilia(context: Context, nombreFamilia : String){

        val url = urls.url+urls.endPointFamilias.endPointAgregarSubFamilia

        val jsonObject = JSONObject()
        try {
            jsonObject.put("token", globalVariable.usuario!!.token)
            jsonObject.put("nombreSubFamilia", nombreFamilia)
            jsonObject.put("idFamilia", listaFamiliaCompleta[invDetalleFamiliaSpinner.selectedItemPosition].familiaId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonObject.toString())

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.mensaje_espera))
        progressDialog.setCancelable(false)
        progressDialog.show()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                runOnUiThread()
                {
                    Toast.makeText(context, context.getString(R.string.mensaje_error_intentear_mas_tarde), Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response)
            {
                val body = response.body()?.string()

                if(body != null && body.isNotEmpty())
                {
                    try
                    {
                        val gson = GsonBuilder().create()
                        val subFamilia = gson.fromJson(body,SubFamiliaObjeto::class.java)

                        if(subFamilia.nombreSubFamilia == null)
                        {
                            throw Exception("")
                        }

                        listaSubFamilia.add(subFamilia.nombreSubFamilia)
                        //listaSubFamiliaCompleta.add(subFamilia)
                        listaFamiliaCompleta[invDetalleFamiliaSpinner.selectedItemPosition].SubFamilia.add(subFamilia)

                        runOnUiThread {
                            val adapter = ArrayAdapter(context,
                                android.R.layout.simple_spinner_item, listaSubFamilia)
                            invDetalleSubFamiliaSpinner.adapter = adapter
                            invDetalleSubFamiliaSpinner.setSelection(listaSubFamilia.size-1)

                            invDetalleSubFamiliaSpinner.onItemSelectedListener = object :
                                AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>,
                                                            view: View, position: Int, id: Long) {
                                    if(listaSubFamilia.size > 0) {
                                        subFamiliaId = listaFamiliaCompleta[invDetalleFamiliaSpinner.selectedItemPosition].SubFamilia[position].subFamiliaId
                                    }
                                }

                                override fun onNothingSelected(parent: AdapterView<*>) {}
                            }
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

    fun eliminarFamilia(context: Context){

        val url = urls.url+urls.endPointFamilias.endPointEliminarFamilia

        val jsonObject = JSONObject()
        try {
            jsonObject.put("token", globalVariable.usuario!!.token)
            jsonObject.put("familiaId", listaFamiliaCompleta[invDetalleFamiliaSpinner.selectedItemPosition].familiaId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonObject.toString())

        val request = Request.Builder()
            .url(url)
            .delete(body)
            .build()

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.mensaje_espera))
        progressDialog.setCancelable(false)
        progressDialog.show()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                runOnUiThread()
                {
                    Toast.makeText(context, context.getString(R.string.mensaje_error_intentear_mas_tarde), Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response)
            {
                val body = response.body()?.string()

                if(body != null && body.isNotEmpty())
                {
                    try
                    {
                        val gson = GsonBuilder().create()

                        val respuesta = gson.fromJson(body,RespuestaObjeto::class.java)

                        if(respuesta.respuesta == 0)
                        {
                            listaFamilia.removeAt(invDetalleFamiliaSpinner.selectedItemPosition)
                            listaFamiliaCompleta.removeAt(invDetalleFamiliaSpinner.selectedItemPosition)

                            runOnUiThread {
                                val adapter = ArrayAdapter(context,
                                    android.R.layout.simple_spinner_item, listaFamilia)
                                invDetalleFamiliaSpinner.adapter = adapter

                                if(listaFamilia.size > 0) {
                                    invDetalleFamiliaSpinner.setSelection(0)

                                    invDetalleFamiliaSpinner.onItemSelectedListener = object :
                                        AdapterView.OnItemSelectedListener {
                                        override fun onItemSelected(
                                            parent: AdapterView<*>,
                                            view: View, position: Int, id: Long
                                        ) {
                                            obtenerSubFamilias(
                                                context,
                                                listaFamiliaCompleta[position].SubFamilia,0
                                            )
                                        }

                                        override fun onNothingSelected(parent: AdapterView<*>) {
                                            // write code to perform some action
                                        }
                                    }
                                }
                            }
                        }
                        else
                        {
                            val errores = Errores()
                            errores.procesarError(context,body,activity)
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

    fun eliminarSubFamilia(context: Context){
        val url = urls.url+urls.endPointFamilias.endPointEliminarSubFamilia

        val jsonObject = JSONObject()
        try {
            jsonObject.put("token", globalVariable.usuario!!.token)
            jsonObject.put("subFamiliaId", listaFamiliaCompleta[invDetalleFamiliaSpinner.selectedItemPosition].SubFamilia[invDetalleSubFamiliaSpinner.selectedItemPosition].subFamiliaId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonObject.toString())


        val request = Request.Builder()
            .url(url)
            .delete(body)
            .build()

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.mensaje_espera))
        progressDialog.setCancelable(false)
        progressDialog.show()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                runOnUiThread()
                {
                    Toast.makeText(context, context.getString(R.string.mensaje_error_intentear_mas_tarde), Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response)
            {
                progressDialog.dismiss()

                val body = response.body()?.string()
                val gson = GsonBuilder().create()
                val respuesta = gson.fromJson(body,RespuestaObjeto::class.java)

                if(body != null && body.isNotEmpty())
                {
                    try
                    {
                        if(respuesta.respuesta == 0)
                        {
                            listaSubFamilia.removeAt(invDetalleSubFamiliaSpinner.selectedItemPosition)
                            listaFamiliaCompleta[invDetalleFamiliaSpinner.selectedItemPosition].SubFamilia.removeAt(invDetalleSubFamiliaSpinner.selectedItemPosition)

                            runOnUiThread {
                                val adapter = ArrayAdapter(context,
                                    android.R.layout.simple_spinner_item, listaSubFamilia)
                                invDetalleSubFamiliaSpinner.adapter = adapter

                                if(listaSubFamilia.size > 0) {
                                    invDetalleSubFamiliaSpinner.setSelection(0)
                                }
                            }
                        }
                        else
                        {
                            val errores = Errores()
                            errores.procesarError(context,body,activity)
                        }

                        invDetalleSubFamiliaSpinner.onItemSelectedListener = object :
                            AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>,
                                                        view: View, position: Int, id: Long) {
                                if(listaSubFamilia.size > 0) {
                                    subFamiliaId = listaFamiliaCompleta[invDetalleFamiliaSpinner.selectedItemPosition].SubFamilia[position].subFamiliaId
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>) {
                                // write code to perform some action
                            }
                        }

                    }
                    catch(e:Exception)
                    {
                        val errores = Errores()
                        errores.procesarError(context,body,activity)
                    }
                }
            }
        })
    }

    fun darDeAltaFoto(){
        val drawable = invDetalleFoto.drawable

        val bitmap: Bitmap = (drawable as BitmapDrawable).bitmap
        val file = bitmapToFile(bitmap)

        val MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg")
        val req: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "token",
                globalVariable.usuario!!.token)
            .addFormDataPart(
                "image",
                invDetalleId.text.toString()+".jpeg",
                RequestBody.create(MEDIA_TYPE_JPEG, file)
            ).build()
        val request = Request.Builder()
            .url(urls.url+urls.endPointImagenes.endpointSubirImagen)
            .post(req)
            .build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                finish()
            }

            override fun onResponse(call: Call, response: Response) {
                finish()
            }
        })
    }

    private fun bitmapToFile(bitmap:Bitmap): File {
        val wrapper = ContextWrapper(applicationContext)

        var file = wrapper.getDir("Images",Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpeg")

        try{
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,stream)
            stream.flush()
            stream.close()
        }catch (e:IOException){
            e.printStackTrace()
        }

        return file
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    openCamera()
                }
                else{
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null)
        {
            runOnUiThread { invDetalleId.setText(result.contents) }
        }
        else
        {
            if (resultCode == Activity.RESULT_OK)
            {
                try
                {
                    val inputStream: InputStream? =
                        if(requestCode == IMAGE_PICK_CODE)
                        {
                            data?.data?.let { contentResolver.openInputStream(it) }
                        }
                        else
                        {
                            image_uri?.let { contentResolver.openInputStream(it) }
                        }

                    cambioFoto = true
                    val bitmap: Bitmap = (Drawable.createFromStream(inputStream, image_uri.toString()) as BitmapDrawable).bitmap

                    val bitmapCuadrado =
                        if(bitmap.height > bitmap.width)
                        {
                            Bitmap.createBitmap(bitmap,0,((bitmap.height - bitmap.width)/2),bitmap.width,bitmap.width)
                        }
                        else
                        {
                            Bitmap.createBitmap(bitmap,((bitmap.width - bitmap.height)/2),0,bitmap.height,bitmap.height)
                        }

                    val resized = Bitmap.createScaledBitmap(bitmapCuadrado, 150, 150, true)

                    invDetalleFoto.setImageBitmap(resized)

                } catch (e: FileNotFoundException) { }
                //runOnUiThread { image_uri?.let { eliminarFoto(it) } }
            }
        }

    }

    /*fun getFilePath(uri : Uri): String{
        val projection =
            arrayOf(MediaStore.Images.Media.DATA)

        val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val columnIndex: Int = cursor.getColumnIndex(projection[0])
            val picturePath: String = cursor.getString(columnIndex) // returns null
            cursor.close()
            return picturePath
        }
        return ""
    }

    fun eliminarFoto(uri : Uri){
        val fdelete = File(getFilePath(uri))

        if (fdelete.exists()) {
            if (fdelete.delete()) { return } else { return }
        }
    }*/

    fun ImageView.loadUrl(url: String) {
        try {Picasso.with(context).load(url).into(this)}
        catch(e:Exception){}
    }

    fun datosVacios() : Boolean {

        var datoVacio = true

        if(invDetalleId.text.toString() == "")
        {
            mensajeLlenarDatos(1)
            return datoVacio
        }
        if(invDetalleNombre.text.toString() == "")
        {
            mensajeLlenarDatos(2)
            return datoVacio
        }
        //if(invDetalleNombreDetalle.text.toString() == ""){return moverCampo(invDetalleNombreDetalle)}
        if(invDetallePrecio.text.toString() == "")
        {
            mensajeLlenarDatos(4)
            return datoVacio
        }
        if(invDetalleCosto.text.toString() == "")
        {
            mensajeLlenarDatos(5)
            return datoVacio
        }
        if(invDetalleCantidad.text.toString() == "")
        {
            mensajeLlenarDatos(6)
            return datoVacio
        }
        if(listaFamiliaCompleta.size == 0)
        {
            mensajeLlenarDatos(7)
            return datoVacio
        }
        if(subFamiliaId == -1 || listaFamiliaCompleta.size == 0 || listaFamiliaCompleta[invDetalleFamiliaSpinner.selectedItemPosition].SubFamilia.size == 0)
        {
            mensajeLlenarDatos(8)
            return datoVacio
        }

        if(invDetalleInventarioOptimo.text.toString() == "")
        {
            mensajeLlenarDatos(9)
            return datoVacio
        }

        datoVacio = false
        return datoVacio
    }

    fun mensajeLlenarDatos(idMensaje: Int)
    {
        var mensaje = ""

        when(idMensaje)
        {
            1 -> mensaje = getString(R.string.mensaje_inventario_detalle_codigo)
            2 -> mensaje = getString(R.string.mensaje_inventario_detalle_nombre)
            4 -> mensaje = getString(R.string.mensaje_inventario_detalle_precio)
            5 -> mensaje = getString(R.string.mensaje_inventario_detalle_costo)
            6 -> mensaje = getString(R.string.mensaje_inventario_detalle_cantidad)
            7 -> mensaje = getString(R.string.mensaje_inventario_detalle_grupo)
            8 -> mensaje = getString(R.string.mensaje_inventario_detalle_subgrupo)
            9 -> mensaje = getString(R.string.mensaje_inventario_detalle_optimo)
        }

        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
    }

    /*fun moverCampo(editText : EditText) : Boolean{
        val animation1 = TranslateAnimation(0.0f,
            editText.width.toFloat()/15,
            0.0f,
            0.0f)

        animation1.duration = 150 // animation duration
        animation1.repeatCount = 2
        animation1.repeatMode = 2
        editText.startAnimation(animation1)

        return true
    }*/

    /*fun moverCampoSpinner(spinner : Spinner) : Boolean{
        val animation1 = TranslateAnimation(0.0f,
            spinner.width.toFloat()/15,
            0.0f,
            0.0f)

        animation1.duration = 150 // animation duration
        animation1.repeatCount = 2
        animation1.repeatMode = 2
        spinner.startAnimation(animation1)

        return true
    }*/

    fun darDeAltaArticulo(){

        val url = urls.url+urls.endPointsInventario.endpointDarAltaArticulo

        val jsonObject = JSONObject()
        try {
            jsonObject.put("token", globalVariable.usuario!!.token)
            jsonObject.put("idArticulo", parseLong(invDetalleId.text.toString()))
            jsonObject.put("nombre", invDetalleNombre.text)
            jsonObject.put("costo", parseDouble(invDetalleCosto.text.toString()))
            jsonObject.put("inventarioOptimo", parseInt(invDetalleInventarioOptimo.text.toString()))
            jsonObject.put("cantidad", parseInt(invDetalleCantidad.text.toString()))
            jsonObject.put("precio", parseDouble(invDetallePrecio.text.toString()))
            jsonObject.put("familia", subFamiliaId)
            jsonObject.put("descripcion", invDetalleNombreDetalle.text)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val client = OkHttpClient()
        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonObject.toString())

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.mensaje_espera))
        progressDialog.setCancelable(false)
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                runOnUiThread()
                {
                    Toast.makeText(context, context.getString(R.string.mensaje_error_intentear_mas_tarde), Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {

                val body = response.body()?.string()

                if(body != null && body.isNotEmpty())
                {
                    try
                    {
                        val gson = GsonBuilder().create()
                        val respuesta = gson.fromJson(body,Respuesta::class.java)

                        if(respuesta.status == 0)
                        {
                            globalVariable.actualizarVentana!!.actualizarInventario = true
                            progressDialog.dismiss()
                            if(cambioFoto) darDeAltaFoto()
                            else finish()
                        }
                        else
                        {
                            val errores = Errores()
                            errores.procesarError(context,body,activity)
                        }
                    }
                    catch(e:Exception){}
                }

                progressDialog.dismiss()
            }
        })
    }

    fun actualizarArticulo(){
        val url = urls.url+urls.endPointsInventario.endPointActualizarArticulo

        val jsonObject = JSONObject()
        try {
            jsonObject.put("token", globalVariable.usuario!!.token)
            jsonObject.put("idArticulo", parseLong(invDetalleId.text.toString()))
            jsonObject.put("nombre", invDetalleNombre.text)
            jsonObject.put("costo", parseDouble(invDetalleCosto.text.toString()))
            jsonObject.put("inventarioOptimo", parseInt(invDetalleInventarioOptimo.text.toString()))
            jsonObject.put("cantidad", parseInt(invDetalleCantidad.text.toString()))
            jsonObject.put("precio", parseDouble(invDetallePrecio.text.toString()))
            jsonObject.put("familia", subFamiliaId)
            jsonObject.put("descripcion", invDetalleNombreDetalle.text)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val client = OkHttpClient()
        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonObject.toString())

        val request = Request.Builder()
            .url(url)
            .put(body)
            .build()

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.mensaje_espera))
        progressDialog.setCancelable(false)
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                runOnUiThread()
                {
                    Toast.makeText(context, context.getString(R.string.mensaje_error_intentear_mas_tarde), Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {

                val body = response.body()?.string()

                if(body != null && body.isNotEmpty())
                {
                    try
                    {
                        val gson = GsonBuilder().create()
                        val respuesta = gson.fromJson(body,Respuesta::class.java)

                        if(respuesta.status == 0)
                        {
                            globalVariable.actualizarVentana!!.actualizarInventario = true
                            progressDialog.dismiss()
                            if(cambioFoto) darDeAltaFoto()
                            else finish()
                        }
                        else
                        {
                            val errores = Errores()
                            errores.procesarError(context,body,activity)
                        }
                    }
                    catch(e:Exception){}
                }

                progressDialog.dismiss()
            }
        })
    }

    fun eliminarArticulo(){
        val url = urls.url+urls.endPointsInventario.endPointEliminarArticulo

        val jsonObject = JSONObject()
        try {
            jsonObject.put("token", globalVariable.usuario!!.token)
            jsonObject.put("idArticulo", parseLong(invDetalleId.text.toString()))
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val client = OkHttpClient()
        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, jsonObject.toString())

        val request = Request.Builder()
            .url(url)
            .delete(body)
            .build()

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.mensaje_espera))
        progressDialog.setCancelable(false)
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                runOnUiThread()
                {
                    Toast.makeText(context, context.getString(R.string.mensaje_error_intentear_mas_tarde), Toast.LENGTH_LONG).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {

                val body = response.body()?.string()

                if(body != null && body.isNotEmpty())
                {
                    try
                    {

                        val gson = GsonBuilder().create()
                        val respuesta = gson.fromJson(body,Respuesta::class.java)

                        if(respuesta.status == 0)
                        {
                            finish()
                        }
                        else
                        {
                            val errores = Errores()
                            errores.procesarError(context,body,activity)
                        }
                    }
                    catch(e:Exception){}
                }

                progressDialog.dismiss()
            }
        })
    }

    fun obtenerFamilias(context: Context, posicion : Int){

        val url = urls.url+urls.endPointFamilias.endPointConsultarFamiliasSubFamilias+"?token="+globalVariable.usuario!!.token

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.mensaje_espera))
        progressDialog.setCancelable(false)
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread()
                {
                    progressDialog.dismiss()
                    Toast.makeText(context, context.getString(R.string.mensaje_error_intentear_mas_tarde), Toast.LENGTH_LONG).show()
                    finish()
                }
            }
            override fun onResponse(call: Call, response: Response)
            {
                val body = response.body()?.string()

                if(body != null && body.isNotEmpty())
                {
                    try
                    {
                        val gson = GsonBuilder().create()
                        listaFamiliaCompleta = gson.fromJson(body,Array<FamiliasSubFamiliasObject>::class.java).toMutableList()

                        for (i in 0 until listaFamiliaCompleta.size){
                            listaFamilia.add(listaFamiliaCompleta[i].nombreFamilia)

                            if(posicionPendiente){
                                for(y in 0 until listaFamiliaCompleta[i].SubFamilia.size){
                                    if(listaFamiliaCompleta[i].SubFamilia[y].subFamiliaId == posicion){
                                        posicionFamilia = i
                                        posicionSubFamilia = y
                                    }
                                }
                            }
                        }

                        runOnUiThread {
                            val adapter = ArrayAdapter(context,
                                android.R.layout.simple_spinner_item, listaFamilia)
                            invDetalleFamiliaSpinner.adapter = adapter

                            if(listaFamiliaCompleta.size > 0)
                                invDetalleFamiliaSpinner.setSelection(posicionFamilia)

                            invDetalleFamiliaSpinner.onItemSelectedListener = object :
                                AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>,
                                                            view: View, position: Int, id: Long) {
                                    obtenerSubFamilias(context,listaFamiliaCompleta[position].SubFamilia, posicionSubFamilia)
                                }

                                override fun onNothingSelected(parent: AdapterView<*>) {}
                            }
                        }
                    }
                    catch(e:Exception)
                    {
                        val errores = Errores()
                        errores.procesarErrorCerrarVentana(context,body,activity)
                    }
                }

                progressDialog.dismiss()

            }
        })

    }

    fun obtenerSubFamilias(context: Context, listaSubFamiliaCompleta: List<SubFamiliaObjeto>, posicion: Int){

        listaSubFamilia.clear()

        runOnUiThread {
            for (familias in listaSubFamiliaCompleta) {
                listaSubFamilia.add(familias.nombreSubFamilia)
            }

            val adapter = ArrayAdapter(context,
                android.R.layout.simple_spinner_item, listaSubFamilia)
            invDetalleSubFamiliaSpinner.adapter = adapter
            if(listaSubFamilia.size > 0)
                invDetalleSubFamiliaSpinner.setSelection(posicion)

            invDetalleSubFamiliaSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    if(listaSubFamilia.size > 0) {
                        subFamiliaId = listaSubFamiliaCompleta[position].subFamiliaId
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }

            posicionPendiente = false
        }
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun abrirGaleria()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED)
            {
                //permission denied
                val permissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            }
            else{
                //permission already granted
                pickImageFromGallery()
            }
        }
        else
        {
            //system OS is < Marshmallow
            pickImageFromGallery()
        }
    }

    override fun abrirCamara() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED
            )
            {
                //permission was not enabled
                val permission = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                //show popup to request permission
                requestPermissions(permission, PERMISSION_CODE)
            }
            else
            {
                //permission already granted
                openCamera()
            }
        }
        else
        {
            //system os is < marshmallow
            openCamera()
        }
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
    }
}
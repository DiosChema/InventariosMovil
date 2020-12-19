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
import android.database.Cursor
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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
import java.lang.Integer.parseInt
import java.lang.Long.parseLong
import java.util.*
import kotlin.collections.ArrayList


class InventarioDetalle : AppCompatActivity() {

    lateinit var invDetalleId : EditText
    lateinit var invDetalleFoto: ImageView
    lateinit var invDetalleNombre : EditText
    lateinit var invDetalleNombreDetalle : EditText
    lateinit var invDetalleCantidad : EditText
    lateinit var invDetallePrecio : EditText
    lateinit var invDetalleCosto : EditText
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
    var posicionFamilia = 0
    var posicionSubFamilia = 0

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_inventario_detalle)

        globalVariable = applicationContext as GlobalClass

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

    }

    fun asignarRecursos() {
        invDetalleId = findViewById(R.id.invDetalleId)
        invDetalleFoto = findViewById(R.id.invDetalleFoto)
        invDetalleNombre = findViewById(R.id.invDetalleNombre)
        invDetalleNombreDetalle = findViewById(R.id.invDetalleNombreDetalle)
        invDetalleCantidad = findViewById(R.id.invDetalleCantidad)
        invDetallePrecio = findViewById(R.id.invDetallePrecio)
        invDetalleCosto = findViewById(R.id.invDetalleCosto)
        invDetalleFamiliaSpinner = findViewById(R.id.invDetalleFamiliaSpinner)
        invDetalleSubFamiliaSpinner = findViewById(R.id.invDetalleSubFamiliaSpinner)
        invDetalleDarDeAlta = findViewById(R.id.invDetalleDarDeAlta)
        invBotonEliminarFamilia = findViewById(R.id.invBotonEliminarFamilia)
        invBotonAgregarFamilia = findViewById(R.id.invBotonAgregarFamilia)
        invBotonEliminarSubFamilia = findViewById(R.id.invBotonEliminarSubFamilia)
        invBotonAgregarSubFamilia = findViewById(R.id.invBotonAgregarSubFamilia)
        invBottonTomarCodigo = findViewById(R.id.invBottonTomarCodigo)
        invDetalleCancelarEdicion = findViewById(R.id.invDetalleCancelarEdicion)
        invDetalleEliminarArticulo = findViewById(R.id.invDetalleEliminarArticulo)

        invDetalleCancelarEdicion.visibility = View.INVISIBLE
        invDetalleEliminarArticulo.visibility = View.INVISIBLE
    }

    fun habilitarEdicion(activar : Boolean) {
        invDetalleId.isEnabled = false
        invDetalleFoto.isEnabled = activar
        invDetalleNombre.isEnabled = activar
        invDetalleNombreDetalle.isEnabled = activar
        invDetalleCantidad.isEnabled = activar
        invDetallePrecio.isEnabled = activar
        invDetalleCosto.isEnabled = activar
        invDetalleFamiliaSpinner.isEnabled = activar
        invDetalleSubFamiliaSpinner.isEnabled = activar
        invDetalleDarDeAlta.isEnabled = true
        invBotonEliminarFamilia.isEnabled = activar
        invBotonAgregarFamilia.isEnabled = activar
        invBotonEliminarSubFamilia.isEnabled = activar
        invBotonAgregarSubFamilia.isEnabled = activar
        invBottonTomarCodigo.isEnabled = false
        invDetalleCancelarEdicion.visibility = View.INVISIBLE
        invDetalleEliminarArticulo.visibility = View.INVISIBLE

        if (activar) {

            invDetalleCancelarEdicion.visibility = View.VISIBLE
            invDetalleEliminarArticulo.visibility = View.VISIBLE

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
        invDetalleNombre.setText(articulo.nombreArticulo)
        invDetalleNombreDetalle.setText(articulo.descripcionArticulo)
        invDetalleCantidad.setText(articulo.cantidadArticulo.toString())
        invDetallePrecio.setText(articulo.precioArticulo.toString())
        invDetalleCosto.setText(articulo.costoArticulo.toString())

        listaFamiliaCompleta.clear()
        listaFamilia.clear()
        cambioFoto = false
        posicionPendiente = true
        obtenerFamilias(this,parseInt(articulo.familiaArticulo))
    }

    fun reLlenarCampos(articulo : InventarioObjeto){
        invDetalleId.setText(articulo.idArticulo.toString())
        invDetalleFoto.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+articulo.idArticulo+".jpeg"+"&token="+globalVariable.usuario!!.token)
        invDetalleNombre.setText(articulo.nombreArticulo)
        invDetalleNombreDetalle.setText(articulo.descripcionArticulo)
        invDetalleCantidad.setText(articulo.cantidadArticulo.toString())
        invDetallePrecio.setText(articulo.precioArticulo.toString())
        invDetalleCosto.setText(articulo.costoArticulo.toString())

        cambioFoto = false
        posicionPendiente = true

        invDetalleFamiliaSpinner.setSelection(posicionFamilia)
        invDetalleSubFamiliaSpinner.setSelection(posicionSubFamilia)
    }



    fun asignarFuncionBotones(esEditar : Boolean){

        invDetalleFoto.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED
                ) {
                    //permission was not enabled
                    val permission = arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    //show popup to request permission
                    requestPermissions(permission, PERMISSION_CODE)
                } else {
                    //permission already granted
                    openCamera()
                }
            } else {
                //system os is < marshmallow
                openCamera()
            }
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
            if (listaFamiliaCompleta[invDetalleFamiliaSpinner.selectedItemPosition].SubFamilia.size > 0)
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
        val dialogText = dialog.findViewById(R.id.dialogText) as EditText
        val dialogAceptar = dialog.findViewById(R.id.dialogAceptar) as Button
        val dialogCancelar = dialog.findViewById(R.id.dialogCancelar) as Button
        val dialogTitulo = dialog.findViewById(R.id.dialogTitulo) as TextView

        dialogTitulo.text = getString(R.string.dialog_agregar_familia)

        dialogAceptar.setOnClickListener {
            dialog.dismiss()
            agregarFamilia(this,dialogText.text.toString())
        }

        dialogCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun showDialogAgregarSubFamilia() {
        val dialog = Dialog(this)
        dialog.setTitle(title)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_text)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogText = dialog.findViewById(R.id.dialogText) as EditText
        val dialogAceptar = dialog.findViewById(R.id.dialogAceptar) as Button
        val dialogCancelar = dialog.findViewById(R.id.dialogCancelar) as Button
        val dialogTitulo = dialog.findViewById(R.id.dialogTitulo) as TextView

        dialogTitulo.text = getString(R.string.dialog_agregar_subfamilia)

        dialogAceptar.setOnClickListener {
            dialog.dismiss()
            agregarSubFamilia(this,dialogText.text.toString())
        }

        dialogCancelar.setOnClickListener {dialog.dismiss()}

        dialog.show()

    }

    private fun showDialogEliminarArticulo() {
        val dialog = Dialog(this)
        dialog.setTitle(title)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_text)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogText = dialog.findViewById(R.id.dialogText) as EditText
        val dialogAceptar = dialog.findViewById(R.id.dialogAceptar) as Button
        val dialogCancelar = dialog.findViewById(R.id.dialogCancelar) as Button
        val dialogTitulo = dialog.findViewById(R.id.dialogTitulo) as TextView

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
        progressDialog.show()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response)
            {
                progressDialog.dismiss()

                val json = response.body()?.string()
                val gson = GsonBuilder().create()
                val familia = gson.fromJson(json,FamiliaObjeto::class.java)

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
        progressDialog.show()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response)
            {
                progressDialog.dismiss()

                val json = response.body()?.string()
                val gson = GsonBuilder().create()
                val subFamilia = gson.fromJson(json,SubFamiliaObjeto::class.java)

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
        progressDialog.show()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response)
            {
                progressDialog.dismiss()

                val json = response.body()?.string()
                val gson = GsonBuilder().create()
                val respuesta = gson.fromJson(json,RespuestaObjeto::class.java)

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
                                        //listaFamiliaCompleta[position].familiaId
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
                    runOnUiThread {
                        Toast.makeText(context, respuesta.mensaje, Toast.LENGTH_SHORT).show()
                    }
                }

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
        progressDialog.show()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response)
            {
                progressDialog.dismiss()

                val json = response.body()?.string()
                val gson = GsonBuilder().create()
                val respuesta = gson.fromJson(json,RespuestaObjeto::class.java)

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
                    }

                }
                else
                {
                    runOnUiThread {
                        Toast.makeText(context, respuesta.mensaje, Toast.LENGTH_SHORT).show()
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
        if (result != null) {
            runOnUiThread { invDetalleId.setText(result.contents) }

        } else {
            if (resultCode == Activity.RESULT_OK){
                cambioFoto = true

                try {
                    val inputStream: InputStream? = image_uri?.let { contentResolver.openInputStream(it) }
                    val bitmap: Bitmap = (Drawable.createFromStream(inputStream, image_uri.toString()) as BitmapDrawable).bitmap
                    val bitmapCuadrado = Bitmap.createBitmap(bitmap,0,((bitmap.height - bitmap.width)/2),bitmap.width,bitmap.width)
                    val resized = Bitmap.createScaledBitmap(bitmapCuadrado, 250, 250, true)
                    invDetalleFoto.setImageBitmap(resized)

                } catch (e: FileNotFoundException) { }
                runOnUiThread { image_uri?.let { eliminarFoto(it) } }
            }
        }
    }

    fun getFilePath(uri : Uri): String{
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
    }

    fun ImageView.loadUrl(url: String) {
        Picasso.with(context).load(url).into(this)
    }

    fun datosVacios() : Boolean {

        if(invDetalleId.text.toString() == ""){return moverCampo(invDetalleId)}
        if(invDetalleNombre.text.toString() == ""){return moverCampo(invDetalleNombre)}
        if(invDetalleNombreDetalle.text.toString() == ""){return moverCampo(invDetalleNombreDetalle)}
        if(invDetallePrecio.text.toString() == ""){return moverCampo(invDetallePrecio)}
        if(invDetalleCosto.text.toString() == ""){return moverCampo(invDetalleCosto)}
        if(invDetalleCantidad.text.toString() == ""){return moverCampo(invDetalleCantidad)}
        if(listaFamiliaCompleta.size == 0){return moverCampoSpinner(invDetalleFamiliaSpinner)}
        if(subFamiliaId == -1 || listaFamiliaCompleta[invDetalleFamiliaSpinner.selectedItemPosition].SubFamilia.size == 0){return moverCampoSpinner(invDetalleSubFamiliaSpinner)}

        /*if( invDetalleId.text.toString() != "" &&
            invDetalleNombre.text.toString() != "" &&
            invDetalleCosto.text.toString() != "" &&
            invDetalleCantidad.text.toString() != "" &&
            invDetallePrecio.text.toString() != "" &&
            subFamiliaId != -1 &&
            invDetalleNombreDetalle.text.toString() != "" &&
            listaFamiliaCompleta.size > 0 &&
            listaFamiliaCompleta[invDetalleFamiliaSpinner.selectedItemPosition].SubFamilia.size > 0) {
            respuesta = false
        }*/

        return false
    }

    fun moverCampo(editText : EditText) : Boolean{
        val animation1 = TranslateAnimation(0.0f,
            editText.width.toFloat()/15,
            0.0f,
            0.0f)

        animation1.duration = 150 // animation duration
        animation1.repeatCount = 2
        animation1.repeatMode = 2
        editText.startAnimation(animation1)

        return true
    }

    fun moverCampoSpinner(spinner : Spinner) : Boolean{
        val animation1 = TranslateAnimation(0.0f,
            spinner.width.toFloat()/15,
            0.0f,
            0.0f)

        animation1.duration = 150 // animation duration
        animation1.repeatCount = 2
        animation1.repeatMode = 2
        spinner.startAnimation(animation1)

        return true
    }

    fun darDeAltaArticulo(){

        val url = urls.url+urls.endPointsInventario.endpointDarAltaArticulo

        val jsonObject = JSONObject()
        try {
            jsonObject.put("token", globalVariable.usuario!!.token)
            jsonObject.put("idArticulo", parseLong(invDetalleId.text.toString()))
            jsonObject.put("nombreArticulo", invDetalleNombre.text)
            jsonObject.put("costoArticulo", parseDouble(invDetalleCosto.text.toString()))
            jsonObject.put("cantidadArticulo", parseInt(invDetalleCantidad.text.toString()))
            jsonObject.put("precioArticulo", parseDouble(invDetallePrecio.text.toString()))
            jsonObject.put("familiaArticulo", subFamiliaId)
            jsonObject.put("descripcionArticulo", invDetalleNombreDetalle.text)
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
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response) {
                progressDialog.dismiss()
                if(cambioFoto) darDeAltaFoto()
                else finish()
            }
        })
    }

    fun actualizarArticulo(){
        val url = urls.url+urls.endPointsInventario.endPointActualizarArticulo

        val jsonObject = JSONObject()
        try {
            jsonObject.put("token", globalVariable.usuario!!.token)
            jsonObject.put("idArticulo", invDetalleId.text)
            jsonObject.put("nombreArticulo", invDetalleNombre.text)
            jsonObject.put("costoArticulo", invDetalleCosto.text)
            jsonObject.put("cantidadArticulo", invDetalleCantidad.text)
            jsonObject.put("precioArticulo", invDetallePrecio.text)
            jsonObject.put("familiaArticulo", subFamiliaId)
            jsonObject.put("descripcionArticulo", invDetalleNombreDetalle.text)
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
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response) {
                progressDialog.dismiss()
                if(cambioFoto) darDeAltaFoto()
                else finish()
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
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response) {
                progressDialog.dismiss()
                finish()
            }
        })
    }

    /*fun obtenerFamilias(context: Context){

        val url = urls.url+urls.endpointObtenerFamilias+"?token="+globalVariable.token

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }
            override fun onResponse(call: Call, response: Response)
            {
                val body = response.body()?.string()
                val gson = GsonBuilder().create()
                listaFamiliaCompleta = gson.fromJson(body,Array<FamiliaObjeto>::class.java).toMutableList()

                for (familias in listaFamiliaCompleta) {
                    listaFamilia.add(familias.nombreFamilia)
                }

                runOnUiThread {
                    val adapter = ArrayAdapter(context,
                        android.R.layout.simple_spinner_item, listaFamilia)
                    invDetalleFamiliaSpinner.adapter = adapter
                    invDetalleFamiliaSpinner.setSelection(0)

                    invDetalleFamiliaSpinner.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>,
                                                    view: View, position: Int, id: Long) {
                            obtenerSubFamilias(context,listaFamiliaCompleta[position].familiaId)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // write code to perform some action
                        }
                    }
                }
            }
        })

    }*/


    fun obtenerFamilias(context: Context, posicion : Int){

        val url = urls.url+urls.endPointFamilias.endPointConsultarFamiliasSubFamilias+"?token="+globalVariable.usuario!!.token

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }
            override fun onResponse(call: Call, response: Response)
            {
                val body = response.body()?.string()
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
/*            adapter = ArrayAdapter(context,
                android.R.layout.simple_spinner_item, listaSubFamilia)
            invDetalleSubFamiliaSpinner.adapter = adapter*/

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
}
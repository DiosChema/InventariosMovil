package com.example.perraco.Activities

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
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.perraco.Objets.*
import com.example.perraco.R
import com.google.gson.GsonBuilder
import com.google.zxing.integration.android.IntentIntegrator
import com.squareup.picasso.Picasso
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.Integer.parseInt
import java.util.*
import kotlin.collections.ArrayList


class InventarioDetalle : AppCompatActivity() {

    lateinit var invDetalleId : EditText
    lateinit var invDetalleFoto: ImageButton
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

    private val urls: Urls = Urls()
    var subFamiliaPendiente = false
    var subFamiliaPendienteIndex = 0

    var listaFamilia:MutableList<String> = ArrayList()
    var listaFamiliaCompleta:MutableList<FamiliaObjeto> = ArrayList()
    var listaSubFamilia:MutableList<String> = ArrayList()
    var listaSubFamiliaCompleta:MutableList<SubFamiliaObjeto> = ArrayList()

    var image_uri: Uri? = null
    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001

    var subFamiliaId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_inventario_detalle)

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
            val inventarioObjeto = intent.getSerializableExtra("articulo") as InventarioObjeto

            invDetalleId.isEnabled = false
            invDetalleFoto.isEnabled = false
            invDetalleNombre.isEnabled = false
            invDetalleNombreDetalle.isEnabled = false
            invDetalleCantidad.isEnabled = false
            invDetallePrecio.isEnabled = false
            invDetalleCosto.isEnabled = false
            invDetalleFamiliaSpinner.isEnabled = false
            invDetalleSubFamiliaSpinner.isEnabled = false
            invDetalleDarDeAlta.isEnabled = false
            invBotonEliminarFamilia.isEnabled = false
            invBotonAgregarFamilia.isEnabled = false
            invBotonEliminarSubFamilia.isEnabled = false
            invBotonAgregarSubFamilia.isEnabled = false
            invBottonTomarCodigo.isEnabled = false

            invDetalleId.setText(inventarioObjeto.idArticulo)
            invDetalleFoto.loadUrl(urls.url+urls.endPointImagenes+inventarioObjeto.idArticulo+".png")
            invDetalleNombre.setText(inventarioObjeto.nombreArticulo)
            invDetalleNombreDetalle.setText(inventarioObjeto.descripcionArticulo)
            invDetalleCantidad.setText(inventarioObjeto.cantidadArticulo.toString())
            invDetallePrecio.setText(inventarioObjeto.precioArticulo)
            invDetalleCosto.setText(inventarioObjeto.costoArticulo)

            obtenerFamilia(this,parseInt(inventarioObjeto.familiaArticulo))
        }
        else
        {
            obtenerFamilias(this)

            invDetalleFoto.setOnClickListener()
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED){
                        //permission was not enabled
                        val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        //show popup to request permission
                        requestPermissions(permission, PERMISSION_CODE)
                    }
                    else{
                        //permission already granted
                        openCamera()
                    }
                }
                else{
                    //system os is < marshmallow
                    openCamera()
                }
            }


            invBottonTomarCodigo.setOnClickListener()
            {
                val intentIntegrator = IntentIntegrator(this)
                intentIntegrator.setBeepEnabled(false)
                intentIntegrator.setCameraId(0)
                intentIntegrator.setPrompt("SCAN")
                intentIntegrator.setBarcodeImageEnabled(false)
                intentIntegrator.initiateScan()
            }

            invBotonAgregarFamilia.setOnClickListener()
            {
                showDialogAgregarFamilia("Añadir Familia")
            }

            invBotonAgregarSubFamilia.setOnClickListener()
            {
                showDialogAgregarSubFamilia("Añadir SubFamilia")
            }

            invBotonEliminarFamilia.setOnClickListener()
            {
                if(listaFamiliaCompleta.size > 0)
                    eliminarFamilia(this)
            }

            invBotonEliminarSubFamilia.setOnClickListener()
            {
                if(listaSubFamiliaCompleta.size > 0)
                    eliminarSubFamilia(this)
            }

        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        invDetalleDarDeAlta.setOnClickListener()
        {
            if(!datosVacios()) {
                darDeAltaArticulo()
            }
        }

    }

    private fun showDialogAgregarFamilia(title: String) {
        val dialog = Dialog(this)
        dialog.setTitle(title)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_text)
        val dialogText = dialog.findViewById(R.id.dialogText) as EditText
        val dialogAceptar = dialog.findViewById(R.id.dialogAceptar) as Button
        val dialogCancelar = dialog.findViewById(R.id.dialogCancelar) as Button
        val dialogTitulo = dialog.findViewById(R.id.dialogTitulo) as TextView

        dialogTitulo.text = title

        dialogAceptar.setOnClickListener {
            dialog.dismiss()
            agregarFamilia(this,dialogText.text.toString())
        }

        dialogCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun showDialogAgregarSubFamilia(title: String) {
        val dialog = Dialog(this)
        dialog.setTitle(title)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_text)
        val dialogText = dialog.findViewById(R.id.dialogText) as EditText
        val dialogAceptar = dialog.findViewById(R.id.dialogAceptar) as Button
        val dialogCancelar = dialog.findViewById(R.id.dialogCancelar) as Button
        val dialogTitulo = dialog.findViewById(R.id.dialogTitulo) as TextView

        dialogTitulo.text = title

        dialogAceptar.setOnClickListener {
            dialog.dismiss()
            agregarSubFamilia(this,dialogText.text.toString())
        }

        dialogCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    fun agregarFamilia(context: Context, nombreFamilia : String){

        val url = urls.url+urls.endPointAgregarFamilia

        val jsonObject = JSONObject()
        try {
            jsonObject.put("tienda", "00001")
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

                val body = response.body()?.string()
                val gson = GsonBuilder().create()
                val familia = gson.fromJson(body,FamiliaObjeto::class.java)

                listaFamilia.add(familia.nombreFamilia)
                listaFamiliaCompleta.add(familia)

                runOnUiThread {
                    val adapter = ArrayAdapter(context,
                        android.R.layout.simple_spinner_item, listaFamilia)
                    invDetalleFamiliaSpinner.adapter = adapter
                    invDetalleFamiliaSpinner.setSelection(listaFamilia.size-1)

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
    }

    fun agregarSubFamilia(context: Context, nombreFamilia : String){

        val url = urls.url+urls.endPointAgregarSubFamilia

        val jsonObject = JSONObject()
        try {
            jsonObject.put("tienda", "00001")
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

                val body = response.body()?.string()
                val gson = GsonBuilder().create()
                val subFamilia = gson.fromJson(body,SubFamiliaObjeto::class.java)

                listaSubFamilia.add(subFamilia.nombreSubFamilia)
                listaSubFamiliaCompleta.add(subFamilia)

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
                                subFamiliaId = listaSubFamiliaCompleta[position].subFamiliaId
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // write code to perform some action
                        }
                    }
                }
            }
        })

    }

    fun eliminarFamilia(context: Context){

        val url = urls.url+urls.endPointEliminarFamilia

        val jsonObject = JSONObject()
        try {
            jsonObject.put("tienda", "00001")
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

                val body = response.body()?.string()
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
                                        listaFamiliaCompleta[position].familiaId
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
        val url = urls.url+urls.endPointEliminarSubFamilia

        val jsonObject = JSONObject()
        try {
            jsonObject.put("tienda", "00001")
            jsonObject.put("subFamiliaId", listaSubFamiliaCompleta[invDetalleSubFamiliaSpinner.selectedItemPosition].subFamiliaId)
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

                val body = response.body()?.string()
                val gson = GsonBuilder().create()
                val respuesta = gson.fromJson(body,RespuestaObjeto::class.java)

                if(respuesta.respuesta == 0)
                {
                    listaSubFamilia.removeAt(invDetalleSubFamiliaSpinner.selectedItemPosition)
                    listaSubFamiliaCompleta.removeAt(invDetalleSubFamiliaSpinner.selectedItemPosition)

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
                                        subFamiliaId = listaSubFamiliaCompleta[position].subFamiliaId
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

        val bitmap: Bitmap = (drawable as BitmapDrawable).getBitmap()
        val resized = Bitmap.createScaledBitmap(bitmap, 200, 350, true)

        val file = bitmapToFile(resized)

        val MEDIA_TYPE_PNG = MediaType.parse("image/png")
        val req: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "image",
                invDetalleId.text.toString()+".png",
                RequestBody.create(MEDIA_TYPE_PNG, file)
            ).build()
        val request = Request.Builder()
            .url(urls.url+urls.endpointSubirImagen)
            .post(req)
            .build()
        val client = OkHttpClient()

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

    private fun bitmapToFile(bitmap:Bitmap): File {
        // Get the context wrapper
        val wrapper = ContextWrapper(applicationContext)

        // Initialize a new file instance to save bitmap object
        var file = wrapper.getDir("Images",Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.png")

        try{
            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG,100,stream)
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
        //called when user presses ALLOW or DENY from Permission Request Popup
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted
                    openCamera()
                }
                else{
                    //permission from popup was denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            runOnUiThread {
                invDetalleId.setText(result.contents)
            }

        } else {
            if (resultCode == Activity.RESULT_OK){
                invDetalleFoto.setImageURI(image_uri)
            }
        }
    }

    fun ImageView.loadUrl(url: String) {
        Picasso.with(context).load(url).into(this)
    }

    fun datosVacios() : Boolean
    {
        var respuesta = true
        if( invDetalleId.text.toString() != "" &&
            invDetalleNombre.text.toString() != "" &&
            invDetalleCosto.text.toString() != "" &&
            invDetalleCantidad.text.toString() != "" &&
            invDetallePrecio.text.toString() != "" &&
            subFamiliaId != -1 &&
            invDetalleNombreDetalle.text.toString() != "")
        {
            respuesta = false
        }

        return respuesta
    }

    fun darDeAltaArticulo(){

        val url = urls.url+urls.endpointDarAltaArticulo

        val jsonObject = JSONObject()
        try {
            jsonObject.put("tienda", "00001")
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
                darDeAltaFoto()
            }
        })

    }

    fun obtenerFamilias(context: Context){

        val url = urls.url+urls.endpointObtenerFamilias+"?tienda=00001"

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

    }

    fun obtenerSubFamilias(context: Context, familiaId: Int){
        var subFamiliaTmp = 0
        val url = urls.url+urls.endpointObtenerSubFamilias+"?tienda=00001"+"&familiaId="+familiaId

        if(subFamiliaPendiente) {
            subFamiliaTmp = subFamiliaPendienteIndex
            subFamiliaPendiente = false
        }

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
            }
            override fun onResponse(call: Call, response: Response)
            {
                listaSubFamilia.clear()
                val body = response.body()?.string()
                val gson = GsonBuilder().create()

                runOnUiThread {
                    var adapter = ArrayAdapter(context,
                        android.R.layout.simple_spinner_item, listaSubFamilia)
                    invDetalleSubFamiliaSpinner.adapter = adapter

                    listaSubFamiliaCompleta = gson.fromJson(body,Array<SubFamiliaObjeto>::class.java).toMutableList()

                    for (familias in listaSubFamiliaCompleta) {
                        listaSubFamilia.add(familias.nombreSubFamilia)
                    }

                    adapter = ArrayAdapter(context,
                        android.R.layout.simple_spinner_item, listaSubFamilia)

                    invDetalleSubFamiliaSpinner.adapter = adapter

                    if(listaSubFamilia.size > 0) {
                        invDetalleSubFamiliaSpinner.setSelection(subFamiliaTmp)
                    }

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

                    progressDialog.dismiss()
                }
            }
        })

    }

    fun obtenerFamilia(context: Context,familia: Int){

        val url = urls.url+urls.endpointObtenerSubFamilia+"?tienda=00001"+"&subFamiliaId="+familia

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
                val listaTmp = gson.fromJson(body,Array<FamiliaSubFamiliaObjeto>::class.java).toMutableList()
                var posicionFamilia = 0

                for (i in 0 until listaTmp.size)
                {
                    listaFamiliaCompleta.add(FamiliaObjeto(listaTmp[i].familiaId,listaTmp[i].nombreFamilia))

                    if(listaTmp[i].subfamilias != null)
                    {
                        posicionFamilia = i
                        listaSubFamiliaCompleta = listaTmp[i].subfamilias.toMutableList()
                    }
                }

                for (familias in listaFamiliaCompleta) {
                    listaFamilia.add(familias.nombreFamilia)
                }

                for (subFamilias in listaSubFamiliaCompleta) {
                    listaSubFamilia.add(subFamilias.nombreSubFamilia)
                }

                for (i in 0 until listaSubFamiliaCompleta.size)
                {
                    if(listaSubFamiliaCompleta[i].subFamiliaId == familia)
                        subFamiliaPendienteIndex = i
                }

                subFamiliaPendiente = true

                runOnUiThread {
                    val adapter = ArrayAdapter(
                        context,
                        android.R.layout.simple_spinner_item, listaFamilia
                    )
                    invDetalleFamiliaSpinner.adapter = adapter
                    invDetalleFamiliaSpinner.setSelection(posicionFamilia)



                    invDetalleFamiliaSpinner.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>,view: View, position: Int, id: Long) {
                            obtenerSubFamilias(context,listaFamiliaCompleta[position].familiaId)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // write code to perform some action
                        }
                    }

                }

            }
        })
    }


}
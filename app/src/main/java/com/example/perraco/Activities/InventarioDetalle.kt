package com.example.perraco.Activities

import android.Manifest
import android.app.Activity
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
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.perraco.Objets.FamiliaObjeto
import com.example.perraco.Objets.InventarioObjeto
import com.example.perraco.Objets.SubFamiliaObjeto
import com.example.perraco.Objets.Urls
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
    private val urls: Urls = Urls()

    var listaFamilia:MutableList<String> = ArrayList()
    var listaFamiliaCompleta:MutableList<FamiliaObjeto> = ArrayList()
    var listaSubFamilia:MutableList<String> = ArrayList()
    var listaSubFamiliaCompleta:MutableList<SubFamiliaObjeto> = ArrayList()

    var image_uri: Uri? = null
    private val PERMISSION_CODE = 1000;
    private val IMAGE_CAPTURE_CODE = 1001

    var subFamiliaId = -1;

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

            if(inventarioObjeto != null)
            {
                invDetalleId.isEnabled = false
                invDetalleFoto.isEnabled = false
                invDetalleNombre.isEnabled = false
                invDetalleNombreDetalle.isEnabled = false
                invDetalleCantidad.isEnabled = false
                invDetallePrecio.isEnabled = false
                invDetalleCosto.isEnabled = false
                invDetalleFamiliaSpinner.isEnabled = false;
                invDetalleSubFamiliaSpinner.isEnabled = false;
                invDetalleDarDeAlta.isEnabled = false;

                invDetalleId.setText(inventarioObjeto.idArticulo.toString())
                val urls = Urls()
                invDetalleFoto.loadUrl(urls.url+urls.endPointImagenes+inventarioObjeto.idArticulo+".png")
                invDetalleNombre.setText(inventarioObjeto.nombreArticulo)
                invDetalleNombreDetalle.setText(inventarioObjeto.descripcionArticulo)
                invDetalleCantidad.setText(inventarioObjeto.cantidadArticulo.toString())
                invDetallePrecio.setText(inventarioObjeto.precioArticulo.toString())
                invDetalleCosto.setText(inventarioObjeto.costoArticulo.toString())

                obtenerFamilia(this,parseInt(inventarioObjeto.familiaArticulo));
            }
        }
        else
        {
            obtenerFamilias(this, -1, -1);

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

            val button3 = findViewById<ImageButton>(R.id.invBottonTomarCodigo)
            button3?.setOnClickListener()
            {
                val intentIntegrator = IntentIntegrator(this)
                intentIntegrator.setBeepEnabled(false)
                intentIntegrator.setCameraId(0)
                intentIntegrator.setPrompt("SCAN")
                intentIntegrator.setBarcodeImageEnabled(false)
                intentIntegrator.initiateScan()
            }

        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        invDetalleDarDeAlta.setOnClickListener()
        {
            if(!datosVacios()) {
                darDeAltaArticulo()
                darDeAltaFoto()
            }
        }

    }



    fun darDeAltaFoto(/*file: File?*/){
        var drawable = invDetalleFoto.drawable

        val bitmap: Bitmap = (drawable as BitmapDrawable).getBitmap()
        val resized = Bitmap.createScaledBitmap(bitmap, 200, 350, true);

        var file = bitmapToFile(resized)

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
        progressDialog.setMessage("Application is loading, please wait")
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
            }

            override fun onResponse(call: Call, response: Response) {
                progressDialog.dismiss()
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




    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //called when image was captured from camera intent
        if (resultCode == Activity.RESULT_OK){
            invDetalleFoto.setImageURI(image_uri)
        }
    }*/

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
            respuesta = false;
        }

        return respuesta;
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
        progressDialog.setMessage("Application is loading, please wait")
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response) {
                progressDialog.dismiss()
            }
        })


    }

    fun obtenerFamilias(context: Context, familia: Int, subFamilia: Int){

        val url = urls.url+urls.endpointObtenerFamilias+"?tienda=00001"
        var familiaTmp = 0;
        var index = 0;

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
                var body = response.body()?.string()
                val gson = GsonBuilder().create()
                listaFamiliaCompleta = gson.fromJson(body,Array<FamiliaObjeto>::class.java).toMutableList()

                for (familias in listaFamiliaCompleta) {
                    listaFamilia.add(familias.nombreFamilia)

                    if(familia != -1 && familias.familiaId == familia) {
                        familiaTmp = index
                    }

                    index++
                }

                runOnUiThread {
                    val adapter = ArrayAdapter(context,
                        android.R.layout.simple_spinner_item, listaFamilia)
                    invDetalleFamiliaSpinner.adapter = adapter
                    invDetalleFamiliaSpinner.setSelection(familiaTmp);

                    invDetalleFamiliaSpinner.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>,
                                                    view: View, position: Int, id: Long) {
                            obtenerSubFamilias(context,listaFamiliaCompleta[position].familiaId,subFamilia)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // write code to perform some action
                        }
                    }
                }
            }
        })

    }

    fun obtenerSubFamilias(context: Context, familiaId: Int, subFamilia: Int){
        var subFamiliaTmp = 0;
        var index = 0;
        val url = urls.url+urls.endpointObtenerSubFamilias+"?tienda=00001"+"&familiaId="+familiaId

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
                listaSubFamilia.clear()
                var body = response.body()?.string()
                val gson = GsonBuilder().create()

                runOnUiThread {
                    var adapter = ArrayAdapter(context,
                        android.R.layout.simple_spinner_item, listaSubFamilia)
                    invDetalleSubFamiliaSpinner.adapter = adapter

                    listaSubFamiliaCompleta = gson.fromJson(body,Array<SubFamiliaObjeto>::class.java).toMutableList()

                    for (familias in listaSubFamiliaCompleta) {
                        listaSubFamilia.add(familias.nombreSubFamilia)

                        if(subFamilia != -1 && familias.subFamiliaId == subFamilia) {
                            subFamiliaTmp = index
                        }

                        index++
                    }

                    adapter = ArrayAdapter(context,
                        android.R.layout.simple_spinner_item, listaSubFamilia)

                    invDetalleSubFamiliaSpinner.adapter = adapter
                    invDetalleSubFamiliaSpinner.setSelection(subFamiliaTmp);

                    invDetalleSubFamiliaSpinner.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>,
                                                    view: View, position: Int, id: Long) {
                            subFamiliaId = listaSubFamiliaCompleta[position].subFamiliaId
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            // write code to perform some action
                        }
                    }
                }
            }
        })

    }

    fun obtenerFamilia(context: Context,familia: Int){

        val url = urls.url+urls.endpointObtenerSubFamilia+"?tienda=00001"+"&subFamiliaId="+familia
        var index = 0;

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
                var body = response.body()?.string()
                val gson = GsonBuilder().create()
                val listaTmp = gson.fromJson(body,Array<SubFamiliaObjeto>::class.java).toMutableList()
                obtenerFamilias(context,listaTmp[0].FamiliaId,familia)
            }
        })
    }


}
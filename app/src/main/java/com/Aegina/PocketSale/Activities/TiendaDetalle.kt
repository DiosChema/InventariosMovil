package com.Aegina.PocketSale.Activities

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
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.Aegina.PocketSale.Dialogs.DialogSeleccionarFoto
import com.Aegina.PocketSale.Metodos.Errores
import com.Aegina.PocketSale.Metodos.Meses
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.Respuesta
import com.Aegina.PocketSale.Objets.TiendaObjeto
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.lang.Integer.parseInt
import java.text.SimpleDateFormat
import java.util.*

class TiendaDetalle : AppCompatActivity(),
    DialogSeleccionarFoto.DialogSeleccionarFoto {

    lateinit var globalVariable: GlobalClass
    lateinit var context : Context
    lateinit var activity: Activity
    private val urls: Urls = Urls()

    var image_uri: Uri? = null
    var cambioFoto : Boolean = false

    lateinit var tiendaDetalleNombre : EditText
    lateinit var tiendaDetalleNumeroTienda : TextView
    lateinit var tiendaDetalleSuscricion : TextView
    lateinit var tiendaDetalleFoto : ImageView
    lateinit var tiendaDetalleEditar : Button
    lateinit var tiendaDetalleCancelarEdicion : Button
    lateinit var tiendaObjeto : TiendaObjeto
    var dialogSeleccionarFoto = DialogSeleccionarFoto()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tienda_detalle)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        globalVariable = this.applicationContext as GlobalClass

        context = this
        activity = this

        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
            StrictMode.setThreadPolicy(policy)
        }

        asignarRecursos()

        obtenerDatosTienda()

        habilitarEdicion(false)

        asignarFuncionBotones()
        dialogSeleccionarFoto.crearDialog(this)
    }

    private fun obtenerDatosTienda() {
        val url = urls.url+urls.endPointUsers.endPointObtenerDatosTienda +
                "?token=" + globalVariable.usuario!!.token

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .get()
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
                    finish()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()!!.string()

                if(body != null && body.isNotEmpty())
                {
                    try
                    {
                        val gson = GsonBuilder().create()
                        tiendaObjeto = gson.fromJson(body, TiendaObjeto::class.java)

                        if(tiendaObjeto.fechaLimiteSuscripcion == null)
                        {
                            throw Exception("")
                        }
                        runOnUiThread()
                        {
                            llenarCampos()
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

    fun habilitarEdicion(activar : Boolean)
    {
        tiendaDetalleNombre.isEnabled = activar
        tiendaDetalleFoto.isEnabled = activar

        if (activar)
        {
            tiendaDetalleEditar.visibility = View.VISIBLE
            tiendaDetalleCancelarEdicion.visibility = View.VISIBLE

            tiendaDetalleEditar.text = getString(R.string.mensaje_actualizar_articulo)
            tiendaDetalleEditar.setOnClickListener{
                actualizarTienda()
            }
        }
        else {
            tiendaDetalleCancelarEdicion.visibility = View.INVISIBLE
            tiendaDetalleEditar.text = getString(R.string.mensaje_editar)
            tiendaDetalleEditar.setOnClickListener{
                habilitarEdicion(true)
            }
        }

    }

    private fun actualizarTienda() {
        val url = urls.url+urls.endPointUsers.endPointActualizarDatosTienda

        val jsonObject = JSONObject()
        try
        {
            jsonObject.put("token", globalVariable.usuario!!.token)
            jsonObject.put("nombre", tiendaDetalleNombre.text)
        }
        catch (e: JSONException)
        {
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
                val body = response.body()!!.string()

                if(body != null && body.isNotEmpty())
                {
                    try
                    {
                        val gson = GsonBuilder().create()
                        val respuesta = gson.fromJson(body, Respuesta::class.java)
                        if(respuesta.status == 0)
                        {
                            if(cambioFoto)
                            {
                                darDeAltaFoto()
                            }
                            else
                            {
                                finish()
                            }
                        }

                    }
                    catch(e:Exception){}
                }

                progressDialog.dismiss()

            }
        })
    }

    private fun llenarCampos()
    {
        tiendaDetalleNombre.setText(tiendaObjeto.nombre)
        tiendaDetalleNumeroTienda.text = getString(R.string.tienda_detalle_tienda) + " #" + parseInt(tiendaObjeto.tienda).toString()

        val nombreMes = Meses()
        val calendar = Calendar.getInstance()
        val formatoFecha = SimpleDateFormat("yyyy-MM-dd")

        calendar.time = formatoFecha.parse(tiendaObjeto.fechaLimiteSuscripcion)// all done

        val fechaTienda = if(parseInt(getString(R.string.numero_idioma))  > 1)
        {
            "" + calendar.get(Calendar.DAY_OF_MONTH) + " " + nombreMes.obtenerMesCorto((calendar.get(Calendar.MONTH) + 1),context) + " " + calendar.get(Calendar.YEAR)
        }
        else
        {
            "" + nombreMes.obtenerMesCorto((calendar.get(Calendar.MONTH) + 1),context) + " " + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.YEAR)
        }

        tiendaDetalleSuscricion.text = fechaTienda
        cambioFoto = false
        tiendaDetalleFoto.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+"t"+globalVariable.usuario!!.tienda+".jpeg"+"&token="+globalVariable.usuario!!.token+"&tipoImagen=2")
    }

    private fun asignarFuncionBotones()
    {
        tiendaDetalleFoto.setOnClickListener()
        {
            dialogSeleccionarFoto.mostrarDialogoFoto()
        }

        if(globalVariable.usuario!!.permisosAdministrador)
        {
            tiendaDetalleCancelarEdicion.visibility = View.INVISIBLE
        }

        tiendaDetalleCancelarEdicion.setOnClickListener{
            llenarCampos()
            habilitarEdicion(false)
        }
    }

    fun darDeAltaFoto()
    {
        val drawable = tiendaDetalleFoto.drawable

        val bitmap: Bitmap = (drawable as BitmapDrawable).bitmap
        val file = bitmapToFile(bitmap)

        val MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg")
        val req: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "token",
                globalVariable.usuario!!.token)
            .addFormDataPart(
                "image",
                "t"+globalVariable.usuario!!.tienda+".jpeg",
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
        }catch (e: IOException){
            e.printStackTrace()
        }

        return file
    }

    private fun openCamera() {
        try
        {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "New Picture")
            values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
            image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            //camera intent
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
            startActivityForResult(cameraIntent, PERMISSION_CODE)
        }
        catch(e:Exception){}
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

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

                val resized = Bitmap.createScaledBitmap(bitmap, (bitmap.width * 300) / bitmap.height, 300, true)
                tiendaDetalleFoto.setImageBitmap(resized)

            } catch (e: FileNotFoundException) { }
            //runOnUiThread { image_uri?.let { eliminarFoto(it) } }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE)
        {
            cambioFoto = true

            val inputStream: InputStream? = data?.data?.let { contentResolver.openInputStream(it) }
            val bitmap: Bitmap = (Drawable.createFromStream(inputStream, image_uri.toString()) as BitmapDrawable).bitmap

            if(bitmap.width > (bitmap.height * 6))
            {
                return
            }

            val resized = Bitmap.createScaledBitmap(bitmap, (bitmap.width * 200) / bitmap.height, 200, true)

            tiendaDetalleFoto.setImageBitmap(resized)
        }

    }

    private fun asignarRecursos() {
        tiendaDetalleNombre = findViewById(R.id.tiendaDetalleNombre)
        tiendaDetalleNumeroTienda = findViewById(R.id.tiendaDetalleNumeroTienda)
        tiendaDetalleSuscricion = findViewById(R.id.tiendaDetalleSuscricion)
        tiendaDetalleFoto = findViewById(R.id.tiendaDetalleFoto)
        tiendaDetalleEditar = findViewById(R.id.tiendaDetalleEditar)
        tiendaDetalleCancelarEdicion = findViewById(R.id.tiendaDetalleCancelarEdicion)

        if(!globalVariable.usuario!!.permisosAdministrador)
        {
            tiendaDetalleEditar.visibility = View.INVISIBLE
        }

        tiendaDetalleCancelarEdicion.visibility = View.INVISIBLE
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,grantResults: IntArray) {
        when(requestCode)
        {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    when(permissions[0])
                    {
                        "android.permission.READ_EXTERNAL_STORAGE" ->
                        {
                            pickImageFromGallery()
                        }
                        "android.permission.CAMERA" ->
                        {
                            openCamera()
                        }
                        "android.permission.WRITE_EXTERNAL_STORAGE" ->
                        {
                            openCamera()
                        }
                    }
                }
                else
                {
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
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

    fun ImageView.loadUrl(url: String) {
        try {Picasso.with(context).load(url).into(this)}
        catch(e: Exception){}
    }
}


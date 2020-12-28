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
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.RespuestaLogin
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
import java.util.*

class TiendaDetalle : AppCompatActivity() {

    lateinit var globalVariable: GlobalClass
    lateinit var context: Context
    private val urls: Urls = Urls()

    var image_uri: Uri? = null
    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    var cambioFoto : Boolean = false

    lateinit var tiendaDetalleNombre : EditText
    lateinit var tiendaDetalleNumeroTienda : EditText
    lateinit var tiendaDetalleSuscricion : EditText
    lateinit var tiendaDetalleFoto : ImageView
    lateinit var tiendaDetalleEditar : Button
    lateinit var tiendaDetalleCancelarEdicion : Button
    lateinit var tiendaObjeto : TiendaObjeto


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tienda_detalle)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        globalVariable = this.applicationContext as GlobalClass

        context = this

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
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response) {
                val json = response.body()!!.string()
                val gson = GsonBuilder().create()

                runOnUiThread()
                {
                    tiendaObjeto = gson.fromJson(json, TiendaObjeto::class.java)
                    llenarCampos()
                    progressDialog.dismiss()
                }

            }
        })
    }

    fun habilitarEdicion(activar : Boolean)
    {
        tiendaDetalleNombre.isEnabled = activar
        tiendaDetalleNumeroTienda.isEnabled = false
        tiendaDetalleSuscricion.isEnabled = false
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
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response) {
                val json = response.body()!!.string()
                progressDialog.dismiss()
                if(cambioFoto)
                {
                    darDeAltaFoto()
                }
                else
                {
                    finish()
                }
            }
        })
    }

    private fun llenarCampos()
    {
        tiendaDetalleNombre.setText(tiendaObjeto.nombre)
        tiendaDetalleNumeroTienda.setText(parseInt(tiendaObjeto.tienda).toString())
        tiendaDetalleSuscricion.setText(tiendaObjeto.fechaLimiteSuscripcion)
        cambioFoto = false
        tiendaDetalleFoto.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+"t"+tiendaObjeto.tienda+".jpeg"+"&token="+globalVariable.usuario!!.token+"&empleado=123")
    }

    private fun asignarFuncionBotones()
    {
        tiendaDetalleFoto.setOnClickListener{
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

        if(globalVariable.usuario!!.permisosAdministrador)
        {
            tiendaDetalleCancelarEdicion.visibility = View.INVISIBLE
        }

        tiendaDetalleCancelarEdicion.setOnClickListener{
            llenarCampos()
            habilitarEdicion(false)
        }
    }

    fun darDeAltaFoto(){
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
                "t"+tiendaObjeto.tienda+".jpeg",
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
        }catch (e: IOException){
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK)
        {
            try
            {
                cambioFoto = true
                val inputStream: InputStream? = image_uri?.let { contentResolver.openInputStream(it) }
                val bitmap: Bitmap = (Drawable.createFromStream(inputStream, image_uri.toString()) as BitmapDrawable).bitmap
                val bitmapCuadrado = Bitmap.createBitmap(bitmap,0,((bitmap.height - bitmap.width)/2),bitmap.width,bitmap.width)
                val resized = Bitmap.createScaledBitmap(bitmapCuadrado, 250, 250, true)
                tiendaDetalleFoto.setImageBitmap(resized)

            } catch (e: FileNotFoundException) { }
            runOnUiThread { image_uri?.let { eliminarFoto(it) } }
        }

    }

    fun eliminarFoto(uri : Uri){
        val fdelete = File(getFilePath(uri))

        if (fdelete.exists()) {
            if (fdelete.delete()) { return } else { return }
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

    fun ImageView.loadUrl(url: String) {
        Picasso.with(context).load(url).into(this)
    }
}


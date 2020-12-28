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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.*
import com.Aegina.PocketSale.Objets.EmpleadoObject
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.InventarioObjeto
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.google.gson.GsonBuilder
import com.google.zxing.integration.android.IntentIntegrator
import com.squareup.picasso.Picasso
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.util.*

class PerfilDetalle : AppCompatActivity() {

    lateinit var perfilDetalleNombre : EditText
    lateinit var perfilDetalleCorreo : EditText
    lateinit var perfilDetalleFoto : ImageView
    lateinit var perfilDetallePermisosVenta : CheckBox
    lateinit var perfilDetallePermisosModificarInventario : CheckBox
    lateinit var perfilDetallePermisosCrearArticulos : CheckBox
    lateinit var perfilDetallePermisosEstadisticas : CheckBox
    lateinit var perfilDetallePermisosProovedor : CheckBox
    lateinit var perfilDetalleDarDeAlta : Button
    lateinit var perfilDetalleCancelarEdicion : Button
    lateinit var perfilDetalleEliminarEmpleado : ImageButton
    lateinit var globalVariable: GlobalClass
    lateinit var context: Context
    private val urls: Urls = Urls()

    var image_uri: Uri? = null
    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    var cambioFoto : Boolean = false

    lateinit var empleadoObject: EmpleadoObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_detalle)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        globalVariable = this.applicationContext as GlobalClass

        context = this

        asignarRecursos()

        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
            StrictMode.setThreadPolicy(policy)
        }

        when {
            intent.getSerializableExtra("empleado") != null -> {
                empleadoObject = intent.getSerializableExtra("empleado") as EmpleadoObject

                habilitarEdicion(false)
                llenarCampos(empleadoObject)
                asignarFuncionBotones(true)
            }
            intent.extras != null -> {
                buscarEmpleado()
            }
            else -> {
                asignarFuncionBotones(false)
            }
        }

    }

    fun asignarFuncionBotones(esEditar : Boolean){

        perfilDetalleFoto.setOnClickListener{
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

        perfilDetalleCancelarEdicion.setOnClickListener{
            llenarCampos(empleadoObject)
            habilitarEdicion(false)
        }

        if(!esEditar){
            perfilDetalleDarDeAlta.setOnClickListener{
                if (!datosVacios())
                    darDeAltaEmpleado()
            }
        }
    }

    private fun darDeAltaEmpleado() {
        val url = urls.url+urls.endPointUsers.endPointAltaEmpleado

        val jsonObject = JSONObject()
        try
        {
            jsonObject.put("token", globalVariable.usuario!!.token)
            jsonObject.put("nombre", perfilDetalleNombre.text)
            jsonObject.put("email", perfilDetalleCorreo.text.toString().toLowerCase())
            jsonObject.put("password", "12345678")
            jsonObject.put("permisosVenta", perfilDetallePermisosVenta.isChecked)
            jsonObject.put("permisosModificarInventario", perfilDetallePermisosModificarInventario.isChecked)
            jsonObject.put("permisosAltaInventario", perfilDetallePermisosCrearArticulos.isChecked)
            jsonObject.put("permisosEstadisticas", perfilDetallePermisosEstadisticas.isChecked)
            jsonObject.put("permisosProovedor", perfilDetallePermisosProovedor.isChecked)
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

    fun darDeAltaFoto(){
        val drawable = perfilDetalleFoto.drawable

        val bitmap: Bitmap = (drawable as BitmapDrawable).bitmap
        val file = bitmapToFile(bitmap)

        val MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg")
        val req: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "token",
                globalVariable.usuario!!.token)
            .addFormDataPart(
                "image",
                perfilDetalleCorreo.text.toString()+".jpeg",
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
                perfilDetalleFoto.setImageBitmap(resized)

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

    fun llenarCampos(empleado : EmpleadoObject){
        perfilDetalleNombre.setText(empleado.nombre)
        perfilDetalleCorreo.setText(empleado.user)
        //perfilDetalleFoto.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+empleado.idArticulo+".jpeg"+"&token="+globalVariable.usuario!!.token)
        perfilDetalleFoto.loadUrl(urls.url+urls.endPointImagenes.endPointImagenes+empleado.user+".jpeg"+"&token="+globalVariable.usuario!!.token+"&empleado=123")
        perfilDetallePermisosVenta.isChecked = empleado.permisosVenta
        perfilDetallePermisosModificarInventario.isChecked = empleado.permisosModificarInventario
        perfilDetallePermisosCrearArticulos.isChecked = empleado.permisosAltaInventario
        perfilDetallePermisosEstadisticas.isChecked = empleado.permisosEstadisticas
        perfilDetallePermisosProovedor.isChecked = empleado.permisosProovedor

        cambioFoto = false
    }

    private fun asignarRecursos() {
        perfilDetalleNombre = findViewById(R.id.perfilDetalleNombre)
        perfilDetalleCorreo = findViewById(R.id.perfilDetalleCorreo)
        perfilDetalleFoto = findViewById(R.id.perfilDetalleFoto)
        perfilDetallePermisosVenta = findViewById(R.id.perfilDetallePermisosVenta)
        perfilDetallePermisosModificarInventario = findViewById(R.id.perfilDetallePermisosModificarInventario)
        perfilDetallePermisosCrearArticulos = findViewById(R.id.perfilDetallePermisosCrearArticulos)
        perfilDetallePermisosEstadisticas = findViewById(R.id.perfilDetallePermisosEstadisticas)
        perfilDetallePermisosProovedor = findViewById(R.id.perfilDetallePermisosProovedor)
        perfilDetalleDarDeAlta = findViewById(R.id.perfilDetalleDarDeAlta)
        perfilDetalleCancelarEdicion = findViewById(R.id.perfilDetalleCancelarEdicion)
        perfilDetalleEliminarEmpleado = findViewById(R.id.perfilDetalleEliminarEmpleado)

        perfilDetalleCancelarEdicion.visibility = View.INVISIBLE
        perfilDetalleEliminarEmpleado.visibility = View.INVISIBLE
    }

    fun habilitarEdicion(activar : Boolean) {
        perfilDetalleCorreo.isEnabled = false
        perfilDetalleNombre.isEnabled = activar
        perfilDetalleFoto.isEnabled = activar
        perfilDetalleFoto.isEnabled = activar

        perfilDetalleDarDeAlta.isEnabled = true
        perfilDetalleCancelarEdicion.visibility = View.INVISIBLE
        perfilDetalleEliminarEmpleado.visibility = View.INVISIBLE

        if(globalVariable.usuario!!.permisosAdministrador)
        {
            perfilDetallePermisosVenta.isEnabled = false
            perfilDetallePermisosModificarInventario.isEnabled = false
            perfilDetallePermisosCrearArticulos.isEnabled = false
            perfilDetallePermisosEstadisticas.isEnabled = false
            perfilDetallePermisosProovedor.isEnabled = false
        }
        else
        {
            perfilDetallePermisosVenta.isEnabled = activar
            perfilDetallePermisosModificarInventario.isEnabled = activar
            perfilDetallePermisosCrearArticulos.isEnabled = activar
            perfilDetallePermisosEstadisticas.isEnabled = activar
            perfilDetallePermisosProovedor.isEnabled = activar
        }


        if (activar) {

            perfilDetalleCancelarEdicion.visibility = View.VISIBLE
            perfilDetalleEliminarEmpleado.visibility = View.VISIBLE

            perfilDetalleDarDeAlta.text = getString(R.string.mensaje_actualizar_articulo)
            perfilDetalleDarDeAlta.setOnClickListener{
                if (!datosVacios())
                    actualizarEmpleado()
            }

            perfilDetalleEliminarEmpleado.setOnClickListener{
                showDialogEliminarEmpleado()
            }

        }
        else {
            perfilDetalleDarDeAlta.text = getString(R.string.mensaje_editar)
            perfilDetalleDarDeAlta.setOnClickListener{
                habilitarEdicion(true)
            }
        }

    }

    private fun actualizarEmpleado() {
        val url = urls.url+urls.endPointUsers.endPointActualizarEmpleado

        val jsonObject = JSONObject()
        try
        {
            jsonObject.put("token", globalVariable.usuario!!.token)
            jsonObject.put("nombre", perfilDetalleNombre.text)
            jsonObject.put("email", empleadoObject.user)
            jsonObject.put("permisosAdministrador", empleadoObject.permisosAdministrador)
            jsonObject.put("permisosVenta", perfilDetallePermisosVenta.isChecked)
            jsonObject.put("permisosModificarInventario", perfilDetallePermisosModificarInventario.isChecked)
            jsonObject.put("permisosAltaInventario", perfilDetallePermisosCrearArticulos.isChecked)
            jsonObject.put("permisosEstadisticas", perfilDetallePermisosEstadisticas.isChecked)
            jsonObject.put("permisosProovedor", perfilDetallePermisosProovedor.isChecked)
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
                if(cambioFoto) darDeAltaFoto()
                else finish()
            }
        })
    }

    fun datosVacios() : Boolean {

        if(perfilDetalleNombre.text.toString() == ""){return moverCampo(perfilDetalleNombre)}
        //if(perfilDetalleCorreo.text.toString() == ""){return moverCampo(perfilDetalleCorreo)}
        if(!perfilDetalleCorreo.text.toString().isEmailValid()){return moverCampo(perfilDetalleCorreo)}


        return false
    }

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
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

    private fun showDialogEliminarEmpleado() {
        val dialog = Dialog(this)
        dialog.setTitle(title)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_text)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogText = dialog.findViewById(R.id.dialogText) as EditText
        val dialogAceptar = dialog.findViewById(R.id.dialogAceptar) as Button
        val dialogCancelar = dialog.findViewById(R.id.dialogCancelar) as Button
        val dialogTitulo = dialog.findViewById(R.id.dialogTitulo) as TextView

        dialogTitulo.text = getString(R.string.perfil_eliminar_articulo)
        dialogText.visibility = View.INVISIBLE

        dialogAceptar.setOnClickListener {
            dialog.dismiss()
            eliminarEmpleado()
        }

        dialogCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun eliminarEmpleado() {
        val url = urls.url+urls.endPointUsers.endPointEliminarEmpleado

        val jsonObject = JSONObject()
        try
        {
            jsonObject.put("token", globalVariable.usuario!!.token)
            jsonObject.put("email", empleadoObject.user)
            jsonObject.put("permisosAdministrador", empleadoObject.permisosAdministrador)
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
                finish()
            }
        })
    }

    private fun buscarEmpleado() {
        val url = urls.url + urls.endPointUsers.endPointObtenerEmpleado + "?user=" + globalVariable.usuario!!.user

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
            override fun onResponse(call: Call, response: Response) {

                val body = response.body()?.string()

                val gson = GsonBuilder().create()
                empleadoObject = gson.fromJson(body, EmpleadoObject::class.java)

                runOnUiThread()
                {
                    habilitarEdicion(false)
                    llenarCampos(empleadoObject)
                    asignarFuncionBotones(true)
                    progressDialog.dismiss()
                }
            }
        })
    }

    fun ImageView.loadUrl(url: String) {
        Picasso.with(context).load(url).into(this)
    }

}
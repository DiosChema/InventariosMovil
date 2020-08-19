package com.example.perraco

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import com.squareup.picasso.Picasso

class InventarioDetalle : AppCompatActivity() {

    lateinit var invDetalleId : EditText;
    lateinit var invDetalleFoto: ImageView
    lateinit var invDetalleNombre : EditText;
    lateinit var invDetalleNombreDetalle : EditText;
    lateinit var invDetalleCantidad : EditText;
    lateinit var invDetallePrecio : EditText;
    lateinit var invDetalleCosto : EditText;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventario_detalle)

        invDetalleId = findViewById(R.id.invDetalleId)
        invDetalleFoto = findViewById(R.id.invDetalleFoto)
        invDetalleNombre = findViewById(R.id.invDetalleNombre)
        invDetalleNombreDetalle = findViewById(R.id.invDetalleNombreDetalle)
        invDetalleCantidad = findViewById(R.id.invDetalleCantidad)
        invDetallePrecio = findViewById(R.id.invDetallePrecio)
        invDetalleCosto = findViewById(R.id.invDetalleCosto)

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

            invDetalleId.setText(inventarioObjeto.idArticulo.toString())
            val urls: Urls = Urls()
            invDetalleFoto.loadUrl(urls.endPointImagenes)
            //invDetalleFoto.loadUrl(inventarioObjeto.urlFoto)
            invDetalleNombre.setText(inventarioObjeto.nombreArticulo)
            invDetalleNombreDetalle.setText(inventarioObjeto.descripcionArticulo)
            invDetalleCantidad.setText(inventarioObjeto.cantidadArticulo.toString())
            invDetallePrecio.setText(inventarioObjeto.precioArticulo.toString())
            invDetalleCosto.setText(inventarioObjeto.costoArticulo.toString())
        }


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    fun ImageView.loadUrl(url: String) {
        Picasso.with(context).load(url).into(this)
    }
}
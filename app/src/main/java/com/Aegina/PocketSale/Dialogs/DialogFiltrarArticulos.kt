package com.Aegina.PocketSale.Dialogs

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatDialogFragment
import com.Aegina.PocketSale.Objets.*
import com.Aegina.PocketSale.R
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.lang.Double.parseDouble
import java.lang.Float.parseFloat
import java.lang.Integer.parseInt

class DialogFiltrarArticulos : AppCompatDialogFragment() {

    lateinit var agregarFiltro: DialogFiltrarArticulos

    val urls: Urls = Urls()
    lateinit var globalVariable: GlobalClass
    lateinit var contextTmp :Context
    lateinit var activityTmp: Activity
    lateinit var dialogFiltrarArticulos:Dialog

    var listaFamilia:MutableList<String> = ArrayList()
    var listaFamiliaCompleta:MutableList<FamiliasSubFamiliasObject> = ArrayList()
    var listaSubFamilia:MutableList<String> = ArrayList()
    var listaSubFamiliaCompleta:MutableList<SubFamiliaObjeto> = ArrayList()

    lateinit var dialogArticulosFamiliaSpinner: Spinner
    lateinit var dialogArticulosSubFamiliaSpinner: Spinner
    lateinit var dialogFiltroArticulosCancelar: Button
    lateinit var dialogFiltroArticulosAceptar: Button
    lateinit var checkBoxFamilia: CheckBox
    lateinit var checkBoxSubFamilia: CheckBox
    lateinit var dialogFiltroNombre: EditText
    lateinit var dialogFiltroCantidadMinimo: EditText
    lateinit var dialogFiltroCantidadMaximo: EditText
    lateinit var dialogFiltroPrecioMinimo: EditText
    lateinit var dialogFiltroPrecioMaximo: EditText
    lateinit var invBottonTomarCodigo: ImageButton


    var subFamiliaId = -1

    fun crearDialog(context : Context, globalVariableTmp: GlobalClass, activity : Activity){

        contextTmp = context
        activityTmp = activity
        globalVariable = globalVariableTmp

        if(listaFamiliaCompleta.size == 0)
            obtenerFamilias(contextTmp)

        dialogFiltrarArticulos = Dialog(context)

        dialogFiltrarArticulos.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogFiltrarArticulos.setCancelable(false)
        dialogFiltrarArticulos.setContentView(R.layout.dialog_filtro_articulos)

        agregarFiltro = context as DialogFiltrarArticulos


        dialogArticulosFamiliaSpinner = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroArticulosFamiliaSpinner) as Spinner
        dialogArticulosSubFamiliaSpinner = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroArticulosSubFamiliaSpinner) as Spinner
        dialogFiltroArticulosCancelar = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroArticulosCancelar) as Button
        dialogFiltroArticulosAceptar = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroArticulosAceptar) as Button
        checkBoxFamilia = dialogFiltrarArticulos.findViewById(R.id.checkBoxFamilia) as CheckBox
        checkBoxSubFamilia = dialogFiltrarArticulos.findViewById(R.id.checkBoxSubFamilia) as CheckBox
        dialogFiltroNombre = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroNombre) as EditText
        dialogFiltroCantidadMinimo = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroCantidadMinimo) as EditText
        dialogFiltroCantidadMaximo = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroCantidadMaximo) as EditText
        dialogFiltroPrecioMinimo = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroPrecioMinimo) as EditText
        dialogFiltroPrecioMaximo = dialogFiltrarArticulos.findViewById(R.id.dialogFiltroPrecioMaximo) as EditText
        invBottonTomarCodigo = dialogFiltrarArticulos.findViewById(R.id.invBottonTomarCodigo) as ImageButton

        dialogArticulosFamiliaSpinner.isEnabled = false
        dialogArticulosSubFamiliaSpinner.isEnabled = false

        checkBoxFamilia.setOnClickListener{
            dialogArticulosFamiliaSpinner.isEnabled = checkBoxFamilia.isChecked
        }

        checkBoxSubFamilia.setOnClickListener{
            dialogArticulosSubFamiliaSpinner.isEnabled = checkBoxSubFamilia.isChecked
        }

        invBottonTomarCodigo.setOnClickListener{
            reestablecerCampos()
        }

        dialogFiltroArticulosCancelar.setOnClickListener{
            dialogFiltrarArticulos.dismiss()
        }

        dialogFiltroArticulosAceptar.setOnClickListener{

            var familiaIdTmp = -1
            var subFamiliaIdTmp = -1
            var minCantidad = -1
            var maxCantidad = -1
            var minPrecio = -1.0
            var maxPrecio = -1.0
            var nombre = ""

            if(checkBoxFamilia.isChecked)
                familiaIdTmp = listaFamiliaCompleta[dialogArticulosFamiliaSpinner.selectedItemPosition].familiaId

            if(checkBoxSubFamilia.isChecked)
                subFamiliaIdTmp = listaSubFamiliaCompleta[dialogArticulosSubFamiliaSpinner.selectedItemPosition].subFamiliaId

            if(dialogFiltroNombre.text.trim().isNotEmpty())
                nombre = dialogFiltroNombre.text.toString()

            if(dialogFiltroCantidadMinimo.text.isNotEmpty())
                minCantidad = parseInt(dialogFiltroCantidadMinimo.text.toString())

            if(dialogFiltroCantidadMaximo.text.isNotEmpty())
                maxCantidad = parseInt(dialogFiltroCantidadMaximo.text.toString())

            if(dialogFiltroPrecioMinimo.text.isNotEmpty())
                minPrecio = parseDouble(dialogFiltroPrecioMinimo.text.toString())

            if(dialogFiltroPrecioMaximo.text.isNotEmpty())
                maxPrecio = parseDouble(dialogFiltroPrecioMaximo.text.toString())

            buscarArticulos(familiaIdTmp,subFamiliaIdTmp,minCantidad,maxCantidad,minPrecio,maxPrecio,nombre)

            //agregarFiltro.filtrosArticulos(familiaIdTmp,subFamiliaIdTmp,minCantidad,maxCantidad,minPrecio,maxPrecio,nombre)

            dialogFiltrarArticulos.dismiss()

        }

    }

    private fun buscarArticulos(
        familiaIdTmp: Int,
        subFamiliaIdTmp: Int,
        minCantidad: Int,
        maxCantidad: Int,
        minPrecio: Double,
        maxPrecio: Double,
        nombre: String)
    {
        val urls = Urls()

        var url = urls.url+urls.endPointsInventario.endPointInventario+"?token="+globalVariable.usuario!!.token

        if(familiaIdTmp != -1) url += "&familiaId=" + familiaIdTmp
        if(subFamiliaIdTmp != -1) url += "&subFamiliaId=" + subFamiliaIdTmp
        if(minCantidad != -1) url += "&minimoCantidad=" + minCantidad
        if(maxCantidad != -1) url += "&maximoCantidad=" + maxCantidad
        if(minPrecio != -1.0) url += "&minimoPrecio=" + minPrecio
        if(maxPrecio != -1.0) url += "&maximoPrecio=" + maxPrecio
        if(nombre != "") url += "&nombreArticulo=" + nombre

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()

        val progressDialog = ProgressDialog(contextTmp)
        progressDialog.setMessage(contextTmp.getString(R.string.mensaje_espera))
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
            }
            override fun onResponse(call: Call, response: Response)
            {
                val body = response.body()?.string()

                if(body != null && body.isNotEmpty()) {
                    val gson = GsonBuilder().create()
                    val model = gson.fromJson(body, Array<InventarioObjeto>::class.java).toList()

                    agregarFiltro.listaArticulos(model.toMutableList())
                }

                progressDialog.dismiss()

            }
        })
    }

    fun reestablecerCampos() {
        if(listaFamilia.size > 0)
            dialogArticulosFamiliaSpinner.setSelection(0)

        dialogFiltroCantidadMinimo.setText("")
        dialogFiltroCantidadMaximo.setText("")

        checkBoxFamilia.isChecked = false
        checkBoxSubFamilia.isChecked = false

    }

    fun mostrarDialog(){
        dialogFiltrarArticulos.show()
    }

    fun obtenerFamilias(context: Context){

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

                for (familias in listaFamiliaCompleta) {
                    listaFamilia.add(familias.nombreFamilia)
                }

                activityTmp.runOnUiThread {
                    val adapter = ArrayAdapter(context,
                        R.layout.item_spinner, listaFamilia)
                    dialogArticulosFamiliaSpinner.adapter = adapter
                    dialogArticulosFamiliaSpinner.setSelection(0)

                    dialogArticulosFamiliaSpinner.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>,
                                                    view: View, position: Int, id: Long) {
                            obtenerSubFamilias(context,position)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }
                }
            }
        })

    }

    fun obtenerSubFamilias(context: Context, familiaId: Int){
        listaSubFamilia.clear()
        listaSubFamiliaCompleta = listaFamiliaCompleta[familiaId].SubFamilia.toMutableList()

        for (familias in listaSubFamiliaCompleta) {
            listaSubFamilia.add(familias.nombreSubFamilia)
        }

        val adapter = ArrayAdapter(context,
            R.layout.item_spinner, listaSubFamilia)
        dialogArticulosSubFamiliaSpinner.adapter = adapter

        if(listaSubFamilia.size > 0) {
            dialogArticulosSubFamiliaSpinner.setSelection(0)
        }

        dialogArticulosSubFamiliaSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if(listaSubFamilia.size > 0) {
                    subFamiliaId = listaSubFamiliaCompleta[position].subFamiliaId
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    interface DialogFiltrarArticulos {
        //fun filtrosArticulos(familiaIdTmp:Int,subFamiliaIdTmp:Int,minCantidad:Int,maxCantidad:Int,minPrecio:Double,maxPrecio:Double,nombre:String)
        fun listaArticulos(listaArticulos: MutableList<InventarioObjeto>)
    }

}
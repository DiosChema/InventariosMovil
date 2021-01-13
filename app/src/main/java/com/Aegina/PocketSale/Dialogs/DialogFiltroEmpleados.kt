package com.Aegina.PocketSale.Dialogs

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import com.Aegina.PocketSale.Metodos.Errores
import com.Aegina.PocketSale.Objets.EmpleadoObject
import com.Aegina.PocketSale.Objets.GlobalClass
import com.Aegina.PocketSale.Objets.Urls
import com.Aegina.PocketSale.R
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class DialogFiltroEmpleados : AppCompatDialogFragment()
{
    lateinit var agregarFiltroEmpleados: DialogFiltroEmpleados

    val urls: Urls = Urls()
    lateinit var globalVariable: GlobalClass
    lateinit var contextTmp : Context
    lateinit var activityTmp: Activity
    lateinit var dialogFiltrarEmpleados: Dialog

    lateinit var dialogFiltroEmpleadoCorreo: EditText
    lateinit var dialogFiltroEmpleadoNombre: EditText
    lateinit var dialogFiltroEmpleadosCancelar: Button
    lateinit var dialogFiltroEmpleadosAceptar: Button

    fun crearDialog(context : Context, globalVariableTmp: GlobalClass, activity : Activity)
    {
        contextTmp = context
        activityTmp = activity
        globalVariable = globalVariableTmp

        dialogFiltrarEmpleados = Dialog(context)

        dialogFiltrarEmpleados.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogFiltrarEmpleados.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        dialogFiltrarEmpleados.setCancelable(false)
        dialogFiltrarEmpleados.setContentView(R.layout.dialog_filtro_empleado)

        agregarFiltroEmpleados = context as DialogFiltroEmpleados

        dialogFiltroEmpleadoCorreo = dialogFiltrarEmpleados.findViewById(R.id.dialogFiltroEmpleadoCorreo) as EditText
        dialogFiltroEmpleadoNombre = dialogFiltrarEmpleados.findViewById(R.id.dialogFiltroEmpleadoNombre) as EditText
        dialogFiltroEmpleadosCancelar = dialogFiltrarEmpleados.findViewById(R.id.dialogFiltroEmpleadosCancelar) as Button
        dialogFiltroEmpleadosAceptar = dialogFiltrarEmpleados.findViewById(R.id.dialogFiltroEmpleadosAceptar) as Button

        dialogFiltroEmpleadosCancelar.setOnClickListener()
        {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm.isAcceptingText)
            {
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            }
            dialogFiltrarEmpleados.dismiss()
        }

        dialogFiltroEmpleadosAceptar.setOnClickListener()
        {
            obtenerEmpleados()
        }
    }

    fun obtenerEmpleados(){
        val urls = Urls()

        var url = urls.url+urls.endPointUsers.endPointObtenerEmpleados+"?token="+globalVariable.usuario!!.token

        if(dialogFiltroEmpleadoCorreo.text.toString().isNotEmpty())
        {
           url += "&correo=" +  dialogFiltroEmpleadoCorreo.text.toString()
        }

        if(dialogFiltroEmpleadoNombre.text.toString().isNotEmpty())
        {
           url += "&nombre=" +  dialogFiltroEmpleadoNombre.text.toString()
        }

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val client = OkHttpClient()

        val progressDialog = ProgressDialog(contextTmp)
        progressDialog.setMessage(contextTmp.getString(R.string.mensaje_espera))
        progressDialog.setCancelable(false)
        progressDialog.show()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                progressDialog.dismiss()
                activityTmp.runOnUiThread()
                {
                    Toast.makeText(contextTmp, contextTmp.getString(R.string.mensaje_error_intentear_mas_tarde), Toast.LENGTH_LONG).show()
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

                        agregarFiltroEmpleados.listaEmpleados(model.toMutableList())

                        val imm = contextTmp.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                        if (imm.isAcceptingText)
                        {
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                        }
                        dialogFiltrarEmpleados.dismiss()
                    }
                    catch(e:Exception)
                    {
                        val errores = Errores()
                        errores.procesarError(contextTmp,body,activityTmp)
                    }

                }

                progressDialog.dismiss()

            }
        })

    }

    fun mostrarVentana()
    {
        dialogFiltrarEmpleados.show()
    }

    interface DialogFiltroEmpleados {
        fun listaEmpleados(empleados: MutableList<EmpleadoObject>)
    }
}
package com.Aegina.PocketSale.Objets

import android.app.Application

class GlobalClass : Application() {
    var usuario: datosUsuario? = null
    var actualizarVentana: ActualizarVentana? = null
    var tokenEspecial :String? = null
}
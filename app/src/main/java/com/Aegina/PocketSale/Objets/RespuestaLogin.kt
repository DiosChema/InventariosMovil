package com.Aegina.PocketSale.Objets

import java.io.Serializable

data class RespuestaLogin(
    var status:Int,
    var mensaje:String,
    var usuario: datosUsuario
): Serializable

data class datosUsuario(
    var token:String,
    var user:String,
    var permisosAdministrador: Boolean,
    var permisosVenta: Boolean,
    var permisosModificarInventario: Boolean,
    var permisosAltaInventario: Boolean,
    var permisosEstadisticas: Boolean,
    var permisosProovedor: Boolean
)
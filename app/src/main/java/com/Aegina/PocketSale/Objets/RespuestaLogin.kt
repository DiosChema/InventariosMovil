package com.Aegina.PocketSale.Objets

import java.io.Serializable

data class RespuestaLogin(
    var status:Int,
    var mensaje:String,
    var usuario: datosUsuario,
    var tokenEspecial: String
): Serializable

data class datosUsuario(
    var token:String,
    var user:String,
    var tienda:Int,
    var permisosAdministrador: Boolean,
    var permisosVenta: Boolean,
    var permisosModificarInventario: Boolean,
    var permisosModificarVenta: Boolean,
    var permisosAltaInventario: Boolean,
    var permisosEstadisticas: Boolean,
    var permisosProovedor: Boolean,
    var permisosModificarProovedor: Boolean,
    var permisosPerdidas: Boolean,
    var permisosModificarPerdidas: Boolean
)
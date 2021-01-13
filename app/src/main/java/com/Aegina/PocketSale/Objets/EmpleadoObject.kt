package com.Aegina.PocketSale.Objets

import java.io.Serializable

data class EmpleadoObject(
    var user:String,
    var password:String,
    var tienda:Double,
    var nombre:String,
    var permisosAdministrador:Boolean,
    var permisosVenta:Boolean,
    var permisosModificarInventario:Boolean,
    var permisosModificarVenta: Boolean,
    var permisosAltaInventario:Boolean,
    var permisosEstadisticas:Boolean,
    var permisosProovedor:Boolean,
    var permisosModificarProovedor: Boolean
):Serializable
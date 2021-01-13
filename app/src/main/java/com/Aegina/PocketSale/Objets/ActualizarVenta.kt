package com.Aegina.PocketSale.Objets

import java.io.Serializable

data class ActualizarVenta(
    var token:String,
    var idVenta:Int,
    var fecha:String,
    var articulos:List<InventarioObjeto>
): Serializable
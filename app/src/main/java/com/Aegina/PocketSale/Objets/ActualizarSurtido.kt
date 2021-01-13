package com.Aegina.PocketSale.Objets

import java.io.Serializable

data class ActualizarSurtido(
    var token:String,
    var idSurtido:Int,
    var fecha:String,
    var articulos:List<InventarioObjeto>
): Serializable
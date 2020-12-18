package com.Aegina.PocketSale.Objets

import java.io.Serializable

data class ActualizarInventarioObject(
    var token:String,
    var fecha:String,
    var articulos:List<InventarioObjeto>
): Serializable
package com.Aegina.PocketSale.Objets

import java.io.Serializable

data class EstadisticaArticuloObject(
    var _id:InventarioObjeto,
    var cantidad:Int,
    var total:Double
): Serializable
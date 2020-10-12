package com.Aegina.PocketSale.Objets

import java.io.Serializable
import java.util.*

data class VentasObjeto(
    var _id:Int,
    var fecha:Date,
    var totalVenta:Double,
    var totalCosto:Double,
    var articulos:List<ArticuloVentaObject>
): Serializable
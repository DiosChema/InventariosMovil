package com.example.perraco.Objets

import java.io.Serializable
import java.util.*

data class VentasObjeto(
    var _id:Int,
    var fecha:Date,
    var totalVenta:Double,
    var articulos:List<ArticuloObjeto>
): Serializable
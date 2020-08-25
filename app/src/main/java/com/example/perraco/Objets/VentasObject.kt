package com.example.perraco.Objets

import java.io.Serializable

data class VentasObjeto(
    var _id:Int,
    var fecha:String,
    var totalVenta:Int
): Serializable
package com.Aegina.PocketSale.Objets

import java.io.Serializable

data class SuscripcionObject(
    var idProducto:String,
    var numeroMeses:Int,
    var precioSuscripcion:String,
    var precioMes:Double
):Serializable
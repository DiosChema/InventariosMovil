package com.Aegina.PocketSale.Objets

import java.io.Serializable

data class Respuesta(
    var status:Int,
    var mensaje:String,
    var respuesta:String
): Serializable
package com.Aegina.PocketSale.Objets

import java.io.Serializable

data class TiendaObjeto(
    var tienda:String,
    var token:String,
    var fechaLimiteSuscripcion:String,
    var nombre:String
):Serializable
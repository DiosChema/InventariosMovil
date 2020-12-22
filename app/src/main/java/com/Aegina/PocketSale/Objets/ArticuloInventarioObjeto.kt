package com.Aegina.PocketSale.Objets

import java.io.Serializable

data class ArticuloInventarioObjeto(
    var idArticulo:Long,
    var nombreArticulo:String,
    var descripcionArticulo:String,
    var cantidadArticulo:Int,
    var precioArticulo:Double,
    var familiaArticulo:String,
    var costoArticulo:Double,
    var urlFoto:String
):Serializable
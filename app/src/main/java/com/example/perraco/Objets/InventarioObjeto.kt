package com.example.perraco.Objets

import java.io.Serializable

data class InventarioObjeto(
    var idArticulo:Long,
    var nombreArticulo:String,
    var descripcionArticulo:String,
    var cantidadArticulo:Int,
    var precioArticulo:Double,
    var familiaArticulo:String,
    var costoArticulo:Double,
    var urlFoto:String
):Serializable
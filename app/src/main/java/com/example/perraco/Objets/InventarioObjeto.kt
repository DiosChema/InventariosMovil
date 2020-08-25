package com.example.perraco.Objets

import java.io.Serializable

data class InventarioObjeto(
    var idArticulo:String,
    var nombreArticulo:String,
    var descripcionArticulo:String,
    var cantidadArticulo:Int,
    var precioArticulo:String,
    var familiaArticulo:String,
    var costoArticulo:String,
    var urlFoto:String
):Serializable
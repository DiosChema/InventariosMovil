package com.example.perraco.Objets

import java.io.Serializable

data class ArticuloObjeto(
    var idArticulo:Long,
    var cantidad:Int,
    var precio:Double,
    var nombre:String,
    var costo:Double
): Serializable
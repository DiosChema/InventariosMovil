package com.example.perraco.Objets

import java.io.Serializable

data class ArticuloObjeto(
    var idArticulo:String,
    var cantidad:Int,
    var precio:Double
): Serializable
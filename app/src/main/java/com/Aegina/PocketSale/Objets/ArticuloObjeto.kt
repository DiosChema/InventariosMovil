package com.Aegina.PocketSale.Objets

import java.io.Serializable

data class ArticuloObjeto(
    var idArticulo:Long,
    var cantidad:Int,
    var precio:Double,
    var nombre:String,
    var costo:Double,
    var modificaInventario:Boolean
): Serializable
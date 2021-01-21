package com.Aegina.PocketSale.Objets

import java.io.Serializable

data class InventarioObjeto(
    var idArticulo:Long,
    var nombre:String,
    var descripcion:String,
    var cantidad:Int,
    var precio:Double,
    var familia:String,
    var costo:Double,
    var inventarioOptimo:Int,
    var modificaInventario:Boolean
):Serializable
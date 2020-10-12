package com.Aegina.PocketSale.Objets

import java.io.Serializable

data class ArticuloVentaObject(
    var idArticulo:Long,
    var cantidad:Int,
    var precio:Double,
    var nombre:String,
    var costo:Double,
    var articulosDetalle:List<InventarioObjeto>
): Serializable
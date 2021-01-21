package com.Aegina.PocketSale.Objets.Inventory

import java.io.Serializable
import java.util.*

data class InventoryDateObject(
    var idArticulo:Long,
    var nombre:String,
    var descripcion:String,
    var cantidad:Int,
    var precio:Double,
    var familia:String,
    var costo:Double,
    var inventarioOptimo:Int,
    var modificaInventario:Boolean,
    var fecha: Date
): Serializable
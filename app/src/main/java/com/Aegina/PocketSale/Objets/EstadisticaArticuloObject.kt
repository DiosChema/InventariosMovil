package com.Aegina.PocketSale.Objets

import com.Aegina.PocketSale.Objets.Inventory.InventoryDateObject
import java.io.Serializable

data class EstadisticaArticuloObject(
    var articulo: InventoryDateObject,
    var cantidad:Int,
    var total:Double
): Serializable
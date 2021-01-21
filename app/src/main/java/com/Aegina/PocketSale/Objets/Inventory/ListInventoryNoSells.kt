package com.Aegina.PocketSale.Objets.Inventory

import com.Aegina.PocketSale.Objets.EstadisticaArticuloObject
import java.io.Serializable

data class ListInventoryNoSells(
    val Inventory: List<InventoryDateObject>,
    val count: Int
): Serializable
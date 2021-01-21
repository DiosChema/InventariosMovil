package com.Aegina.PocketSale.Objets.Inventory

import com.Aegina.PocketSale.Objets.EstadisticaArticuloObject
import com.Aegina.PocketSale.Objets.InventarioObjeto
import java.io.Serializable

data class ListEstadisticaArticuloObject(
    val inventory: List<EstadisticaArticuloObject>,
    val count: Int
): Serializable
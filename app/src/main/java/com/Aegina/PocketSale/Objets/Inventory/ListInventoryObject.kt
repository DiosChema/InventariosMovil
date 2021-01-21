package com.Aegina.PocketSale.Objets.Inventory

import com.Aegina.PocketSale.Objets.ArticuloInventarioObjeto
import java.io.Serializable

data class ListInventoryObject(
    val Inventory:List<ArticuloInventarioObjeto>,
    val count: Int
): Serializable
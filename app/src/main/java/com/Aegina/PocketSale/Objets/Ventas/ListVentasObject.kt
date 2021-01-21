package com.Aegina.PocketSale.Objets.Ventas

import com.Aegina.PocketSale.Objets.VentasObjeto
import java.io.Serializable

data class ListVentasObject(
    val sales:List<VentasObjeto>,
    val count: Int
): Serializable
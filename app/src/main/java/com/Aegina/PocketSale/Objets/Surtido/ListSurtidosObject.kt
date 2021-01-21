package com.Aegina.PocketSale.Objets.Surtido

import com.Aegina.PocketSale.Objets.VentasObjeto
import java.io.Serializable

data class ListSurtidosObject(
    val supplies:List<VentasObjeto>,
    val count: Int
): Serializable
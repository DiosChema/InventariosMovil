package com.Aegina.PocketSale.Objets

import java.io.Serializable

data class EstadisticaInventarioObject(
    var totalCostoVenta:Double,
    var totalCostoProovedor:Double
): Serializable
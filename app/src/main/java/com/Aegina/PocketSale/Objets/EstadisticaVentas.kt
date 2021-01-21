package com.Aegina.PocketSale.Objets

import java.io.Serializable

data class EstadisticaVentasObject(
    var totalCostoVenta:Double,
    var totalCostoProovedor:Double,
    var totalLoss:Double,
    var numeroVentas:Int
): Serializable
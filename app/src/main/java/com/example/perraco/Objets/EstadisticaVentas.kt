package com.example.perraco.Objets

import java.io.Serializable

data class EstadisticaVentasObject(
    var totalVentas:Double,
    var totalCostos:Double,
    var numeroVentas:Int
): Serializable
package com.Aegina.PocketSale.Objets

data class ActualizarVentana(
    var actualizarInventario: Boolean = false,
    var actualizarVentas: Boolean = false,
    var actualizarSurtido: Boolean = false,
    var actualizarEmpleados: Boolean = false,
    var updateLoss: Boolean = false
)
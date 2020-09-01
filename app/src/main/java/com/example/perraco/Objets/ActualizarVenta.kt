package com.example.perraco.Objets

import java.io.Serializable

data class ActualizarVenta(
    var tienda:String,
    var idFactura:Int,
    var articulos:List<ActualizarArticuloObjeto>
): Serializable
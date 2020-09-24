package com.example.perraco.Objets

import java.io.Serializable

data class ActualizarInventarioObject(
    var token:String,
    var articulos:List<InventarioObjeto>
): Serializable
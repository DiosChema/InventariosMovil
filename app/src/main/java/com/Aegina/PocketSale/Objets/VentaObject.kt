package com.Aegina.PocketSale.Objets

import java.io.Serializable

data class VentaObjeto(
    var token:String,
    var fecha:String,
    var articulos:List<ArticuloObjeto>
): Serializable
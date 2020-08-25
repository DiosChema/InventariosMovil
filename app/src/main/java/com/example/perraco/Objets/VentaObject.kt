package com.example.perraco.Objets

import com.example.perraco.Objets.ArticuloObjeto
import java.io.Serializable

data class VentaObjeto(
    var tienda:String,
    var fecha:String,
    var articulos:List<ArticuloObjeto>
): Serializable
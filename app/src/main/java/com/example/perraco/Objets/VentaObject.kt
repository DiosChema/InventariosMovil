package com.example.perraco.Objets

import com.example.perraco.Objets.ArticuloObjeto
import java.io.Serializable

data class VentaObjeto(
    var token:String,
    var fecha:String,
    var articulos:List<ArticuloObjeto>
): Serializable
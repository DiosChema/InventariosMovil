package com.Aegina.PocketSale.Objets.Loss

import com.Aegina.PocketSale.Objets.InventarioObjeto
import java.io.Serializable

data class UpdateLoss(
    var token:String,
    var idLoss:Int,
    var fecha:String,
    var articulos:List<InventarioObjeto>
): Serializable
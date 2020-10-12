package com.Aegina.PocketSale.Objets

import java.io.Serializable

data class FamiliasSubFamiliasObject(
    var familiaId:Int,
    var nombreFamilia:String,
    var SubFamilia:MutableList<SubFamiliaObjeto>
): Serializable
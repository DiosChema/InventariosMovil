package com.example.perraco.Objets

import java.io.Serializable

data class FamiliasSubFamiliasObject(
    var familiaId:Int,
    var nombreFamilia:String,
    var SubFamilia:List<SubFamiliaObjeto>
): Serializable
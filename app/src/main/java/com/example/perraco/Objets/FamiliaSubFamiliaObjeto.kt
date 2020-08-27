package com.example.perraco.Objets

import java.io.Serializable

data class FamiliaSubFamiliaObjeto(
    var familiaId:Int,
    var nombreFamilia:String,
    var subfamilias:List<SubFamiliaObjeto>
):Serializable
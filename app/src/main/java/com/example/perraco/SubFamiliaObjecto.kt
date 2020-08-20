package com.example.perraco

import java.io.Serializable

data class SubFamiliaObjeto(
    var subFamiliaId:Int,
    var nombreSubFamilia:String,
    var FamiliaId:Int
):Serializable
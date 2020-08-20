package com.example.perraco

import java.io.Serializable

data class Urls(
    var url:String,
    var endPointInventario:String,
    var endPointImagenes:String,
    var endpointObtenerFamilias:String,
    var endpointObtenerSubFamilias:String,
    var endpointObtenerSubFamilia:String,
    var endpointDarAltaArticulo:String,
    var endpointSubirImagen:String)
{
    constructor(): this(
        "https://tienditaplus.herokuapp.com/",
        "obtenerInventario",
        "images/kunikida_hanamaru.jpg",
        "consultarFamilias",
        "consultarSubFamilias",
        "consultarSubFamilia",
        "darAltaArticulo",
        "subirImagen")
}

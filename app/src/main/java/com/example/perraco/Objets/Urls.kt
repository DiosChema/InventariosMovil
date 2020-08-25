package com.example.perraco.Objets

import java.io.Serializable

data class Urls(
    var url:String,
    var endPointInventario:String,
    var endPointImagenes:String,
    var endpointObtenerFamilias:String,
    var endpointObtenerSubFamilias:String,
    var endpointObtenerSubFamilia:String,
    var endpointDarAltaArticulo:String,
    var endpointSubirImagen:String,
    var endPointArticulo:String,
    var endPointVenta:String,
    var endPointObtenerFacturas:String)
{
    constructor(): this(
        "https://tienditaplus.herokuapp.com/",
        "obtenerInventario",
        "imagen?image=",
        "consultarFamilias",
        "consultarSubFamilias",
        "consultarSubFamilia",
        "darAltaArticulo",
        "subirImagen",
        "obtenerArticuloInventario",
        "darAltaFactura",
        "obtenerFacturas")
}

package com.example.perraco.Objets

import com.google.gson.reflect.TypeToken

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
    var endPointObtenerFacturas:String,
    var endPointAgregarFamilia:String,
    var endPointAgregarSubFamilia:String,
    var endPointEliminarFamilia:String,
    var endPointEliminarSubFamilia:String,
    var endPointActualizarArticulo:String,
    var endPointEliminarArticulo:String,
    var endPointEliminarVenta:String,
    var endPointActualizarVenta:String,
    var endPointLoginUsuario:String,
    var endPointRegistrarNuevaTienda:String)
{
    constructor(): this(
        /*"https://tienditaplus.herokuapp.com/"*/
        "http://pvgestordeinventario-env.eba-p2bc44jy.us-east-1.elasticbeanstalk.com/",
        "obtenerInventario",
        "imagen?image=",
        "consultarFamilias",
        "consultarSubFamilias",
        "consultarSubFamilia",
        "darAltaArticulo",
        "subirImagen",
        "obtenerArticuloInventario",
        "darAltaFactura",
        "obtenerFacturas",
        "agregarFamilia",
        "agregarSubFamilia",
        "eliminarFamilia",
        "eliminarSubFamilia",
        "actualizarArticulo",
        "eliminarArticulo",
        "eliminarVenta",
        "actualizarVenta",
        "loginUsuario",
        "registrarNuevaTienda")

}

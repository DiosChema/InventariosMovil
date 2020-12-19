package com.Aegina.PocketSale.Objets

data class Urls(
    var url:String,
    var endPointsInventario:UrlsInventario,
    var endPointEstadisticas: UrlsEstadisticas,
    var endPointFamilias: UrlsFamilias,
    var endPointImagenes: UrlsImagenes,
    var endPointSurtidos: UrlsSurtidos,
    var endPointUsers: UrlsUsers,
    var endPointVentas: UrlsVentas
)
{
    constructor(): this(
        "https://tienditaplus.herokuapp.com/",
        /*"http://pvgestordeinventario-env.eba-p2bc44jy.us-east-1.elasticbeanstalk.com/",*/
        UrlsInventario(
            "inventario/darAltaArticulo",
            "inventario/eliminarArticulo",
            "inventario/actualizarArticulo",
            "inventario/obtenerInventario",
            "inventario/obtenerArticuloInventario",
            "inventario/buscarArticulosPorFamilia",
            "inventario/buscarArticulosPorSubFamilia"),
        UrlsEstadisticas(
            "estadisticas/buscarArticulosMasVendidos",
            "estadisticas/obtenerTotalInventario"),
        UrlsFamilias(
            "familias/consultarFamilias",
            "familias/consultarSubFamilia",
            "familias/consultarSubFamilias",
            "familias/consultarFamiliasSubFamilias",
            "familias/agregarFamilia",
            "familias/agregarSubFamilia",
            "familias/eliminarFamilia",
            "familias/eliminarSubFamilia"),
        UrlsImagenes(
            "imagenes/obtenerImagen?image=",
            "imagenes/subirImagen",
            "imagenes/eliminarImagen"),
        UrlsSurtidos(
            "surtido/darAltaSurtido",
            "surtido/buscarSurtido"),
        UrlsUsers(
            "users/loginUsuario",
            "users/registrarNuevaTienda",
            "users/actualizarFecha",
            "users/obtenerEmpleados",
            "users/darAltaEmpleado",
            "users/eliminarEmpleado",
            "users/actualizarEmpleado"),
        UrlsVentas(
            "ventas/actualizarVenta",
            "ventas/buscarVentaPorFecha",
            "ventas/darAltaVenta",
            "ventas/eliminarVenta",
            "ventas/obtenerVentas",
            "ventas/estadisticasPorFecha"
        )
    )
}

data class UrlsInventario(
    var endpointDarAltaArticulo:String,
    var endPointEliminarArticulo:String,
    var endPointActualizarArticulo:String,
    var endPointInventario:String,
    var endPointArticulo:String,
    var endPointArticulosPorFamilia:String,
    var endPointArticulosPorSubFamilia:String
)

data class UrlsEstadisticas(
    var endPointArticulosMasVendidos:String,
    var endPointEstadisticasInventario:String
)

data class UrlsFamilias(
    var endpointObtenerFamilias:String,
    var endpointObtenerSubFamilia:String,
    var endpointObtenerSubFamilias:String,
    var endPointConsultarFamiliasSubFamilias:String,
    var endPointAgregarFamilia:String,
    var endPointAgregarSubFamilia:String,
    var endPointEliminarFamilia:String,
    var endPointEliminarSubFamilia:String
)

data class UrlsImagenes(
    var endPointImagenes:String,
    var endpointSubirImagen:String,
    var endpointEliminarImagen:String
)

data class UrlsSurtidos(
    var endPointActualizarInventario:String,
    var endPointObtenerSurtidos:String
)

data class UrlsUsers(
    var endPointLoginUsuario:String,
    var endPointRegistrarNuevaTienda:String,
    var endPointActualizarFechaCompra:String,
    var endPointObtenerEmpleados:String,
    var endPointAltaEmpleado:String,
    var endPointEliminarEmpleado:String,
    var endPointActualizarEmpleado:String
)

data class UrlsVentas(
    var endPointActualizarVenta:String,
    var endPointBuscarVentaPorFecha:String,
    var endPointVenta:String,
    var endPointEliminarVenta:String,
    var endPointObtenerVentas:String,
    var endPointEstadisticasPorFecha:String
)
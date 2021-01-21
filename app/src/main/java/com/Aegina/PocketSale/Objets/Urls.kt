package com.Aegina.PocketSale.Objets

data class Urls(
    var url:String,
    var endPointsInventario:UrlsInventario,
    var endPointEstadisticas: UrlsEstadisticas,
    var endPointFamilias: UrlsFamilias,
    var endPointImagenes: UrlsImagenes,
    var endPointSurtidos: UrlsSurtidos,
    var endPointUsers: UrlsUsers,
    var endPointEmpleados: UrlsEmpleados,
    var endPointVentas: UrlsVentas,
    var endPointLoss: UrlsLoss
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
            "inventario/buscarArticulosPorSubFamilia",
            "inventario/obtenerArticuloNoVendidos"),
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
            "surtido/buscarSurtido",
            "surtido/eliminarSurtido",
            "surtido/actualizarSurtido"),
        UrlsUsers(
            "users/loginUsuario",
            "users/registrarNuevaTienda",
            "users/actualizarFecha",
            "users/obtenerDatosTienda",
            "users/actualizarDatosTienda",
            "users/cambiarContrasena",
            "users/recuperarContrasena",
            "users/obtenerDatosTiendaTokenEspecial",
            "users/obtenerTokenCompra"),
        UrlsEmpleados(
            "empleados/obtenerEmpleados",
            "empleados/darAltaEmpleado",
            "empleados/eliminarEmpleado",
            "empleados/actualizarEmpleado",
            "empleados/obtenerEmpleado"),
        UrlsVentas(
            "ventas/actualizarVenta",
            "ventas/buscarVentaPorFecha",
            "ventas/darAltaVenta",
            "ventas/eliminarVenta",
            "ventas/obtenerVentas",
            "ventas/estadisticasPorFecha"),
        UrlsLoss(
            "loss/createLoss",
            "loss/getLoss",
            "loss/removeLoss",
            "loss/updateLoss"
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
    var endPointArticulosPorSubFamilia:String,
    var endPointArticulosNoVendidos:String
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
    var endPointObtenerSurtidos:String,
    var endPointEliminarSurtido:String,
    var endPointActualizarSurtido:String
)

data class UrlsUsers(
    var endPointLoginUsuario:String,
    var endPointRegistrarNuevaTienda:String,
    var endPointActualizarFechaCompra:String,
    var endPointObtenerDatosTienda:String,
    var endPointActualizarDatosTienda:String,
    var endPointCambiarContrasena:String,
    var endPointRecuperarContrasena:String,
    var endPointObtenerDatosTiendaTokenEspecial:String,
    var endPointObtenerTokenCompra:String
)

data class UrlsEmpleados(
    var endPointObtenerEmpleados:String,
    var endPointAltaEmpleado:String,
    var endPointEliminarEmpleado:String,
    var endPointActualizarEmpleado:String,
    var endPointObtenerEmpleado:String
)

data class UrlsVentas(
    var endPointActualizarVenta:String,
    var endPointBuscarVentaPorFecha:String,
    var endPointVenta:String,
    var endPointEliminarVenta:String,
    var endPointObtenerVentas:String,
    var endPointEstadisticasPorFecha:String
)

data class UrlsLoss(
    var endPointLossItems:String,
    var endPointGetLoss:String,
    var endPointRemoveLoss:String,
    var endPointUpdateLoss:String
)
package com.example.perraco

import java.io.Serializable

data class Urls(var url:String, var endPointInventario:String, var endPointImagenes:String)
{
    constructor(): this("http://192.168.1.76:4000/", "obtenerInventario", "images/kunikida_hanamaru.jpg")
}

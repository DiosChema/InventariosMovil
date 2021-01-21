package com.Aegina.PocketSale.Metodos

class Paginado {

    fun obtenerPaginadoMaximo(totalArticulos: Int, limiteArticulos: Int): Int
    {
        var maximoPaginas = totalArticulos / limiteArticulos -1
        if((totalArticulos % limiteArticulos) > 0)
        {
            maximoPaginas++
        }

        return maximoPaginas
    }
}
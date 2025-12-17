package com.tzilacatzin.rutasperrunas.model

data class Paseo(
    var id: String = "",
    val idDuenio: String = "",
    val idPaseador: String = "",
    val nombresMascotas: List<String> = emptyList(),
    val estado: String = "SOLICITADO",
    val codigoFin: String = "",
    val costoTotal: Double = 0.0,
    val latitud: Double = 0.0,
    val longitud: Double = 0.0
)
package com.es.aplicacion.dto

import java.util.Date

data class TareaDTO(
    val id: String? = null,
    val autor: String? = null,
    val objetivo: String? = null,
    val fecha: Date? = null,
    val completada: Boolean = false
)
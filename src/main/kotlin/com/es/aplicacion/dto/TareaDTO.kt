package com.es.aplicacion.dto

import java.util.Date

data class TareaDTO(
    val id: String? = null,
    val objetivo: String,
    val autor: String,
    val fecha: Date,
    val completada: Boolean
)
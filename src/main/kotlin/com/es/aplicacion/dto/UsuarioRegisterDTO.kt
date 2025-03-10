package com.es.aplicacion.dto

import com.es.aplicacion.model.Direccion

data class UsuarioRegisterDTO(
    val username: String,
    val email: String,
    val password: String,
    val passwordRepeat: String,
    val roles: String?,
    val direccion: Direccion?
)

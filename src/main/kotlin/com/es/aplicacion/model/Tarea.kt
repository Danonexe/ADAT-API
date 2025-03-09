package com.es.aplicacion.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document("Tareas")
data class Tarea (
    @Id
    val _id : String?,
    val autor : String?,
    val objetivo : String?,
    val fecha : Date?,
    val completada: Boolean = false
)
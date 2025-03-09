package com.es.aplicacion.service

import com.es.aplicacion.dto.TareaDTO
import com.es.aplicacion.error.exception.BadRequestException
import com.es.aplicacion.error.exception.UnauthorizedException
import com.es.aplicacion.model.Tarea
import com.es.aplicacion.repository.TareaRepository
import com.es.aplicacion.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.Date

@Service
class TareaService {

    @Autowired
    private lateinit var tareaRepository: TareaRepository

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    fun obtenerTareas(): List<TareaDTO> {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name
        val roles = authentication.authorities.map { it.authority }

        val tareas = if (roles.contains("ROLE_ADMIN")) {
            // Administrador puede ver todas las tareas
            tareaRepository.findAll()
        } else {
            // Usuario normal solo puede ver sus tareas
            tareaRepository.findByAutor(username)
        }

        return tareas.map { tarea ->
            TareaDTO(
                id = tarea._id,
                autor = tarea.autor,
                objetivo = tarea.objetivo,
                fecha = tarea.fecha,
                completada = tarea.completada
            )
        }
    }

    fun crearTarea(tareaDTO: TareaDTO): TareaDTO {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name
        val roles = authentication.authorities.map { it.authority }

        // Validación de campos
        if (tareaDTO.objetivo.isNullOrBlank()) {
            throw BadRequestException("El objetivo de la tarea no puede estar vacío")
        }

        // Si el autor es diferente al usuario autenticado, verificar que sea admin
        if (tareaDTO.autor != username && !roles.contains("ROLE_ADMIN")) {
            throw UnauthorizedException("No tienes permisos para crear tareas para otros usuarios")
        }

        // Verificar que el usuario para el que se crea la tarea existe
        val autorFinal = tareaDTO.autor ?: username
        usuarioRepository.findByUsername(autorFinal).orElseThrow {
            BadRequestException("El usuario $autorFinal no existe")
        }

        // Crear la tarea
        val tarea = Tarea(
            _id = null, // MongoDB generará el ID
            autor = autorFinal,
            objetivo = tareaDTO.objetivo,
            fecha = tareaDTO.fecha ?: Date(),
            completada = false // Por defecto, una tarea nueva no está completada
        )

        val tareaGuardada = tareaRepository.save(tarea)

        return TareaDTO(
            id = tareaGuardada._id,
            autor = tareaGuardada.autor,
            objetivo = tareaGuardada.objetivo,
            fecha = tareaGuardada.fecha,
            completada = tareaGuardada.completada
        )
    }


    fun marcarTareaComoCompletada(id: String): TareaDTO {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name
        val roles = authentication.authorities.map { it.authority }

        val tarea = tareaRepository.findById(id).orElseThrow {
            BadRequestException("Tarea no encontrada")
        }

        // Si es el propietario de la tarea o es admin
        if (tarea.autor != username && !roles.contains("ROLE_ADMIN")) {
            throw UnauthorizedException("No tienes permisos para modificar esta tarea")
        }

        val tareaCompletada = Tarea(
            _id = tarea._id,
            autor = tarea.autor,
            objetivo = tarea.objetivo,
            fecha = tarea.fecha,
            completada = true
        )

        val tareaActualizada = tareaRepository.save(tareaCompletada)

        return TareaDTO(
            id = tareaActualizada._id,
            autor = tareaActualizada.autor,
            objetivo = tareaActualizada.objetivo,
            fecha = tareaActualizada.fecha,
            completada = tareaActualizada.completada
        )
    }

    fun desmarcarTareaComoCompletada(id: String): TareaDTO {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name
        val roles = authentication.authorities.map { it.authority }

        val tarea = tareaRepository.findById(id).orElseThrow {
            BadRequestException("Tarea no encontrada")
        }



        val tareaNoCompletada = Tarea(
            _id = tarea._id,
            autor = tarea.autor,
            objetivo = tarea.objetivo,
            fecha = tarea.fecha,
            completada = false
        )

        val tareaActualizada = tareaRepository.save(tareaNoCompletada)

        return TareaDTO(
            id = tareaActualizada._id,
            autor = tareaActualizada.autor,
            objetivo = tareaActualizada.objetivo,
            fecha = tareaActualizada.fecha,
            completada = tareaActualizada.completada
        )
    }


    fun eliminarTarea(id: String) {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name
        val roles = authentication.authorities.map { it.authority }

        val tarea = tareaRepository.findById(id).orElseThrow {
            BadRequestException("Tarea no encontrada")
        }

        // Verificar permisos
        if (tarea.autor != username && !roles.contains("ROLE_ADMIN")) {
            throw UnauthorizedException("No tienes permisos para eliminar esta tarea")
        }

        tareaRepository.deleteById(id)
    }
}
package com.es.aplicacion.service

import com.es.aplicacion.dto.TareaDTO
import com.es.aplicacion.dto.TareaInsertarDTO
import com.es.aplicacion.error.exception.BadRequestException
import com.es.aplicacion.model.Tarea
import com.es.aplicacion.repository.TareaRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class TareaService {

    @Autowired
    private lateinit var tareaRepository: TareaRepository
    @Autowired
    private lateinit var usuarioService: UsuarioService

    fun getTareas(): List<Tarea> {
        val auth = SecurityContextHolder.getContext().authentication
        val usuario = auth.name
        val roles = auth.authorities.map(GrantedAuthority::getAuthority)

        return when {
            "ROLE_ADMIN" in roles -> {
                // Si es administrador, devuelve todas las tareas
                tareaRepository.findAll()
            }
            "ROLE_USER" in roles -> {
                // Si es usuario normal, devuelve solo sus tareas
                tareaRepository.findByAutor(usuario).orElse(emptyList())
            }
            else -> {
                // Si no tiene ninguno de estos roles, devuelve lista vacía
                emptyList()
            }
        }
    }

    fun getTareaById(id: String): Tarea? {
        return tareaRepository.findById(id).orElseThrow { ChangeSetPersister.NotFoundException() }
    }

    fun crearTarea(tarea: TareaInsertarDTO): TareaDTO {
        val auth = SecurityContextHolder.getContext().authentication
        val usuario = auth.name
        val rol = auth.authorities.map(GrantedAuthority::getAuthority)

        if (tarea.objetivo.isBlank()) {
            throw BadRequestException("El titulo no puede estar en blanco")
        }

        // Verificar permiso para crear tarea
        if ("ROLE_USER" in rol) {
            if (tarea.autor != usuario) {
                throw BadRequestException("El autor debe ser el usuario actual")
            }
        } else {
            usuarioService.buscarUsuario(tarea.autor)
        }

        val tareaEntity = Tarea(
            _id = null,
            autor = tarea.autor,
            objetivo = tarea.objetivo,
            fecha = Date.from(Instant.now()),
            false
        )

        val savedTarea = tareaRepository.save(tareaEntity)

        return TareaDTO(
            id = savedTarea._id,
            objetivo = savedTarea.objetivo.toString(),
            autor = savedTarea.autor.toString(),
            fecha = savedTarea.fecha,
            completada = savedTarea.completada
        )
    }


    fun deleteTarea(id: String) {
        val tarea = getTareaById(id) ?: throw ChangeSetPersister.NotFoundException()
        val auth = SecurityContextHolder.getContext().authentication
        val usuario = auth.name
        val rol = auth.authorities.map(GrantedAuthority::getAuthority)

        when {
            "ROLE_ADMIN" in rol -> {
                tareaRepository.deleteById(id)
            }
            "ROLE_USER" in rol && tarea.autor == usuario -> {
                tareaRepository.deleteById(id)
            }
            else -> throw BadRequestException("No tiene permiso para borrar esa tarea")
        }
    }

    fun completarTarea(id: String): TareaDTO {
        val tarea = getTareaById(id) ?: throw ChangeSetPersister.NotFoundException()
        val auth = SecurityContextHolder.getContext().authentication
        val usuario = auth.name
        val rol = auth.authorities.map(GrantedAuthority::getAuthority)

        // Verificar permisos: solo el propietario o admin pueden completar la tarea
        when {
            "ROLE_ADMIN" in rol -> {
            }
            "ROLE_USER" in rol && tarea.autor == usuario -> {
            }
            else -> throw BadRequestException("No tiene permiso para completar esta tarea")
        }

        // Solo actualizar si no está ya completada
        if (tarea.completada) {
            throw BadRequestException("tarea ya completada")
        }

        // Crear una copia de la tarea con el estado completado
        val tareaCompletada = Tarea(
            _id = tarea._id,
            autor = tarea.autor,
            objetivo = tarea.objetivo,
            fecha = tarea.fecha,
            completada = true
        )

        tareaRepository.save(tareaCompletada)
        val TareaDTO = TareaDTO(tarea._id, tarea.autor.toString(), tarea.objetivo.toString(),tarea.fecha,tarea.completada)
        return TareaDTO
    }

    fun descompletarTarea(id: String): TareaDTO {
        val tarea = getTareaById(id) ?: throw ChangeSetPersister.NotFoundException()
        val auth = SecurityContextHolder.getContext().authentication
        val usuario = auth.name
        val rol = auth.authorities.map(GrantedAuthority::getAuthority)

        // Verificar permisos: solo el propietario o admin pueden descompletar la tarea
        when {
            "ROLE_ADMIN" in rol -> {
                // Los administradores pueden descompletar cualquier tarea
            }
            "ROLE_USER" in rol && tarea.autor == usuario -> {
                // El propietario puede descompletar su propia tarea
            }
            else -> throw BadRequestException("No tiene permiso para descompletar esta tarea")
        }

        // Solo actualizar si ya está completada
        if (!tarea.completada) {
            throw BadRequestException("La tarea ya está marcada como pendiente")
        }

        // Crear una copia de la tarea con el estado no completado
        val tareaPendiente = Tarea(
            _id = tarea._id,
            autor = tarea.autor,
            objetivo = tarea.objetivo,
            fecha = tarea.fecha,
            completada = false
        )
        val tareaActualizada = tareaRepository.save(tareaPendiente)

        return TareaDTO(
            id = tareaActualizada._id,
            autor = tareaActualizada.autor.toString(),
            objetivo = tareaActualizada.objetivo.toString(),
            fecha = tareaActualizada.fecha,
            completada = tareaActualizada.completada
        )
    }

}
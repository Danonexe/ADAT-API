package com.es.aplicacion.controller

import com.es.aplicacion.dto.TareaDTO
import com.es.aplicacion.service.TareaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tareas")
class TareaController {

    @Autowired
    private lateinit var tareaService: TareaService

    @GetMapping
    fun obtenerTareas(): ResponseEntity<List<TareaDTO>> {
        val tareas = tareaService.obtenerTareas()
        return ResponseEntity(tareas, HttpStatus.OK)
    }

    @PostMapping
    fun crearTarea(@RequestBody tareaDTO: TareaDTO): ResponseEntity<TareaDTO> {
        val tareaNueva = tareaService.crearTarea(tareaDTO)
        return ResponseEntity(tareaNueva, HttpStatus.CREATED)
    }

    @PutMapping("/{id}/completar")
    fun marcarTareaComoCompletada(@PathVariable id: String): ResponseEntity<TareaDTO> {
        val tarea = tareaService.marcarTareaComoCompletada(id)
        return ResponseEntity(tarea, HttpStatus.OK)
    }

    @PutMapping("/{id}/descompletar")
    fun desmarcarTareaComoCompletada(@PathVariable id: String): ResponseEntity<TareaDTO> {
        val tarea = tareaService.desmarcarTareaComoCompletada(id)
        return ResponseEntity(tarea, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun eliminarTarea(@PathVariable id: String): ResponseEntity<Void> {
        tareaService.eliminarTarea(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
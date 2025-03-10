package com.es.aplicacion.controller

import com.es.aplicacion.dto.TareaDTO
import com.es.aplicacion.dto.TareaInsertarDTO
import com.es.aplicacion.model.Tarea
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
    fun obtenerTareas(): ResponseEntity<List<Tarea>> {
        val tareas = tareaService.getTareas()
        return ResponseEntity(tareas, HttpStatus.OK)
    }

    @PostMapping
    fun crearTarea(@RequestBody TareaInsertarDTO:TareaInsertarDTO): ResponseEntity<TareaDTO> {
        val tareaNueva = tareaService.crearTarea(TareaInsertarDTO)
        return ResponseEntity(tareaNueva, HttpStatus.CREATED)
    }

    @PutMapping("/{id}/completar")
    fun marcarTareaComoCompletada(@PathVariable id: String): ResponseEntity<TareaDTO> {
        val tarea = tareaService.completarTarea(id)
        return ResponseEntity(tarea, HttpStatus.OK)
    }

    @PutMapping("/{id}/descompletar")
    fun desmarcarTareaComoCompletada(@PathVariable id: String): ResponseEntity<TareaDTO> {
        val tarea = tareaService.descompletarTarea(id)
        return ResponseEntity(tarea, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun eliminarTarea(@PathVariable id: String): ResponseEntity<Void> {
        tareaService.deleteTarea(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
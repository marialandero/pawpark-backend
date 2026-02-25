package com.pawpark.backend.controller;

import com.pawpark.backend.model.Mascota;
import com.pawpark.backend.service.MascotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mascotas")
@CrossOrigin(origins = "*") // <-- ORIGEN para habilitar la conexión desde el frontend (Flutter)
@Tag(name = "Mascotas", description = "Operaciones relacionadas con las mascotas")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    @GetMapping
    @Operation(summary = "Listas todas las mascotas", description = "Devuelve la lista completa de mascotas")
    public List<Mascota> listarMascotas() {
        return  mascotaService.listarMascotas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una mascota", description = "Devuelve una mascota según su ID")
    public Mascota obtenerMascota(@PathVariable Long id) {
        return  mascotaService.obtenerMascota(id);
    }

    @PostMapping
    @Operation(summary = "Crear una mascota", description = "Crea una nueva mascota en la base de datos")
    public Mascota crearMascota(@RequestBody Mascota mascota) {
        return  mascotaService.crearMascota(mascota);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una mascota", description = "Actualiza los datos de una mascota existente")
    public Mascota actualizarMascota(@PathVariable Long id, @RequestBody Mascota datos) {
        return mascotaService.actualizarMascota(id, datos);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una mascota", description = "Elimina una mascota según su ID")
    public void eliminarMascota(@PathVariable Long id) {
        mascotaService.eliminarMascota(id);
    }
}

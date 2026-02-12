package com.pawpark.backend.controller;

import com.pawpark.backend.model.Foto;
import com.pawpark.backend.service.FotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fotos")
@Tag(name = "Fotos", description = "Operaciones relacionadas con las fotos")
public class FotoController {

    @Autowired
    private FotoService fotoService;

    @GetMapping
    @Operation(summary = "Listar todas las fotos", description = "Devuelve la lista completa de fotos")
    public List<Foto> listarFotos() {
        return fotoService.listarFotos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una foto", description = "Devuelve una foto según su ID")
    public Foto obtenerFoto(@PathVariable Long id) {
        return fotoService.obtenerFoto(id);
    }

    @PostMapping
    @Operation(summary = "Crear una foto", description = "Crea una nueva foto en la base de datos")
    public Foto crearFoto(@RequestBody Foto foto) {
        return fotoService.crearFoto(foto);
    }


}

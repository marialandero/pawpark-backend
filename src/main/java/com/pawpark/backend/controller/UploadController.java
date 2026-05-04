package com.pawpark.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Controlador encargado de la gestión de archivos multimedia.
 * Proporciona un punto centralizado para la subida de imágenes de perfil y posts.
 */
@RestController

@Tag(name = "Utilidades de Almacenamiento", description = "Endpoints para la carga de archivos multimedia al servidor")@CrossOrigin(origins = "*") /* <-- Habilita el acceso desde Flutter para el envío de archivos binarios */
public class UploadController {

    // Directorio raíz donde se almacenarán las imágenes físicamente
    private static final String UPLOAD_DIR = "uploads/";

    @PostMapping("/upload")
    @Operation(summary = "Subir imagen genérica", description = "Recibe un archivo binario, lo almacena localmente con un nombre único y devuelve dicho nombre para ser guardado en la base de datos.")
    public String uploadImagen(@RequestParam("file") MultipartFile file) {
        try {

            // Crear carpeta si no existe
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Genera un nombre único mediante timestamp (para evitar conflictos)
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // Definición de la ruta física en el sistema de archivos
            Path filePath = Paths.get(UPLOAD_DIR + fileName);

            // Transferencia de los bytes del archivo al almacenamiento local
            Files.copy(file.getInputStream(), filePath);

            // Retornamos solo el nombre del archivo para que el frontend lo asocie a una entidad (Usuario/Mascota)
            return fileName;

        } catch (IOException e) {
            throw new RuntimeException("Error subiendo imagen", e);
        }
    }
}

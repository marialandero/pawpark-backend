package com.pawpark.backend.controller;

import com.pawpark.backend.dto.PostResponse;
import com.pawpark.backend.model.Post;
import com.pawpark.backend.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "*") /* <-- ORIGEN habilitado para permitir la conexión desde Flutter.
Esencial para que el feed social cargue imágenes y datos sin restricciones de seguridad de red. */
@Tag(name = "Posts e Interacción Social", description = "Operaciones para gestionar el feed de publicaciones de usuarios y carga de contenido multimedia")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    @Operation(summary = "Crear una nueva publicación", description = "Registra un post en la base de datos vinculándolo a un usuario y, opcionalmente, a una mascota.")
    public PostResponse crearPost(@RequestBody Map<String, Object> payload) {
        /* Se utiliza un Map para extraer dinámicamente los datos (texto, imagen, uid)
           y se delega la construcción de la entidad al servicio. */
        return postService.crearPost(payload);
    }

    @GetMapping("/feed")
    @Operation(summary = "Obtener feed global", description = "Recupera todas las publicaciones del sistema en formato PostResponse para visualización en el muro principal.")
    public List<PostResponse> obtenerFeed() {
        return postService.getFeed();
    }

    @GetMapping("/usuario/{uid}")
    @Operation(summary = "Obtener posts de un usuario", description = "Filtra y devuelve las publicaciones realizadas por un dueño específico mediante su Firebase UID.")
    public List<PostResponse> obtenerPorUsuario(@PathVariable String uid) {
        return postService.getByUsuario(uid);
    }

    @GetMapping("/mascota/{id}")
    @Operation(summary = "Obtener posts de una mascota", description = "Recupera las publicaciones donde una mascota específica ha sido etiquetada.")
    public List<PostResponse> obtenerPorMascota(@PathVariable Long id) {
        return postService.getByMascota(id);
    }

    @PostMapping("/upload")
    @Operation(summary = "Subir imagen de publicación", description = "Procesa un archivo multimedia (MultipartFile) y retorna el nombre único generado para almacenarlo en la base de datos.")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        /* El controlador gestiona la entrada del flujo binario y el servicio
           se encarga de la persistencia física en el servidor o storage. */
        String nombreImagen = postService.guardarImagen(file);
        return ResponseEntity.ok(nombreImagen);
    }
}
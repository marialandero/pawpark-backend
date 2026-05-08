package com.pawpark.backend.controller;

import com.pawpark.backend.dto.PostResponse;
import com.pawpark.backend.dto.LikersResponse;
import com.pawpark.backend.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping("/{postId}/like/{usuarioUid}")
    @Operation(summary = "Alternar like (dar/quitar)")
    public ResponseEntity<Void> toggleLike(@PathVariable Long postId, @PathVariable String usuarioUid) {
        postService.toggleLike(postId, usuarioUid);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un post propio")
    public ResponseEntity<Void> eliminarPost(@PathVariable Long id) {
        postService.eliminarPost(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/feed/{usuarioUid}")
    @Operation(summary = "Obtener feed global", description = "Recupera todas las publicaciones del sistema en formato PostResponse para visualización en el muro principal.")
    public List<PostResponse> obtenerFeed(@PathVariable String usuarioUid) {
        return postService.getFeed(usuarioUid);
    }

    @GetMapping("/usuario/{uid}/{usuarioActualUid}")
    @Operation(summary = "Obtener posts de un usuario", description = "Filtra y devuelve las publicaciones realizadas por un dueño específico mediante su Firebase UID.")
    public List<PostResponse> obtenerPorUsuario(@PathVariable String uid, @PathVariable String usuarioActualUid) {
        return postService.getByUsuario(uid, usuarioActualUid);
    }

    @GetMapping("/mascota/{id}/{usuarioActualUid}")
    @Operation(summary = "Obtener posts de una mascota", description = "Recupera las publicaciones donde una mascota específica ha sido etiquetada.")
    public List<PostResponse> obtenerPorMascota(@PathVariable Long id, @PathVariable String usuarioActualUid) {
        return postService.getByMascota(id, usuarioActualUid);
    }

    @GetMapping("/{postId}/likers")
    @Operation(summary = "Obtener lista de usuarios que dieron like")
    public List<LikersResponse> obtenerLikers(@PathVariable Long postId) {
        return postService.obtenerUsuariosQueDieronLike(postId);
    }
}
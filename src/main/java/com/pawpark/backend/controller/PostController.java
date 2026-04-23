package com.pawpark.backend.controller;

import com.pawpark.backend.dto.PostResponse;
import com.pawpark.backend.model.Post;
import com.pawpark.backend.model.Usuario;
import com.pawpark.backend.model.Mascota;
import com.pawpark.backend.service.PostService;
import com.pawpark.backend.service.UsuarioService;
import com.pawpark.backend.service.MascotaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "*") /* <-- ORIGEN para habilitar la conexión desde el frontend (Flutter), es como la llave
de paso que permite que el flujo de datos entre el frontend y el backend esté abierto y sin restricciones de seguridad
de red durante las pruebas */
@Tag(name = "Posts", description = "Operaciones relacionadas con los posts")
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private MascotaService mascotaService;

    @PostMapping
    public Post crearPost(@RequestBody Map<String, Object> payload) {
        // Buscar autor por Firebase UID
        String firebaseUid = (String) payload.get("firebaseUidAutor");
        Usuario autor = usuarioService.buscarPorFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Buscar mascotas etiquetadas por sus IDs
        List<Integer> idsMascotas = (List<Integer>) payload.get("mascotasIds");
        List<Mascota> etiquetadas = idsMascotas.stream()
                .map(id -> mascotaService.obtenerMascota(id.longValue()))
                .collect(Collectors.toList());

        // Construir y guardar el Post
        Post post = Post.builder()
                .rutaImagen((String) payload.get("rutaImagen"))
                .descripcion((String) payload.get("descripcion"))
                .autor(autor)
                .mascotasEtiquetadas(etiquetadas)
                .build();

        return postService.crearPost(post);
    }

    @GetMapping("/feed")
    public List<PostResponse> obtenerFeed() {
        return postService.listarTodoParaFeed()
                .stream()
                .map(post -> PostResponse.builder()
                        .id(post.getId())
                        .rutaImagen(post.getRutaImagen())
                        .descripcion(post.getDescripcion())
                        .fechaCreacion(post.getFechaCreacion())
                        .autorNombre(post.getAutor().getNombre())
                        .autorUid(post.getAutor().getFirebaseUid())
                        .mascotasNombres(
                                post.getMascotasEtiquetadas()
                                        .stream()
                                        .map(m -> m.getNombre())
                                        .toList()
                        )
                        .build()
                )
                .toList();
    }

    @GetMapping("/usuario/{id}")
    public List<Post> obtenerPorUsuario(@PathVariable Long id) {
        return postService.listarPorUsuario(id);
    }

    @GetMapping("/mascota/{id}")
    public List<Post> obtenerPorMascota(@PathVariable Long id) {
        return postService.listarPorMascota(id);
    }
}

package com.pawpark.backend.service;

import com.pawpark.backend.dto.PostResponse;
import com.pawpark.backend.model.Mascota;
import com.pawpark.backend.model.Post;
import com.pawpark.backend.model.Usuario;
import com.pawpark.backend.repository.PostRepository;
import com.pawpark.backend.service.MascotaService;
import com.pawpark.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.io.IOException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private MascotaService mascotaService;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostResponse crearPost(Map<String, Object> payload) {

        String firebaseUid = (String) payload.get("firebaseUidAutor");

        Usuario autor = usuarioService.buscarPorFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Integer> idsMascotas = (List<Integer>) payload.get("mascotasIds");

        List<Mascota> mascotas = idsMascotas != null
                ? idsMascotas.stream()
                .map(id -> mascotaService.obtenerMascota(id.longValue()))
                .toList()
                : new ArrayList<>();

        Post post = Post.builder()
                .rutaImagen((String) payload.get("rutaImagen"))
                .descripcion((String) payload.get("descripcion"))
                .autor(autor)
                .mascotasEtiquetadas(mascotas)
                .fechaCreacion(LocalDateTime.now())
                .build();

        Post saved = postRepository.save(post);

        return mapToResponse(saved);
    }

    public List<PostResponse> getFeed() {
        return postRepository.findAllByOrderByFechaCreacionDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<PostResponse> getByUsuario(String uid) {
        return postRepository.findAllByOrderByFechaCreacionDesc()
                .stream()
                .filter(p -> p.getAutor().getFirebaseUid().equals(uid))
                .map(this::mapToResponse)
                .toList();
    }

    public List<PostResponse> getByMascota(Long id) {
        return postRepository.findByMascotasEtiquetadasIdOrderByFechaCreacionDesc(id)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private PostResponse mapToResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .rutaImagen(post.getRutaImagen())
                .descripcion(post.getDescripcion())
                .fechaCreacion(post.getFechaCreacion())
                .autorNombre(post.getAutor().getNombre())
                .autorUid(post.getAutor().getFirebaseUid())
                .autorFotoPerfil(post.getAutor().getFotoPerfil())
                .mascotasNombres(
                        post.getMascotasEtiquetadas()
                                .stream()
                                .map(Mascota::getNombre)
                                .toList()
                )
                .build();
    }

    public String guardarImagen(MultipartFile file) {
        try {
            // Creamos un nombre único para que no se machaquen fotos con el mismo nombre
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // Definimos la ruta de la carpeta "uploads"
            Path root = Paths.get("uploads");

            // Si la carpeta no existe, la creamos
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            // Copiamos el archivo a la carpeta
            Files.copy(file.getInputStream(), root.resolve(fileName));

            return fileName; // Devolvemos el nombre que se guardará en la DB
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar la imagen: " + e.getMessage());
        }
    }
}
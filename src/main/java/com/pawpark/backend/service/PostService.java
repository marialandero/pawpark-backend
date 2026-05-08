package com.pawpark.backend.service;

import com.pawpark.backend.dto.PostResponse;
import com.pawpark.backend.dto.LikersResponse;
import com.pawpark.backend.model.Mascota;
import com.pawpark.backend.model.Post;
import com.pawpark.backend.model.Usuario;
import com.pawpark.backend.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    @Transactional
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
        Post saved = postRepository.saveAndFlush(post);
        return mapToResponse(saved);
    }


    public List<PostResponse> getFeed(String usuarioActualUid) {
        return postRepository.findAllByOrderByFechaCreacionDesc()
                .stream()
                .map(post -> this.mapToResponse(post, usuarioActualUid))
                .toList();
    }


    public List<PostResponse> getByUsuario(String uid, String usuarioActualUid) {
        return postRepository.findAllByOrderByFechaCreacionDesc()
                .stream()
                .filter(p -> p.getAutor().getFirebaseUid().equals(uid))
                .map(post -> this.mapToResponse(post, usuarioActualUid))
                .toList();
    }


    public List<PostResponse> getByMascota(Long id, String usuarioActualUid) {
        return postRepository.findByMascotaId(id)
                .stream()
                .map(post -> this.mapToResponse(post, usuarioActualUid))
                .toList();
    }


    // ESTE ES EL QUE USA EL FEED (CON LIKES)
    private PostResponse mapToResponse(Post post, String usuarioActualUid) {
        // Aseguramos que la lista nunca sea nula para evitar errores
        List<String> likesList = post.getLikedByUids() != null ? post.getLikedByUids() : new ArrayList<>();
        return PostResponse.builder()
                .id(post.getId())
                .rutaImagen(post.getRutaImagen())
                .descripcion(post.getDescripcion())
                .fechaCreacion(post.getFechaCreacion())
                .autorNombre(post.getAutor().getNombre())
                .autorNickname(post.getAutor().getNickname())
                .autorUid(post.getAutor().getFirebaseUid())
                .autorFotoPerfil(post.getAutor().getFotoPerfil())
                .mascotasNombres(
                        post.getMascotasEtiquetadas() != null
                                ? post.getMascotasEtiquetadas().stream().map(Mascota::getNombre).toList()
                                : new ArrayList<>()
                )
                .likes(likesList.size())
                .liked(usuarioActualUid != null && likesList.contains(usuarioActualUid))
                .build();
    }


    // ESTE ES EL "ATAJO" PARA QUE NO DE ERROR EN crearPost
    private PostResponse mapToResponse(Post post) {
        // Simplemente llama al de arriba enviando 'null' en el usuario
        return mapToResponse(post, null);
    }


    @Transactional
    public void toggleLike(Long postId, String usuarioUid) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post no encontrado"));

        if (post.getLikedByUids().contains(usuarioUid)) {
            post.getLikedByUids().remove(usuarioUid);
        } else {
            post.getLikedByUids().add(usuarioUid);
        }
        postRepository.save(post);
    }


    @Transactional
    public void eliminarPost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new RuntimeException("El post no existe");
        }
        postRepository.deleteById(id);
    }


    public List<LikersResponse> obtenerUsuariosQueDieronLike(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post no encontrado"));
        // Convertimos la lista de UIDs en una lista de objetos Usuario con sus datos
        return post.getLikedByUids().stream()
                .map(uid -> usuarioService.buscarPorFirebaseUid(uid)
                        .map(u -> LikersResponse.builder()
                                .firebaseUid(u.getFirebaseUid())
                                .nombre(u.getNombre())
                                .nickname(u.getNickname())
                                .fotoPerfil(u.getFotoPerfil())
                                .build())
                        .orElse(null))
                .filter(u -> u != null) // Limpiamos por si algún usuario ya no existe
                .toList();
    }
}
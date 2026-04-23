package com.pawpark.backend.service;

import com.pawpark.backend.model.Post;
import com.pawpark.backend.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public Post crearPost(Post post) {
        post.setFechaCreacion(LocalDateTime.now());
        return postRepository.save(post);
    }

    public List<Post> listarTodoParaFeed() {
        return postRepository.findAllByOrderByFechaCreacionDesc();
    }

    public List<Post> listarPorUsuario(Long usuarioId) {
        return postRepository.findByAutorIdOrderByFechaCreacionDesc(usuarioId);
    }

    public List<Post> listarPorMascota(Long mascotaId) {
        return postRepository.findByMascotasEtiquetadasIdOrderByFechaCreacionDesc(mascotaId);
    }
}

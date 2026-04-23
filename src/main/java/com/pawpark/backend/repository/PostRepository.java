package com.pawpark.backend.repository;

import com.pawpark.backend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // Para ver las fotos en el perfil del Usuario
    List<Post> findByAutorIdOrderByFechaCreacionDesc(Long usuarioId);

    // Para ver las fotos donde sale una Mascota específica
    List<Post> findByMascotasEtiquetadasIdOrderByFechaCreacionDesc(Long mascotaId);

    // Para el Feed global
    List<Post> findAllByOrderByFechaCreacionDesc();
}

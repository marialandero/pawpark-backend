package com.pawpark.backend.repository;

import com.pawpark.backend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // Para ver las fotos en el perfil del Usuario
    List<Post> findByAutorIdOrderByFechaCreacionDesc(Long usuarioId);

    // Para ver las fotos donde sale una Mascota específica
    // List<Post> findByMascotasEtiquetadasIdOrderByFechaCreacionDesc(Long mascotaId);

    // Para el Feed global
    List<Post> findAllByOrderByFechaCreacionDesc();

    @Query("SELECT p FROM Post p JOIN p.mascotasEtiquetadas m WHERE m.id = :id")
    List<Post> findByMascotaId(@Param("id") Long id);
}

package com.pawpark.backend.repository;

import com.pawpark.backend.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // Para la pestaña de solo Mis posts
    List<Post> findByAutorIdInOrderByFechaCreacionDesc(List<Long> usuarioIds);

    // Para la pestaña de los posts de Seguidos
    // Buscamos posts donde el autor esté en la lista de "siguiendo" del usuario actual
    @Query("SELECT p FROM Post p WHERE p.autor IN " +
            "(SELECT u FROM Usuario u1 JOIN u1.siguiendo u WHERE u1.id = :usuarioId) " +
            "ORDER BY p.fechaCreacion DESC")
    List<Post> findPostsDeSeguidos(@Param("usuarioId") Long usuarioId);

    // Para la pestaña del Feed global
    List<Post> findAllByOrderByFechaCreacionDesc();

    // Para ver las fotos donde sale una Mascota específica
    @Query("SELECT p FROM Post p JOIN p.mascotasEtiquetadas m WHERE m.id = :id")
    List<Post> findByMascotaId(@Param("id") Long id);
}

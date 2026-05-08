package com.pawpark.backend.repository;
import com.pawpark.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByFirebaseUid(String firebaseUid);

    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT DISTINCT u FROM Usuario u " +
            "LEFT JOIN u.mascotas m " +
            "WHERE (LOWER(u.nombre) LIKE LOWER(concat('%', :query, '%')) " +
            "OR LOWER(u.nickname) LIKE LOWER(concat('%', :query, '%')) " +
            "OR LOWER(m.nombre) LIKE LOWER(concat('%', :query, '%'))) " +
            "ORDER BY " +
            "CASE WHEN :viewerUid IN (SELECT s.firebaseUid FROM u.seguidores s) THEN 0 ELSE 1 END, " +
            "u.nombre ASC")
    List<Usuario> buscarConPrioridad(@Param("query") String query, @Param("viewerUid") String viewerUid);
}

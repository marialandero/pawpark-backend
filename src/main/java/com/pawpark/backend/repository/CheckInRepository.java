package com.pawpark.backend.repository;

import com.pawpark.backend.model.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    // Cuenta cuántos perritos hay en una zona cuya estancia no ha caducado
    long countByZonaOsmIdAndFechaExpiracionAfter(String osmId, LocalDateTime ahora);

    // Busca el check-in activo de un usuario (por si quiere salir manualmente)
    List<CheckIn> findAllByUsuarioFirebaseUidAndFechaExpiracionAfter(String uid, LocalDateTime ahora);

    // Lista todos los check-ins activos de una zona para ver quiénes están
    List<CheckIn> findByZonaOsmIdAndFechaExpiracionAfter(String osmId, LocalDateTime ahora);
}

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

    // Esta es la consulta clave para el mapa:
    // Busca check-ins de una zona específica que aún no hayan caducado
    List<CheckIn> findAllByZonaOsmIdAndFechaExpiracionAfter(String osmId, LocalDateTime ahora);
}

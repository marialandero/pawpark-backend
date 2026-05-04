package com.pawpark.backend.repository;

import com.pawpark.backend.model.Zona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ZonaRepository extends JpaRepository<Zona, Long> {
    // Busca una zona por su ID de OpenStreetMap para no duplicarla
    Optional<Zona> findByOsmId(String osmId);
}

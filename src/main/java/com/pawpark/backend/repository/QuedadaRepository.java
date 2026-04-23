package com.pawpark.backend.repository;

import com.pawpark.backend.model.Quedada;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuedadaRepository extends JpaRepository<Quedada, Long> {

    // Buscar quedadas organizadas por un usuario concreto
    List<Quedada> findByCreadorFirebaseUid(String firebaseUid);

    // Buscar todas las quedadas ordenadas por la más próxima en el tiempo
    List<Quedada> findAllByOrderByFechaHoraAsc();
}

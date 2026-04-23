package com.pawpark.backend.repository;

import com.pawpark.backend.model.EstanciaParque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstanciaParqueRepository extends JpaRepository<EstanciaParque, Long> {
    List<EstanciaParque> findByParqueId(Long parqueId);
    List<EstanciaParque> findByMascotaId(Long mascotaId);
    // Para ver quién está actualmente en un parque específico
    List<EstanciaParque> findByParqueIdAndFechaSalidaIsNull(Long parqueId);
}

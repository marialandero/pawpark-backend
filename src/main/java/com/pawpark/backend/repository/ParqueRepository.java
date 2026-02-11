package com.pawpark.backend.repository;

import com.pawpark.backend.model.Parque;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParqueRepository extends JpaRepository<Parque, Long> {}

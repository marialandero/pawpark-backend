package com.pawpark.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un lugar físico (parque, plaza, pipicán) en el mapa.
 * Los datos se sincronizan dinámicamente con OpenStreetMap (OSM).
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Zona {

    // ID único proporcionado por OpenStreetMap. Evita duplicidad de zonas.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String osmId;

    private String nombre;
    private double latitud;
    private double longitud;

    // Tipo de lugar según OSM (ej: "park", "townsquare") para iconos personalizados.
    private String tipo;

    // Relación con los check-ins actuales para conocer la ocupación en tiempo real.
    @OneToMany(mappedBy = "zona", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<CheckIn> checkInsActivos = new ArrayList<>();
}

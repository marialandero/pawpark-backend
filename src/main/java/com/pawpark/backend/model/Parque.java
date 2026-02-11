package com.pawpark.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private double latitud;
    private double longitud;
    private String localidad;

    @OneToMany(mappedBy = "parque", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EstanciaParque> mascotasPresentes = new ArrayList<>();
}

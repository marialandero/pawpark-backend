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
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String raza;
    private int edad;
    private String descripcion;
    private String foto;

    @Enumerated(EnumType.STRING)
    private Comportamiento comportamiento;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario dueno;

    // Amigos favoritos de la mascota
    @ManyToMany
    @JoinTable(
            name = "amistad_mascota",
            joinColumns = @JoinColumn(name = "mascota_id"),
            inverseJoinColumns = @JoinColumn(name = "amigo_id")
    )
    private List<Mascota> amigosFavoritos = new ArrayList<>();
}

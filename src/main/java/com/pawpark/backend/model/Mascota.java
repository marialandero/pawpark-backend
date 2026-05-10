package com.pawpark.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private int edad;
    private String descripcion;

    @Column(length = 500) // Ampliamos para la URL de Firebase
    private String fotoPerfilMascota;

    private String duenoFirebaseUid;

    @Enumerated(EnumType.STRING)
    private Raza raza;

    @ElementCollection(targetClass = Comportamiento.class)
    @CollectionTable(name = "mascota_comportamientos", joinColumns = @JoinColumn(name = "mascota_id"))
    @Column(name = "comportamiento")
    @Enumerated(EnumType.STRING)
    @Builder.Default // Necesario para que Lombok no ignore la inicialización al usar el Builder
    private List<Comportamiento> comportamientos = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnoreProperties("mascotas")
    private Usuario dueno;

    // Amigos favoritos de la mascota
    @ManyToMany
    @JsonIgnoreProperties({"amigosFavoritos", "checkIns", "dueno"})
    @JoinTable(name = "amistad_mascota",
            joinColumns = @JoinColumn(name = "mascota_id"),
            inverseJoinColumns = @JoinColumn(name = "amigo_id")
    )
    private List<Mascota> amigosFavoritos = new ArrayList<>();

    // Esto asegura que si borro al perro, sus check-ins en el mapa se borren solos
    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<CheckIn> checkIns = new ArrayList<>();
}

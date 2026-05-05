package com.pawpark.backend.model;

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
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Para vincular el login con la autenticación de Firebase
    @Column(unique = true)
    private String firebaseUid;

    private String nombre;
    private String nickname;
    private String email;
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private String fotoPerfil;
    private String localidad;
    private String memberSince;
    private int encountersCount;

    // Mascotas propias del usuario
    @OneToMany(mappedBy = "dueno", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("dueno")
    private List<Mascota> mascotas = new ArrayList<>();

    // Relación obtener la lista de publicaciones
    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("autor")
    private List<Post> posts = new ArrayList<>();

    // Sistema de Seguidores (Sustituye a "Amigos")
    // Esta lista representa a quién sigue este usuario (unidireccional)
    @ManyToMany
    @JoinTable(
            name = "usuario_siguiendo",
            joinColumns = @JoinColumn(name = "seguidor_id"),
            inverseJoinColumns = @JoinColumn(name = "seguido_id")
    )
    @JsonIgnoreProperties({"siguiendo", "seguidores", "mascotas", "mascotasFavoritas"})
    private List<Usuario> siguiendo = new ArrayList<>();

    @ManyToMany(mappedBy = "siguiendo")
    @JsonIgnoreProperties({"siguiendo", "seguidores", "mascotas", "mascotasFavoritas"})
    private List<Usuario> seguidores = new ArrayList<>();

    // Sistema de Mascotas Favoritas
    // Permite al usuario marcar perros específicos para darles prioridad en el mapa[cite: 1]
    @ManyToMany
    @JoinTable(
            name = "usuario_mascotas_favoritas",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "mascota_id")
    )
    @JsonIgnoreProperties("dueno")
    private List<Mascota> mascotasFavoritas = new ArrayList<>();
}

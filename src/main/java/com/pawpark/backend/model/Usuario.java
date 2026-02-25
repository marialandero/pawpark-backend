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

    private String fotoPerfil;
    private String localidad;
    private String memberSince;
    private int encountersCount;

    // Mascotas del usuario
    @OneToMany(mappedBy = "dueno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mascota> mascotas = new ArrayList<>();

    // Amigos (otros usuarios)
    @ManyToMany
    @JoinTable(
            name = "amistad_usuario",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "amigo_id")
    )
    private List<Usuario> amigos = new ArrayList<>();
}

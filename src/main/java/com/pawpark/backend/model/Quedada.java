package com.pawpark.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quedada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(length = 500)
    private String descripcion;

    private LocalDateTime fechaHora; // Fecha y hora del evento

    private String lugarNombre; // Nombre del parque o punto de encuentro

    // Dueño de la quedada (quién la creó)
    @ManyToOne
    @JoinColumn(name = "creador_id")
    private Usuario creador;

    // Lista de HUMANOS que han confirmado asistencia
    @ManyToMany
    @JoinTable(
            name = "quedada_usuarios_asistentes",
            joinColumns = @JoinColumn(name = "quedada_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<Usuario> usuariosAsistentes = new ArrayList<>();

    // Lista de PERROS que han sido seleccionados para ir
    @ManyToMany
    @JoinTable(
            name = "quedada_perros_asistentes",
            joinColumns = @JoinColumn(name = "quedada_id"),
            inverseJoinColumns = @JoinColumn(name = "mascota_id")
    )
    private List<Mascota> perrosAsistentes = new ArrayList<>();
}

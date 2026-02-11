package com.pawpark.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Foto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private LocalDateTime fechaSubida;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario dueño;

    @ManyToMany
    @JoinTable(
            name = "etiqueta_foto",
            joinColumns = @JoinColumn(name = "foto_id"),
            inverseJoinColumns = @JoinColumn(name = "mascota_id")
    )
    private List<Mascota> mascotasEtiquetadas = new ArrayList<>();
}

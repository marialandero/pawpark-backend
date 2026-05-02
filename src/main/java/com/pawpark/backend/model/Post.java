package com.pawpark.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rutaImagen; // URL de Firebase Storage o ruta local

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private LocalDateTime fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnoreProperties({"mascotas", "amigos"}) // Evitamos bucles infinitos en el JSON
    private Usuario autor;

    // Relación clave: Mascotas que aparecen en la foto
    @ManyToMany
    @JoinTable(
            name = "post_mascotas_etiquetadas",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "mascota_id")
    )
    @JsonIgnoreProperties({"dueno", "posts"})
    private List<Mascota> mascotasEtiquetadas = new ArrayList<>();

    
}

package com.pawpark.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostResponse {

    private Long id;
    private String rutaImagen;
    private String descripcion;
    private LocalDateTime fechaCreacion;

    private String autorNombre;
    private String autorNickname;
    private String autorUid;
    private String autorFotoPerfil;

    private List<String> mascotasNombres;

    private int likes;
    private boolean liked;
}
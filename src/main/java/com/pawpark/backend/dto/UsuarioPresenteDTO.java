package com.pawpark.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioPresenteDTO {
    private String usuario;
    private String fotoPerfil;
    private String firebaseUid;
    private List<String> mascotas;
}
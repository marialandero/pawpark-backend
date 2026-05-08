package com.pawpark.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikersResponse {
    private String firebaseUid;
    private String nombre;
    private String nickname;
    private String fotoPerfil;
}

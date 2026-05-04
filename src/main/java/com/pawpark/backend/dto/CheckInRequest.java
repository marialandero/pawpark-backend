package com.pawpark.backend.dto;

import lombok.Data;

/**
 * Solicitud para registrar la presencia de un binomio Usuario-Mascota en una zona.
 */
@Data
public class CheckInRequest {

    // UID de Firebase para identificar al dueño que realiza la acción.
    private String uid;

    // ID de la mascota específica que acompaña al usuario.
    private Long mascotaId;

    // Referencia de la zona de OpenStreetMap donde se encuentran físicamente.
    private String osmId;
}

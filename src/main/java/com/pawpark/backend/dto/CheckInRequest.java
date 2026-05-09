package com.pawpark.backend.dto;

import lombok.Data;

import java.util.List;

/**
 * Solicitud para registrar la presencia de un binomio Usuario-Mascota en una zona.
 */
@Data
public class CheckInRequest {

    // UID de Firebase para identificar al dueño que realiza la acción.
    private String uid;

    // IDs de las mascotas que acompañan al usuario.
    private List<Long> mascotasIds;

    // Referencia de la zona de OpenStreetMap donde se encuentran físicamente.
    private String osmId;
}

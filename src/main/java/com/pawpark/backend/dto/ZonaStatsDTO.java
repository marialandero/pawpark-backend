package com.pawpark.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objeto de respuesta que combina datos geográficos con estadísticas de ocupación.
 * Permite al frontend renderizar los indicadores numéricos (burbujas) sobre el mapa.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZonaStatsDTO {

    private String osmId;
    private String nombre;
    private double latitud;
    private double longitud;

    // Cantidad de mascotas con un Check-In activo (no expirado) en esta zona.
    private long perrosPresentes;
}

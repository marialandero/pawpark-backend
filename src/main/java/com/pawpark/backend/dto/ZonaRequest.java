package com.pawpark.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objeto de transferencia para recibir datos geográficos desde el frontend.
 * Se utiliza para la sincronización de puntos de interés detectados vía OpenStreetMap.
 */
@Data
@Builder
@NoArgsConstructor // <--- FUNDAMENTAL para Jackson
@AllArgsConstructor
public class ZonaRequest {

    // ID único de OpenStreetMap (ej: "way/1234567"). Es la clave de sincronización.
    private String osmId;

    private String nombre;
    private double latitud;
    private double longitud;

    // Categoría del lugar (ej: "park", "townsquare") para aplicar lógica visual en el mapa.
    private String tipo;
}
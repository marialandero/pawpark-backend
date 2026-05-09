package com.pawpark.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZonaSincroRequest {
    private String uid; // El usuario que está mirando el mapa
    private List<ZonaRequest> zonas; // La lista de zonas que ha encontrado OSM
}
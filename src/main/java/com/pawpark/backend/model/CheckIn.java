package com.pawpark.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el usuario que hace el check-in
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "mascota_id")
    private Mascota mascota;

    @ManyToOne
    @JoinColumn(name = "zona_id")
    private Zona zona;

    private LocalDateTime fechaEntrada;

    /**
     * Sustituimos fechaSalida por fechaExpiracion.
     * Esto permite que el backend "limpie" automáticamente el mapa
     * sin que el usuario tenga que marcar manualmente que se ha ido.
     */
    private LocalDateTime fechaExpiracion;
}

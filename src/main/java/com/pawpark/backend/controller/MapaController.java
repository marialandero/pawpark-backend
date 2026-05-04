package com.pawpark.backend.controller;

import com.pawpark.backend.dto.CheckInRequest;
import com.pawpark.backend.dto.ZonaRequest;
import com.pawpark.backend.dto.ZonaStatsDTO;
import com.pawpark.backend.service.MapaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mapa")
@CrossOrigin(origins = "*") /* <-- ORIGEN habilitado para permitir la comunicación con Flutter.
Al igual que en Mascotas, esto elimina las restricciones de CORS durante el desarrollo y pruebas
permitiendo que el mapa cargue los datos de ocupación sin bloqueos de seguridad */
@Tag(name = "Mapa y Presencia", description = "Operaciones relacionadas con la geolocalización, sincronización de zonas y check-ins")
public class MapaController {

    @Autowired
    private MapaService mapaService;

    @PostMapping("/sincronizar")
    @Operation(summary = "Sincronizar zonas del mapa", description = "Recibe las zonas detectadas por el frontend y devuelve el conteo de mascotas en tiempo real para cada una")
    public ResponseEntity<List<ZonaStatsDTO>> sincronizar(@RequestBody List<ZonaRequest> zonas) {
        /*
           Este método es el corazón del mapa. Recibe una lista de lo que el usuario ve en su pantalla
           y el backend decide si debe registrar nuevas zonas o simplemente contar cuántos perritos
           hay en las existentes.
        */
        return ResponseEntity.ok(mapaService.sincronizarZonas(zonas));
    }

    @PostMapping("/checkin")
    @Operation(summary = "Realizar Check-in en una zona", description = "Registra que un usuario y su mascota están en un parque o plaza específica")
    public ResponseEntity<Void> hacerCheckIn(@RequestBody CheckInRequest request) {
        /*
           Vincula la mascota seleccionada por el usuario con la zona donde se encuentra.
           Automáticamente establece una fecha de expiración para que el mapa se mantenga limpio.
        */
        mapaService.registrarCheckIn(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/salida/{uid}")
    @Operation(summary = "Salida manual", description = "Finaliza la estancia del usuario en el mapa de forma inmediata")
    public ResponseEntity<Void> salir(@PathVariable String uid) {
        /*
           Permite que el usuario avise que se va del parque.
           Técnicamente, el sistema adelanta la expiración del check-in a "ahora mismo"
           para que la burbuja del mapa se actualice al instante.
        */
        mapaService.salidaManual(uid);
        return ResponseEntity.ok().build();
    }
}
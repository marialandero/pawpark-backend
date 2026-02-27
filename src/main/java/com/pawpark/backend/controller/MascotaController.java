package com.pawpark.backend.controller;

import com.pawpark.backend.model.Comportamiento;
import com.pawpark.backend.model.Mascota;
import com.pawpark.backend.model.Usuario;
import com.pawpark.backend.service.MascotaService;
import com.pawpark.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mascotas")
@CrossOrigin(origins = "*") // <-- ORIGEN para habilitar la conexión desde el frontend (Flutter)
@Tag(name = "Mascotas", description = "Operaciones relacionadas con las mascotas")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Crear una mascota vinculada a un usuario")
    public Mascota crearMascota(@RequestBody Map<String, Object> payload) {

        // 1. Extraemos el UID que viene de Flutter
        String firebaseUid = (String) payload.get("firebaseUidDueno");

        // 2. Buscamos al usuario real (Usamos UsuarioService y guardamos en Usuario)
        Usuario dueno = usuarioService.buscarPorFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con UID: " + firebaseUid));

        // 3. Creamos el objeto Mascota manualmente con los datos del JSON
        Mascota mascota = new Mascota();
        mascota.setNombre((String) payload.get("nombre"));
        mascota.setRaza((String) payload.get("raza"));
        mascota.setEdad((Integer) payload.get("edad"));
        mascota.setFoto((String) payload.get("foto"));

        // Convertimos el String del comportamiento al Enum
        String compString = (String) payload.get("comportamiento");
        mascota.setComportamiento(Comportamiento.valueOf(compString));

        // 4. ¡VINCULAMOS AL DUEÑO! (Esto es lo que te faltaba)
        mascota.setDueno(dueno);

        return mascotaService.crearMascota(mascota);
    }

    @GetMapping
    @Operation(summary = "Listas todas las mascotas", description = "Devuelve la lista completa de mascotas")
    public List<Mascota> listarMascotas() {
        return  mascotaService.listarMascotas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una mascota", description = "Devuelve una mascota según su ID")
    public Mascota obtenerMascota(@PathVariable Long id) {
        return  mascotaService.obtenerMascota(id);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una mascota", description = "Actualiza los datos de una mascota existente")
    public Mascota actualizarMascota(@PathVariable Long id, @RequestBody Mascota datos) {
        return mascotaService.actualizarMascota(id, datos);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una mascota", description = "Elimina una mascota según su ID")
    public void eliminarMascota(@PathVariable Long id) {
        mascotaService.eliminarMascota(id);
    }
}

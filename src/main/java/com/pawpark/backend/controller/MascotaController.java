package com.pawpark.backend.controller;

import com.pawpark.backend.model.Comportamiento;
import com.pawpark.backend.model.Mascota;
import com.pawpark.backend.model.Raza;
import com.pawpark.backend.model.Usuario;
import com.pawpark.backend.service.MascotaService;
import com.pawpark.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mascotas")
@CrossOrigin(origins = "*") /* <-- ORIGEN para habilitar la conexión desde el frontend (Flutter), es como la llave
de paso que permite que el flujo de datos entre el frontend y el backend esté abierto y sin restricciones de seguridad
de red durante las pruebas */
@Tag(name = "Mascotas", description = "Operaciones relacionadas con las mascotas")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Crear una mascota vinculada a un usuario")
    public Mascota crearMascota(@RequestBody Map<String, Object> payload) {

        // Extraemos el UID que viene de Flutter
        String firebaseUid = (String) payload.get("duenoFirebaseUid");

        // Buscamos al usuario real (Usamos UsuarioService y guardamos en Usuario)
        Usuario dueno = usuarioService.buscarPorFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con UID: " + firebaseUid));

        // Creamos el objeto Mascota manualmente con los datos del JSON
        Mascota mascota = new Mascota();
        mascota.setNombre((String) payload.get("nombre"));
        mascota.setEdad((Integer) payload.get("edad"));
        mascota.setFotoPerfilMascota((String) payload.get("fotoPerfilMascota"));
        mascota.setDuenoFirebaseUid(firebaseUid); // Guardamos el UID también como String para el frontend

        // Convertimos el String de la raza al Enum
        String razaString = (String) payload.get("raza");
        mascota.setRaza(Raza.valueOf(razaString));

        // Usamos lógica de 'comportamientos' en plural para coincidir con Flutter y el modelo
        List<String> compStrings = (List<String>) payload.get("comportamiento");
        if (compStrings != null) {
            List<Comportamiento> listaEnums = compStrings.stream()
                    .map(Comportamiento::valueOf)
                    .toList();
            mascota.setComportamientos(listaEnums);
        }

        // Vinculamos al dueño
        mascota.setDueno(dueno);

        return mascotaService.crearMascota(mascota);
    }

    @GetMapping
    @Operation(summary = "Listar todas las mascotas", description = "Devuelve la lista completa de mascotas")
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

    @PutMapping("/{id}/descripcion")
    @Operation(summary = "Actualizar solo la descripción", description = "Actualiza la descripción y devuelve la mascota completa")
    public ResponseEntity<Mascota> actualizarDescripcion(@PathVariable Long id, @RequestBody String nuevaDesc) {
        try {
            // Obtenemos la mascota actual usando el service
            Mascota mascota = mascotaService.obtenerMascota(id);
            // Modificamos el campo
            mascota.setDescripcion(nuevaDesc);
            // Guardamos los cambios usando el service
            // (El service ya devuelve la mascota guardada)
            Mascota actualizada = mascotaService.actualizarMascota(id, mascota);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una mascota", description = "Elimina una mascota según su ID")
    public void eliminarMascota(@PathVariable Long id) {
        mascotaService.eliminarMascota(id);
    }


}

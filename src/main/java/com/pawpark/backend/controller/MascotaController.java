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
@Tag(name = "Mascotas", description = "Operaciones de gestión de mascotas, incluyendo vinculación con dueños y gestión de perfiles")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Crear una mascota vinculada a un usuario")
    public Mascota crearMascota(@RequestBody Map<String, Object> payload) {

        // Extraemos el identificador único que viene de la sesión de Flutter
        String firebaseUid = (String) payload.get("duenoFirebaseUid");

        // Validación de existencia del dueño en la base de datos relacional
        Usuario dueno = usuarioService.buscarPorFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con UID: " + firebaseUid));

        // Mapeo manual del objeto Mascota con los datos del JSON para asegurar la integridad de los tipos de datos
        Mascota mascota = new Mascota();
        mascota.setNombre((String) payload.get("nombre"));
        mascota.setEdad((Integer) payload.get("edad"));
        mascota.setFotoPerfilMascota((String) payload.get("fotoPerfilMascota"));
        mascota.setDuenoFirebaseUid(firebaseUid); // Guardamos el UID también como String para el frontend

        // Conversión segura de tipos: String de la solicitud al tipo Raza (Enum)
        String razaString = (String) payload.get("raza");
        mascota.setRaza(Raza.valueOf(razaString));

        // Procesamiento de comportamientos: conversión de lista de Strings a lista de Enums
        List<String> compStrings = (List<String>) payload.get("comportamientos");
        if (compStrings != null) {
            List<Comportamiento> listaEnums = compStrings.stream()
                    .map(Comportamiento::valueOf)
                    .toList();
            mascota.setComportamientos(listaEnums);
        }

        // Establecimiento de la relación bidireccional en el modelo (vinculamos al dueño)
        mascota.setDueno(dueno);

        return mascotaService.crearMascota(mascota);
    }

    @GetMapping
    @Operation(summary = "Listar todas las mascotas", description = "Devuelve la lista completa de mascotas registradas")
    public List<Mascota> listarMascotas() {
        return mascotaService.listarMascotas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una mascota por ID", description = "Recupera la ficha completa de una mascota específica")
    public Mascota obtenerMascota(@PathVariable Long id) {
        return mascotaService.obtenerMascota(id);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una mascota", description = "Permite la modificación integral de los datos de una mascota")
    public Mascota actualizarMascota(@PathVariable Long id, @RequestBody Mascota datos) {
        return mascotaService.actualizarMascota(id, datos);
    }

    @PutMapping("/{id}/perfil")
    @Operation(summary = "Actualizar biografía y edad", description = "Endpoint para modificar la descripción y la edad de la mascota simultáneamente")
    public ResponseEntity<Mascota> actualizarPerfil(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            // Extraemos los datos del Map de forma segura
            String nuevaDesc = (String) body.get("descripcion");

            // La edad puede venir como Integer o Double dependiendo de la librería JSON,
            // lo convertimos a Integer de forma segura
            Integer nuevaEdad = null;
            if (body.get("edad") != null) {
                nuevaEdad = ((Number) body.get("edad")).intValue();
            }

            // Llamamos al nuevo método del servicio
            Mascota actualizada = mascotaService.actualizarPerfil(id, nuevaDesc, nuevaEdad);

            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null);
        }
    }



    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una mascota", description = "Elimina el registro de una mascota según su ID")
    public void eliminarMascota(@PathVariable Long id) {
        mascotaService.eliminarMascota(id);
    }

    @PutMapping("/{id}/foto")
    @Operation(summary = "Actualizar imagen de perfil", description = "Modifica la referencia de la fotografía de la mascota en el sistema")
    public ResponseEntity<Mascota> actualizarFotoMascota(@PathVariable Long id, @RequestBody Map<String, String> body) {

        try {
            Mascota mascota = mascotaService.obtenerMascota(id);
            String foto = body.get("fotoPerfilMascota");
            if (foto != null && !foto.isBlank()) {
                mascota.setFotoPerfilMascota(foto);
            }
            return ResponseEntity.ok(mascotaService.actualizarMascota(id, mascota));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
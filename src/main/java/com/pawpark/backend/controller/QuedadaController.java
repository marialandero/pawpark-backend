package com.pawpark.backend.controller;

import com.pawpark.backend.model.Quedada;
import com.pawpark.backend.model.Usuario;
import com.pawpark.backend.service.QuedadaService;
import com.pawpark.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/quedadas")
@CrossOrigin(origins = "*") /* <-- ORIGEN para habilitar la conexión desde el frontend (Flutter), es como la llave
de paso que permite que el flujo de datos entre el frontend y el backend esté abierto y sin restricciones de seguridad
de red durante las pruebas */
@Tag(name = "Quedadas", description = "Operaciones relacionadas con las quedadas que organizan los dueños de las mascotas")
public class QuedadaController {

    @Autowired
    private QuedadaService quedadaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Listar todas las quedadas", description = "Devuelve el listado completo de eventos programados en la plataforma.")
    public List<Quedada> listar() {
        return quedadaService.listarTodas();
    }

    @PostMapping
    @Operation(summary = "Organizar una quedada", description = "Crea un nuevo evento vinculando al creador y sus mascotas.")
    public Quedada crear(@RequestBody Map<String, Object> payload) {
        // Extraemos los datos básicos del mapa
        String creadorUid = (String) payload.get("creadorUid");
        String titulo = (String) payload.get("titulo");
        String descripcion = (String) payload.get("descripcion");
        String lugarNombre = (String) payload.get("lugarNombre");
        String fechaHoraStr = (String) payload.get("fechaHora");

        // Extraemos la lista de IDs de mascotas (opcional)
        List<Integer> mascotasIdsInt = (List<Integer>) payload.get("mascotasIds");
        List<Long> mascotasIds = (mascotasIdsInt != null)
                ? mascotasIdsInt.stream().map(Integer::longValue).toList()
                : new ArrayList<>();

        // Dejamos que el service haga todo el trabajo sucio
        return quedadaService.crearQuedadaCompleta(creadorUid, titulo, descripcion, lugarNombre, fechaHoraStr, mascotasIds);
    }

    @PostMapping("/{id}/unirse")
    @Operation(summary = "Apuntarse a una quedada", description = "Registra la asistencia de un usuario y sus mascotas seleccionadas a un evento específico.")
    public Quedada unirse(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        // Buscamos al usuario que desea unirse
        String firebaseUid = (String) payload.get("usuarioUid");
        Usuario usuario = usuarioService.buscarPorFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtenemos la lista de IDs de mascotas que el usuario seleccionó en Flutter
        List<Integer> mascotasIdsInt = (List<Integer>) payload.get("mascotasIds");
        // Convertimos los IDs de Integer (JSON) a Long (Java JPA) para la consulta en DB
        List<Long> mascotasIds = mascotasIdsInt.stream().map(Integer::longValue).toList();

        return quedadaService.unirse(id, usuario, mascotasIds);
    }

    @PostMapping("/{id}/desapuntarse")
    @Operation(summary = "Abandonar quedada", description = "Elimina al usuario y sus mascotas de la lista de asistentes de un evento.")
    public Quedada desapuntarse(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        String firebaseUid = (String) payload.get("usuarioUid");
        Usuario usuario = usuarioService.buscarPorFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return quedadaService.desapuntarse(id, usuario);
    }
}

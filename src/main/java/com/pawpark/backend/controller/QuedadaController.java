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
    @Operation(summary = "Organizar una quedada", description = "Crea un nuevo evento vinculando al creador y parseando la fecha enviada desde Flutter.")
    public Quedada crear(@RequestBody Map<String, Object> payload) {
        // Identificamos al creador mediante su UID de Firebase
        String firebaseUid = (String) payload.get("creadorUid");
        Usuario creador = usuarioService.buscarPorFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Construcción manual de la entidad Quedada para asegurar el mapeo de tipos
        Quedada quedada = new Quedada();
        quedada.setTitulo((String) payload.get("titulo"));
        quedada.setDescripcion((String) payload.get("descripcion"));
        quedada.setLugarNombre((String) payload.get("lugarNombre"));
        // La fecha se recibe en formato String ISO y se convierte a LocalDateTime de Java
        quedada.setFechaHora(LocalDateTime.parse((String) payload.get("fechaHora")));
        quedada.setCreador(creador);

        // Inicialización de la lista de asistentes para evitar NullPointerException
        if(quedada.getUsuariosAsistentes() == null) {
            quedada.setUsuariosAsistentes(new ArrayList<>());
        }
        // El creador es automáticamente el primer asistente
        quedada.getUsuariosAsistentes().add(creador);

        return quedadaService.procesarYCrearQuedada(quedada);
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

package com.pawpark.backend.controller;

import com.pawpark.backend.model.Quedada;
import com.pawpark.backend.model.Usuario;
import com.pawpark.backend.service.QuedadaService;
import com.pawpark.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
@Tag(name = "Quedadas", description = "Operaciones relacionadas con las quedadas")
public class QuedadaController {

    @Autowired
    private QuedadaService quedadaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<Quedada> listar() {
        return quedadaService.listarTodas();
    }

    @PostMapping
    public Quedada crear(@RequestBody Map<String, Object> payload) {
        // Extraemos al creador
        String firebaseUid = (String) payload.get("creadorUid");
        Usuario creador = usuarioService.buscarPorFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Mapeamos los datos
        Quedada quedada = new Quedada();
        quedada.setTitulo((String) payload.get("titulo"));
        quedada.setDescripcion((String) payload.get("descripcion"));
        quedada.setLugarNombre((String) payload.get("lugarNombre"));
        // La fecha vendrá como String ISO desde Flutter
        quedada.setFechaHora(LocalDateTime.parse((String) payload.get("fechaHora")));
        quedada.setCreador(creador);

        // Nos aseguramos de que la lista esté inicializada
        if(quedada.getUsuariosAsistentes() == null) {
            quedada.setUsuariosAsistentes(new ArrayList<>());
        }
        // El creador es automáticamente el primer asistente
        quedada.getUsuariosAsistentes().add(creador);

        return quedadaService.crearQuedada(quedada);
    }

    @PostMapping("/{id}/unirse")
    public Quedada unirse(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        // Obtenemos al humano por su UID
        String firebaseUid = (String) payload.get("usuarioUid");
        Usuario usuario = usuarioService.buscarPorFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Obtenemos la lista de IDs de mascotas que el usuario seleccionó en Flutter
        List<Integer> mascotasIdsInt = (List<Integer>) payload.get("mascotasIds");
        // Convertimos de Integer a Long (que es lo que usa Java para los IDs)
        List<Long> mascotasIds = mascotasIdsInt.stream().map(Integer::longValue).toList();

        return quedadaService.unirse(id, usuario, mascotasIds);
    }

    @PostMapping("/{id}/desapuntarse")
    public Quedada desapuntarse(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        String firebaseUid = (String) payload.get("usuarioUid");
        Usuario usuario = usuarioService.buscarPorFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return quedadaService.desapuntarse(id, usuario);
    }
}

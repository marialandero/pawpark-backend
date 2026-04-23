package com.pawpark.backend.controller;

import com.pawpark.backend.model.Usuario;
import com.pawpark.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*") /* <-- ORIGEN para habilitar la conexión desde el frontend (Flutter), es como la llave
de paso que permite que el flujo de datos entre el frontend y el backend esté abierto y sin restricciones de seguridad
de red durante las pruebas */
@Tag(name = "Usuarios", description = "Operaciones relacionadas con los usuarios") // <-- TAG
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Listar todos los usuarios", description = "Devuelve la lista completa de usuarios")
    public List<Usuario> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario", description = "Devuelve un usuario según su ID")
    public Usuario obtenerUsuario(@PathVariable Long id) {
        return usuarioService.obtenerUsuario(id);
    }

    @GetMapping("/firebase/{uid}")
    @Operation(summary = "Obtener por Firebase UID", description = "Busca los datos de MySQL usando el ID de Firebase")
    public ResponseEntity<Usuario> obtenerPorFirebaseUid(@PathVariable String uid) {
        // Este método sirve para que, al hacer login, Flutter pida los datos de este usuario
        return usuarioService.buscarPorFirebaseUid(uid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear un usuario", description = "Crea un nuevo usuario en la base de datos")
    public Usuario crearUsuario(@RequestBody Usuario usuario) {
        return usuarioService.crearUsuario(usuario);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un usuario", description = "Actualiza los datos de un usuario existente")
    public Usuario actualizarUsuario(@PathVariable Long id, @RequestBody Usuario datos) {
        return usuarioService.actualizarUsuario(id, datos);
    }

    @PutMapping("/firebase/{uid}")
    @Operation(summary = "Actualizar usuario por Firebase UID", description = "Busca al usuario por su UID de Firebase y actualiza sus datos")
    public ResponseEntity<Usuario> actualizarPorFirebaseUid(@PathVariable String uid, @RequestBody Usuario datos) {
        return usuarioService.buscarPorFirebaseUid(uid)
                .map(usuario -> {
                    // Llamamos al servicio de actualización usando el ID real encontrado
                    Usuario actualizado = usuarioService.actualizarUsuario(usuario.getId(), datos);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario", description = "Elimina un usuario según su ID")
    public void eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
    }
}

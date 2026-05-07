package com.pawpark.backend.controller;

import com.pawpark.backend.model.Usuario;
import com.pawpark.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*") /* <-- ORIGEN para habilitar la conexión desde el frontend (Flutter), es como la llave
de paso que permite que el flujo de datos entre el frontend y el backend esté abierto y sin restricciones de seguridad
de red durante las pruebas */
@Tag(name = "Usuarios", description = "Operaciones relacionadas con la gestión de perfiles de usuario y autenticación vinculada a Firebase")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Listar todos los usuarios", description = "Devuelve la lista completa de los usuarios registrados en el sistema")
    public List<Usuario> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario por ID", description = "Busca y devuelve los datos de un usuario mediante su clave primaria (ID)")
    public Usuario obtenerUsuario(@PathVariable Long id) {
        return usuarioService.obtenerUsuario(id);
    }

    @GetMapping("/firebase/{uid}")
    @Operation(summary = "Obtener por Firebase UID", description = "Endpoint crítico para el login: recupera el perfil de usuario de MySQL usando el identificador único de Firebase")
    public ResponseEntity<Usuario> obtenerPorFirebaseUid(@PathVariable String uid) {
        /* Este método sirve para que, al hacer login, Flutter pida los datos de este usuario, es decir
        * sincroniza la sesión de Firebase Auth con los datos de perfil de nuestra DB */
        return usuarioService.buscarPorFirebaseUid(uid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear un usuario", description = "Registra un nuevo usuario en el sistema")
    public Usuario crearUsuario(@RequestBody Usuario usuario) {
        return usuarioService.crearUsuario(usuario);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un usuario", description = "Modifica los datos de un perfil existente mediante su ID")
    public Usuario actualizarUsuario(@PathVariable Long id, @RequestBody Usuario datos) {
        return usuarioService.actualizarUsuario(id, datos);
    }

    @PutMapping("/firebase/{uid}")
    @Operation(summary = "Actualizar perfil por Firebase UID", description = "Permite al usuario editar su perfil identificándolo mediante su sesión activa de Firebase")
    public ResponseEntity<Usuario> actualizarPorFirebaseUid(@PathVariable String uid, @RequestBody Usuario datos) {
        return usuarioService.buscarPorFirebaseUid(uid)
                .map(usuario -> {
                    // Mapeamos el UID al ID interno de MySQL para realizar la persistencia
                    Usuario actualizado = usuarioService.actualizarUsuario(usuario.getId(), datos);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario", description = "Borra permanentemente el perfil de un usuario del sistema")
    public void eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
    }

    // BÚSQUEDA Y MULTIMEDIA

    @GetMapping("/buscar")
    @Operation(summary = "Buscar usuarios por nombre", description = "Permite buscar perfiles de otros dueños de mascotas mediante una consulta de texto")
    public List<Usuario> buscarUsuarios(@RequestParam String query) {
        return usuarioService.buscarPorNombre(query);
    }

    @PostMapping("/{seguidorUid}/seguir/{seguidoUid}")
    @Operation(summary = "Alternar seguimiento", description = "Permite a un usuario seguir o dejar de seguir a otro mediante sus Firebase UIDs")
    public ResponseEntity<Void> alternarSeguimiento(
            @PathVariable String seguidorUid,
            @PathVariable String seguidoUid) {
        usuarioService.alternarSeguimiento(seguidorUid, seguidoUid);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{uid}/favorito/{mascotaId}")
    @Operation(summary = "Alternar mascota favorita", description = "Añade o elimina una mascota de la lista de favoritos del usuario")
    public ResponseEntity<Void> alternarMascotaFavorita(
            @PathVariable String uid,
            @PathVariable Long mascotaId) {
        usuarioService.alternarMascotaFavorita(uid, mascotaId);
        return ResponseEntity.ok().build();
    }
}

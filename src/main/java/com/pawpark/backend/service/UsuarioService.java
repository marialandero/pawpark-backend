package com.pawpark.backend.service;

import com.pawpark.backend.exception.RecursoNoEncontradoException;
import com.pawpark.backend.model.Usuario;
import com.pawpark.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // 📌 Imagen por defecto (solo nombre de archivo, NO ruta completa)
    private static final String DEFAULT_FOTO = "person_default.png";

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
    }

    public Optional<Usuario> buscarPorFirebaseUid(String uid) {
        return usuarioRepository.findByFirebaseUid(uid);
    }

    // 🔥 CREAR USUARIO CON FOTO POR DEFECTO CONTROLADA
    public Usuario crearUsuario(Usuario usuario) {

        String foto = usuario.getFotoPerfil();

        // 🚨 Normalización de foto de perfil
        if (foto == null ||
                foto.isBlank() ||
                foto.startsWith("assets/") ||
                foto.startsWith("http")) {

            usuario.setFotoPerfil(DEFAULT_FOTO);
        }

        return usuarioRepository.save(usuario);
    }

    public Usuario actualizarUsuario(Long id, Usuario datos) {
        Usuario usuario = obtenerUsuario(id);
        usuario.setNombre(datos.getNombre());
        usuario.setNickname(datos.getNickname());
        usuario.setEmail(datos.getEmail());
        usuario.setDescripcion(datos.getDescripcion());
        usuario.setFotoPerfil(datos.getFotoPerfil());
        usuario.setLocalidad(datos.getLocalidad());
        usuario.setEncountersCount(datos.getEncountersCount());
        // MISMA NORMALIZACIÓN AQUÍ TAMBIÉN
        String foto = datos.getFotoPerfil();
        if (foto != null &&
                !foto.isBlank() &&
                !foto.startsWith("assets/")) {
            usuario.setFotoPerfil(foto);
        }
        return usuarioRepository.save(usuario);
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public List<Usuario> buscarPorNombre(String query) {
        return usuarioRepository.findByNombreContainingIgnoreCase(query);
    }

    public String guardarImagen(MultipartFile file) {
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            Path root = Paths.get("uploads");

            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            Files.copy(file.getInputStream(), root.resolve(fileName));

            return fileName;

        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar la imagen: " + e.getMessage());
        }
    }
}

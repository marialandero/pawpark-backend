package com.pawpark.backend.service;

import com.pawpark.backend.exception.RecursoNoEncontradoException;
import com.pawpark.backend.model.Usuario;
import com.pawpark.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

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

    public Usuario crearUsuario(Usuario usuario) {
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
        return usuarioRepository.save(usuario);
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
}

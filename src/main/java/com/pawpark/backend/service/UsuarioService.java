package com.pawpark.backend.service;

import com.pawpark.backend.exception.RecursoNoEncontradoException;
import com.pawpark.backend.model.Mascota;
import com.pawpark.backend.model.Post;
import com.pawpark.backend.model.Quedada;
import com.pawpark.backend.model.Usuario;
import com.pawpark.backend.repository.MascotaRepository;
import com.pawpark.backend.repository.PostRepository;
import com.pawpark.backend.repository.QuedadaRepository;
import com.pawpark.backend.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private MascotaRepository mascotaRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private QuedadaRepository quedadaRepository;

    // Imagen por defecto (solo nombre de archivo, NO ruta completa)
    private static final String DEFAULT_PERSON_IMAGE = "https://firebasestorage.googleapis.com/v0/b/pawpark-26b38.firebasestorage.app/o/person_default.png?alt=media&token=55bf0c34-547e-4517-bb11-cd202572caa7";

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

    // CREAR USUARIO CON FOTO POR DEFECTO CONTROLADA
    public Usuario crearUsuario(Usuario usuario) {
        String foto = usuario.getFotoPerfil();
        // Normalización de foto de perfil
        if (foto == null ||
                foto.isBlank() ||
                foto.startsWith("assets/") ||
                foto.startsWith("http")) {
            usuario.setFotoPerfil(DEFAULT_PERSON_IMAGE);
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
        String foto = datos.getFotoPerfil();
        if (foto != null &&
                !foto.isBlank() &&
                !foto.startsWith("assets/")) {
            usuario.setFotoPerfil(foto);
        }
        return usuarioRepository.save(usuario);
    }

    //LÓGICA DE SEGUIDORES Y FAVORITOS
    @Transactional
    public void alternarSeguimiento(String seguidorUid, String seguidoUid) {
        Usuario seguidor = buscarPorFirebaseUid(seguidorUid)
                .orElseThrow(() -> new RecursoNoEncontradoException("Seguidor no encontrado"));
        Usuario seguido = buscarPorFirebaseUid(seguidoUid)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario a seguir no encontrado"));
        if (seguidor.getSiguiendo().contains(seguido)) {
            seguidor.getSiguiendo().remove(seguido);
        } else {
            seguidor.getSiguiendo().add(seguido);
        }
        usuarioRepository.save(seguidor);
    }

    @Transactional
    public void alternarMascotaFavorita(String usuarioUid, Long mascotaId) {
        Usuario usuario = buscarPorFirebaseUid(usuarioUid)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        Mascota mascota = mascotaRepository.findById(mascotaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mascota no encontrada"));
        if (usuario.getMascotasFavoritas().contains(mascota)) {
            usuario.getMascotasFavoritas().remove(mascota);
        } else {
            usuario.getMascotasFavoritas().add(mascota);
        }
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        // Limpiar referencias de cada una de sus mascotas
        for (Mascota mascota : usuario.getMascotas()) {
            limpiarReferenciasDeMascota(mascota);
        }
        // Limpiar al usuario de las listas de 'seguidores' de otros
        // (Para evitar que alguien siga a un ID que ya no existe)
        List<Usuario> todos = usuarioRepository.findAll();
        for (Usuario u : todos) {
            u.getSiguiendo().remove(usuario);
            u.getSeguidores().remove(usuario);
            usuarioRepository.save(u);
        }
        // Borrar al usuario definitivamente
        // Esto borrará en cascada sus Mascotas, sus Posts y sus Check-ins
        usuarioRepository.delete(usuario);
    }

    // Método de apoyo para limpiar el "pegamento" de las tablas ManyToMany
    private void limpiarReferenciasDeMascota(Mascota mascota) {
        // Quitar de favoritos de OTROS
        List<Usuario> usuarios = usuarioRepository.findAll();
        for (Usuario u : usuarios) {
            if (u.getMascotasFavoritas().contains(mascota)) {
                u.getMascotasFavoritas().remove(mascota);
                usuarioRepository.save(u);
            }
        }

        // Quitar de etiquetas en Posts de OTROS
        List<Post> posts = postRepository.findAll();
        for (Post p : posts) {
            if (p.getMascotasEtiquetadas().contains(mascota)) {
                p.getMascotasEtiquetadas().remove(mascota);
                postRepository.save(p);
            }
        }

        // Quitar de Quedadas
        List<Quedada> quedadas = quedadaRepository.findAll();
        for (Quedada q : quedadas) {
            if (q.getPerrosAsistentes().contains(mascota)) {
                q.getPerrosAsistentes().remove(mascota);
                quedadaRepository.save(q);
            }
        }
    }

    public List<Usuario> buscarPorNombre(String query) {
        return usuarioRepository.findByNombreContainingIgnoreCase(query);
    }

    public List<Usuario> buscarConPrioridad(String query, String viewerUid) {
        return usuarioRepository.buscarConPrioridad(query, viewerUid);
    }
}

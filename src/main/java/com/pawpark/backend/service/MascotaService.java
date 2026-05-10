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

@Service
public class MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private QuedadaRepository quedadaRepository;

    // Imagen por defecto en backend (archivo real en /uploads)
    private static final String DEFAULT_DOG_IMAGE = "https://firebasestorage.googleapis.com/v0/b/pawpark-26b38.firebasestorage.app/o/dog_default.png?alt=media&token=61f37e02-5c11-4456-ae75-2af2132c1813";

    public List<Mascota> listarMascotas() {
        return mascotaRepository.findAll();
    }

    public Mascota obtenerMascota(Long id) {
        return mascotaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Mascota no encontrada"));
    }

    // Crear mascota con imagen por defecto controlada
    public Mascota crearMascota(Mascota mascota) {
        // Control de imagen por defecto
        String foto = mascota.getFotoPerfilMascota();
        if (foto == null || foto.isBlank()) {
            mascota.setFotoPerfilMascota(DEFAULT_DOG_IMAGE);
        }
        return mascotaRepository.save(mascota);
    }

    public Mascota actualizarMascota(Long id, Mascota datos) {
        Mascota mascota = obtenerMascota(id);
        mascota.setNombre(datos.getNombre());
        mascota.setRaza(datos.getRaza());
        mascota.setEdad(datos.getEdad());
        mascota.setDescripcion(datos.getDescripcion());
        mascota.setDuenoFirebaseUid(datos.getDuenoFirebaseUid());
        mascota.setComportamientos(datos.getComportamientos());
        // Misma normalización que en crear
        String foto = datos.getFotoPerfilMascota();

        if (foto != null && !foto.isBlank()) {
            mascota.setFotoPerfilMascota(foto);
        }
        return mascotaRepository.save(mascota);
    }

    public Mascota actualizarPerfil(Long id, String descripcion, Integer edad) {
        Mascota mascota = obtenerMascota(id); // Reutiliza tu método que lanza excepción si no existe
        if (descripcion != null) {
            mascota.setDescripcion(descripcion);
        }
        if (edad != null) {
            mascota.setEdad(edad);
        }
        return mascotaRepository.save(mascota);
    }

    @Transactional
    public void eliminarMascota(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        // Limpiamos de la tabla intermedia de mascotas favoritas
        List<Usuario> usuarios = usuarioRepository.findAll();
        for (Usuario u : usuarios) {
            if (u.getMascotasFavoritas().contains(mascota)) {
                u.getMascotasFavoritas().remove(mascota);
                usuarioRepository.save(u);
            }
        }
        // Limpiamos de la tabla intermedia de POSTS (Mascotas etiquetadas)
        // Buscamos todos los posts donde esta mascota esté etiquetada
        List<Post> postsConMascota = postRepository.findAll();
        for (Post p : postsConMascota) {
            if (p.getMascotasEtiquetadas().contains(mascota)) {
                p.getMascotasEtiquetadas().remove(mascota);
                postRepository.save(p); // Al guardar el post, se limpia la tabla intermedia
            }
        }
        // Limpiamos de QUEDADAS (Tabla: quedada_perros_asistentes)
        List<Quedada> quedadas = quedadaRepository.findAll();
        for (Quedada q : quedadas) {
            if (q.getPerrosAsistentes().contains(mascota)) {
                q.getPerrosAsistentes().remove(mascota);
                quedadaRepository.save(q);
            }
        }
        // Ahora que ninguna tabla intermedia la bloquea, procedemos al borrado
        // (Check-ins y comportamientos se borran solos por el CascadeType.ALL que pusimos)
        mascotaRepository.delete(mascota);
    }
}

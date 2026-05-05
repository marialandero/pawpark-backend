package com.pawpark.backend.service;

import com.pawpark.backend.exception.RecursoNoEncontradoException;
import com.pawpark.backend.model.Mascota;
import com.pawpark.backend.model.Usuario;
import com.pawpark.backend.repository.MascotaRepository;
import com.pawpark.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Imagen por defecto en backend (archivo real en /uploads)
    private static final String DEFAULT_DOG_IMAGE = "dog_default.png";

    public List<Mascota> listarMascotas() {
        return mascotaRepository.findAll();
    }

    public Mascota obtenerMascota(Long id) {
        return mascotaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Mascota no encontrada"));
    }

    // Crear mascota con imagen por defecto controlada
    public Mascota crearMascota(Mascota mascota) {

        // 2. Control de imagen por defecto
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

    public void eliminarMascota(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe"));
        mascotaRepository.delete(mascota);
    }
}

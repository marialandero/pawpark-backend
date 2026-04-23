package com.pawpark.backend.service;

import com.pawpark.backend.exception.RecursoNoEncontradoException;
import com.pawpark.backend.model.Mascota;
import com.pawpark.backend.repository.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;

    public List<Mascota> listarMascotas() {
        return mascotaRepository.findAll();
    }

    public Mascota obtenerMascota(Long id) {
        return mascotaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Mascota no encontrada"));
    }

    public Mascota crearMascota(Mascota mascota) {
        return mascotaRepository.save(mascota);
    }

    public Mascota actualizarMascota(Long id, Mascota datos) {
        Mascota mascota = obtenerMascota(id);
        mascota.setNombre(datos.getNombre());
        mascota.setRaza(datos.getRaza());
        mascota.setEdad(datos.getEdad());
        mascota.setDescripcion(datos.getDescripcion());
        mascota.setFotoPerfilMascota(datos.getFotoPerfilMascota());
        mascota.setDuenoFirebaseUid(datos.getDuenoFirebaseUid());
        mascota.setComportamientos(datos.getComportamientos());
        return mascotaRepository.save(mascota);
    }

    public void eliminarMascota(Long id) {
        mascotaRepository.deleteById(id);
    }
}

package com.pawpark.backend.service;

import com.pawpark.backend.model.Mascota;
import com.pawpark.backend.model.Quedada;
import com.pawpark.backend.model.Usuario;
import com.pawpark.backend.repository.MascotaRepository;
import com.pawpark.backend.repository.QuedadaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class QuedadaService {

    @Autowired
    private QuedadaRepository quedadaRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private UsuarioService usuarioService;

    public List<Quedada> listarTodas() {
        return quedadaRepository.findAllByOrderByFechaHoraAsc();
    }

    public Quedada procesarYCrearQuedada(Quedada quedada) {
        return quedadaRepository.save(quedada);
    }

    @Transactional
    public Quedada unirse(Long quedadaId, Usuario usuario, List<Long> mascotasIds) {
        Quedada quedada = quedadaRepository.findById(quedadaId)
                .orElseThrow(() -> new RuntimeException("Quedada no encontrada"));

        // Añadimos al humano si no estaba ya
        if (!quedada.getUsuariosAsistentes().contains(usuario)) {
            quedada.getUsuariosAsistentes().add(usuario);
        }
        // Buscamos las mascotas por sus ids y las añadimos a la lista de la quedada
        List<Mascota> mascotasAAsistir = mascotaRepository.findAllById(mascotasIds);
        for (Mascota m :  mascotasAAsistir) {
            if (!quedada.getPerrosAsistentes().contains(m)) {
                quedada.getPerrosAsistentes().add(m);
            }
        }
        return quedadaRepository.save(quedada);
    }

    @Transactional
    public Quedada desapuntarse(Long quedadaId, Usuario usuario) {
        Quedada quedada = quedadaRepository.findById(quedadaId)
                .orElseThrow(() -> new RuntimeException("Quedada no encontrada"));

        // Quitamos al usuario de la lista de asistentes
        quedada.getUsuariosAsistentes().removeIf(u -> u.getId().equals(usuario.getId()));

        // Quitamos a todos sus perros de la lista de perros asistentes
        // Usamos removeIf para filtrar los perros cuyo dueño coincida con el usuario que se va
        quedada.getPerrosAsistentes().removeIf(m -> m.getDueno() != null && m.getDueno().getId().equals(usuario.getId()));

        return quedadaRepository.save(quedada);
    }
}

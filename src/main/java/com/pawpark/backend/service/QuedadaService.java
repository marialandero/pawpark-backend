package com.pawpark.backend.service;

import com.pawpark.backend.model.Mascota;
import com.pawpark.backend.model.Quedada;
import com.pawpark.backend.model.Usuario;
import com.pawpark.backend.repository.MascotaRepository;
import com.pawpark.backend.repository.QuedadaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Transactional
    public Quedada crearQuedadaCompleta(String creadorUid, String titulo, String descripcion,
                                        String lugarNombre, String fechaHoraStr, List<Long> mascotasIds) {

        // 1. Buscamos al creador en la base de datos
        Usuario creador = usuarioService.buscarPorFirebaseUid(creadorUid)
                .orElseThrow(() -> new RuntimeException("Usuario creador no encontrado"));

        // 2. Creamos e inicializamos la entidad
        Quedada quedada = new Quedada();
        quedada.setTitulo(titulo);
        quedada.setDescripcion(descripcion);
        quedada.setLugarNombre(lugarNombre);
        quedada.setFechaHora(LocalDateTime.parse(fechaHoraStr));
        quedada.setCreador(creador);

        // 3. Inicializamos las listas de asistentes (importante para evitar NullPointer)
        quedada.setUsuariosAsistentes(new ArrayList<>());
        quedada.setPerrosAsistentes(new ArrayList<>());

        // 4. El creador se añade a sí mismo como asistente humano[cite: 1, 6]
        quedada.getUsuariosAsistentes().add(creador);

        // 5. Si hay mascotas seleccionadas, las buscamos y las añadimos[cite: 5, 6]
        if (mascotasIds != null && !mascotasIds.isEmpty()) {
            List<Mascota> mascotas = mascotaRepository.findAllById(mascotasIds);
            quedada.getPerrosAsistentes().addAll(mascotas);
        }

        // 6. Guardamos la quedada con todas sus relaciones establecidas
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

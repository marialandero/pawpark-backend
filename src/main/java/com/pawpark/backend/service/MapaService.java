package com.pawpark.backend.service;

import com.pawpark.backend.dto.CheckInRequest;
import com.pawpark.backend.dto.UsuarioPresenteDTO;
import com.pawpark.backend.dto.ZonaRequest;
import com.pawpark.backend.dto.ZonaStatsDTO;
import com.pawpark.backend.model.CheckIn;
import com.pawpark.backend.model.Mascota;
import com.pawpark.backend.model.Usuario;
import com.pawpark.backend.model.Zona;
import com.pawpark.backend.repository.CheckInRepository;
import com.pawpark.backend.repository.MascotaRepository;
import com.pawpark.backend.repository.UsuarioRepository;
import com.pawpark.backend.repository.ZonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MapaService {

    @Autowired
    private ZonaRepository zonaRepository;
    @Autowired
    private CheckInRepository checkInRepository;
    @Autowired
    private UsuarioRepository usuarioRepository; // Necesitas este para buscar al dueño
    @Autowired
    private MascotaRepository mascotaRepository;

    /**
     * Sincroniza las zonas y devuelve las estadísticas.
     * Nota: Para que la prioridad social funcione, deberás ampliar este método
     * en el futuro para calcular 'tieneSeguidos' y 'tieneSeguidosFavoritos'.
     */
    public List<ZonaStatsDTO> sincronizarZonas(List<ZonaRequest> zonasDto, String userUid) {
        // Buscamos al usuario que consulta para conocer sus seguidos y favoritos
        Usuario usuarioConsulta = usuarioRepository.findByFirebaseUid(userUid)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return zonasDto.stream().map(dto -> {
            // Buscamos o creamos la zona
            Zona zona = zonaRepository.findByOsmId(dto.getOsmId())
                    .orElseGet(() -> zonaRepository.save(Zona.builder()
                            .osmId(dto.getOsmId())
                            .nombre(dto.getNombre())
                            .latitud(dto.getLatitud())
                            .longitud(dto.getLongitud())
                            .tipo(dto.getTipo())
                            .build()));

            // Obtenemos los check-ins activos en esta zona específica
            List<CheckIn> checkInsEnZona = checkInRepository.findAllByZonaOsmIdAndFechaExpiracionAfter(
                    zona.getOsmId(), LocalDateTime.now());

            // ¿Hay algún usuario al que yo sigo en este parque?
            boolean tieneSeguidos = checkInsEnZona.stream()
                    .anyMatch(ci -> usuarioConsulta.getSiguiendo().contains(ci.getUsuario()));

            // ¿Hay alguna mascota marcada como favorita por mí en este parque?
            boolean tieneFavs = checkInsEnZona.stream()
                    .anyMatch(ci -> usuarioConsulta.getMascotasFavoritas().contains(ci.getMascota()));

            // Por ahora devolvemos los booleanos en false hasta que implementes la lógica de seguidos
            return new ZonaStatsDTO(zona.getOsmId(), zona.getNombre(),
                    zona.getLatitud(), zona.getLongitud(), checkInsEnZona.size(), tieneSeguidos, tieneFavs, zona.getTipo(), obtenerUsuariosEnZona(zona.getOsmId()));
        }).toList();
    }

    /**
     * Crea un registro de presencia. Por defecto, dura 90 minutos
     * para evitar que el perro se quede "atrapado" en el mapa si el usuario olvida salir.
     */
    public void registrarCheckIn(CheckInRequest request) {
        System.out.println("DEBUG: Recibiendo check-in para UID: " + request.getUid());
        System.out.println("DEBUG: Mascotas seleccionadas: " + request.getMascotasIds());
        System.out.println("DEBUG: Zona OSM ID: " + request.getOsmId());
        Usuario user = usuarioRepository.findByFirebaseUid(request.getUid())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // CAMBIO AQUÍ: En lugar de orElseThrow, usamos orElseGet para crearla si no existe
        Zona zona = zonaRepository.findByOsmId(request.getOsmId())
                .orElseGet(() -> {
                    // Si llegamos aquí es que por algún motivo la zona no se sincronizó antes.
                    // Podrías registrarla con datos básicos o lanzar un error controlado.
                    // Lo ideal es que la zona ya exista, pero para evitar el crash:
                    return zonaRepository.save(Zona.builder()
                            .osmId(request.getOsmId())
                            .nombre("Zona cargada por Check-in") // Nombre genérico si no lo tenemos
                            .build());
                });

        // REGLA: Cerramos cualquier estancia previa antes de entrar a una nueva zona
        salidaManual(request.getUid());

        // Registramos cada mascota seleccionada
        if (request.getMascotasIds() != null) {
            request.getMascotasIds().forEach(id -> {
                Mascota pet = mascotaRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Mascota no encontrada: " + id));

                CheckIn checkIn = CheckIn.builder()
                        .usuario(user)
                        .mascota(pet)
                        .zona(zona)
                        .fechaEntrada(LocalDateTime.now())
                        .fechaExpiracion(LocalDateTime.now().plusMinutes(90))
                        .build();

                checkInRepository.save(checkIn);
            });
        }
    }

    /**
     * Salida manual: se actualiza la expiración al momento actual para que
     * el perro desaparezca del mapa inmediatamente.
     */
    public void salidaManual(String uid) {
        // Busca todos los check-ins activos del usuario y los caduca todos
        List<CheckIn> checkInsActivos = checkInRepository.findAllByUsuarioFirebaseUidAndFechaExpiracionAfter(uid, LocalDateTime.now());

        checkInsActivos.forEach(check -> {
            check.setFechaExpiracion(LocalDateTime.now());
            checkInRepository.save(check);
        });
    }


    private List<UsuarioPresenteDTO> obtenerUsuariosEnZona(String osmId) {
        // Buscamos los check-ins activos en esa zona
        List<CheckIn> checkInsActivos = checkInRepository.findAllByZonaOsmIdAndFechaExpiracionAfter(osmId, LocalDateTime.now());

        // Agrupamos por usuario (porque un usuario puede ir con varias mascotas)
        return checkInsActivos.stream()
                .collect(Collectors.groupingBy(CheckIn::getUsuario))
                .entrySet().stream()
                .map(entry -> {
                    Usuario u = entry.getKey();
                    List<String> nombresMascotas = entry.getValue().stream()
                            .map(ci -> ci.getMascota().getNombre())
                            .collect(Collectors.toList());

                    return UsuarioPresenteDTO.builder()
                            .usuario(u.getNombre())
                            .fotoPerfil(u.getFotoPerfil())
                            .firebaseUid(u.getFirebaseUid())
                            .mascotas(nombresMascotas)
                            .build();
                })
                .collect(Collectors.toList());
    }
}

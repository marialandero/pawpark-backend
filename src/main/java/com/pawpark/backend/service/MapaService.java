package com.pawpark.backend.service;

import com.pawpark.backend.dto.CheckInRequest;
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
    public List<ZonaStatsDTO> sincronizarZonas(List<ZonaRequest> zonasDto) {
        return zonasDto.stream().map(dto -> {
            Zona zona = zonaRepository.findByOsmId(dto.getOsmId())
                    .orElseGet(() -> zonaRepository.save(Zona.builder()
                            .osmId(dto.getOsmId())
                            .nombre(dto.getNombre())
                            .latitud(dto.getLatitud())
                            .longitud(dto.getLongitud())
                            .tipo(dto.getTipo())
                            .build()));

            long conteo = checkInRepository.countByZonaOsmIdAndFechaExpiracionAfter(zona.getOsmId(), LocalDateTime.now());

            // Por ahora devolvemos los booleanos en false hasta que implementes la lógica de seguidos
            return new ZonaStatsDTO(zona.getOsmId(), zona.getNombre(),
                    zona.getLatitud(), zona.getLongitud(), conteo, false, false, zona.getTipo());
        }).toList();
    }

    /**
     * Crea un registro de presencia. Por defecto, dura 90 minutos
     * para evitar que el perro se quede "atrapado" en el mapa si el usuario olvida salir.
     */
    public void registrarCheckIn(CheckInRequest request) {
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
}

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
     * Compara los parques que ve el usuario en el móvil con la base de datos local.
     * Si un parque es nuevo, lo registra. Si ya existe, cuenta cuántos perros hay.
     */
    public List<ZonaStatsDTO> sincronizarZonas(List<ZonaRequest> zonasDto) {
        return zonasDto.stream().map(dto -> {

            // Buscamos por osmId. Si no existe, creamos la zona "al vuelo" (Lazy Loading).
            Zona zona = zonaRepository.findByOsmId(dto.getOsmId())
                    .orElseGet(() -> zonaRepository.save(Zona.builder()
                            .osmId(dto.getOsmId())
                            .nombre(dto.getNombre())
                            .latitud(dto.getLatitud())
                            .longitud(dto.getLongitud())
                            .tipo(dto.getTipo())
                            .build()));
            // Contamos los check-ins cuya fecha de expiración aún no ha llegado.
            long conteo = checkInRepository.countByZonaOsmIdAndFechaExpiracionAfter(zona.getOsmId(), LocalDateTime.now());

            return new ZonaStatsDTO(zona.getOsmId(), zona.getNombre(),
                    zona.getLatitud(), zona.getLongitud(), conteo);
        }).toList();
    }

    /**
     * Crea un registro de presencia. Por defecto, dura 90 minutos
     * para evitar que el perro se quede "atrapado" en el mapa si el usuario olvida salir.
     */
    public void registrarCheckIn(CheckInRequest request) {
        Usuario user = usuarioRepository.findByFirebaseUid(request.getUid()).orElseThrow();
        Mascota pet = mascotaRepository.findById(request.getMascotaId()).orElseThrow();
        Zona zona = zonaRepository.findByOsmId(request.getOsmId()).orElseThrow();

        CheckIn checkIn = CheckIn.builder()
                .usuario(user)
                .mascota(pet)
                .zona(zona)
                .fechaEntrada(LocalDateTime.now())
                .fechaExpiracion(LocalDateTime.now().plusMinutes(90)) // El TTL de 90 min
                .build();

        checkInRepository.save(checkIn);
    }

    /**
     * Salida manual: se actualiza la expiración al momento actual para que
     * el perro desaparezca del mapa inmediatamente.
     */
    public void salidaManual(String uid) {
        checkInRepository.findByUsuarioFirebaseUidAndFechaExpiracionAfter(uid, LocalDateTime.now())
                .ifPresent(check -> {
                    check.setFechaExpiracion(LocalDateTime.now());
                    checkInRepository.save(check);
                });
    }
}

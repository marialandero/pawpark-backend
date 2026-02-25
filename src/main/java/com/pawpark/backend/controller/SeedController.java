package com.pawpark.backend.controller;
import com.pawpark.backend.model.*;
import com.pawpark.backend.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;

@RestController
@RequestMapping("/utils")
@CrossOrigin(origins = "*") // <-- ORIGEN para habilitar la conexión desde el frontend (Flutter)
@Tag(name = "Utils", description = "Sección para operaciones auxiliares")
@RequiredArgsConstructor
public class SeedController {

    private final UsuarioRepository usuarioRepository;
    private final MascotaRepository mascotaRepository;
    private final FotoRepository fotoRepository;
    private final ParqueRepository parqueRepository;
    private final EstanciaParqueRepository estanciaParqueRepository;

    @PostMapping("/seed")
    @Operation(summary = "Cargar datos de prueba", description = "Precarga datos ficticios con los que hacer pruebas.")
    public String seedData() {

        // Evitar crear duplicados
        if (usuarioRepository.count() > 0) {
            return "La base de datos ya tiene datos, no se creó seed.";
        }

        // --- Usuarios ---
        Usuario maria = Usuario.builder()
                .nombre("María Landero")
                .nickname("marialc")
                .email("maria@gamil.com")
                .fotoPerfil("https://images.unsplash.com/photo-1749700332038-640b00de758c")
                .localidad("Plaza de las Marismas, Isla Cristina")
                .memberSince("2026")
                .encountersCount(5)
                .amigos(new java.util.ArrayList<>()) // inicializar lista
                .mascotas(new java.util.ArrayList<>())
                .build();

        Usuario juan = Usuario.builder()
                .nombre("Juan Pérez")
                .nickname("juanP")
                .email("juan@example.com")
                .fotoPerfil("https://images.unsplash.com/photo-1524504388940-b1c1722653e1")
                .localidad("Madrid")
                .memberSince("2024")
                .encountersCount(3)
                .amigos(new java.util.ArrayList<>())
                .mascotas(new java.util.ArrayList<>())
                .build();

        // Relación de amigos
        maria.getAmigos().add(juan);
        juan.getAmigos().add(maria);

        usuarioRepository.saveAll(Arrays.asList(maria, juan));

        // --- Mascotas ---
        Mascota max = Mascota.builder()
                .nombre("Max")
                .raza("Golden Retriever")
                .edad(3)
                .descripcion("Muy juguetón y amigable")
                .foto("https://images.unsplash.com/photo-1734966213753-1b361564bab4")
                .comportamiento(Comportamiento.JUGUETON)
                .dueno(maria)
                .amigosFavoritos(new java.util.ArrayList<>())
                .build();

        Mascota luna = Mascota.builder()
                .nombre("Luna")
                .raza("French Bulldog")
                .edad(2)
                .descripcion("Tranquila y cariñosa")
                .foto("https://images.unsplash.com/photo-1684873050913-76ef6a740f8e")
                .comportamiento(Comportamiento.TRANQUILO)
                .dueno(maria)
                .amigosFavoritos(new java.util.ArrayList<>())
                .build();

        Mascota rocky = Mascota.builder()
                .nombre("Rocky")
                .raza("Beagle")
                .edad(4)
                .descripcion("Curioso y aventurero")
                .foto("https://images.unsplash.com/photo-1685387714439-edef4bd70ef5")
                .comportamiento(Comportamiento.AVENTURERO)
                .dueno(juan)
                .amigosFavoritos(new java.util.ArrayList<>())
                .build();

        // Amigos favoritos
        max.getAmigosFavoritos().add(rocky);
        luna.getAmigosFavoritos().add(max);

        mascotaRepository.saveAll(Arrays.asList(max, luna, rocky));

        // --- Fotos ---
        Foto foto1 = Foto.builder()
                .url("https://images.unsplash.com/photo-1598136490432-99d36f3cfdc8")
                .fechaSubida(LocalDateTime.now().minusDays(2))
                .dueño(maria)
                .mascotasEtiquetadas(Arrays.asList(max, luna))
                .build();

        Foto foto2 = Foto.builder()
                .url("https://images.unsplash.com/photo-1592194996308-7b43878e84a6")
                .fechaSubida(LocalDateTime.now().minusDays(1))
                .dueño(juan)
                .mascotasEtiquetadas(Arrays.asList(rocky))
                .build();

        fotoRepository.saveAll(Arrays.asList(foto1, foto2));

        // --- Parques ---
        Parque retiro = Parque.builder()
                .nombre("Parque del Retiro")
                .latitud(40.4153)
                .longitud(-3.6847)
                .localidad("Madrid")
                .build();

        Parque casaDeCampo = Parque.builder()
                .nombre("Casa de Campo")
                .latitud(40.4260)
                .longitud(-3.7453)
                .localidad("Madrid")
                .build();

        parqueRepository.saveAll(Arrays.asList(retiro, casaDeCampo));

        // --- Estancias en parques ---
        EstanciaParque e1 = EstanciaParque.builder()
                .mascota(max)
                .parque(retiro)
                .fechaEntrada(LocalDateTime.now().minusHours(1))
                .fechaSalida(null)
                .build();

        EstanciaParque e2 = EstanciaParque.builder()
                .mascota(rocky)
                .parque(casaDeCampo)
                .fechaEntrada(LocalDateTime.now().minusHours(2))
                .fechaSalida(null)
                .build();

        estanciaParqueRepository.saveAll(Arrays.asList(e1, e2));

        return "Seed ejecutado correctamente. Usuarios, mascotas, fotos y parques creados.";
    }
}

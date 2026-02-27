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
        Usuario marta = Usuario.builder()
                .nombre("Marta Pérez")
                .nickname("martaprz")
                .email("marta@gmail.com")
                .fotoPerfil("https://unsplash.com/es/fotos/closeup-photography-of-woman-smiling-mEZ3PoFGs_k")
                .localidad("Plaza de las Marismas, Isla Cristina")
                .memberSince("2026")
                .encountersCount(5)
                .amigos(new java.util.ArrayList<>()) // inicializar lista
                .mascotas(new java.util.ArrayList<>())
                .build();

        Usuario juan = Usuario.builder()
                .nombre("Juan Ruíz")
                .nickname("juanR")
                .email("juan@gmail.com")
                .fotoPerfil("https://unsplash.com/es/fotos/hombre-con-chaqueta-azul-y-gorra-amarilla-de-pie-en-la-montana-durante-el-dia-7TU5JJAwPyU")
                .localidad("Ayamonte")
                .memberSince("2024")
                .encountersCount(3)
                .amigos(new java.util.ArrayList<>())
                .mascotas(new java.util.ArrayList<>())
                .build();

        // Relación de amigos
        marta.getAmigos().add(juan);
        juan.getAmigos().add(marta);

        usuarioRepository.saveAll(Arrays.asList(marta, juan));

        // --- Mascotas ---
        Mascota max = Mascota.builder()
                .nombre("Max")
                .raza("Golden Retriever")
                .edad(3)
                .descripcion("Muy juguetón y amigable")
                .foto("https://images.unsplash.com/photo-1734966213753-1b361564bab4")
                .comportamiento(Comportamiento.JUGUETON)
                .dueno(marta)
                .amigosFavoritos(new java.util.ArrayList<>())
                .build();

        Mascota luna = Mascota.builder()
                .nombre("Luna")
                .raza("French Bulldog")
                .edad(2)
                .descripcion("Tranquila y cariñosa")
                .foto("https://images.unsplash.com/photo-1684873050913-76ef6a740f8e")
                .comportamiento(Comportamiento.TRANQUILO)
                .dueno(marta)
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

        // --- Parques ---
        Parque plaza = Parque.builder()
                .nombre("Plaza de las Marismas")
                .latitud(40.4153)
                .longitud(-3.6847)
                .localidad("Isla Cristina")
                .build();

        Parque paseo = Parque.builder()
                .nombre("Paseo de las Flores")
                .latitud(40.4260)
                .longitud(-3.7453)
                .localidad("Isla Cristina")
                .build();

        parqueRepository.saveAll(Arrays.asList(plaza, paseo));

        // --- Estancias en parques ---
        EstanciaParque ep1 = EstanciaParque.builder()
                .mascota(max)
                .parque(plaza)
                .fechaEntrada(LocalDateTime.now().minusHours(1))
                .fechaSalida(null)
                .build();

        EstanciaParque ep2 = EstanciaParque.builder()
                .mascota(rocky)
                .parque(paseo)
                .fechaEntrada(LocalDateTime.now().minusHours(2))
                .fechaSalida(null)
                .build();

        estanciaParqueRepository.saveAll(Arrays.asList(ep1, ep2));

        return "Seed ejecutado correctamente. Usuarios, mascotas, fotos y parques creados.";
    }
}

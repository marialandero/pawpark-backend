package com.pawpark.backend.controller;

import com.pawpark.backend.dto.PostResponse;
import com.pawpark.backend.service.PostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "*")
@Tag(name = "Posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    public PostResponse crearPost(@RequestBody Map<String, Object> payload) {

        return postService.crearPost(payload);
    }

    @GetMapping("/feed")
    public List<PostResponse> obtenerFeed() {
        return postService.getFeed();
    }

    @GetMapping("/usuario/{uid}")
    public List<PostResponse> obtenerPorUsuario(@PathVariable String uid) {
        return postService.getByUsuario(uid);
    }

    @GetMapping("/mascota/{id}")
    public List<PostResponse> obtenerPorMascota(@PathVariable Long id) {
        return postService.getByMascota(id);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        // El controlador recibe el archivo y llama al service
        String nombreImagen = postService.guardarImagen(file);
        return ResponseEntity.ok(nombreImagen);
    }
}
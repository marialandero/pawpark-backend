package com.pawpark.backend.service;

import com.pawpark.backend.exception.RecursoNoEncontradoException;
import com.pawpark.backend.model.Foto;
import com.pawpark.backend.repository.FotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FotoService {

    @Autowired
    private FotoRepository fotoRepository;

    public List<Foto> listarFotos() {
        return  fotoRepository.findAll();
    }

    public Foto obtenerFoto(Long id) {
        return fotoRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Foto no encontrada"));
    }

    public Foto crearFoto(Foto foto) {
        return fotoRepository.save(foto);
    }

    public void eliminarFoto(Long id) {
        fotoRepository.deleteById(id);
    }

}
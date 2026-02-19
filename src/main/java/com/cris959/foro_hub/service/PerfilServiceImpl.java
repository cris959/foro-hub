package com.cris959.foro_hub.service;

import com.cris959.foro_hub.dto.DatosListaPerfil;
import com.cris959.foro_hub.dto.DatosRegistroPerfil;
import com.cris959.foro_hub.mapper.PerfilMapper;
import com.cris959.foro_hub.model.Perfil;
import com.cris959.foro_hub.model.PerfilNombre;
import com.cris959.foro_hub.repository.PerfilRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PerfilServiceImpl implements IPerfilService{

        private final PerfilRepository perfilRepository;

        private final PerfilMapper perfilMapper;

    public PerfilServiceImpl(PerfilRepository perfilRepository, PerfilMapper perfilMapper) {
        this.perfilRepository = perfilRepository;
        this.perfilMapper = perfilMapper;
    }


    @Override
    public List<DatosListaPerfil> listarPerfiles() {
        return perfilRepository.findAll()
                .stream()
                .map(perfilMapper::toDatosListaPerfil)
                .collect(Collectors.toList());
    }

    @Override
    public DatosListaPerfil buscarPorNombre(PerfilNombre nombre) {
        var perfil = perfilRepository.findByNombre(nombre)
                .orElseThrow(() -> new EntityNotFoundException("Perfil no encontrado"));
        return perfilMapper.toDatosListaPerfil(perfil);
    }

    @Override
    public DatosListaPerfil guardar(DatosRegistroPerfil datos) {
        // Creamos la entidad (puedes usar un constructor en Perfil o el Mapper)
        Perfil perfil = new Perfil(null, datos.nombre());
        perfilRepository.save(perfil);
        return perfilMapper.toDatosListaPerfil(perfil);
    }
}

package com.cris959.foro_hub.service;

import com.cris959.foro_hub.dto.DatosListaPerfil;
import com.cris959.foro_hub.dto.DatosRegistroPerfil;
import com.cris959.foro_hub.infra.errores.ValidacionException;
import com.cris959.foro_hub.mapper.PerfilMapper;
import com.cris959.foro_hub.model.Perfil;
import com.cris959.foro_hub.model.PerfilNombre;
import com.cris959.foro_hub.repository.PerfilRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
    public List<DatosListaPerfil> listarPerfiles() {
        return perfilRepository.findAll()
                .stream()
                .map(perfilMapper::toDatosListaPerfil)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DatosListaPerfil buscarPorNombre(PerfilNombre nombre) {
        var perfil = perfilRepository.findByNombre(nombre)
                .orElseThrow(() -> new EntityNotFoundException("Perfil no encontrado"));
        return perfilMapper.toDatosListaPerfil(perfil);
    }

    @Override
    @Transactional
    public DatosListaPerfil guardar(DatosRegistroPerfil datos) {
        // Validación de seguridad: evitar duplicados antes de guardar
        if (perfilRepository.existsByNombre(datos.nombre())) {
            throw new ValidacionException("Este perfil ya existe.");
        }

        Perfil perfil = new Perfil(null, datos.nombre());
        // No es estrictamente necesario asignar el resultado de save() si usas @Transactional,
        // pero es buena práctica para obtener el ID generado por la BD.
        var perfilGuardado = perfilRepository.save(perfil);

        return perfilMapper.toDatosListaPerfil(perfilGuardado);
    }
}

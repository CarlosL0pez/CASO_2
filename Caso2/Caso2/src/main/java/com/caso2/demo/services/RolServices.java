package com.caso2.demo.services;

import com.caso2.demo.domain.Rol;
import com.caso2.demo.repository.RolRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolServices {

    @Autowired
    private RolRepository rolRepository;

    // ESTE ES EL QUE OCUPÁS
    public List<Rol> getRoles() {
        return rolRepository.findAll();
    }

    public Optional<Rol> getRol(Long id) {
        return rolRepository.findById(id);
    }

    public Optional<Rol> getRolPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }

    public Rol save(Rol rol) {
        return rolRepository.save(rol);
    }

    public void delete(Long id) {
        rolRepository.deleteById(id);
    }
}

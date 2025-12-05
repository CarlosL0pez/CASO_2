

package com.caso2.demo.services;

import com.caso2.demo.domain.Rol;
import com.caso2.demo.domain.Usuario;
import com.caso2.demo.repository.RolRepository;
import com.caso2.demo.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServices {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServices(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* ============================
       CONSULTAS
    ============================ */

    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios(boolean activo) {
        if (activo) {
            return usuarioRepository.findByActivoTrue();
        }
        return usuarioRepository.findAll();
    }
     public Optional<Usuario> getUsuarioByUsername(String nombre) {
        return usuarioRepository.findByNombre(nombre);
    }
     public List<Usuario> buscarUsuariosPorNombre(String nombre) {
    return usuarioRepository.findByNombreContainingIgnoreCase(nombre);
}

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuario(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean existeUsuarioPorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    /* ============================
       CREAR O MODIFICAR USUARIO
    ============================ */
    @Transactional
    public void save(Usuario usuario, boolean encriptaClave) {

        final Long id = usuario.getId();

        // Validar email repetido
        Optional<Usuario> existente = usuarioRepository.findByEmail(usuario.getEmail());
        if (existente.isPresent()) {

            // Si estoy creando → error
            if (id == null || !existente.get().getId().equals(id)) {
                throw new DataIntegrityViolationException("El email ya está en uso");
            }
        }

        // Crear usuario nuevo
        if (id == null) {
            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                throw new IllegalArgumentException("La contraseña es obligatoria.");
            }

            usuario.setPassword(
                    encriptaClave ? passwordEncoder.encode(usuario.getPassword())
                                  : usuario.getPassword()
            );
        }
        // Actualizar usuario existente
        else {
            Usuario userBD = usuarioRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                usuario.setPassword(userBD.getPassword());  // mantener contraseña
            } else {
                usuario.setPassword(
                        encriptaClave ? passwordEncoder.encode(usuario.getPassword())
                                      : usuario.getPassword()
                );
            }
        }

        usuarioRepository.save(usuario);
    }

    /* ============================
       ELIMINAR USUARIO
    ============================ */
    @Transactional
    public void delete(Long id) {

        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("El usuario no existe.");
        }

        try {
            usuarioRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "No se puede eliminar el usuario porque tiene datos relacionados.", e);
        }
    }

    /* ============================
       ASIGNAR ROL (1 usuario = 1 rol)
    ============================ */
    @Transactional
    public Usuario asignarRol(Long idUsuario, String rolNombre) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Rol rol = rolRepository.findByNombre(rolNombre)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        usuario.setRol(rol);

        return usuarioRepository.save(usuario);
    }
}

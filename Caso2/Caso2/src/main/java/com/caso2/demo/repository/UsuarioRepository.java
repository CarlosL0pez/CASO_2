package com.caso2.demo.repository;

import com.caso2.demo.domain.Usuario;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /* ============================
       LOGIN
    ============================ */

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByEmailAndActivoTrue(String email);

    boolean existsByEmail(String email);

    List<Usuario> findByActivoTrue();


    /* ============================
       1. Buscar usuario por nombre EXACTO
    ============================ */
    Optional<Usuario> findByNombre(String nombre);


    /* ============================
       2. Coincidencia parcial nombre
    ============================ */
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);


    /* ============================
       3. Coincidencia parcial nombre / apellido / email
    ============================ */
    @Query("""
           SELECT u FROM Usuario u
           WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
              OR LOWER(u.apellido) LIKE LOWER(CONCAT('%', :texto, '%'))
              OR LOWER(u.email) LIKE LOWER(CONCAT('%', :texto, '%'))
           """)
    List<Usuario> buscarPorCoincidencia(@Param("texto") String texto);


    /* ============================
       4. Buscar usuarios por rol
    ============================ */
    @Query("""
           SELECT u FROM Usuario u 
           WHERE u.rol.nombre = :nombreRol
           """)
    List<Usuario> findByRolNombre(@Param("nombreRol") String nombreRol);


    /* ============================
       5. Buscar usuarios por rango de fechas
    ============================ */
    @Query("""
           SELECT u FROM Usuario u
           WHERE u.fechaCreacion BETWEEN :desde AND :hasta
           """)
    List<Usuario> findByFechaCreacionBetween(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );
}

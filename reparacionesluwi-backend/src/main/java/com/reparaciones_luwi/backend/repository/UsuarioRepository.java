package com.reparaciones_luwi.backend.repository;

import com.reparaciones_luwi.backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    //Buscar un usuario por su correo electrónico.
    Optional<Usuario> findByCorreo(String correo);

    //Validar o comprobar si un correo ya está registrado.
    boolean existsByCorreo(String correo);
}

package com.reparaciones_luwi.backend.service;

import com.reparaciones_luwi.backend.dto.RegistroUsuarioRequest;
import com.reparaciones_luwi.backend.dto.UsuarioResponse;
import com.reparaciones_luwi.backend.entity.Usuario;
import com.reparaciones_luwi.backend.exception.CorreoYaRegistradoException;
import com.reparaciones_luwi.backend.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,PasswordEncoder passwordEncoder){
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse registrarUsuario(RegistroUsuarioRequest solicitud){

        String correo = solicitud.getCorreo().trim().toLowerCase(Locale.ROOT);

        if(usuarioRepository.existsByCorreo(correo)){
            throw new CorreoYaRegistradoException("Ya existe una cuenta con el correo " + correo);
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(solicitud.getNombre().trim());
        usuario.setCorreo(correo);
        usuario.setPasswordHash(passwordEncoder.encode(solicitud.getContrasena()));
        usuario.setTelefono(solicitud.getTelefono().trim());
        usuario.setRol(solicitud.getRol());
        usuario.setCiudad(solicitud.getCiudad().trim());

        String direccion = solicitud.getDireccionReferencia();
        if(direccion != null && !direccion.isBlank()){
            usuario.setDireccionReferencia(direccion.trim());
        }

        Usuario guardado = usuarioRepository.save(usuario);

        return new UsuarioResponse(guardado.getIdUsuario(), guardado.getNombre(), guardado.getCorreo(),
                guardado.getTelefono(), guardado.getRol(), guardado.getCiudad(), guardado.getDireccionReferencia(),
                guardado.getFechaRegistro(), guardado.getActivo());
    }
}

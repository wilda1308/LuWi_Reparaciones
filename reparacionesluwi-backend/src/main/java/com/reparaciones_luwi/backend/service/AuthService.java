package com.reparaciones_luwi.backend.service;

import com.reparaciones_luwi.backend.dto.LoginRequest;
import com.reparaciones_luwi.backend.dto.LoginResponse;
import com.reparaciones_luwi.backend.dto.UsuarioResponse;
import com.reparaciones_luwi.backend.entity.Usuario;
import com.reparaciones_luwi.backend.repository.UsuarioRepository;
import com.reparaciones_luwi.backend.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService){
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse iniciarSesion(LoginRequest request){
        Usuario usuario =
                usuarioRepository.findByCorreo(request.correo().trim().toLowerCase()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Correo o contraseña incorrectos"));

        if(!usuario.getActivo()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La cuenta está inactiva");
        }

        if(!passwordEncoder.matches(request.contrasena(), usuario.getPasswordHash())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos");
        }

        String token = jwtService.generateToken(usuario.getCorreo(), usuario.getRol().name());

        UsuarioResponse usuarioResponse = new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getTelefono(),
                usuario.getRol(),
                usuario.getCiudad(),
                usuario.getDireccionReferencia(),
                usuario.getFechaRegistro(),
                usuario.getActivo()
        );

        return new LoginResponse(token, "Bearer", jwtService.getExpirationMs(), usuarioResponse);
    }
}

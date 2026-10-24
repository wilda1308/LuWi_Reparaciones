package com.reparaciones_luwi.backend.controller;


import com.reparaciones_luwi.backend.dto.LoginRequest;
import com.reparaciones_luwi.backend.dto.LoginResponse;
import com.reparaciones_luwi.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> iniciarSesion(@Valid @RequestBody LoginRequest request){
        LoginResponse respuesta = authService.iniciarSesion(request);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/me")
    public Map<String, Object> obtenerUsuarioAutenticado(Authentication authentication){
        return Map.of(
          "correo", authentication.getName(), "autoridades", authentication.getAuthorities()
        );
    }

}

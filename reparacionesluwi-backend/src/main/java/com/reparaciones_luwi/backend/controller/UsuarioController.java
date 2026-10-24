package com.reparaciones_luwi.backend.controller;

import com.reparaciones_luwi.backend.dto.RegistroUsuarioRequest;
import com.reparaciones_luwi.backend.dto.UsuarioResponse;
import com.reparaciones_luwi.backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrarUsuario(@Valid @RequestBody RegistroUsuarioRequest solicitud){
        UsuarioResponse respuesta =  usuarioService.registrarUsuario(solicitud);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
}

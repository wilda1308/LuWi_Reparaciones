package com.reparaciones_luwi.backend.controller;

import com.reparaciones_luwi.backend.dto.CrearReparacionRequest;
import com.reparaciones_luwi.backend.dto.ReparacionResponse;
import com.reparaciones_luwi.backend.service.ReparacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reparaciones")
public class ReparacionController {

    private final ReparacionService reparacionService;

    public ReparacionController(ReparacionService reparacionService){
        this.reparacionService =reparacionService;
    }

    @PostMapping
    public ResponseEntity<ReparacionResponse> crear(@Valid @RequestBody CrearReparacionRequest request,
                                                    Authentication authentication){
        ReparacionResponse respuesta = reparacionService.crear(request, authentication);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/mis-reparaciones")
    public ResponseEntity<List<ReparacionResponse>> listarMisReparaciones(Authentication authentication){
        return ResponseEntity.ok(reparacionService.listarMisReparaciones(authentication));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<ReparacionResponse>> listarDisponibles(@RequestParam(required = false) String ciudad){
        return ResponseEntity.ok(reparacionService.listarDisponibles(ciudad
        ));
    }
}

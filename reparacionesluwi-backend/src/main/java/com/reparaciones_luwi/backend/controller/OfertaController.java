package com.reparaciones_luwi.backend.controller;

import com.reparaciones_luwi.backend.dto.CrearOfertaRequest;
import com.reparaciones_luwi.backend.dto.OfertaResponse;
import com.reparaciones_luwi.backend.service.OfertaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OfertaController {

    private final OfertaService ofertaService;

    public OfertaController(OfertaService ofertaService){
        this.ofertaService = ofertaService;
    }

    @PostMapping("/api/reparaciones/{idReparacion}/ofertas")
    public ResponseEntity<OfertaResponse> crear(@PathVariable Long idReparacion, @Valid @RequestBody CrearOfertaRequest request, Authentication authentication){
        return ResponseEntity.status(HttpStatus.CREATED).body(ofertaService.crear(idReparacion, request,
                authentication));
    }

    @GetMapping("/api/reparaciones/{idReparacion}/ofertas")
    public ResponseEntity<List<OfertaResponse>> listarPorReparacion(@PathVariable Long idReparacion, Authentication authentication){
        return ResponseEntity.ok(ofertaService.listarPorReparacion(idReparacion, authentication));
    }

    @GetMapping("/api/ofertas/mis-ofertas")
    public ResponseEntity<List<OfertaResponse>> listarMisOfertas(Authentication authentication){
        return ResponseEntity.ok(ofertaService.listarMisOfertas(authentication));
    }

    @PutMapping("/api/ofertas/{idOferta}/aceptar")
    public ResponseEntity<OfertaResponse> aceptar(@PathVariable Long idOferta, Authentication authentication){
        return ResponseEntity.ok(ofertaService.aceptar(idOferta, authentication));
    }
}

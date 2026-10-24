package com.reparaciones_luwi.backend.dto;

import com.reparaciones_luwi.backend.entity.EstadoOferta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OfertaResponse (
        Long idOferta,
        Long idReparacion,
        String tituloReparacion,
        Long idReparador,
        String nombreReparador,
        BigDecimal precioPresupuesto,
        String descripcion,
        String tiempoEstimado,
        EstadoOferta estado,
        LocalDateTime fechaOferta
) {
}

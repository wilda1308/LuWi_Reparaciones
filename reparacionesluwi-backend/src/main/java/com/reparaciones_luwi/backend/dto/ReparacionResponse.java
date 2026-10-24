package com.reparaciones_luwi.backend.dto;

import com.reparaciones_luwi.backend.entity.EstadoReparacion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReparacionResponse(
        Long idReparacion,
        Long idCliente,
        String titulo,
        String descripcion,
        String categoria,
        String marcaObjeto,
        BigDecimal presupuestoMinimo,
        BigDecimal presupuestoMaximo,
        String ciudad,
        String zona,
        EstadoReparacion estado,
        LocalDateTime fechaPublicacion
) {
}

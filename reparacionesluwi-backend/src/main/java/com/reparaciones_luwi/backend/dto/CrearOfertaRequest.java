package com.reparaciones_luwi.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CrearOfertaRequest(

        @NotNull(message = "El precio propuesto es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que cero")
        BigDecimal precioPropuesto,

        @NotBlank(message = "La descripción de la oferta es obligatoria")
        String descripcion,

        @NotBlank(message = "El tiempo estimado es obligatorio")
        @Size(max = 100, message = "El tiempo estimado no puede superar 100 caracteres")
        String tiempoEstimado
) {


}

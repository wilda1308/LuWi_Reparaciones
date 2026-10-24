package com.reparaciones_luwi.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CrearReparacionRequest(

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 120, message = "El título no puede superar los 120 caracteres")
        String titulo,

        @NotBlank(message = "La descripción es obligatoria")
        String descripcion,

        @NotBlank(message = "La categoría es onligatoria")
        @Size(max = 80, message = "La categoría no puede superar 80 caracteres")
        String categoria,

        @Size(max = 100, message = "La marca no puede superar 100 caracteres")
        String marcaObjeto,

        @DecimalMin(value = "0.0", inclusive = false, message = "El presupuesto mínimo debe ser mayor que cero")
        BigDecimal presupuestoMinimo,

        @DecimalMin(value = "0.0", inclusive = false, message = "El presupuesto máximo debe ser mayor que cero")
        BigDecimal presupuestoMaximo,

        @NotBlank(message = "La ciudades obligatoria")
        @Size(max = 100)
        String ciudad,

        @NotBlank(message = "La zona es obligatoria")
        @Size(max = 150)
        String zona

) {
}

package com.reparaciones_luwi.backend.dto;

import com.reparaciones_luwi.backend.entity.RolUsuario;
import java.time.LocalDateTime;

public record UsuarioResponse(
        Long idUsuario,
        String nombre,
        String correo,
        String telefono,
        RolUsuario rolUsuario,
        String ciudad,
        String direccionReferencia,
        LocalDateTime fechaRegistro,
        Boolean activo
) {

}

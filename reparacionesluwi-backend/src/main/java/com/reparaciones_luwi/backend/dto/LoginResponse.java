package com.reparaciones_luwi.backend.dto;

public record LoginResponse(String token, String tipo, long expiraEnMs, UsuarioResponse usuario) {

}

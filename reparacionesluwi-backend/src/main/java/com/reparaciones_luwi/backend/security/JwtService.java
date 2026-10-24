package com.reparaciones_luwi.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs) {
                byte[] keyBytes = Decoders.BASE64.decode(secret);

                this.secretKey = Keys.hmacShaKeyFor(keyBytes);
                this.expirationMs = expirationMs;
    }

    public String generateToken(String correo, String rol){
        Date fechaActual = new Date();
        Date fechaExpiracion = new Date(fechaActual.getTime() + expirationMs);

        return Jwts.builder().subject(correo).claim("rol", rol).issuedAt(fechaActual).expiration(fechaExpiracion).signWith(secretKey).compact();
    }

    public boolean isTokenValid(String token){
        try{
            extractClaims(token);
            return true;
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e){
            return false;
        }
    }

    public Claims extractClaims(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }

    public String extractCorreo(String token){
        return extractClaims(token).getSubject();
    }

    public long getExpirationMs() {
        return expirationMs;
    }
}

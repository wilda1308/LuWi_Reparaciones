package com.reparaciones_luwi.backend.security;

import com.reparaciones_luwi.backend.entity.Usuario;
import com.reparaciones_luwi.backend.repository.UsuarioRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UsuarioRepository usuarioRepository
    ){
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String autorizationHeader = request.getHeader("Authorization");

        if(autorizationHeader == null || !autorizationHeader.startsWith("Bearer ")){

            filterChain.doFilter(request, response);
            return;
        }

        String token = autorizationHeader.substring(7);

        try {
            if(!jwtService.isTokenValid(token)){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado");
                return;
            }

            Claims claims = jwtService.extractClaims(token);
            String correo = claims.getSubject();

            Optional<Usuario> usuarioEncontrado = usuarioRepository.findByCorreo(correo);

            if(usuarioEncontrado.isEmpty() || !usuarioEncontrado.get().getActivo()){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuario no autorizado");
                return;
            }

            Usuario usuario = usuarioEncontrado.get();

            String autoridad = "ROLE_" + usuario.getRol().name();

            UsernamePasswordAuthenticationToken autenticacion = new UsernamePasswordAuthenticationToken(
                    usuario.getCorreo(), null, List.of(new SimpleGrantedAuthority(autoridad))
            );

            SecurityContextHolder.getContext().setAuthentication(autenticacion);
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e){
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Token invalido");
            return;
        }

        filterChain.doFilter(request, response);
    }
}

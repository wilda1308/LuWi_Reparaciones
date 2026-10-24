package com.reparaciones_luwi.backend.service;

import com.reparaciones_luwi.backend.dto.CrearReparacionRequest;
import com.reparaciones_luwi.backend.dto.ReparacionResponse;
import com.reparaciones_luwi.backend.entity.EstadoReparacion;
import com.reparaciones_luwi.backend.entity.Reparacion;
import com.reparaciones_luwi.backend.entity.RolUsuario;
import com.reparaciones_luwi.backend.entity.Usuario;
import com.reparaciones_luwi.backend.repository.ReparacionRepository;
import com.reparaciones_luwi.backend.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReparacionService {

    private final ReparacionRepository reparacionRepository;
    private final UsuarioRepository usuarioRepository;

    public ReparacionService(ReparacionRepository reparacionRepository, UsuarioRepository usuarioRepository){
        this.reparacionRepository = reparacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ReparacionResponse crear(CrearReparacionRequest request, Authentication authentication){
        //identificamos al usuario mediante el JWT
        Usuario cliente  = usuarioRepository.findByCorreo(authentication.getName()).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Usuario autenticado no encontrado"
        ));


        if(!Boolean.TRUE.equals(cliente.getActivo())){
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "La cuenta está inactiva"
            );
        }

        //Solo los clientes pueden publicar solicitudes.
        if(cliente.getRol() != RolUsuario.CLIENTE){
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Solo los clientes pueden publicar reparaciones"
            );
        }

        //Validamos que el presupuesto mínimo no supuere el máximo
        if(request.presupuestoMinimo() != null && request.presupuestoMaximo() != null && request.presupuestoMinimo().compareTo(request.presupuestoMaximo()) > 0){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El presupuesto mínimo no puede superar al máximo"
            );
        }

        Reparacion reparacion = new Reparacion();

        reparacion.setCliente(cliente);
        reparacion.setTitulo(request.titulo().trim());
        reparacion.setDescripcion(request.descripcion().trim());
        reparacion.setCategoria(request.categoria().trim());
        reparacion.setMarcaObjeto(request.marcaObjeto() == null? null : request.marcaObjeto().trim());
        reparacion.setPresupuestoMinimo(request.presupuestoMinimo());
        reparacion.setPresupuestoMaximo(request.presupuestoMaximo());
        reparacion.setCiudad(request.ciudad().trim());
        reparacion.setZona(request.zona().trim());
        reparacion.setEstado(EstadoReparacion.PUBLICADA);

        Reparacion guardada = reparacionRepository.save(reparacion);

        return convertirRespuesta(guardada);
    }

    @Transactional(readOnly = true)
    public List<ReparacionResponse> listarMisReparaciones(Authentication authentication){
        Usuario cliente  =
                usuarioRepository.findByCorreo(authentication.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario autenticado no encontrado"));
        return reparacionRepository.findByCliente_IdUsuario(cliente.getIdUsuario()).stream().map(this::convertirRespuesta).toList();
    }

    @Transactional(readOnly = true)
    public List<ReparacionResponse> listarDisponibles(String ciudad){
        List<Reparacion> lista = (ciudad == null || ciudad.isBlank())?
                reparacionRepository.findByEstadoOrderByFechaPublicacionDesc(EstadoReparacion.PUBLICADA) :
                reparacionRepository.findByEstadoAndCiudadIgnoreCaseOrderByFechaPublicacionDesc(EstadoReparacion.PUBLICADA, ciudad.trim());
        return lista.stream().map(this::convertirRespuesta).toList();
    }

    private ReparacionResponse convertirRespuesta(Reparacion reparacion){
        return new ReparacionResponse(
                reparacion.getIdReparacion(),
                reparacion.getCliente().getIdUsuario(),
                reparacion.getTitulo(),
                reparacion.getDescripcion(),
                reparacion.getCategoria(),
                reparacion.getMarcaObjeto(),
                reparacion.getPresupuestoMinimo(),
                reparacion.getPresupuestoMaximo(),
                reparacion.getCiudad(),
                reparacion.getZona(),
                reparacion.getEstado(),
                reparacion.getFechaPublicacion()
        );
    }
}

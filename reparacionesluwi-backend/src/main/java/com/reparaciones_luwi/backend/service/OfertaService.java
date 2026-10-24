package com.reparaciones_luwi.backend.service;

import com.reparaciones_luwi.backend.dto.CrearOfertaRequest;
import com.reparaciones_luwi.backend.dto.OfertaResponse;
import com.reparaciones_luwi.backend.entity.*;
import com.reparaciones_luwi.backend.repository.AcuerdoRepository;
import com.reparaciones_luwi.backend.repository.OfertaRepository;
import com.reparaciones_luwi.backend.repository.ReparacionRepository;
import com.reparaciones_luwi.backend.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class OfertaService {

    private final OfertaRepository ofertaRepository;
    private final ReparacionRepository reparacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final AcuerdoRepository acuerdoRepository;

    public OfertaService(OfertaRepository ofertaRepository, ReparacionRepository reparacionRepository,
                         UsuarioRepository usuarioRepository, AcuerdoRepository acuerdoRepository) {
        this.ofertaRepository = ofertaRepository;
        this.reparacionRepository = reparacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.acuerdoRepository = acuerdoRepository;
    }

    @Transactional
    public OfertaResponse crear(Long idReparacion, CrearOfertaRequest request, Authentication authentication){
        Usuario reparador = obtenerUsuario(authentication);
        Reparacion reparacion = buscarReparacion(idReparacion);

        if(reparacion.getEstado() != EstadoReparacion.PUBLICADA){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta reparación ya no recibe ofertas");
        }

        if(ofertaRepository.existsByReparacion_IdReparacionAndReparador_IdUsuario(idReparacion, reparador.getIdUsuario())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya enviaste una oferta para esta reparación");
        }

        Oferta oferta = new Oferta();
        oferta.setReparacion(reparacion);
        oferta.setReparador(reparador);
        oferta.setPrecioPropuesto(request.precioPropuesto());
        oferta.setDescripcion(request.descripcion().trim());
        oferta.setTiempoEstimado(request.tiempoEstimado().trim());

        return convertir(ofertaRepository.save(oferta));
    }

    @Transactional(readOnly = true)
    public List<OfertaResponse> listarPorReparacion (Long idReparacion, Authentication authentication){

        Usuario cliente = obtenerUsuario(authentication);
        Reparacion reparacion = buscarReparacion(idReparacion);

        if(!reparacion.getCliente().getIdUsuario().equals(cliente.getIdUsuario())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta reparación no te pertenece");
        }

        return ofertaRepository.findByReparacion_IdReparacion(idReparacion).stream().map(this::convertir).toList();
    }

    @Transactional(readOnly = true)
    public List<OfertaResponse> listarMisOfertas(Authentication authentication){
        Usuario reparador = obtenerUsuario(authentication);
        return ofertaRepository.findByReparador_IdUsuario(reparador.getIdUsuario()).stream().map(this::convertir).toList();
    }

    @Transactional
    public OfertaResponse aceptar(Long idOferta, Authentication authentication){
        Usuario cliente =obtenerUsuario(authentication);
        Oferta oferta =
                ofertaRepository.findById(idOferta).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La oferta no existe"));
        Reparacion reparacion = oferta.getReparacion();

        if(!reparacion.getCliente().getIdUsuario().equals(cliente.getIdUsuario())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta reparación no te pertenece");
        }

        if(reparacion.getEstado() != EstadoReparacion.PUBLICADA){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La reparación ya fue asígnada o cerrada");
        }

        if(oferta.getEstado() != EstadoOferta.PENDIENTE){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La oferta ya no está pendiente por reparar");
        }

        List<Oferta> pendientes =
                ofertaRepository.findByReparacion_IdReparacionAndEstado(reparacion.getIdReparacion(),
                        EstadoOferta.PENDIENTE);

        for (Oferta o : pendientes){
            o.setEstado(o.getIdOferta().equals(oferta.getIdOferta())? EstadoOferta.ACEPTADA : EstadoOferta.RECHAZADA);
        }

        reparacion.setEstado(EstadoReparacion.ASIGNADA);

        Acuerdo acuerdo = new Acuerdo();
        acuerdo.setReparacion(reparacion);
        acuerdo.setOferta(oferta);
        acuerdo.setPrecioAcordado(oferta.getPrecioPropuesto());
        acuerdoRepository.save(acuerdo);

        return convertir(oferta);
    }

    private Usuario obtenerUsuario(Authentication authentication){
        return usuarioRepository.findByCorreo(authentication.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario autenticado no encontrado"));
    }

    private Reparacion buscarReparacion(Long idReparacion){
        return reparacionRepository.findById(idReparacion).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La reparación no existe"));
    }

    private OfertaResponse convertir(Oferta oferta){
        return new OfertaResponse(
                oferta.getIdOferta(),
                oferta.getReparacion().getIdReparacion(),
                oferta.getReparacion().getTitulo(),
                oferta.getReparador().getIdUsuario(),
                oferta.getReparador().getNombre(),
                oferta.getPrecioPropuesto(),
                oferta.getDescripcion(),
                oferta.getTiempoEstimado(),
                oferta.getEstado(),
                oferta.getFechaOferta()
        );
    }
}

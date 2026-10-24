package com.reparaciones_luwi.backend.repository;

import com.reparaciones_luwi.backend.entity.EstadoOferta;
import com.reparaciones_luwi.backend.entity.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfertaRepository extends JpaRepository<Oferta, Long> {

    //Consultar las ofertas presentadas para una reparación.
    List<Oferta> findByReparacion_IdReparacion(Long idReparacion);

    //Consultar las ofertas enviadas por un reparador.
    List<Oferta> findByReparador_IdUsuario(Long idUsuario);

    //Consultar las ofertas de una reparación según su estado.
    List<Oferta> findByReparacion_IdReparacionAndEstado(Long idReparacion, EstadoOferta estado);

    //Comprobar si un reparador ya ofertó por una reparación.
    boolean existsByReparacion_IdReparacionAndReparador_IdUsuario(Long idReparacion, Long idusuario);
}

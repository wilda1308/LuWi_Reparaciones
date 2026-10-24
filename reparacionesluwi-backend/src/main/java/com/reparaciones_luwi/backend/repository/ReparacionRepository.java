package com.reparaciones_luwi.backend.repository;

import com.reparaciones_luwi.backend.entity.EstadoReparacion;
import com.reparaciones_luwi.backend.entity.Reparacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReparacionRepository extends JpaRepository<Reparacion, Long> {
    List<Reparacion> findByCliente_IdUsuario(Long idUsuario);

    List<Reparacion> findByEstadoOrderByFechaPublicacionDesc(EstadoReparacion estado);

    List<Reparacion> findByEstadoAndCiudadIgnoreCaseOrderByFechaPublicacionDesc( EstadoReparacion estado, String ciudad);
}

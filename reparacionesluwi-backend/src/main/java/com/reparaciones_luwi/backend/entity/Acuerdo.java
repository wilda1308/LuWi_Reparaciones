package com.reparaciones_luwi.backend.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "acuerdos")
public class Acuerdo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acuerdo")
    private Long idAcuerdo;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reparacion_id", nullable = false, unique = true)
    private Reparacion reparacion;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oferta_id", nullable = false, unique = true)
    private Oferta oferta;

    @Column(name = "precio_acordado", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioAcordado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoAcuerdo estado;

    @Column(name = "fecha_acuerdo", nullable = false, updatable = false)
    private LocalDateTime fechaAcuerdo;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    public Acuerdo(){
    }

    @PrePersist
    protected void antesDeGuardar(){
        LocalDateTime ahora = LocalDateTime.now();
        if(estado == null) estado = EstadoAcuerdo.CONFIRMADO;
        fechaAcuerdo = ahora;
        fechaActualizacion = ahora;
    }

    @PreUpdate
    protected void antesDeActualizar(){
        fechaActualizacion = LocalDateTime.now();
    }

    public Long getIdAcuerdo() {
        return idAcuerdo;
    }

    public Reparacion getReparacion() {
        return reparacion;
    }

    public void setReparacion(Reparacion reparacion) {
        this.reparacion = reparacion;
    }

    public Oferta getOferta() {
        return oferta;
    }

    public void setOferta(Oferta oferta) {
        this.oferta = oferta;
    }

    public BigDecimal getPrecioAcordado() {
        return precioAcordado;
    }

    public void setPrecioAcordado(BigDecimal precioAcordado) {
        this.precioAcordado = precioAcordado;
    }

    public EstadoAcuerdo getEstado() {
        return estado;
    }

    public void setEstado(EstadoAcuerdo estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaAcuerdo() {
        return fechaAcuerdo;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}

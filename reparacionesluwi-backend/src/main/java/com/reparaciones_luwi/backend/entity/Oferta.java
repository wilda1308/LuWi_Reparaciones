package com.reparaciones_luwi.backend.entity;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ofertas")
public class Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_oferta")
    private Long idOferta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reparacion_id", nullable = false)
    private Reparacion reparacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reparador_id", nullable = false)
    private Usuario reparador;

    @Column(name = "precio_propuesto", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioPropuesto;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "tiempo_estimado", nullable = false, length = 100)
    private String tiempoEstimado;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoOferta estado;

    @Column(name = "fecha_oferta", nullable = false, updatable = false)
    private LocalDateTime fechaOferta;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    public Oferta(){
    }

    @PrePersist
    protected void antesDeGuardar(){
        if(estado == null){
            estado = EstadoOferta.PENDIENTE;
        }

        if(fechaOferta == null){
            fechaOferta = LocalDateTime.now();
        }

        if(fechaActualizacion == null){
            fechaActualizacion = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void antesDeActualizar(){
        fechaActualizacion = LocalDateTime.now();
    }

    public Long getIdOferta() {
        return idOferta;
    }

    public void setIdOferta(Long idOferta) {
        this.idOferta = idOferta;
    }

    public Reparacion getReparacion() {
        return reparacion;
    }

    public void setReparacion(Reparacion reparacion) {
        this.reparacion = reparacion;
    }

    public Usuario getReparador() {
        return reparador;
    }

    public void setReparador(Usuario reparador) {
        this.reparador = reparador;
    }

    public BigDecimal getPrecioPropuesto() {
        return precioPropuesto;
    }

    public void setPrecioPropuesto(BigDecimal precioPropuesto) {
        this.precioPropuesto = precioPropuesto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTiempoEstimado() {
        return tiempoEstimado;
    }

    public void setTiempoEstimado(String tiempoEstimado) {
        this.tiempoEstimado = tiempoEstimado;
    }

    public EstadoOferta getEstado() {
        return estado;
    }

    public void setEstado(EstadoOferta estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaOferta() {
        return fechaOferta;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}

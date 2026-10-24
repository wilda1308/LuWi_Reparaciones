package com.reparaciones_luwi.backend.entity;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reparaciones")
public class Reparacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reparacion")
    private Long idReparacion;

    //Usuario que publica la reparación
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, length = 80)
    private String categoria;

    @Column(name = "marca_objeto", length = 100)
    private String marcaObjeto;

    @Column(name = "presupuesto_minimo", precision = 12, scale = 2)
    private BigDecimal presupuestoMinimo;

    @Column(name = "presupuesto_maximo", precision = 12, scale = 2)
    private BigDecimal presupuestoMaximo;

    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(nullable = false, length = 150)
    private String zona;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReparacion estado = EstadoReparacion.PUBLICADA;

    @Column(name = "fecha_publicacion", nullable = false, updatable = false)
    private LocalDateTime fechaPublicacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private  LocalDateTime fechaActualizacion;

    @PrePersist
    protected void antesDeGuardar(){
        LocalDateTime ahora = LocalDateTime.now();
        fechaPublicacion = ahora;
        fechaActualizacion = ahora;
    }

    @PreUpdate
    protected void antesDeActualizar(){
        fechaActualizacion = LocalDateTime.now();
    }

    public Reparacion(){
    }

    public Long getIdReparacion() {
        return idReparacion;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getMarcaObjeto() {
        return marcaObjeto;
    }

    public void setMarcaObjeto(String marcaObjeto) {
        this.marcaObjeto = marcaObjeto;
    }

    public BigDecimal getPresupuestoMinimo() {
        return presupuestoMinimo;
    }

    public void setPresupuestoMinimo(BigDecimal presupuestoMinimo) {
        this.presupuestoMinimo = presupuestoMinimo;
    }

    public BigDecimal getPresupuestoMaximo() {
        return presupuestoMaximo;
    }

    public void setPresupuestoMaximo(BigDecimal presupuestoMaximo) {
        this.presupuestoMaximo = presupuestoMaximo;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public EstadoReparacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoReparacion estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}

package com.reparaciones_luwi.backend.dto;

import com.reparaciones_luwi.backend.entity.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegistroUsuarioRequest {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre es muy largo")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo es inválido o no tiene un formato válido")
    @Size(max = 150, message = "El correo es demasiado largo")
    private String correo;

    @NotBlank(message = "La constraseña es obligatoria")
    @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
    private String contrasena;

    @NotBlank(message = "El telefono es obligatorio")
    @Size(max = 20, message = "El telefono es demasiado largo")
    private String telefono;

    @NotNull(message = "Debes seleccionar un rol")
    private RolUsuario rol;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100, message = "La ciudad es demasiado larga")
    private String ciudad;

    @Size(max = 255, message = "La dirección de referencia es demasiado larga")
    private String direccionReferencia;

    public RegistroUsuarioRequest(){
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccionReferencia() {
        return direccionReferencia;
    }

    public void setDireccionReferencia(String direccionReferencia) {
        this.direccionReferencia = direccionReferencia;
    }
}

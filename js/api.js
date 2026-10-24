
//Variable constante de la ruta principal del backend (No se debe modificar a menos que cambie el puerto o la URL principal del backend).
const API_URL = "http://localhost:8080";

/* El backend es Stateless (no recuerda quien eres entre peticiones o solicitudes), por eso existe JWT en el backend para que cada petición traiga el token como una credencial. Guardamos todo lo necesario en localStorage del navegador*/

//-------- Objeto Sesion (token y usuario se almacenan en localStorage) para mayor manipulación ----------------------
const Sesion = {
    //Funcion de guardar login con usuario y token
    guardar(login) {
        if(!login || !login.token || !login.usuario){
            throw new Error("Respuesta de login inesperada")
        }

        localStorage.setItem("luwi_token", login.token);
        localStorage.setItem("luwi_usuario", JSON.stringify(login.usuario));
    },

    //Obtener token de localStorage para no tener que repetir solicitud de login cada vez que se vuelve a retomar el aplicativo o se realiza una acción
    token(){
        return localStorage.getItem("luwi_token");
    },

    //Obtener usuario de localStorage para no tener que repetir solicitud de login cada vez que se vuelve a retomar el aplicativo o se realiza una acción
    usuario(){
        try{
            return JSON.parse(localStorage.getItem("luwi_usuario"));
        } catch{
            return null;
        }
    },

    // Limpiamos localStorage en caso de cerrar sesión
    limpiar(){
        localStorage.removeItem("luwi_token");
        localStorage.removeItem("luwi_usuario");
    },

    // Función de cerrar sesion, eliminamos los registros en el localStorage del navegador y retornamos a la pagina de inicio de sesión.
    cerrar(){
        this.limpiar();
        window.location.href = "/pages/login.html";
    },

    // Funcion para dirigir a panel luego de sesion exitosa a su respectivo panel según el rol
    irAlPanel(){
        const usuario = this.usuario();
        
        window.location.href = usuario && usuario.rolUsuario === "REPARADOR" ? "/pages/reparador/panelReparador.html" : "/pages/cliente/panelCliente.html";
    },

    // Para los paneles: si no hay sesión o el rol no corresponde redirige
    exigirRol(rolEsperado){
        const usuario = this.usuario();
        if(!this.token() || !usuario){
            window.location.href = "/pages/login.html";
            return null;
        }

        if(usuario.rolUsuario !== rolEsperado){
            this.irAlPanel();
            return null;
        }

        return usuario;
    }
};

/*------------------------- Llamadas al backend -------------------------*/
async function apiFetch(ruta, opciones = {}) {
    const usarToken = opciones.auth !== false;
    const token = Sesion.token();

    const headers = {"Content-Type": "application/json"};
    if(usarToken && token){
        headers.Authorization = "Bearer " + token;
    }

    let respuesta;

    try{
        respuesta = await fetch(API_URL + ruta,{
            method: opciones.method || "GET", headers, body: opciones.body
        });
    } catch{
        throw new Error("No se pudo conectar con el servidor");
    }

    // Token vencido o inválido
    if(respuesta.status === 401 && usarToken && token){
        Sesion.cerrar();
        throw new Error("Tu sesión expiró, inicia sesión nuevamente.")
    }

    let datos = null;
    const texto = await respuesta.text();
    if(texto){
        try{ datos = JSON.parse(texto);} catch{
        }
    }

    if(!respuesta.ok){
        const mensaje = (datos && datos.mensaje) || (respuesta.status === 403 ? "No tienes permiso para esta acción" : "Error " + respuesta.status);
        throw new Error(mensaje);
    }

    return datos;   
}

// ----------------------------------- Utilidades para visualización en pantalla -----------------
function escaparHtml(texto){
    return String(texto ?? "")
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#39;");
}

function mostrarAlerta(contenedor, mensaje, tipo = "danger"){
    contenedor.innerHTML = `<div class="alert alert-${tipo} py-2" role="alert">${escaparHtml(mensaje)}</div>`;
}

function formatearPesos(valor){
    if(valor === null || valor === undefined) return "-";
    return new Intl.NumberFormat("es-CO", {
        style: "currency", currency: "COP", maximumFractionDigits: 0
    }).format(valor);
}

function formatearFecha(fechaIso){
    if(!fechaIso) return "";
    return new Date(fechaIso).toLocaleDateString("es-CO", {
        day: "2-digit", month: "short", year: "numeric"
    });
}
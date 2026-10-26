
/* ----------------- Estados de estilos y textos ------------------*/
const BADGES = {
    PUBLICADA: "bg-warning text-dark",
    ASIGNADA: "bg-success",
    CANCELADA: "bg-secondary",
    FINALIZADA: "bg-dark",
    PENDIENTE: "bg-warning text-dark",
    ACEPTADA: "bg-success",
    RECHAZADA: "bg-danger",
    RETIRADA: "bg-secondary"
};

const MENSAJES_ESTADO = {
    PENDIENTE: "Esperando respuesta del cliente.",
    ACEPTADA: "¡El cliente aceptó tu oferta!",
    RECHAZADA: "El cliente eligió otra oferta.",
    RETIRADA: "Retiraste esta oferta."
};

/* ------------------ Guardia: solo reparadores -------------------- */
const usuario = Sesion.exigirRol("REPARADOR");

/* ------------------- Elementos de la página ---------------------- */

const formFiltro = document.getElementById("formFiltro");
const filtroCiudad = document.getElementById("filtroCiudad");
const alertaDisponibles = document.getElementById("alertaDisponibles");
const listaDisponibles = document.getElementById("listaDisponibles");

const alertaMisOfertas = document.getElementById("alertaMisOfertas");
const listaMisOfertas = document.getElementById("listaMisOfertas");
const contadorOfertas = document.getElementById("contadorOfertas");

const formOferta = document.getElementById("formOferta");
const alertaOferta = document.getElementById("alertaOferta");
const botonEnviarOferta = formOferta.querySelector("button[type=submit]");
const modalOferta = new bootstrap.Modal(document.getElementById("modalOferta"));

/* ------------------ Estado en memoria ---------------- */
let idsOfertados = new Set(); //ids de reparaciones que ya oferto como reparador.
let reparacionSeleccionada = null; //id de la reparación del modal.


/* ------------------ Arranque ------------------- */
async function iniciar() {
    document.getElementById("nombreUsuario").textContent = usuario.nombre;
    filtroCiudad.value = usuario.ciudad;

    document.getElementById("btnSalir").addEventListener("click", () => Sesion.cerrar());
    document.getElementById("btnActualizarOfertas").addEventListener("click", cargarMisOfertas);

    document.getElementById("btnTodas").addEventListener("click", () => {
        filtroCiudad.value = "";
        cargarDisponibles();
    });

    formFiltro.addEventListener("submit", (evento) =>{
        evento.preventDefault();
        cargarDisponibles();
    });
    listaDisponibles.addEventListener("click", manejarClicDisponibles);
    formOferta.addEventListener("submit", enviarOferta);

    // Primero mis ofertas, para saber a cuáles ya el reparador ofertó al dibujar el feed
    await cargarMisOfertas();
    await cargarDisponibles();
}

/* -------------- Pestaña 1: reparaciones disponibles ------------------- */
function textoPresupuesto(min, max){
    if(min != null && max != null) return `${formatearPesos(min)} a ${formatearPesos(max)}`;
    if(min != null) return `Desde ${formatearPesos(min)}`;
    if(max != null) return `Hasta ${formatearPesos(max)}`;
    return "No definido";
}

function crearTarjetaDisponible(reparacion){
    const marca = reparacion.marcaObjeto ? ` · ${escaparHtml(reparacion.marcaObjeto)}` : "";
    const yaOferto = idsOfertados.has(reparacion.idReparacion);

    const boton = yaOferto ?
        `<button class = "btn btn-secondary btn-sm" disabled>
            <i class = "bi bi-check2"></i> Ya ofertaste
        </button>`
        :
        `<button class = "btn btn-primary btn-sm" data-accion = "ofertar" data-id = "${reparacion.idReparacion}" data-titulo = "${escaparHtml(reparacion.titulo)}">
            <i class = "bi bi-cash-coin"></i> Ofertar
        </button>`;

    return `
    <div class = "card shadow-sm border-0 mb-3">
        <div class = "card-body">
            <h3 class = "h5 mb-1">${escaparHtml(reparacion.titulo)}</h3>
            <p class = "text-secondary small mb-2">
                ${escaparHtml(reparacion.categoria)}${marca} · ${escaparHtml(reparacion.zona)}, ${escaparHtml(reparacion.ciudad)} · ${formatearFecha(reparacion.fechaPublicacion)}
            </p>
            <p class = "mb-2">${escaparHtml(reparacion.descripcion)}</p>
            <p class = "mb-3"><strong>Presupuesto del cliente:</strong> ${textoPresupuesto(reparacion.presupuestoMinimo, reparacion.presupuestoMaximo)}</p>
            ${boton}
        </div>
    </div>`;
}

async function cargarDisponibles() {
    alertaDisponibles.innerHTML = "";
    listaDisponibles.innerHTML = `<p class = "text-secondary">Cargando...</p>`;
    
    const ciudad = filtroCiudad.value.trim();
    const ruta = ciudad ? `/api/reparaciones/disponibles?ciudad=${encodeURIComponent(ciudad)}` : "/api/reparaciones/disponibles";

    try{
        const reparaciones = await apiFetch(ruta);

        if(reparaciones.length === 0){
            const lugar = ciudad ? " en " + escaparHtml(ciudad) : "";
            listaDisponibles.innerHTML = `<div class = "text-center text-secondary py-5">No hay reparaciones disponibles ${lugar}.</div>`
            return;
        }

        listaDisponibles.innerHTML = reparaciones.map(crearTarjetaDisponible).join("");
    } catch (error){
        listaDisponibles.innerHTML = "";
        mostrarAlerta(alertaDisponibles, error.message);
    }
}

function manejarClicDisponibles(evento){
    const boton = evento.target.closest("button[data-accion='ofertar']");
    if(!boton) return;

    abrirModalOferta(boton.dataset.id, boton.dataset.titulo);
}

/* ----------------- Modal: Enviar una oferta a una reparación ---------------- */
function abrirModalOferta(idReparacion, titulo){
    reparacionSeleccionada = idReparacion;

    document.getElementById("modalTituloReparacion").textContent = titulo;
    formOferta.reset();
    formOferta.classList.remove("was-validated");
    alertaOferta.innerHTML = "";

    modalOferta.show();
}

async function enviarOferta(evento) {
    evento.preventDefault();
    alertaOferta.innerHTML = "";

    if(!formOferta.checkValidity()){
        formOferta.classList.add("was-validated");
        return;
    }

    const datosOferta = new FormData(formOferta);
    const datos = {
        precioPropuesto: Number(datosOferta.get("precioPropuesto")),
        descripcion: datosOferta.get("descripcion").trim(),
        tiempoEstimado: datosOferta.get("tiempoEstimado").trim()
    };

    botonEnviarOferta.disabled = true;
    botonEnviarOferta.textContent = "Enviando...";

    try{
        await apiFetch(`/api/reparaciones/${reparacionSeleccionada}/ofertas`,{
            method: "POST",
            body: JSON.stringify(datos)
        });

        modalOferta.hide();

        //Primero cargamos y al final mostramos el aviso
        //(Si no cargarDisponibles() lo borraría al limpiar la alerta)
        await cargarMisOfertas();
        await cargarDisponibles();
        mostrarAlerta(alertaDisponibles, "Oferta enviada. Puedes seguirla en la pestaña \"Mis ofertas\".", "success");
    } catch (error){
        mostrarAlerta(alertaOferta, error.message);
    } finally{
        botonEnviarOferta.disabled = false;
        botonEnviarOferta.textContent = "Enviar oferta";
    }
}

/* --------------- Pestaña 2: Mis ofertas ------------------ */
function crearTarjetaMiOferta(o){
    const borde = o.estado === "ACEPTADA" ? "border border-success border-3 rounded" : "border-0";

    return `
    <div class = "card shadow-sm mb-3 ${borde}">
        <div class = "card-body">
            <div class = "d-flex justify-content-between align-items-start gap-2">
                <h3 class = "h5 mb-1">${escaparHtml(o.tituloReparacion)}</h3>
                <span class = "badge ${BADGES[o.estado]}">${o.estado}</span>
            </div>
            <p class = "fw-bold textNaranjas mb-2">${formatearPesos(o.precioPropuesto)}</p>
            <p class = "mb-2">${escaparHtml(o.descripcion)}</p>
            <p class = "text-secondary small mb-2">
                <i class = "bi bi-clock"></i> ${escaparHtml(o.tiempoEstimado)} · Enviada el ${formatearFecha(o.fechaOferta)}
            </p>
            <p class = "small fw-semibold mb-0">${MENSAJES_ESTADO[o.estado] || ""}</p>
        </div>
    </div>`;
}

async function cargarMisOfertas() {
    alertaMisOfertas.innerHTML = "";

    try{
        const ofertas = await apiFetch("/api/ofertas/mis-ofertas");

        idsOfertados = new Set(ofertas.map(o => o.idReparacion));
        contadorOfertas.textContent = ofertas.length;

        if(ofertas.length === 0){
            listaMisOfertas.innerHTML = `<div class = "text-center text-secondary py-5">Aún no has enviado ninguna oferta a una reparación.</div>`;
            return;
        }

        ofertas.sort((a, b) => new Date(b.fechaOferta) - new Date(a.fechaOferta));
        listaMisOfertas.innerHTML = ofertas.map(crearTarjetaMiOferta).join("");
    } catch (error){
        listaMisOfertas.innerHTML = "";
        mostrarAlerta(alertaMisOfertas, error.message);
    }
}

if(usuario){
    iniciar();
}